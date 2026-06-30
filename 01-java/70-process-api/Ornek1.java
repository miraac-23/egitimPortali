// Ornek1: ProcessHandle (Java 9) — çalışan süreçler hakkında bilgi (yeni süreç başlatmadan).
// Çalıştırma: java Ornek1.java
public class Ornek1 {

    public static void main(String[] args) {
        // Şu anki JVM sürecinin tanıtıcısı:
        ProcessHandle benim = ProcessHandle.current();
        System.out.println("Bu sürecin PID'i: " + benim.pid());

        // Süreç bilgileri (info): komut, başlangıç zamanı, kullanıcı... (platforma göre dolu/boş olabilir)
        ProcessHandle.Info bilgi = benim.info();
        System.out.println("Komut    : " + bilgi.command().orElse("(bilinmiyor)"));
        System.out.println("Başlangıç: " + bilgi.startInstant().map(Object::toString).orElse("(bilinmiyor)"));
        System.out.println("Kullanıcı: " + bilgi.user().orElse("(bilinmiyor)"));

        // Ebeveyn süreç (varsa):
        benim.parent().ifPresentOrElse(
                p -> System.out.println("Ebeveyn PID: " + p.pid()),
                () -> System.out.println("Ebeveyn süreç bilgisi yok"));

        // Süreç canlı mı?
        System.out.println("Süreç canlı mı? " + benim.isAlive());

        System.out.println("""

                --- ProcessHandle (Java 9) ---
                Çalışan SÜREÇLER hakkında bilgi verir (yeni süreç BAŞLATMADAN):
                  pid(), info() (komut/başlangıç/kullanıcı), parent(), children(), isAlive(), destroy().
                ProcessHandle.current(): bu JVM süreci. ProcessHandle.allProcesses(): sistemdeki süreçler.
                İzleme, yönetim ve süreç ağaçlarını incelemek için kullanılır (bilgiler platforma göre değişir).""");
    }
}
