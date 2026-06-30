package com.egitim.portal.content;

import com.egitim.portal.content.ContentModels.Category;
import com.egitim.portal.content.ContentModels.CodeFile;
import com.egitim.portal.content.ContentModels.CodeLevel;
import com.egitim.portal.content.ContentModels.TopicDetail;
import com.egitim.portal.content.ContentModels.TopicSummary;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Depodaki eğitim içeriğini (README + örnek Java dosyaları) dosya sisteminden
 * okuyup yapılandırılmış DTO'lara dönüştüren servis.
 *
 * İçerik kök dizini {@code egitim.content.root} ile verilebilir; verilmezse
 * çalışma dizininden yukarı doğru "01-java" ve "02-spring" klasörlerini içeren
 * dizin aranır (depo kökü).
 */
@Service
public class ContentService {

    private static final Logger log = LoggerFactory.getLogger(ContentService.class);

    /** Kategori sırası (numaralı kök klasörler). Sadece diskte var olanlar gösterilir. */
    private static final List<String> CATEGORY_ORDER = List.of(
            "01-java", "02-spring", "03-spring-boot", "04-spring-vs-spring-boot", "05-java-versiyon-analizi");

    /** Kategori klasörü -> görünen ad/açıklama. */
    private static final Map<String, String[]> CATEGORY_META = Map.of(
            "01-java", new String[]{"Java", "Java dili: temeller, OOP, koleksiyonlar, modern Java (lambda/stream/Optional), eşzamanlılık, JDBC ve ekosistem."},
            "02-spring", new String[]{"Spring", "Spring Framework: IoC/DI, çekirdek container, yapılandırma, Spring MVC, veri erişimi, AOP ve Spring Security."},
            "03-spring-boot", new String[]{"Spring Boot", "Spring Boot: hızlı başlangıç, otomatik yapılandırma, REST, Spring Data JPA, güvenlik, test ve üretim araçları."},
            "04-spring-vs-spring-boot", new String[]{"Spring vs Spring Boot", "Spring ile Spring Boot'un karşılaştırması: farklar, ne zaman hangisi, geçiş ve örneklerle kıyas."},
            "05-java-versiyon-analizi", new String[]{"Java Versiyon Analizi", "Java 8'den 25'e sürüm sürüm yenilikler: lambda/stream, modüller, var, record, sealed, pattern matching, virtual threads ve dahası — örneklerle."}
    );

    private static final Pattern ORNEK_PATTERN = Pattern.compile("(?i)Ornek(\\d+)");

    @Value("${egitim.content.root:}")
    private String configuredRoot;

    private Path contentRoot;

    @PostConstruct
    void init() {
        this.contentRoot = resolveContentRoot();
        log.info("İçerik kök dizini: {}", contentRoot);
    }

    private Path resolveContentRoot() {
        if (configuredRoot != null && !configuredRoot.isBlank()) {
            Path p = Paths.get(configuredRoot).toAbsolutePath().normalize();
            if (isRepoRoot(p)) {
                return p;
            }
            log.warn("egitim.content.root geçersiz ({}), otomatik arama yapılıyor.", p);
        }
        // Çalışma dizininden yukarı doğru depo kökünü ara.
        Path current = Paths.get("").toAbsolutePath().normalize();
        for (int i = 0; i < 8 && current != null; i++) {
            if (isRepoRoot(current)) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException(
                "Eğitim içeriği bulunamadı. '01-java' ve '02-spring' klasörlerini içeren depo kökünden "
                        + "çalıştırın ya da egitim.content.root özelliğini verin.");
    }

    private boolean isRepoRoot(Path p) {
        return Files.isDirectory(p.resolve("01-java")) && Files.isDirectory(p.resolve("02-spring"));
    }

    // ---------------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------------

    public List<Category> getCategories() {
        List<Category> result = new ArrayList<>();
        int order = 1;
        for (String catId : CATEGORY_ORDER) {
            if (!Files.isDirectory(contentRoot.resolve(catId))) {
                continue; // henüz oluşturulmamış kategoriyi gösterme
            }
            String[] meta = CATEGORY_META.getOrDefault(catId, new String[]{catId, ""});
            int count = listTopicDirs(catId).size();
            result.add(new Category(catId, meta[0], meta[1], order++, count));
        }
        return result;
    }

    public List<TopicSummary> getAllTopics(String categoryFilter, String query) {
        List<TopicSummary> all = new ArrayList<>();
        for (String catId : CATEGORY_ORDER) {
            if (!Files.isDirectory(contentRoot.resolve(catId))) {
                continue;
            }
            if (categoryFilter != null && !categoryFilter.isBlank() && !categoryFilter.equals(catId)) {
                continue;
            }
            for (Path dir : listTopicDirs(catId)) {
                all.add(toSummary(catId, dir));
            }
        }
        all.sort(Comparator.comparing(TopicSummary::category).thenComparingInt(TopicSummary::order));
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase(Locale.ROOT);
            all = all.stream()
                    .filter(t -> t.title().toLowerCase(Locale.ROOT).contains(q)
                            || t.slug().toLowerCase(Locale.ROOT).contains(q)
                            || (t.summary() != null && t.summary().toLowerCase(Locale.ROOT).contains(q)))
                    .toList();
        }
        return all;
    }

    public Optional<TopicDetail> getTopic(String category, String slug) {
        Path dir = contentRoot.resolve(category).resolve(slug);
        if (!isTopicDir(dir) || !category.equals(dir.getParent().getFileName().toString())) {
            return Optional.empty();
        }
        // Güvenlik: çözülen yol mutlaka içerik kökünün altında olmalı.
        if (!dir.toAbsolutePath().normalize().startsWith(contentRoot)) {
            return Optional.empty();
        }
        String readme = readFile(dir.resolve("README.md"));
        String title = extractTitle(readme, slug);
        String summary = extractSummary(readme);
        List<CodeFile> codeFiles = new ArrayList<>();
        for (Path codePath : listJavaFiles(dir)) {
            String fileName = codePath.getFileName().toString();
            String base = fileName.substring(0, fileName.length() - 5);
            String content = readFile(codePath);
            codeFiles.add(new CodeFile(base, labelFor(base), fileName, "java", countLines(content), content));
        }
        String[] meta = CATEGORY_META.getOrDefault(category, new String[]{category, ""});
        return Optional.of(new TopicDetail(
                category + "/" + slug, category, meta[0], slug, title, summary,
                orderFromSlug(slug), readme, codeFiles));
    }

    /** İçerik kök dizini (kod çalıştırıcı tarafından kullanılır). */
    public Path getContentRoot() {
        return contentRoot;
    }

    /**
     * Bir konudaki örnek Java dosyasının yolunu dosya adıyla döndürür.
     * fileName güvenlik için: ".java" ile bitmeli, yol ayracı içermemeli ve
     * çözülen yol mutlaka içerik kökü altındaki konu klasöründe olmalıdır.
     */
    public Optional<Path> resolveExampleFile(String category, String slug, String fileName) {
        if (fileName == null || !fileName.endsWith(".java")
                || fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            return Optional.empty();
        }
        Path file = contentRoot.resolve(category).resolve(slug).resolve(fileName).toAbsolutePath().normalize();
        if (!file.startsWith(contentRoot) || !Files.isRegularFile(file)) {
            return Optional.empty();
        }
        return Optional.of(file);
    }

    /** Konu klasöründeki .java dosyalarını sıralı döndürür (Ornek1, Ornek2, ... önce). */
    private List<Path> listJavaFiles(Path dir) {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".java"))
                    .sorted(Comparator.comparingInt(this::ornekNo)
                            .thenComparing(p -> p.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** "Ornek2.java" -> 2; eşleşmezse büyük bir sayı (sona atılır). */
    private int ornekNo(Path p) {
        Matcher m = ORNEK_PATTERN.matcher(p.getFileName().toString());
        return m.find() ? Integer.parseInt(m.group(1)) : 1000;
    }

    /** Dosya adından görünen etiket üretir: "Ornek1" -> "Örnek 1"; eski adlar desteklenir. */
    private String labelFor(String base) {
        Matcher m = ORNEK_PATTERN.matcher(base);
        if (m.matches()) {
            return "Örnek " + m.group(1);
        }
        return switch (base) {
            case "TemelOrnek" -> "Temel";
            case "OrtaOrnek" -> "Orta";
            case "IleriOrnek" -> "İleri";
            default -> base;
        };
    }

    // ---------------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------------

    private TopicSummary toSummary(String category, Path dir) {
        String slug = dir.getFileName().toString();
        String readme = readFile(dir.resolve("README.md"));
        String title = extractTitle(readme, slug);
        String summary = extractSummary(readme);
        List<CodeLevel> levels = new ArrayList<>();
        for (Path codePath : listJavaFiles(dir)) {
            String fileName = codePath.getFileName().toString();
            String base = fileName.substring(0, fileName.length() - 5);
            levels.add(new CodeLevel(base, labelFor(base), fileName));
        }
        String[] meta = CATEGORY_META.getOrDefault(category, new String[]{category, ""});
        return new TopicSummary(category + "/" + slug, category, meta[0], slug, title, summary,
                orderFromSlug(slug), levels);
    }

    private List<Path> listTopicDirs(String category) {
        Path catDir = contentRoot.resolve(category);
        if (!Files.isDirectory(catDir)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(catDir)) {
            return stream
                    .filter(this::isTopicDir)
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Konu klasörü = README.md içeren ve "ornek" ile başlamayan dizin. */
    private boolean isTopicDir(Path p) {
        if (!Files.isDirectory(p)) {
            return false;
        }
        String name = p.getFileName().toString();
        if (name.startsWith("ornek")) {
            return false;
        }
        return Files.isRegularFile(p.resolve("README.md"));
    }

    private String readFile(Path path) {
        try {
            return Files.isRegularFile(path) ? Files.readString(path) : "";
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int countLines(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        return (int) content.lines().count();
    }

    /** README'deki ilk Markdown "# Başlık" satırını alır; yoksa slug'ı okunabilir hale getirir. */
    private String extractTitle(String readme, String slug) {
        if (readme != null) {
            for (String line : readme.split("\n", 60)) {
                String t = line.strip();
                if (t.startsWith("# ")) {
                    return t.substring(2).strip();
                }
            }
        }
        return humanizeSlug(slug);
    }

    /** README'nin ilk dolu (başlık olmayan) paragrafını özet olarak alır. */
    private String extractSummary(String readme) {
        if (readme == null || readme.isBlank()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean started = false;
        for (String raw : readme.split("\n")) {
            String line = raw.strip();
            if (line.isEmpty()) {
                if (started) {
                    break;
                }
                continue;
            }
            if (line.startsWith("#") || line.startsWith(">") || line.startsWith("|") || line.startsWith("```")) {
                if (started) {
                    break;
                }
                continue;
            }
            started = true;
            sb.append(line).append(' ');
        }
        String summary = sb.toString().strip();
        return summary.length() > 280 ? summary.substring(0, 277) + "..." : summary;
    }

    private String humanizeSlug(String slug) {
        String s = slug.replaceFirst("^\\d+-", "").replace('-', ' ');
        return s.isEmpty() ? slug : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private int orderOf(String categoryId) {
        return "01-java".equals(categoryId) ? 1 : 2;
    }

    /** "05-concurrency-temelleri" -> 5 (sıralama için sayısal önek). */
    private int orderFromSlug(String slug) {
        StringBuilder digits = new StringBuilder();
        for (char c : slug.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            } else {
                break;
            }
        }
        return digits.isEmpty() ? 999 : Integer.parseInt(digits.toString());
    }
}
