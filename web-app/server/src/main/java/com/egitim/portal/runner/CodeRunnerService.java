package com.egitim.portal.runner;

import com.egitim.portal.content.ContentService;
import com.egitim.portal.runner.RunModels.RunResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Depodaki örnek Java dosyalarını backend'de çalıştırıp çıktısını döndürür.
 *
 * İki tür örnek desteklenir:
 *  - Tek dosya saf Java (package'sız): {@code java Dosya.java} ile çalışır, çıktı basıp biter.
 *  - Spring Boot örneği (package'lı): {@code ornekler} Gradle modülünde {@code bootRun -Papp=<fqcn>}
 *    ile çalışır; "Started ..." görülünce kısa bir bekleme sonrası durdurulur (sunucu açık kalır).
 *
 * İSTEĞE BAĞLI DÜZENLEME: İstemci, bir örneğin ekranda DÜZENLENMİŞ kaynağını ({@code source}) ve
 * programa beslenecek girdiyi ({@code stdin}) gönderebilir. Düzenlenmiş kaynak geçici bir dosyaya
 * yazılıp onun yerine çalıştırılır; stdin sürecin standart girişine beslenir (Scanner çalışır).
 *
 * GÜVENLİK NOTU: Bu YEREL bir eğitim/geliştirme aracıdır (localhost). Düzenlenmiş kod kabul
 * edildiğinden, çalıştırma ortamı güvenilen geliştiricinin makinesidir; herkese açık bir sunucuda
 * çalıştırılmamalıdır. Çalıştırma yine de bilinen bir (category, slug, file) örneği bağlamında olur.
 */
@Service
public class CodeRunnerService {

    private static final Logger log = LoggerFactory.getLogger(CodeRunnerService.class);

    private static final Pattern PACKAGE_PATTERN =
            Pattern.compile("(?m)^\\s*package\\s+([\\w.]+)\\s*;");
    private static final Pattern STARTED_PATTERN =
            Pattern.compile("Started \\w+ in |JVM running for |Tomcat started on port");
    /** Preview özelliği/AP'si nedeniyle derlemenin başarısız olduğunu gösteren javac mesajları. */
    private static final Pattern PREVIEW_HINT =
            Pattern.compile("(?i)preview (feature|API)|--enable-preview");

    /** Portalın JDK'sının dil seviyesi; preview tekrar denemesinde {@code --source} için kullanılır. */
    private static final String SOURCE_RELEASE = Integer.toString(Runtime.version().feature());

    private static final int MAX_OUTPUT_CHARS = 200_000;

    /** Spring Boot örnekleri 8080'i kullandığından aynı anda yalnızca biri çalışabilir. */
    private final ReentrantLock springLock = new ReentrantLock();

    private final ContentService contentService;

    @Value("${egitim.runner.java-timeout-seconds:30}")
    private long javaTimeoutSeconds;

    @Value("${egitim.runner.spring-timeout-seconds:180}")
    private long springTimeoutSeconds;

    @Value("${egitim.runner.spring-grace-seconds:7}")
    private long springGraceSeconds;

    public CodeRunnerService(ContentService contentService) {
        this.contentService = contentService;
    }

    /** Geriye dönük uyumluluk: diskteki örneği girdisiz çalıştırır. */
    public Optional<RunResult> run(String category, String slug, String fileName) {
        return run(category, slug, fileName, null, null);
    }

    /**
     * Verilen örneği çalıştırır. Dosya bulunamazsa {@link Optional#empty()} döner.
     *
     * @param editedSource (opsiyonel) ekranda düzenlenmiş kaynak. Boş/null ise diskteki dosya çalışır;
     *                     doluysa geçici bir dosyaya yazılıp ONUN yerine çalıştırılır.
     * @param stdin        (opsiyonel) programın standart girdisine beslenecek metin (Scanner için).
     */
    public Optional<RunResult> run(String category, String slug, String fileName,
                                   String editedSource, String stdin) {
        Optional<Path> fileOpt = contentService.resolveExampleFile(category, slug, fileName);
        if (fileOpt.isEmpty()) {
            return Optional.empty();
        }
        Path diskFile = fileOpt.get();

        boolean edited = editedSource != null && !editedSource.isBlank();
        String source;
        Path runFile;
        Path editTempDir = null;
        try {
            if (edited) {
                // Düzenlenmiş kaynağı, ORİJİNAL dosya adıyla geçici bir dizine yaz ve onu çalıştır.
                // (Aynı ad korunduğu için package/Boot derlemesinde public-sınıf adı eşleşmesi bozulmaz.)
                editTempDir = Files.createTempDirectory("egitim-edit-");
                runFile = editTempDir.resolve(fileName);
                Files.writeString(runFile, editedSource);
                source = editedSource;
            } else {
                runFile = diskFile;
                source = Files.readString(diskFile);
            }
        } catch (IOException e) {
            if (editTempDir != null) sil(editTempDir);
            return Optional.of(errorResult("JAVA_FILE", fileName, "Kaynak hazırlanamadı: " + e.getMessage()));
        }

        try {
            Matcher pkg = PACKAGE_PATTERN.matcher(source);
            if (pkg.find()) {
                String fqcn = pkg.group(1) + "." + classNameOf(runFile);
                // Gerçek bir Spring Boot uygulaması mı (sunucu), yoksa package'lı sıradan bir
                // Spring örneği mi (main + ApplicationContext)? Boot ise gömülü Tomcat ile çalışır;
                // değilse javac ile derleyip portal classpath'iyle çalıştırırız (proxy/AOP için şart).
                boolean bootApp = source.contains("@SpringBootApplication") || source.contains("SpringApplication.run");
                // Güvenlik örneği kendi SecurityFilterChain'ini tanımlar; diğer örneklerde portalın
                // classpath'indeki spring-security'nin varsayılan kilidini devre dışı bırakırız.
                boolean guvenlikOrnegi = source.contains("SecurityFilterChain")
                        || source.contains("EnableWebSecurity") || source.contains("EnableMethodSecurity");
                return Optional.of(bootApp
                        ? runCompiledBoot(runFile, fqcn, guvenlikOrnegi, stdin)
                        : runCompiledJava(runFile, fqcn, stdin));
            }
            return Optional.of(runJavaFile(runFile, source, stdin));
        } finally {
            if (editTempDir != null) sil(editTempDir);
        }
    }

    // ---------------------------------------------------------------------
    // Package'lı Spring örneği: javac ile derle, portal classpath'iyle çalıştır.
    // (Tek-dosya kaynak başlatıcının aksine gerçek .class üretildiği için CGLIB/AOP çalışır.)
    // ---------------------------------------------------------------------

    private RunResult runCompiledJava(Path file, String fqcn, String stdin) {
        String cp = System.getProperty("java.class.path"); // Spring + AOP + JDBC + H2 + ...
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory("egitim-run-");
        } catch (IOException e) {
            return errorResult("SPRING", fqcn, "Geçici dizin oluşturulamadı: " + e.getMessage());
        }
        long start = System.currentTimeMillis();
        try {
            // 1) Derle
            ProcessBuilder javac = new ProcessBuilder("javac", "-encoding", "UTF-8", "-parameters",
                    "-cp", cp, "-d", tempDir.toString(), file.toString());
            javac.redirectErrorStream(true);
            Process jp;
            try {
                jp = javac.start();
            } catch (IOException e) {
                return errorResult("SPRING", fqcn, "'javac' başlatılamadı: " + e.getMessage());
            }
            OutputCollector jc = new OutputCollector(jp);
            jc.start();
            boolean compiled;
            try {
                compiled = jp.waitFor(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                compiled = false;
            }
            if (!compiled) {
                jp.destroyForcibly();
                return errorResult("SPRING", fqcn, "Derleme zaman aşımına uğradı.");
            }
            if (jp.exitValue() != 0) {
                return build("SPRING", fqcn, "javac " + file.getFileName(), jp.exitValue(), false,
                        System.currentTimeMillis() - start, jc.finish(), "Derleme hatası");
            }

            // 2) Çalıştır (portal classpath + derlenen sınıflar)
            String runCp = tempDir + java.io.File.pathSeparator + cp;
            ProcessBuilder java = new ProcessBuilder("java", "-cp", runCp, fqcn);
            java.directory(file.getParent().toFile());
            java.redirectErrorStream(true);
            Process rp = java.start();
            feedStdin(rp, stdin);
            OutputCollector rc = new OutputCollector(rp);
            rc.start();
            boolean exited;
            try {
                exited = rp.waitFor(javaTimeoutSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                exited = false;
            }
            boolean stopped = !exited;
            if (!exited) {
                rp.destroyForcibly();
            }
            Integer exitCode = exited ? rp.exitValue() : null;
            long duration = System.currentTimeMillis() - start;
            String note = stopped ? "Süre aşımı (" + javaTimeoutSeconds + " sn) — durduruldu." : null;
            return build("SPRING", fqcn, "java " + fqcn, exitCode, stopped, duration, rc.finish(), note);
        } catch (IOException e) {
            return errorResult("SPRING", fqcn, "Çalıştırma hatası: " + e.getMessage());
        } finally {
            sil(tempDir);
        }
    }

    /** Geçici dizini özyinelemeli siler. */
    private void sil(Path dir) {
        try (Stream<Path> s = Files.walk(dir)) {
            s.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) {}
            });
        } catch (IOException ignored) {
        }
    }

    // ---------------------------------------------------------------------
    // Spring Boot uygulaması: javac ile derle, portal classpath'iyle çalıştır,
    // "Started ..." görülünce kısa bir bekleme sonrası durdur (sunucu açık kalır).
    // ---------------------------------------------------------------------

    private RunResult runCompiledBoot(Path file, String fqcn, boolean guvenlikOrnegi, String stdin) {
        String cp = System.getProperty("java.class.path"); // spring-boot, tomcat, jpa, H2 ...
        // Boot örnekleri 8080'i kullanabilir; aynı anda yalnızca biri çalışsın.
        boolean locked;
        try {
            locked = springLock.tryLock(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            locked = false;
        }
        if (!locked) {
            return errorResult("SPRING_BOOT", fqcn,
                    "Şu anda başka bir Spring Boot örneği çalışıyor (8080 meşgul). Birkaç saniye sonra tekrar deneyin.");
        }

        Path tempDir = null;
        Process process = null;
        long start = System.currentTimeMillis();
        try {
            tempDir = Files.createTempDirectory("egitim-boot-");

            // 1) Derle
            ProcessBuilder javac = new ProcessBuilder("javac", "-encoding", "UTF-8", "-parameters",
                    "-cp", cp, "-d", tempDir.toString(), file.toString());
            javac.redirectErrorStream(true);
            Process jp = javac.start();
            OutputCollector jc = new OutputCollector(jp);
            jc.start();
            boolean compiled = jp.waitFor(60, TimeUnit.SECONDS);
            if (!compiled) {
                jp.destroyForcibly();
                return errorResult("SPRING_BOOT", fqcn, "Derleme zaman aşımına uğradı.");
            }
            if (jp.exitValue() != 0) {
                return build("SPRING_BOOT", fqcn, "javac " + file.getFileName(), jp.exitValue(), false,
                        System.currentTimeMillis() - start, jc.finish(), "Derleme hatası");
            }

            // 2) Çalıştır (gömülü Tomcat başlar)
            // ÖNEMLİ: portalın classpath'i kendi application.yml'ini de içerir; örneğin onu
            // OKUMAMASI için spring.config.name'i var olmayan bir ada çekiyoruz ve portu 8080'e
            // sabitliyoruz (örnekler kendilerini http://localhost:8080 üzerinden çağırır).
            String runCp = tempDir + java.io.File.pathSeparator + cp;
            List<String> cmd = new ArrayList<>(List.of("java", "-cp", runCp, fqcn,
                    "--spring.config.name=egitim-ornek-bos",
                    "--server.port=8080",
                    "--spring.main.banner-mode=off"));
            // Güvenlik örneği değilse, classpath'teki spring-security'nin varsayılan kilidini kapat.
            if (!guvenlikOrnegi) {
                cmd.add("--spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,"
                        + "org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration");
            }
            ProcessBuilder java = new ProcessBuilder(cmd);
            java.directory(file.getParent().toFile());
            java.redirectErrorStream(true);
            process = java.start();
            feedStdin(process, stdin);
            OutputCollector rc = new OutputCollector(process);
            rc.start();

            long hardDeadline = start + springTimeoutSeconds * 1000;
            long graceDeadline = -1;
            boolean stopped = false;
            while (process.isAlive()) {
                long now = System.currentTimeMillis();
                if (graceDeadline < 0 && STARTED_PATTERN.matcher(rc.snapshot()).find()) {
                    graceDeadline = now + springGraceSeconds * 1000; // self-test çıktısı için bekle
                }
                if ((graceDeadline > 0 && now >= graceDeadline) || now >= hardDeadline) {
                    stopped = true;
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    stopped = true;
                    break;
                }
            }
            boolean alive = process.isAlive();
            Integer exitCode = alive ? null : process.exitValue();
            if (alive) {
                destroyTree(process);
            }
            String output = rc.finish();
            long duration = System.currentTimeMillis() - start;
            String note;
            if (graceDeadline > 0) {
                note = "Spring Boot uygulaması başlatıldı ve self-test çıktısı alındı; sunucu açık kaldığı "
                        + "için otomatik durduruldu. (Yerelde sürekli çalıştırmak için bu sınıfı main olarak başlatın.)";
            } else if (stopped) {
                note = "Süre aşımı (" + springTimeoutSeconds + " sn). İlk çalıştırma yavaş olabilir; tekrar deneyin.";
            } else {
                note = "Uygulama kendiliğinden sonlandı.";
            }
            return build("SPRING_BOOT", fqcn, "java " + fqcn, exitCode, stopped, duration, output, note);
        } catch (IOException e) {
            return errorResult("SPRING_BOOT", fqcn, "Çalıştırma hatası: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return errorResult("SPRING_BOOT", fqcn, "Kesintiye uğradı.");
        } finally {
            if (process != null && process.isAlive()) {
                destroyTree(process);
            }
            springLock.unlock();
            if (tempDir != null) {
                sil(tempDir);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Tek dosya saf Java
    // ---------------------------------------------------------------------

    private RunResult runJavaFile(Path file, String source, String stdin) {
        // Classpath seçimi:
        //  - Spring kullanan örnekler (import org.springframework...) -> portalın TAM classpath'i
        //    (Spring Context/AOP/JDBC, hibernate-validator, H2 vb. hepsi hazır gelir).
        //  - Diğer (saf Java/JDBC) örnekler -> sadece H2 sürücüsü (hızlı, sade).
        boolean springOrnegi = source.contains("org.springframework") || source.contains("jakarta.");
        String cp = springOrnegi ? System.getProperty("java.class.path") : h2JarPathOrNull();

        // 1) Düz çalıştırma (varsayılan).
        RunResult result = execJavaSource(file, cp, stdin, false);
        // 2) Bu JDK'nın preview özelliklerini (örn. Java 21 string templates / structured concurrency /
        //    scoped values) kullanan örnekler varsayılan olarak derlenmez. Preview hatası alırsak
        //    bir kez de --enable-preview --source <jdk> ile dene. Daha yeni bir JDK gerektiren
        //    örnekler (sembol bulunamadı vb.) yine başarısız olur ve ilk hata gösterilir.
        if (result.exitCode() != null && result.exitCode() != 0
                && PREVIEW_HINT.matcher(result.output()).find()) {
            RunResult preview = execJavaSource(file, cp, stdin, true);
            if (preview.exitCode() != null && preview.exitCode() == 0) {
                return preview;
            }
        }
        return result;
    }

    /** Tek dosya saf Java kaynağını (isteğe bağlı preview ile) çalıştırır. */
    private RunResult execJavaSource(Path file, String cp, String stdin, boolean enablePreview) {
        boolean springOrnegi = cp != null && cp.equals(System.getProperty("java.class.path"));

        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        if (enablePreview) {
            cmd.add("--enable-preview");
            cmd.add("--source");
            cmd.add(SOURCE_RELEASE);
        }
        if (cp != null && !cp.isBlank()) {
            cmd.add("-cp");
            cmd.add(cp);
        }
        cmd.add(file.toString());
        String previewFlag = enablePreview ? "--enable-preview --source " + SOURCE_RELEASE + " " : "";
        String command = springOrnegi
                ? "java " + previewFlag + "-cp <spring-classpath> " + file.getFileName()
                : (cp != null ? "java " + previewFlag + "-cp <h2.jar> " + file.getFileName()
                              : "java " + previewFlag + file.getFileName());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(file.getParent().toFile());
        pb.redirectErrorStream(true);

        long start = System.currentTimeMillis();
        Process process;
        try {
            process = pb.start();
        } catch (IOException e) {
            return errorResult("JAVA_FILE", file.getFileName().toString(),
                    "'java' başlatılamadı (JDK 21 PATH'te mi?): " + e.getMessage());
        }

        feedStdin(process, stdin);
        OutputCollector collector = new OutputCollector(process);
        collector.start();

        boolean exited;
        try {
            exited = process.waitFor(javaTimeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exited = false;
        }
        boolean stopped = !exited;
        if (!exited) {
            process.destroyForcibly();
        }
        Integer exitCode = exited ? process.exitValue() : null;
        String output = collector.finish();
        long duration = System.currentTimeMillis() - start;

        String note = stopped
                ? "Süre aşımı (" + javaTimeoutSeconds + " sn) — program durduruldu."
                : null;
        return build("JAVA_FILE", file.getFileName().toString(), command, exitCode, stopped, duration, output, note);
    }

    // ---------------------------------------------------------------------
    // Spring Boot örneği (ornekler modülü)
    // ---------------------------------------------------------------------

    private RunResult runSpringBoot(String fqcn) {
        String command = "./gradlew bootRun -Papp=" + fqcn;
        Path moduleDir = contentService.getContentRoot().resolve("02-spring").resolve("ornekler");
        if (!Files.isDirectory(moduleDir)) {
            return errorResult("SPRING_BOOT", fqcn, "ornekler Gradle modülü bulunamadı: " + moduleDir);
        }

        boolean locked;
        try {
            locked = springLock.tryLock(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            locked = false;
        }
        if (!locked) {
            return errorResult("SPRING_BOOT", fqcn,
                    "Şu anda başka bir Spring Boot örneği çalışıyor (8080 meşgul). Birkaç saniye sonra tekrar deneyin.");
        }

        long start = System.currentTimeMillis();
        Process process = null;
        try {
            String gradlew = isWindows() ? "gradlew.bat" : "./gradlew";
            ProcessBuilder pb = new ProcessBuilder(
                    gradlew, "--console=plain", "-q", "bootRun", "-Papp=" + fqcn);
            pb.directory(moduleDir.toFile());
            pb.redirectErrorStream(true);
            try {
                process = pb.start();
            } catch (IOException e) {
                return errorResult("SPRING_BOOT", fqcn, "Gradle başlatılamadı: " + e.getMessage());
            }

            OutputCollector collector = new OutputCollector(process);
            collector.start();

            // "Started ..." görülene kadar bekle; sonra grace süresi kadar daha
            // (CommandLineRunner self-test çıktısı için) bekleyip durdur. Aksi halde hard timeout.
            long hardDeadline = start + springTimeoutSeconds * 1000;
            long graceDeadline = -1;
            boolean stopped = false;
            while (process.isAlive()) {
                long now = System.currentTimeMillis();
                if (graceDeadline < 0 && STARTED_PATTERN.matcher(collector.snapshot()).find()) {
                    graceDeadline = now + springGraceSeconds * 1000;
                }
                if (graceDeadline > 0 && now >= graceDeadline) {
                    stopped = true;
                    break;
                }
                if (now >= hardDeadline) {
                    stopped = true;
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    stopped = true;
                    break;
                }
            }

            boolean alive = process.isAlive();
            Integer exitCode = alive ? null : process.exitValue();
            if (alive) {
                destroyTree(process);
            }
            String output = collector.finish();
            long duration = System.currentTimeMillis() - start;

            String note;
            if (graceDeadline > 0) {
                note = "Spring Boot uygulaması başlatıldı ve self-test çıktısı alındı; sunucu açık kaldığı için "
                        + "otomatik durduruldu. (Yerelde sürekli çalıştırmak için: " + command + ")";
            } else if (stopped) {
                note = "Süre aşımı (" + springTimeoutSeconds + " sn). İlk çalıştırmada Gradle bağımlılıkları "
                        + "indiriliyor olabilir; tekrar deneyin.";
            } else {
                note = "Uygulama kendiliğinden sonlandı.";
            }
            return build("SPRING_BOOT", fqcn, command, exitCode, stopped, duration, output, note);
        } finally {
            if (process != null && process.isAlive()) {
                destroyTree(process);
            }
            springLock.unlock();
        }
    }

    // ---------------------------------------------------------------------
    // Yardımcılar
    // ---------------------------------------------------------------------

    private String classNameOf(Path file) {
        String name = file.getFileName().toString();
        return name.endsWith(".java") ? name.substring(0, name.length() - 5) : name;
    }

    /**
     * İstemciden gelen girdiyi (stdin) sürecin standart girişine yazar ve akışı KAPATIR (EOF).
     * Böylece Scanner gibi okuyucular girdiyi alır; kapatma EOF verir (sonsuz beklemeyi önler).
     * Ayrı bir daemon thread'de yapılır (büyük girdide bloklamamak için).
     */
    private void feedStdin(Process process, String stdin) {
        Thread t = new Thread(() -> {
            try (var os = process.getOutputStream()) {
                if (stdin != null && !stdin.isEmpty()) {
                    os.write(stdin.getBytes(StandardCharsets.UTF_8));
                    if (!stdin.endsWith("\n")) {
                        os.write('\n'); // son satır da okunabilsin
                    }
                    os.flush();
                }
            } catch (IOException ignored) {
                // süreç girdiyi okumadan kapanmış olabilir
            }
        }, "run-stdin");
        t.setDaemon(true);
        t.start();
    }

    private void destroyTree(Process process) {
        process.descendants().forEach(ProcessHandle::destroy);
        process.destroy();
        try {
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.descendants().forEach(ProcessHandle::destroyForcibly);
                process.destroyForcibly();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    /** Portalın classpath'indeki H2 jar'ının yolunu bulur (JDBC örnekleri için). */
    private String h2JarPathOrNull() {
        try {
            Class<?> driver = Class.forName("org.h2.Driver");
            java.net.URL loc = driver.getProtectionDomain().getCodeSource().getLocation();
            java.io.File jar = new java.io.File(loc.toURI());
            return jar.isFile() ? jar.getPath() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private RunResult errorResult(String kind, String target, String message) {
        log.warn("Çalıştırma hatası [{}] {}: {}", kind, target, message);
        return build(kind, target, "", -1, false, 0, message, "Hata");
    }

    private RunResult build(String kind, String target, String command, Integer exitCode,
                            boolean stopped, long duration, String output, String note) {
        boolean truncated = output.length() > MAX_OUTPUT_CHARS;
        String trimmed = truncated ? output.substring(0, MAX_OUTPUT_CHARS) + "\n... [çıktı kırpıldı]" : output;
        return new RunResult(kind, target, command, exitCode, stopped, duration, truncated, trimmed, note);
    }

    /** Process çıktısını ayrı bir thread'de toplar (bloklamayı önler). */
    private static final class OutputCollector {
        private final Process process;
        private final StringBuilder sb = new StringBuilder();
        private final Thread thread;

        OutputCollector(Process process) {
            this.process = process;
            this.thread = new Thread(this::read, "run-output");
            this.thread.setDaemon(true);
        }

        void start() {
            thread.start();
        }

        private void read() {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    synchronized (sb) {
                        sb.append(line).append('\n');
                    }
                }
            } catch (IOException ignored) {
                // process kapatıldığında stream kapanır
            }
        }

        String snapshot() {
            synchronized (sb) {
                return sb.toString();
            }
        }

        String finish() {
            try {
                thread.join(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return snapshot();
        }
    }
}
