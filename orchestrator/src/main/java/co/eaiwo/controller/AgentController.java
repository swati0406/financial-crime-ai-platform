package co.eaiwo.controller;

import co.eaiwo.agent.OrchestratorService;
import co.eaiwo.service.AgentService;
import co.eaiwo.service.IngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AgentService service;
    private final IngestionService ingestionService;
    private final OrchestratorService orchestratorService;

    public AgentController(AgentService service,
                           IngestionService ingestionService,
                           OrchestratorService orchestratorService) {
        this.service = service;
        this.ingestionService = ingestionService;
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/ask")
    public Mono<ResponseEntity<Map<String, String>>> ask(
            @RequestBody Map<String, String> body) {
        String query     = body.getOrDefault("query", "");
        String sessionId = body.getOrDefault("sessionId", "default");
        return Mono.fromCallable(() -> service.handle(sessionId, query))
                .subscribeOn(Schedulers.boundedElastic())
                .map(answer -> ResponseEntity.ok(Map.of("answer", answer)));
    }

    @PostMapping("/orchestrator/ask")
    public Mono<ResponseEntity<Map<String, String>>> orchestratorAsk(
            @RequestBody Map<String, String> body) {
        String query = body.getOrDefault("query", "");
        return Mono.fromCallable(() -> orchestratorService.handle(query))
                .subscribeOn(Schedulers.boundedElastic())
                .map(answer -> ResponseEntity.ok(Map.of("answer", answer)));
    }

    @PostMapping("/ingest")
    public Mono<ResponseEntity<String>> ingest(
            @RequestBody Map<String, String> body) {
        String filePath = body.get("filePath");
        String docName  = body.getOrDefault("docName", "document");
        return Mono.fromCallable(() -> {
            ingestionService.ingestPdf(filePath, docName);
            return ResponseEntity.ok("Ingestion complete: " + docName);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}