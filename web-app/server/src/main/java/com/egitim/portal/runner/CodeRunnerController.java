package com.egitim.portal.runner;

import com.egitim.portal.runner.RunModels.RunRequest;
import com.egitim.portal.runner.RunModels.RunResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kod çalıştırma REST API'si — istemcideki "Çalıştır" butonu bu ucu çağırır.
 */
@RestController
@RequestMapping("/api/run")
public class CodeRunnerController {

    private final CodeRunnerService runnerService;

    public CodeRunnerController(CodeRunnerService runnerService) {
        this.runnerService = runnerService;
    }

    /** POST /api/run — body: { category, slug, file, source?, stdin? } */
    @PostMapping
    public ResponseEntity<RunResult> run(@RequestBody RunRequest request) {
        if (request == null || request.category() == null || request.slug() == null || request.file() == null) {
            return ResponseEntity.badRequest().build();
        }
        return runnerService.run(request.category(), request.slug(), request.file(),
                        request.source(), request.stdin())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
