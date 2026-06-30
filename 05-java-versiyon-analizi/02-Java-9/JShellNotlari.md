# JShell (Java REPL) — Java 9 Detaylı Türkçe Notlar

## NEDİR?
JShell, Java 9 ile gelen resmi **REPL** (Read-Eval-Print-Loop) aracıdır. Yani Java kodunu **tek tek, anında** çalıştırıp sonucunu görmenizi sağlayan interaktif bir komut satırı ortamıdır. Python'daki `python`, Node.js'teki `node` konsolu gibi, ama Java için.

Java 9'a kadar en küçük Java kod parçasını çalıştırmak için bile şunları yapmak gerekiyordu:
1. Bir `.java` dosyası oluştur,
2. İçine `public class` ve `public static void main(String[] args)` yaz,
3. `javac` ile derle,
4. `java` ile çalıştır.

JShell, bu zorunlu tören kodunu (`class`, `main`) ortadan kaldırır.

## NEDEN GELDİ? (Hangi problem?)
- **Öğrenme engeli:** Yeni başlayanlar "Hello World" için bile sınıf/main kalıbını ezberlemek zorundaydı.
- **Hızlı deneme ihtiyacı:** "Bu regex çalışıyor mu?", "`String.format` şu argümanlarla ne döndürür?" gibi küçük sorular için koca bir derleme döngüsü gerekiyordu.
- **API keşfi:** Bir kütüphanenin metotlarını denemek için sürekli dosya oluşturmak verimsizdi.

## NASIL BAŞLATILIR?
JDK 9+ kuruluysa terminalden:
```
jshell
```
Açılışta şuna benzer bir karşılama gelir:
```
|  Welcome to JShell -- Version 9
|  For an introduction type: /help intro

jshell>
```
Ek kütüphanelerle (classpath) başlatmak için:
```
jshell --class-path lib/mylib.jar
```
Bir modül yolu ile:
```
jshell --module-path mods --add-modules com.banka.cekirdek
```
Çıkmak için: `/exit`

## TEMEL KOMUTLAR

| Komut | Açıklama |
|-------|----------|
| `/help` | Tüm komutların listesi |
| `/help intro` | JShell'e kısa giriş |
| `/vars` | Tanımlanan tüm değişkenleri listeler |
| `/methods` | Tanımlanan tüm metotları listeler |
| `/types` | Tanımlanan sınıf/interface/enum'ları listeler |
| `/imports` | Aktif import'ları listeler (JShell birçok yaygın paketi otomatik import eder) |
| `/list` | Bu oturumda girilen tüm kod parçacıklarını (snippet) numaralı gösterir |
| `/edit <id>` | Bir snippet'i harici editörde düzenler |
| `/drop <id>` | Bir snippet'i (değişken/metot) siler |
| `/save <dosya>` | Oturumdaki snippet'leri bir dosyaya kaydeder |
| `/open <dosya>` | Bir dosyadaki kodu JShell'e yükler |
| `/reset` | Oturumu sıfırlar (tüm değişken/metotları siler) |
| `/exit` | JShell'den çıkar |

## ÖRNEK OTURUM ÇIKTILARI

### 1) Tören kodu olmadan ifade çalıştırma
```
jshell> 2 + 3 * 4
$1 ==> 14

jshell> "Merhaba" + " Dunya"
$2 ==> "Merhaba Dunya"
```
> `$1`, `$2` JShell'in otomatik atadığı geçici değişken adlarıdır. Sonra bunları tekrar kullanabilirsiniz: `$1 * 2` -> `28`.

### 2) Değişken ve metot tanımlama (class/main gerekmeden!)
```
jshell> int x = 10
x ==> 10

jshell> String selam(String ad) {
   ...>     return "Merhaba " + ad;
   ...> }
|  created method selam(String)

jshell> selam("Ayse")
$5 ==> "Merhaba Ayse"
```

### 3) Otomatik import sayesinde hazır kütüphaneler
```
jshell> List.of(1, 2, 3).stream().mapToInt(Integer::intValue).sum()
$6 ==> 6
```
> `java.util.List`, `java.util.stream.*` gibi paketler önceden import edilmiştir.

### 4) Değişkenleri ve metotları listeleme
```
jshell> /vars
|    int x = 10
|    String $2 = "Merhaba Dunya"

jshell> /methods
|    String selam(String)
```

### 5) Oturumu listeleme, kaydetme, açma
```
jshell> /list

   1 : int x = 10;
   2 : String selam(String ad) { return "Merhaba " + ad; }

jshell> /save oturum.jsh

jshell> /reset
|  Resetting state.

jshell> /open oturum.jsh

jshell> selam("Mehmet")
$3 ==> "Merhaba Mehmet"
```

### 6) Hata anında anında geri bildirim
```
jshell> int y = "metin"
|  Error:
|  incompatible types: java.lang.String cannot be converted to int
|  int y = "metin";
|          ^-----^
```
> Derleme döngüsü beklemeden hatayı anında görürsünüz.

## SEKME (TAB) İLE OTOMATİK TAMAMLAMA
JShell, `Tab` tuşuyla otomatik tamamlama ve API keşfi sunar:
```
jshell> "metin".to<Tab>
toCharArray()   toLowerCase(   toString()   toUpperCase(   ...
```
`Shift+Tab` ardından `v` ile bir ifadenin sonucundan otomatik değişken üretebilirsiniz.

## GERÇEK HAYATTA NE İŞE YARAR?
- **Hızlı prototipleme:** Bir algoritmayı, regex'i veya tarih formatını saniyeler içinde denemek.
- **API öğrenme/keşfi:** Yeni bir kütüphanenin metotlarını `Tab` tamamlama ile keşfetmek.
- **Eğitim:** Java öğretirken öğrencinin `class/main` kalabalığıyla boğulmadan dile odaklanması.
- **Hata ayıklama yardımcısı:** Karmaşık bir ifadenin parça parça ne döndürdüğünü görmek.
- **Demo/sunum:** Canlı kod gösterimleri için ideal (interaktif, anlık sonuç).
- **Betikleme:** `.jsh` dosyalarını `jshell dosya.jsh` ile çalıştırıp küçük Java betikleri koşturmak.

## ÖZET
JShell, Java'yı "derle-çalıştır" hantallığından kurtarıp diğer modern dillerin sunduğu interaktif deneyimi getirir. Üretim kodu yazmak için değil; **denemek, öğrenmek ve keşfetmek** için tasarlanmıştır. Java 9'un öğrenme eğrisini ve geliştirici verimliliğini ciddi biçimde iyileştiren özelliklerinden biridir.
