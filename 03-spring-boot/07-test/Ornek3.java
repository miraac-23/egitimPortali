// Ornek3: MockMvc ile web katmanı testi — controller'ı sunucu başlatmadan test etmek.
// Çalıştırma: portal derleyip çalıştırır (JUnit Platform Launcher ile).
package com.egitim.springboot.test;

import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;

import static org.hamcrest.Matchers.containsString;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Ornek3 {

    // Test edilecek controller.
    @RestController
    static class UrunController {
        @GetMapping("/urun/{id}")
        String urun(@PathVariable int id) {
            return id == 1 ? "Klavye" : "urun yok";
        }
    }

    static class UrunControllerTest {
        // standaloneSetup: tam sunucu/context başlatmadan, sadece controller'ı MockMvc'ye bağlar.
        private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new UrunController()).build();

        @Test
        void mevcutUrun() throws Exception {
            // HTTP isteğini taklit et ve yanıtı doğrula (gerçek ağ/port yok).
            mvc.perform(get("/urun/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Klavye")));
        }

        @Test
        void olmayanUrun() throws Exception {
            mvc.perform(get("/urun/99"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("yok")));
        }
    }

    public static void main(String[] args) {
        System.out.println("================ MOCKMVC (web katmanı testi) ================");
        testCalistir(UrunControllerTest.class);
        System.out.println("""

                --- MockMvc ---
                Controller'ı GERÇEK sunucu başlatmadan, HTTP isteklerini taklit ederek test eder.
                perform(get(...)).andExpect(status().isOk()) ... ile yanıtı doğrularsın. Hızlıdır.
                Spring'de @WebMvcTest ile yalnızca web katmanını yükleyen dilim testleri yazılır.""");
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
