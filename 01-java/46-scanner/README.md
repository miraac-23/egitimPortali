# Scanner Sınıfı

`Scanner`, Java'da metin girdisini okuyup parçalara ayırmanın en pratik yoludur. Klavyeden
kullanıcı girdisi almak, bir dosyayı satır satır işlemek veya bir metni belirteçlere (token)
ayırmak için kullanılır. Tek bir sınıfla hem ham metni hem de sayı/boolean gibi tipleri okuyabilmen,
onu öğrenirken ilk karşılaşılan araçlardan biri yapar.


## Kaynak ve tip tip okuma

`Scanner` herhangi bir kaynaktan okuyabilir: `System.in` (klavye), `File`, `String`. Girdiyi
varsayılan olarak **boşluklara** göre belirteçlere böler ve her belirteci istenen **tipte** okur:

```java
Scanner sc = new Scanner("Ada 30 175.5 true");
String ad = sc.next();         // "Ada"
int yas = sc.nextInt();        // 30
double boy = sc.nextDouble();  // 175.5
boolean uye = sc.nextBoolean();// true
```

Metotlar: `next()` (bir belirteç), `nextInt()`, `nextLong()`, `nextDouble()`, `nextBoolean()`,
`nextLine()` (tüm satır). Örnek 1 (`./Ornek1.java`) tip tip okumayı ve güvenli okumayı gösterir.

## Güvenli okuma: hasNextX()

Bir belirteci yanlış tiple okumaya çalışmak `InputMismatchException` atar. Bu yüzden okumadan
**önce** kontrol et:

```java
while (sc.hasNext()) {
    if (sc.hasNextInt()) toplam += sc.nextInt(); // sayıysa al
    else sc.next();                              // değilse atla
}
```

`hasNext()`, `hasNextInt()`, `hasNextLine()` gibi metotlar, kaynakta uygun veri olup olmadığını
okumadan söyler — döngülerin ve doğrulamanın temelidir.

## Satır okuma ve özel ayraç

- **`nextLine()`**: Satır sonuna kadar olan tüm metni okur (içindeki boşluklar dahil).
- **`useDelimiter(regex)`**: Belirteç ayracını değiştirir. Örneğin virgülle ayrılmış (CSV) veriyi
  okumak için `useDelimiter(",")`.

Örnek 2 (`./Ornek2.java`) satır satır okumayı, CSV ayracını ve aşağıdaki klasik tuzağı gösterir.

## Klasik tuzak: nextInt() sonrası nextLine()

En sık yapılan hata: `nextInt()`/`nextDouble()`, okuduğu sayıdan sonraki **satır sonunu (`\n`)
tüketmez**. Hemen ardından `nextLine()` çağırırsan, o boş kalan satır sonunu okur (boş string döner):

```java
int sayi = sc.nextInt();
sc.nextLine();          // <-- bırakılan satır sonunu TÜKET
String isim = sc.nextLine(); // şimdi doğru satırı okur
```

Çözüm: araya fazladan bir `nextLine()` koymak (veya tüm girdiyi `nextLine()` ile okuyup elle
ayrıştırmak).

## Kaynak kapatma

`Scanner` bir kaynağı sarar; işin bitince `close()` çağır (özellikle `File` kaynaklarında).
`try-with-resources` ile otomatik kapatabilirsin: `try (Scanner sc = new Scanner(dosya)) { ... }`.
(`System.in`'i saran Scanner'ı kapatmak System.in'i de kapatır; ona dikkat.)

## Alternatifler

- **`BufferedReader`**: Büyük dosyaları satır satır okumada `Scanner`'dan daha hızlıdır
  (`readLine()`).
- **`String.split(regex)`**: Elindeki bir metni hızlıca parçalara ayırmak için.
- **`Files.lines(path)`**: Bir dosyayı satır akışı (Stream) olarak okumak için.

## Özet

`Scanner` ile metni belirteçlere ayırıp tip tip okumayı (Örnek 1); `hasNextX()` ile güvenli
okumayı, satır okuma ve özel ayraçları, ve `nextInt`/`nextLine` tuzağını (Örnek 2) öğrendik. Klavye
girdisi ve basit metin/dosya ayrıştırma için ideal; büyük dosyalarda `BufferedReader`/`Files.lines`
tercih edilir. Sırada en çok kullanılan koleksiyon: **ArrayList**.
