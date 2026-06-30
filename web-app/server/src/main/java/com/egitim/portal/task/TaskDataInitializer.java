package com.egitim.portal.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Uygulama açılışında H2'ye örnek görevler yükler (canlı demo başlangıç verisi). */
@Configuration
public class TaskDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(TaskDataInitializer.class);

    @Bean
    public CommandLineRunner seedTasks(TaskRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    new Task("Spring eğitimini hazırla", "Örnek uygulamayı tamamla", TaskStatus.IN_PROGRESS, 5),
                    new Task("README yaz", "Mimari ve curl örnekleri", TaskStatus.TODO, 3),
                    new Task("Ortamı kur", "JDK 21 + Gradle 8.5", TaskStatus.DONE, 2),
                    new Task("React istemcisini bağla", "RTK Query + MUI ile UI", TaskStatus.IN_PROGRESS, 4)
            ));
            log.info("Örnek görevler yüklendi: {} görev", repository.count());
        };
    }
}
