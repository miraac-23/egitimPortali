// Ornek1: Birim test (unit test) — JUnit 5 + AssertJ ile saf iş mantığını test etmek.
// Bu dosya, içindeki @Test sınıfını JUnit Platform Launcher ile çalıştırıp sonucu yazar.
// (Normalde testleri 'gradle test' / IDE çalıştırır; burada portalda görebilmen için programatik.)
// Çalıştırma: portal derleyip çalıştırır.
package com.egitim.springboot.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

public class Ornek1 {

    // --- Test edilen sınıf (saf iş mantığı, Spring'e ihtiyaç yok) ---
    static class Sepet {
        private double toplam = 0;
        void ekle(double fiyat) {
            if (fiyat < 0) throw new IllegalArgumentException("fiyat negatif olamaz");
            toplam += fiyat;
        }
        double indirimliToplam(double oran) { return toplam * (1 - oran); }
        double getToplam() { return toplam; }
    }

    // --- Test sınıfı: @Test metotları + AssertJ iddiaları (assertions) ---
    static class SepetTest {
        Sepet sepet;

        @BeforeEach           // her testten önce taze kurulum
        void setUp() { sepet = new Sepet(); }

        @Test
        @DisplayName("ürün ekleyince toplam artar")
        void urunEkleme() {
            sepet.ekle(100);
            sepet.ekle(50);
            assertThat(sepet.getToplam()).isEqualTo(150);
        }

        @Test
        @DisplayName("indirim doğru hesaplanır")
        void indirim() {
            sepet.ekle(200);
            assertThat(sepet.indirimliToplam(0.10)).isEqualTo(180);
        }

        @Test
        @DisplayName("negatif fiyat reddedilir")
        void negatifFiyat() {
            assertThatThrownBy(() -> sepet.ekle(-5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("negatif");
        }
    }

    public static void main(String[] args) {
        System.out.println("================ BİRİM TEST (JUnit 5 + AssertJ) ================");
        testCalistir(SepetTest.class);
        System.out.println("""

                --- Birim test ---
                @Test ile test metodu, @BeforeEach ile her testten önce kurulum yazılır.
                AssertJ akıcı iddialar sunar: assertThat(x).isEqualTo(y), assertThatThrownBy(...).
                Birim testler hızlıdır ve TEK bir sınıfı/metodu yalıtılmış test eder.""");
    }

    // JUnit testlerini programatik çalıştırıp özet yazan yardımcı.
    static void testCalistir(Class<?> testSinifi) {
        LauncherDiscoveryRequest req = request().selectors(selectClass(testSinifi)).build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener dinleyici = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(dinleyici);
        launcher.execute(req);
        TestExecutionSummary ozet = dinleyici.getSummary();
        System.out.printf("Çalışan: %d | Başarılı: %d | Başarısız: %d%n",
                ozet.getTestsStartedCount(), ozet.getTestsSucceededCount(), ozet.getTestsFailedCount());
        ozet.getFailures().forEach(f -> System.out.println("  HATA: " + f.getException()));
        if (ozet.getTestsFailedCount() == 0) System.out.println("Tüm testler GEÇTİ ✓");
        ozet.printFailuresTo(new PrintWriter(System.out));
    }
}
