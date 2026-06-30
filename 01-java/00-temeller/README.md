# Java'ya Giriş ve Temeller

Bu bölümde Java yolculuğuna ilk adımı atıyoruz. Amacımız, daha sonra gelecek tüm
konuların üzerine inşa edileceği sağlam bir zemin kurmak: Java'nın ne olduğunu, kodun
nasıl çalıştığını, verileri nasıl sakladığımızı ve onlarla nasıl işlem yaptığımızı
rahatça anlamak. Acele etmene gerek yok; her kavramı küçük, çalışan örneklerle göreceğiz.

## Java nedir?

Java; platform bağımsız, nesne yönelimli, güçlü tip denetimine sahip bir programlama
dilidir. En büyük gücü şu sözle özetlenir: **"Write Once, Run Anywhere"** — bir kez yaz,
her yerde çalıştır. Yazdığın kaynak kodu, işletim sistemine göre yeniden yazmana gerek
kalmadan Windows, Linux ve macOS üzerinde aynı şekilde çalışır.

Peki bu sihir nasıl oluyor? Java kodu doğrudan işletim sistemine değil, **JVM**'e (Java
Virtual Machine) konuşur. Sen kaynak kodu yazarsın, derleyici onu **bytecode**'a çevirir,
JVM de bu bytecode'u çalıştığı makineye uygun şekilde çalıştırır.

### JDK, JRE ve JVM

Bu üç kısaltma sık karıştırılır; aralarındaki ilişkiyi iç içe halkalar gibi düşünebilirsin:

- **JVM (Java Virtual Machine):** Bytecode'u çalıştıran sanal makine. Platform bağımsızlığın kalbidir.
- **JRE (Java Runtime Environment):** JVM + Java programlarını *çalıştırmak* için gereken kütüphaneler.
- **JDK (Java Development Kit):** JRE + *geliştirme* araçları (derleyici `javac`, `java` başlatıcısı, hata ayıklayıcı vb.). Kod yazmak için JDK kurman gerekir.

> Kısaca: **Geliştirmek için JDK, çalıştırmak için JRE, yürütmek için JVM.**

### İlk program

Modern JDK'larda (Java 11+) tek dosyalık bir programı derlemeden doğrudan çalıştırabilirsin:

```bash
java Ornek1.java
```

Örnek 1 (`./Ornek1.java`) ekrana bir karşılama yazısı basar ve `System.getProperty` ile
çalıştığın JVM'in sürümünü, sağlayıcısını ve işletim sistemini gösterir. Çalıştırınca
kendi ortamının bilgilerini göreceksin.

## Değişkenler ve veri tipleri

Değişken, bir değeri saklamak için isim verdiğimiz bir kutudur. Java **güçlü tipli** bir
dil olduğundan, her değişkenin bir tipi vardır ve bu tip ne tür veri tutabileceğini belirler.

En sık kullanılan ilkel (primitive) tipler:

| Tip | Açıklama | Örnek |
|-----|----------|-------|
| `int` | Tam sayı (4 byte) | `int yas = 30;` |
| `long` | Büyük tam sayı (8 byte) | `long n = 85_000_000L;` |
| `double` | Ondalıklı, çift duyarlık | `double f = 19.99;` |
| `float` | Ondalıklı, tek duyarlık | `float o = 0.5f;` |
| `boolean` | Mantıksal: `true`/`false` | `boolean a = true;` |
| `char` | Tek karakter (tek tırnak) | `char h = 'A';` |
| `byte`, `short` | Küçük tam sayılar | `byte b = 120;` |

`String` ise tek bir karakter değil, bir metin (karakter dizisi) tutar ve aslında bir
nesnedir: `String isim = "Ayşe";`

### Tip dönüşümü (casting)

Küçük bir tipten büyüğüne geçiş otomatiktir (`int` → `double`). Tersi yönde, yani bilgi
kaybı riski olan geçişlerde Java senden açık bir **cast** ister:

```java
double fiyat = 1999.99;
int tam = (int) fiyat; // 1999 — ondalık kısım atılır
```

Örnek 2 (`./Ornek2.java`) bütün bu tipleri tanımlar, otomatik ve açık dönüşümleri
gösterir, ardından operatörlere geçer.

## Operatörler

Operatörler, değerler üzerinde işlem yapmamızı sağlar:

- **Aritmetik:** `+`, `-`, `*`, `/`, `%` (kalan). Dikkat: iki tam sayının bölümü yine tam
  sayıdır (`17 / 5 == 3`). Ondalıklı sonuç için en az bir tarafı `double` yapmalısın.
- **Karşılaştırma:** `==`, `!=`, `>`, `<`, `>=`, `<=` — sonuçları her zaman `boolean`'dır.
- **Mantıksal:** `&&` (ve), `||` (veya), `!` (değil). `&&` ve `||` "kısa devre" yapar:
  sonucu belirleyen ilk koşul yeterliyse ikinciye bakmaz.

Örnek 2 bu operatörlerin hepsini çalışan kod üzerinde gösterir.

## Ekrana yazdırma ve biçimlendirme

`System.out.println(...)` bir satır yazıp alt satıra geçer. Daha düzenli, hizalı çıktılar
için `System.out.printf(...)` veya `String.format(...)` kullanırız. En sık biçim
belirteçleri:

- `%d` tam sayı, `%f` ondalık, `%.2f` iki ondalık basamak
- `%s` metin, `%n` satır sonu, `%%` gerçek `%` işareti
- `%,.2f` binlik ayraçlı ondalık (ör. `10.000,00` gibi okunur sayı)

Örnek 3 (`./Ornek3.java`) bunları kullanarak basit bir faiz hesabı ve bir geometri
hesabını biçimli biçimde ekrana yazar.

## Konsoldan veri okuma (Scanner)

Gerçek programlarda kullanıcıdan veri almak isteyebilirsin. Bunun klasik yolu `Scanner`'dır:

```java
import java.util.Scanner;
Scanner okuyucu = new Scanner(System.in);
System.out.print("Adın: ");
String ad = okuyucu.nextLine();
```


## Özet

Java'nın platform bağımsızlığını JVM'e borçlu olduğunu, JDK/JRE/JVM ayrımını, değişken
tiplerini, tip dönüşümünü, operatörleri ve biçimli çıktıyı gördük. Bu temeller; döngüler,
metotlar ve nesneler gibi bundan sonraki her konunun yapı taşıdır. Sırada akışı kontrol
etmeyi sağlayan **kontrol yapıları** var.
