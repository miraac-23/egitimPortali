package com.egitim.portal.content;

import com.egitim.portal.content.ContentModels.Category;
import com.egitim.portal.content.ContentModels.TopicDetail;
import com.egitim.portal.content.ContentModels.TopicSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Eğitim içeriği REST API'si — React istemcisi bu endpoint'leri RTK Query ile çağırır.
 */
@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /** GET /api/content/categories — üst kategoriler (Java, Spring) + konu sayıları. */
    @GetMapping("/categories")
    public List<Category> categories() {
        return contentService.getCategories();
    }

    /**
     * GET /api/content/topics
     * GET /api/content/topics?category=01-java
     * GET /api/content/topics?q=concurrency
     */
    @GetMapping("/topics")
    public List<TopicSummary> topics(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q) {
        return contentService.getAllTopics(category, q);
    }

    /** GET /api/content/topics/{category}/{slug} — README + kod dosyaları. */
    @GetMapping("/topics/{category}/{slug}")
    public ResponseEntity<TopicDetail> topic(
            @PathVariable String category,
            @PathVariable String slug) {
        return contentService.getTopic(category, slug)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
