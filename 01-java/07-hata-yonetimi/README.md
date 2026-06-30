# Hata Yönetimi (Exceptions)

Hiçbir program kusursuz bir dünyada çalışmaz: dosya bulunamaz, ağ kopar, kullanıcı beklenmedik
veri girer, sıfıra bölünür. İyi bir yazılımcı bu durumları yok saymaz; onları öngörür ve
zarifçe ele alır. Java'da bunun aracı **exception (istisna)** mekanizmasıdır. Amaç, program
bir sorunla karşılaştığında çökmek yerine kontrollü biçimde tepki vermesidir.

## try, catch, finally

Riskli kodu bir `try` bloğuna koyarız; bir hata oluşursa akış ilgili `catch` bloğuna atlar:

```java
try {
    int sonuc = 10 / 0;            // ArithmeticException fırlar
} catch (ArithmeticException e) {
    System.out.println(e.getMessage());
}
```

`catch`, yakalamak istediğin exception tipini belirtir. Hata o tipteyse blok çalışır, değilse
exception yukarı doğru "kabarır". `finally` bloğu ise **her durumda** — hata olsa da olmasa
da — çalışır; bu yüzden temizlik/kaynak kapatma işleri için idealdir.

Örnek 1 (`./Ornek1.java`) en sık karşılaşacağın `ArithmeticException`, `NullPointerException`
ve `ArrayIndexOutOfBoundsException`'ı yakalar ve `finally`'nin davranışını gösterir.

## Checked vs unchecked

Java'da exception'lar iki büyük aileye ayrılır:

- **Checked exceptions** (`Exception` soyundan, `RuntimeException` hariç): Derleyici bunları
  ele almanı **zorunlu** tutar. Ya `try-catch` ile yakalarsın ya da metot imzasında `throws`
  ile bildirirsin. Genelde dış dünyayla ilgili, öngörülebilir sorunlardır (dosya yok, ağ hatası).
- **Unchecked exceptions** (`RuntimeException` soyundan): Derleyici zorunlu tutmaz. Genelde
  programlama hatalarıdır (`NullPointerException`, `IllegalArgumentException`,
  `NumberFormatException`).

## throw ve throws

`throw` bir exception'ı **elle fırlatır**; `throws` ise bir metodun hangi checked exception'ı
fırlatabileceğini **bildirir**:

```java
static void paraCek(double bakiye, double tutar) throws YetersizBakiyeException {
    if (tutar > bakiye)
        throw new YetersizBakiyeException("Yetersiz bakiye");
}
```

### Özel (custom) exception

Kendi anlamlı exception tiplerini tanımlayabilirsin. `Exception`'dan türetirsen checked,
`RuntimeException`'dan türetirsen unchecked olur. Bu, hataya iş anlamı katar:
`YetersizBakiyeException`, `int -1` döndürmekten çok daha açıktır. Örnek 2 (`./Ornek2.java`)
checked/unchecked ayrımını, `throw/throws`'u ve özel bir exception'ı bir banka senaryosunda
gösterir.

## try-with-resources

Dosya, veritabanı bağlantısı, akış (stream) gibi kaynakları işin bitince **kapatmak**
zorundayız. Bunu `finally` ile elle yapmak hataya açıktır. Java bunun için temiz bir yol
sunar: `try-with-resources`. Parantez içinde açtığın, `AutoCloseable`'ı uygulayan kaynaklar,
blok bitince (hata olsa bile) **otomatik** kapatılır:

```java
try (Baglanti b = new Baglanti("db")) {
    b.sorguCalistir("...");
} // b.close() otomatik çağrılır
```

### multi-catch

Birden çok exception tipini aynı şekilde ele alacaksan, tek blokta `|` ile birleştirebilirsin:

```java
catch (NumberFormatException | ArithmeticException e) { ... }
```

Örnek 3 (`./Ornek3.java`) `AutoCloseable` bir kaynak yazar, `try-with-resources` ile otomatik
kapanışı (hatalı senaryo dahil) ve `multi-catch`'i gösterir.

## İyi uygulama notları

- Boş `catch` blokları yazma; hatayı en azından logla. Sessizce yutulan hata, bulunması en zor
  hatadır.
- `catch (Exception e)` gibi çok geniş yakalamalardan, gerçekten gerekmedikçe kaçın.
- Akışı kontrol etmek için exception kullanma; exception "istisnai" durumlar içindir.
- Anlamlı mesajlar ve uygun (özel) tipler kullan.

## Özet

Riskli kodu `try` ile sarmayı, `catch` ile hataya tepki vermeyi, `finally` ve
`try-with-resources` ile kaynakları güvenle kapatmayı öğrendik. Checked/unchecked ayrımını,
`throw/throws`'u ve özel exception'ları gördük. Artık programların hata karşısında
çökmesini değil, kontrollü davranmasını sağlayabilirsin. Sırada, çok sayıda veriyi yönetmenin
güçlü yolu: **Collections Framework**.
