package com.egitim.portal.runner;

/** Kod çalıştırma API'sinin istek/yanıt tipleri. */
public final class RunModels {

    private RunModels() {
    }

    /** İstemciden gelen çalıştırma isteği. */
    public record RunRequest(
            String category, // "01-java"
            String slug,     // "05-coklu-is-parcacigi-ve-eszamanlilik"
            String file,     // "Ornek1.java"
            String source,   // (opsiyonel) ekranda düzenlenmiş kaynak kod; boş/null ise diskteki dosya çalışır
            String stdin     // (opsiyonel) programın standart girdisine (klavye) beslenecek metin
    ) {
    }

    /** Çalıştırma sonucu. */
    public record RunResult(
            String kind,       // "JAVA_FILE" | "SPRING_BOOT"
            String target,     // çalıştırılan dosya adı veya FQCN
            String command,    // kullanıcıya gösterilecek komut
            Integer exitCode,  // null -> süre/işaret nedeniyle durduruldu
            boolean stopped,   // timeout/işaret ile durdurulduysa true
            long durationMs,
            boolean truncated, // çıktı kırpıldıysa true
            String output,     // birleşik stdout+stderr
            String note        // açıklama (örn. neden durduruldu)
    ) {
    }
}
