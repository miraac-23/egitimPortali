// =============================================================================
//  Java 22 - Foreign Function & Memory (FFM) API  (JEP 454 - KALICI / STABLE)
// =============================================================================
//
//  ÖNEMLİ NOT:
//  -----------
//  FFM API, Java 19/20/21 boyunca "preview" (önizleme) olarak ilerledi.
//  JAVA 22 ile birlikte ARTIK STANDART (non-preview / kalıcı) hale geldi.
//  Yani bu dosyayı --enable-preview bayrağına GEREK OLMADAN derleyebilirsin.
//
//  DERLEME:
//      javac --release 22 ForeignFunctionMemoryApi.java
//
//  ÇALIŞTIRMA (native erişim uyarısını susturmak için --enable-native-access):
//      java --enable-native-access=ALL-UNNAMED ForeignFunctionMemoryApi
//
//  NOT: java.lang.foreign paketi modülünde olduğu için ek bir --add-modules
//  bayrağına gerek yoktur (java.base içindedir).
//
//  Bu örnek, işletim sisteminin STANDART C KÜTÜPHANESİ (libc) içindeki
//  fonksiyonları Java'dan, JNI YAZMADAN doğrudan çağırır:
//      - strlen(const char*)  : bir C string'in uzunluğunu döner
//      - abs(int)             : tam sayının mutlak değeri
//      - printf(const char*)  : C tarafında ekrana yazar (upcall'a benzemese de
//                               variadic fonksiyon çağrısına örnektir)
//
//  Ayrıca bir UPCALL örneği de var: Java tarafında yazılmış bir karşılaştırma
//  fonksiyonunu, C standart kütüphanesinin qsort fonksiyonuna geri-çağrı
//  (callback) olarak veriyoruz.
// =============================================================================

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ForeignFunctionMemoryApi {

    public static void main(String[] args) throws Throwable {

        System.out.println("=== Java 22 Foreign Function & Memory (FFM) API ===\n");

        // ---------------------------------------------------------------------
        //  1) TEMEL KAVRAMLAR
        // ---------------------------------------------------------------------
        //  Linker        : Java ile native (yerel) ABI arasındaki köprü.
        //                  Linker.nativeLinker() platformun C ABI'sini verir.
        //  SymbolLookup  : Bir kütüphanedeki fonksiyon sembollerini (adreslerini)
        //                  bulmamızı sağlar.
        //  FunctionDescriptor : Native fonksiyonun imzasını (dönüş + parametre
        //                  tiplerini) tanımlar.
        //  MethodHandle  : Çağrılabilir Java referansı; native fonksiyonu
        //                  buradan invoke ederiz (downcall).
        //  Arena         : Bellek (MemorySegment) yaşam döngüsünü yönetir.
        //                  try-with-resources ile kapanınca tüm bellek
        //                  GÜVENLİ biçimde serbest bırakılır.
        //  MemorySegment : Off-heap (yığın dışı / native) bellek bloğu.
        //  ValueLayout   : Bir değerin bellekteki yerleşimini/boyutunu tanımlar
        //                  (ör. JAVA_INT, JAVA_BYTE, ADDRESS).
        // ---------------------------------------------------------------------

        Linker linker = Linker.nativeLinker();

        // Standart C kütüphanesinin sembollerini bul (libc / msvcrt vb.).
        // Linker'ın varsayılan lookup'ı, platformun standart kütüphanesini içerir.
        SymbolLookup stdLib = linker.defaultLookup();

        // =====================================================================
        //  ÖRNEK 1: C  strlen()  fonksiyonunu çağırma (DOWNCALL)
        // =====================================================================
        // C imzası:  size_t strlen(const char *s);
        //  - Dönüş tipi: size_t  -> 64-bit platformda JAVA_LONG
        //  - Parametre : const char* -> ADDRESS (bir bellek adresi / pointer)
        ornekStrlen(linker, stdLib);

        // =====================================================================
        //  ÖRNEK 2: C  abs()  fonksiyonu (DOWNCALL, sadece skaler tipler)
        // =====================================================================
        // C imzası:  int abs(int n);
        ornekAbs(linker, stdLib);

        // =====================================================================
        //  ÖRNEK 3: C  qsort()  ile UPCALL (Java callback'i C'ye verme)
        // =====================================================================
        // C imzası:  void qsort(void *base, size_t nmemb, size_t size,
        //                       int (*compar)(const void *, const void *));
        ornekQsortUpcall(linker, stdLib);

        System.out.println("\n=== Tüm FFM örnekleri tamamlandı ===");
    }

    // -------------------------------------------------------------------------
    //  ÖRNEK 1: strlen
    // -------------------------------------------------------------------------
    private static void ornekStrlen(Linker linker, SymbolLookup stdLib) throws Throwable {
        System.out.println("--- ÖRNEK 1: C strlen() çağrısı ---");

        // strlen sembolünü bul. Bulunamazsa Optional boş döner.
        MemorySegment strlenAddr = stdLib.find("strlen")
                .orElseThrow(() -> new RuntimeException("strlen sembolü bulunamadı"));

        // Fonksiyon imzasını tanımla:
        //   dönüş: JAVA_LONG (size_t), parametre: ADDRESS (const char*)
        FunctionDescriptor strlenDesc =
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);

        // Çağrılabilir MethodHandle (downcall handle) üret.
        MethodHandle strlen = linker.downcallHandle(strlenAddr, strlenDesc);

        // Arena: ayırdığımız native belleğin yaşam döngüsünü yönetir.
        // try-with-resources bloğu bitince bellek otomatik ve GÜVENLİ serbest kalır.
        // (Manuel free() çağrısı YOK -> memory leak ve use-after-free riski düşer.)
        try (Arena arena = Arena.ofConfined()) {
            String metin = "Merhaba FFM API!";

            // Java String'i, C'nin beklediği NUL ('\0') ile sonlanan
            // bir byte dizisine çevirip native belleğe yaz.
            // NOT: Java 22'de bu metot allocateFrom(String) adındadır.
            // (Java 21 preview'da adı allocateUtf8String idi -> yeniden adlandırıldı.)
            MemorySegment cString = arena.allocateFrom(metin);

            // Native fonksiyonu çağır. Dönüş long (size_t).
            long uzunluk = (long) strlen.invoke(cString);

            System.out.println("  Metin       : \"" + metin + "\"");
            System.out.println("  Java length : " + metin.length());
            System.out.println("  C strlen()  : " + uzunluk + " byte (UTF-8 byte sayısı)");
            // Not: Türkçe karakter içermediği için bu metinde length == strlen.
            // 'ç','ş' gibi çok-baytlı UTF-8 karakterlerde C strlen byte sayar,
            // Java length ise UTF-16 kod birimi sayar -> farklı olabilir.
        }
        System.out.println();
    }

    // -------------------------------------------------------------------------
    //  ÖRNEK 2: abs
    // -------------------------------------------------------------------------
    private static void ornekAbs(Linker linker, SymbolLookup stdLib) throws Throwable {
        System.out.println("--- ÖRNEK 2: C abs() çağrısı ---");

        MemorySegment absAddr = stdLib.find("abs")
                .orElseThrow(() -> new RuntimeException("abs sembolü bulunamadı"));

        // int abs(int) -> dönüş JAVA_INT, parametre JAVA_INT
        FunctionDescriptor absDesc =
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

        MethodHandle abs = linker.downcallHandle(absAddr, absDesc);

        // Skaler tip olduğu için Arena'ya gerek yok (bellek ayırmıyoruz).
        int sonuc = (int) abs.invoke(-12345);
        System.out.println("  abs(-12345) = " + sonuc);
        System.out.println();
    }

    // -------------------------------------------------------------------------
    //  ÖRNEK 3: qsort + UPCALL (Java karşılaştırıcısını C'ye callback olarak ver)
    // -------------------------------------------------------------------------
    private static void ornekQsortUpcall(Linker linker, SymbolLookup stdLib) throws Throwable {
        System.out.println("--- ÖRNEK 3: C qsort() + Java upcall (callback) ---");

        MemorySegment qsortAddr = stdLib.find("qsort")
                .orElseThrow(() -> new RuntimeException("qsort sembolü bulunamadı"));

        // void qsort(void* base, size_t nmemb, size_t size, comparator)
        FunctionDescriptor qsortDesc = FunctionDescriptor.ofVoid(
                ValueLayout.ADDRESS,   // base   (dizinin adresi)
                ValueLayout.JAVA_LONG, // nmemb  (eleman sayısı)
                ValueLayout.JAVA_LONG, // size   (her elemanın byte boyutu)
                ValueLayout.ADDRESS);  // compar (karşılaştırıcı fonksiyon pointer'ı)

        MethodHandle qsort = linker.downcallHandle(qsortAddr, qsortDesc);

        try (Arena arena = Arena.ofConfined()) {
            int[] veriler = { 42, 7, 1000, -3, 88, 0, 17 };
            System.out.println("  Sıralama öncesi : " + java.util.Arrays.toString(veriler));

            // Diziyi native belleğe int (4 byte) dizisi olarak ayır ve doldur.
            // NOT: Java 22'de N elemanlık ayırma için allocate(layout, count) kullanılır.
            // (Java 21 preview'da allocateArray(layout, count) idi -> yeniden adlandırıldı.)
            MemorySegment nativeDizi =
                    arena.allocate(ValueLayout.JAVA_INT, veriler.length);
            for (int i = 0; i < veriler.length; i++) {
                nativeDizi.setAtIndex(ValueLayout.JAVA_INT, i, veriler[i]);
            }

            // ---- UPCALL: Java metodunu C'nin çağırabileceği bir adrese dönüştür ----
            // C comparator imzası:  int compar(const void* a, const void* b);
            // Her iki parametre de elemanın adresidir (ADDRESS).
            FunctionDescriptor comparatorDesc = FunctionDescriptor.of(
                    ValueLayout.JAVA_INT,  // dönüş: int (<0, 0, >0)
                    ValueLayout.ADDRESS,   // a
                    ValueLayout.ADDRESS);  // b

            // Java tarafındaki statik karşılaştırma metodumuza MethodHandle al.
            MethodHandle comparatorHandle = MethodHandles.lookup().findStatic(
                    ForeignFunctionMemoryApi.class,
                    "intKarsilastir",
                    MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class));

            // Java handle'ını native bir fonksiyon pointer'ına (upcall stub) çevir.
            MemorySegment comparatorPtr = linker.upcallStub(
                    comparatorHandle, comparatorDesc, arena);

            // qsort'u çağır. C, sıralama sırasında bizim Java metodumuzu çağıracak.
            qsort.invoke(
                    nativeDizi,
                    (long) veriler.length,
                    ValueLayout.JAVA_INT.byteSize(), // her int 4 byte
                    comparatorPtr);

            // Sonucu native bellekten geri oku.
            int[] sirali = new int[veriler.length];
            for (int i = 0; i < veriler.length; i++) {
                sirali[i] = nativeDizi.getAtIndex(ValueLayout.JAVA_INT, i);
            }
            System.out.println("  Sıralama sonrası: " + java.util.Arrays.toString(sirali));
            System.out.println("  (Sıralamayı C qsort yaptı, karşılaştırmayı Java yaptı!)");
        }
        System.out.println();
    }

    /**
     * C qsort tarafından çağrılan UPCALL hedefi.
     * Parametreler, sıralanan int elemanlarının native ADRESLERİdir.
     * Adresten gerçek int değerini okumak için segmenti yeniden yorumlarız.
     *
     * @param aPtr birinci elemanın adresi
     * @param bPtr ikinci elemanın adresi
     * @return a<b -> negatif, a==b -> 0, a>b -> pozitif
     */
    private static int intKarsilastir(MemorySegment aPtr, MemorySegment bPtr) {
        // Gelen adresler boyutsuzdur; 4 byte'lık int olarak yeniden yorumla.
        int a = aPtr.reinterpret(ValueLayout.JAVA_INT.byteSize())
                    .get(ValueLayout.JAVA_INT, 0);
        int b = bPtr.reinterpret(ValueLayout.JAVA_INT.byteSize())
                    .get(ValueLayout.JAVA_INT, 0);
        return Integer.compare(a, b);
    }
}
