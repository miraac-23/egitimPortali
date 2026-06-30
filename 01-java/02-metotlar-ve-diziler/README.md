# Metotlar ve Diziler

Programlar büyüdükçe aynı kodu tekrar tekrar yazmak hem yorucu hem de hataya açıktır.
**Metotlar**, bir işi bir kez tanımlayıp istediğimiz kadar çağırmamızı sağlar; kodu
parçalara böler, okunur ve test edilebilir kılar. **Diziler** ise tek bir isim altında
çok sayıda değeri tutmamıza imkân verir. Bu ikisi birlikte, gerçek programların iskeletini
oluşturur.

## Metotlar

Bir metot; bir isim, parametreler (girdi), bir gövde (yapılan iş) ve bir dönüş tipinden
(çıktı) oluşur:

```java
static int kare(int x) {   // dönüş tipi: int, parametre: int x
    return x * x;          // return ile değeri geri gönderir
}
```

Değer döndürmeyen metotların dönüş tipi `void`'dir; bunlar yalnızca bir iş yapar (örneğin
ekrana yazar) ve bir şey geri vermez.

> Bu örneklerde metotları `static` tanımlıyoruz; böylece nesne oluşturmadan doğrudan
> `main` içinden çağırabiliyoruz. `static`'in ne anlama geldiğini OOP bölümünde derinlemesine
> göreceğiz.

### Metot aşırı yükleme (overloading)

Aynı isimde, fakat **farklı parametre listesiyle** birden çok metot tanımlayabilirsin.
Derleyici, çağrıdaki argümanlara bakarak hangisini çalıştıracağına karar verir:

```java
int topla(int a, int b)            { return a + b; }
double topla(double a, double b)   { return a + b; }
int topla(int a, int b, int c)     { return a + b + c; }
```

Dönüş tipi tek başına ayırt edici değildir; fark **parametrelerde** olmalıdır.

### varargs (değişken sayıda parametre)

Kaç argüman geleceğini bilmiyorsan `tip...` sözdizimini kullanırsın. İçeride bir dizi gibi
davranır:

```java
static int toplamHepsi(int... sayilar) { ... }
toplamHepsi();              // 0
toplamHepsi(1, 2, 3, 4, 5); // 15
```

Örnek 1 (`./Ornek1.java`) parametre/dönüş değeri, overloading ve varargs'ı bir arada gösterir.

## Diziler

Dizi, **aynı tipteki** sabit sayıda değeri yan yana tutan bir yapıdır. Bir kez
oluşturulduğunda boyutu değişmez ve indeksleri **0'dan** başlar:

```java
int[] notlar = {72, 95, 48, 88};
notlar.length;  // 4
notlar[0];      // 72 (ilk eleman)
```

Diziyi gezmenin iki yolu vardır: indekse ihtiyacın yoksa `for-each` daha temizdir, indeks
gerekiyorsa klasik `for` kullanırsın:

```java
for (int not : notlar) { ... }              // for-each
for (int i = 0; i < notlar.length; i++) {}  // klasik for
```

`java.util.Arrays` sınıfı dizilerle çalışmayı kolaylaştırır: `Arrays.toString` (yazdırma),
`Arrays.sort` (sıralama), `Arrays.copyOf` (kopyalama) gibi. Örnek 2 (`./Ornek2.java`) bir
not dizisinde toplam, ortalama, en büyük/küçük değeri bulur ve diziyi sıralar.

### Çok boyutlu diziler

Bir dizinin elemanları yine dizi olabilir; böylece tablolar (matrisler) elde ederiz:

```java
int[][] a = {
    {1, 2, 3},
    {4, 5, 6}
};
a.length;     // 2 (satır sayısı)
a[0].length;  // 3 (sütun sayısı)
a[1][2];      // 6
```

İç içe `for` döngüleriyle satır ve sütunlar üzerinde geziniriz. Örnek 3 (`./Ornek3.java`)
iki matrisi toplayan ve yazdıran metotlar yazar, ardından satır toplamlarını hesaplar.

## Özet

Metotlarla kodu tekrar kullanılabilir parçalara böldük; overloading ve varargs ile esnek
imzalar tanımladık. Dizilerle çok sayıda veriyi tek isim altında topladık, tek ve çok
boyutlu dizilerde gezindik. Sırada, verileri ve davranışları bir arada paketleyen asıl
büyük adım var: **nesne yönelimli programlama**.
