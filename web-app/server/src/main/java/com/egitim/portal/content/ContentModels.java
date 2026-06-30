package com.egitim.portal.content;

import java.util.List;

/**
 * İçerik API'sinin döndürdüğü veri tipleri (DTO record'ları).
 */
public final class ContentModels {

    private ContentModels() {
    }

    /** Üst kategori: "01-java" veya "02-spring". */
    public record Category(
            String id,        // klasör adı: "01-java"
            String title,     // görünen ad: "Java"
            String description,
            int order,        // sıralama anahtarı
            int topicCount
    ) {
    }

    /** Bir konunun özeti (liste/sidebar için). */
    public record TopicSummary(
            String id,            // "01-java/05-concurrency-temelleri"
            String category,      // "01-java"
            String categoryTitle, // "Java"
            String slug,          // "05-concurrency-temelleri"
            String title,         // README'deki ilk # başlık
            String summary,       // README'nin ilk paragrafı
            int order,
            List<CodeLevel> levels
    ) {
    }

    /** Konunun tüm detayı: README + kod dosyaları. */
    public record TopicDetail(
            String id,
            String category,
            String categoryTitle,
            String slug,
            String title,
            String summary,
            int order,
            String readme,            // ham markdown
            List<CodeFile> codeFiles
    ) {
    }

    /** Kod örneği meta bilgisi (sekme başlığı için). */
    public record CodeLevel(
            String key,       // dosya adı (uzantısız): "Ornek1"
            String label,     // görünen ad: "Örnek 1"
            String fileName   // "Ornek1.java"
    ) {
    }

    /** Tek bir kod dosyası (içerik dahil). */
    public record CodeFile(
            String key,
            String label,
            String fileName,
            String language,  // "java"
            int lineCount,
            String content
    ) {
    }
}
