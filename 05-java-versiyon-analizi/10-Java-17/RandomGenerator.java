// =============================================================================
//  RandomGenerator.java
//  JAVA 17 (LTS) - Enhanced Pseudo-Random Number Generators (JEP 356)
// =============================================================================
//
//  NEDİR?
//  Java 17, rastgele sayı üreticileri için yeni ve ortak bir arayüz ailesi
//  getirdi: java.util.random.RandomGenerator. Artık Random, SecureRandom,
//  SplittableRandom ve yeni algoritmalar (LXM, Xoshiro/Xoroshiro ailesi) hepsi
//  ORTAK bir arayüz altında toplandı.
//
//  NEDEN GELDİ?
//  Eski java.util.Random:
//    - Tek bir (zayıf) algoritma sunuyordu (LCG - lineer kongrüent).
//    - Farklı algoritmaları kolayca değiştirmek (pluggable) mümkün değildi.
//    - Stream tabanlı, paralel/bölünebilir (splittable) üretim için tutarlı bir
//      soyutlama yoktu.
//  Yeni RandomGeneratorFactory ile algoritmayı İSME GÖRE seçebilir, ortak
//  arayüz üzerinden kullanabiliriz.
//
//  NOT: Dosyadaki public sınıf adının dosya adıyla eşleşmesi gerekir; bu yüzden
//  public sınıf adı "RandomGenerator"dır. JDK arayüzü olan
//  java.util.random.RandomGenerator'a karışmaması için onu TAM NİTELİKLİ
//  (fully-qualified) isimle kullanıyoruz; ayrı bir import YAPMIYORUZ.
//
//  Derlemek için: javac RandomGenerator.java
//  Çalıştırmak  : java RandomGenerator
// =============================================================================

import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

public class RandomGenerator {

    public static void main(String[] args) {

        System.out.println("=== JAVA 17 (LTS): Enhanced Random Number Generators ===\n");

        // ---------------------------------------------------------------------
        // 1) ESKİ YÖNTEM: java.util.Random (hâlâ çalışır, ama tek algoritma)
        // ---------------------------------------------------------------------
        System.out.println("--- 1) ESKİ: java.util.Random ---");
        var eski = new java.util.Random(42); // sabit tohum -> tekrarlanabilir
        System.out.println("  Eski Random ilk 3 değer: "
                + eski.nextInt(100) + ", " + eski.nextInt(100) + ", " + eski.nextInt(100));
        System.out.println();

        // ---------------------------------------------------------------------
        // 2) YENİ: RandomGenerator arayüzü üzerinden, algoritma SEÇİLEBİLİR
        // ---------------------------------------------------------------------
        System.out.println("--- 2) YENİ: RandomGeneratorFactory ile algoritma seçimi ---");
        // "L64X128MixRandom" modern, kaliteli bir LXM üreticisidir.
        java.util.random.RandomGenerator modern = RandomGeneratorFactory
                .of("L64X128MixRandom")
                .create(42);
        System.out.println("  Modern üreticinin ilk 3 değeri: "
                + modern.nextInt(100) + ", " + modern.nextInt(100) + ", " + modern.nextInt(100));
        System.out.println();

        // ---------------------------------------------------------------------
        // 3) En basit kullanım: varsayılan üretici
        // ---------------------------------------------------------------------
        System.out.println("--- 3) En kısa kullanım: getDefault() ---");
        java.util.random.RandomGenerator varsayilan =
                java.util.random.RandomGenerator.getDefault();
        System.out.println("  Varsayılan üretici bir zar atışı: " + (varsayilan.nextInt(6) + 1));
        System.out.println();

        // ---------------------------------------------------------------------
        // 4) Stream API ile rastgele sayı üretimi
        // ---------------------------------------------------------------------
        System.out.println("--- 4) Stream ile 5 adet rastgele sayı ---");
        String sayilar = varsayilan.ints(5, 1, 101) // 5 adet, [1,100]
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(", "));
        System.out.println("  " + sayilar);
        System.out.println();

        // ---------------------------------------------------------------------
        // 5) Kullanılabilir tüm algoritmaları listele
        // ---------------------------------------------------------------------
        System.out.println("--- 5) Bu JDK'da mevcut algoritmalar ---");
        RandomGeneratorFactory.all()
                .map(RandomGeneratorFactory::name)
                .sorted()
                .forEach(ad -> System.out.println("  - " + ad));
        System.out.println();

        // ---------------------------------------------------------------------
        // GERÇEK HAYAT NOTU:
        //  - Güvenlik gerektiren durumlar (token, şifre, anahtar) için DAİMA
        //    java.security.SecureRandom kullanın; bu algoritmalar genel amaçlıdır.
        //  - Paralel/çok çekirdekli simülasyonlarda "splittable" (örn. L64X128)
        //    veya "jumpable" üreticiler tercih edilir (alt-akışlar arası bağımsızlık).
        //  - Tekrarlanabilir testler için sabit tohum (seed) verin.
        // ---------------------------------------------------------------------
        System.out.println("Not: Güvenlik için SecureRandom; simülasyon için splittable üreticiler.");
    }
}
