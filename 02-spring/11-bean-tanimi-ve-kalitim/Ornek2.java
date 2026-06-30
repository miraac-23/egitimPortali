// Ornek2: BEAN DEFINITION INHERITANCE (bean tanımı kalıtımı).
// Birden çok bean'in ortak yapılandırması varsa, bunu her birinde TEKRARLAMAK yerine
// bir "şablon" (parent) tanımda toplarsın; çocuk tanımlar ondan MİRAS alır ve yalnızca
// farklı kısımları ezer (override). Bu, NESNE kalıtımı değil; bean TANIMI kalıtımıdır.
// (Klasik XML'de <bean parent="..."> ile yapılır; burada programatik olarak gösteriyoruz.)
// Çalıştırma: java Ornek2.java
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class Ornek2 {

    public static void main(String[] args) {
        var bf = new DefaultListableBeanFactory();

        // 1) PARENT (şablon) tanım: ortak property'ler burada. abstract=true -> kendisi
        //    örneklenmez, yalnızca miras için kullanılır.
        RootBeanDefinition sablon = new RootBeanDefinition();
        sablon.setAbstract(true);
        sablon.getPropertyValues().add("ulke", "TR").add("paraBirimi", "TL");
        bf.registerBeanDefinition("temelAyar", sablon);

        // 2) ÇOCUK tanımlar: parent'tan ulke/paraBirimi'ni MİRAS alır, kendi alanını ekler.
        GenericBeanDefinition satis = cocuk("Satis");
        satis.getPropertyValues().add("bolum", "Satış");
        bf.registerBeanDefinition("satisAyar", satis);

        GenericBeanDefinition muhasebe = cocuk("Muhasebe");
        muhasebe.getPropertyValues().add("bolum", "Muhasebe");
        // Bu çocuk ortak değeri EZİYOR (override): paraBirimi'ni USD yapıyor.
        muhasebe.getPropertyValues().add("paraBirimi", "USD");
        bf.registerBeanDefinition("muhasebeAyar", muhasebe);

        // 3) Sonuç: miras alınan + ezilen değerleri gör.
        System.out.println("satisAyar    -> " + bf.getBean("satisAyar"));
        System.out.println("muhasebeAyar -> " + bf.getBean("muhasebeAyar"));

        System.out.println("""

                --- Ne oldu? ---
                * 'ulke=TR' ikisinde de var ama yalnızca ŞABLONDA yazıldı (DRY).
                * satisAyar paraBirimi'ni miras aldı (TL).
                * muhasebeAyar aynı alanı EZDİ (USD).
                Bean tanımı kalıtımı, tekrarı azaltır; ortak ayarları tek yerde toplar.""");
    }

    // Ortak parent'a bağlı bir çocuk tanım üretir (setParentName = miras kaynağı).
    static GenericBeanDefinition cocuk(String etiket) {
        GenericBeanDefinition c = new GenericBeanDefinition();
        c.setParentName("temelAyar");   // <-- MİRAS: temelAyar'ın property'lerini devralır
        c.setBeanClass(Ayar.class);
        return c;
    }
}

// Basit bir JavaBean (setter'lar üzerinden property enjeksiyonu yapılır).
class Ayar {
    private String ulke, paraBirimi, bolum;
    public void setUlke(String u) { this.ulke = u; }
    public void setParaBirimi(String p) { this.paraBirimi = p; }
    public void setBolum(String b) { this.bolum = b; }
    public String toString() {
        return "Ayar(ulke=" + ulke + ", paraBirimi=" + paraBirimi + ", bolum=" + bolum + ")";
    }
}
