// Ornek2: Mockito ile yalıtılmış test — bağımlılığı sahte (mock) nesneyle değiştirmek.
// Çalıştırma: portal derleyip çalıştırır (JUnit Platform Launcher ile).
package com.egitim.springboot.test;

import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import static org.mockito.Mockito.*;

public class Ornek2 {

    // Test edilecek servis, bir repository'ye bağımlı.
    interface UrunRepository { Optional<String> adBul(int id); }

    static class UrunServisi {
        private final UrunRepository repo;
        UrunServisi(UrunRepository repo) { this.repo = repo; }
        String adGetir(int id) { return repo.adBul(id).orElse("bilinmiyor"); }
    }

    static class UrunServisiTest {
        @Test
        void bulunanUrun() {
            // 1) Sahte repository oluştur (gerçek DB'ye gitmez).
            UrunRepository sahteRepo = mock(UrunRepository.class);
            // 2) Davranışını programla: id=1 için "Klavye" dön.
            when(sahteRepo.adBul(1)).thenReturn(Optional.of("Klavye"));

            UrunServisi servis = new UrunServisi(sahteRepo);
            assertThat(servis.adGetir(1)).isEqualTo("Klavye");

            // 3) Etkileşimi doğrula: repo.adBul(1) gerçekten çağrıldı mı?
            verify(sahteRepo).adBul(1);
        }

        @Test
        void bulunmayanUrun() {
            UrunRepository sahteRepo = mock(UrunRepository.class);
            when(sahteRepo.adBul(anyInt())).thenReturn(Optional.empty()); // hiçbir id bulunamasın
            UrunServisi servis = new UrunServisi(sahteRepo);
            assertThat(servis.adGetir(99)).isEqualTo("bilinmiyor");
        }
    }

    public static void main(String[] args) {
        System.out.println("================ MOCKITO (yalıtılmış test) ================");
        testCalistir(UrunServisiTest.class);
        System.out.println("""

                --- Mockito ---
                mock(...) ile sahte bağımlılık; when(...).thenReturn(...) ile davranış programlama;
                verify(...) ile etkileşim doğrulama. Böylece servisi DB/ağ olmadan, yalıtılmış test edersin.
                Spring'de @MockBean ile bir bean'i sahteyle değiştirip dilim (slice) testlerinde kullanırsın.""");
    }

    static void testCalistir(Class<?> testSinifi) {
        LauncherDiscoveryRequest req = request().selectors(selectClass(testSinifi)).build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener dinleyici = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(dinleyici);
        launcher.execute(req);
        TestExecutionSummary ozet = dinleyici.getSummary();
        System.out.printf("Çalışan: %d | Başarılı: %d | Başarısız: %d%n",
                ozet.getTestsStartedCount(), ozet.getTestsSucceededCount(), ozet.getTestsFailedCount());
        if (ozet.getTestsFailedCount() == 0) System.out.println("Tüm testler GEÇTİ ✓");
        ozet.printFailuresTo(new PrintWriter(System.out));
    }
}
