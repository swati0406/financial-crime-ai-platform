package co.eaiwo.service;

import co.eaiwo.model.LlmDtos.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class LlmService {

    private final WebClient client;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();

    public LlmService(WebClient openRouterClient,
                      @Value("${openrouter.model}") String model) {
        this.client = openRouterClient;
        this.model = model;
    }

    @CircuitBreaker(name = "llm")
    @RateLimiter(name = "llm")
    @Retry(name = "llm")
    public ChatCompletionResponse callWithTools(List<ChatMessage> messages,
                                                List<ToolDef> tools) {
        ChatCompletionRequest req = new ChatCompletionRequest(model, messages, tools);

        return client.post()
                .uri("/chat/completions")
                .bodyValue(req)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    System.out.println("LLM ERROR BODY: " + body);
                                    return Mono.error(new RuntimeException("LLM error: " + body));
                                })
                )
                .toEntity(ChatCompletionResponse.class)
                .map(entity -> {
                    HttpHeaders h = entity.getHeaders();
                    System.out.println("TOKENS IN: "  + h.getFirst("x-openai-usage-input-tokens"));
                    System.out.println("TOKENS OUT: " + h.getFirst("x-openai-usage-output-tokens"));
                    System.out.println("COST: "       + h.getFirst("x-openai-usage-cost"));
                    return entity.getBody();
                })
                .block();
    }

    // ── Public tool lists ─────────────────────────────────────────────────────

    public List<ToolDef> defaultTools() {
        return List.of(
                new ToolDef("function", new FunctionDef("getCustomerProfile",
                        "Get customer profile, segment and risk rating",
                        getProfileSchema())),
                new ToolDef("function", new FunctionDef("getFraudScore",
                        "Get fraud risk score and reasons for a customer",
                        getFraudSchema())),
                new ToolDef("function", new FunctionDef("getTransactionHistory",
                        "Get recent transaction history and patterns for a customer",
                        txnSchema())),
                new ToolDef("function", new FunctionDef("sanctionsCheck",
                        "Screen a person or entity name against OFAC, UK, UN and EU sanctions lists",
                        sanctionsSchema())),
                new ToolDef("function", new FunctionDef("checkPaymentRisk",
                        "Get payments and risk assessments for a customer",
                        paymentRiskSchema())),
                new ToolDef("function", new FunctionDef("searchPolicyDocuments",
                        "Search internal compliance policy documents for policies, procedures, regulations, EDD, CDD, PEPs, SARs, AML and fraud rules",
                        searchSchema())),
                new ToolDef("function", new FunctionDef("getSARGuidance",
                        "Get SAR submission guidance and legal requirements for a suspicious activity scenario",
                        sarSchema())),
                new ToolDef("function", new FunctionDef("createAuditLog",
                        "Create an audit log entry for a significant compliance action",
                        auditSchema())),
                new ToolDef("function", new FunctionDef("sendAlert",
                        "Send a compliance alert or notification",
                        alertSchema()))
        );
    }

    public List<ToolDef> fraudTools() {
        return List.of(
                new ToolDef("function", new FunctionDef("getFraudScore",
                        "Get fraud risk score", getFraudSchema())),
                new ToolDef("function", new FunctionDef("getTransactionHistory",
                        "Get transaction history", txnSchema())),
                new ToolDef("function", new FunctionDef("searchPolicyDocuments",
                        "Search policy documents", searchSchema()))
        );
    }

    public List<ToolDef> customerTools() {
        return List.of(
                new ToolDef("function", new FunctionDef("getCustomerProfile",
                        "Get customer profile", getProfileSchema())),
                new ToolDef("function", new FunctionDef("sanctionsCheck",
                        "Screen against sanctions lists", sanctionsSchema()))
        );
    }

    public List<ToolDef> paymentTools() {
        return List.of(
                new ToolDef("function", new FunctionDef("sanctionsCheck",
                        "Screen against sanctions lists", sanctionsSchema())),
                new ToolDef("function", new FunctionDef("checkPaymentRisk",
                        "Get payment risk assessment", paymentRiskSchema()))
        );
    }

    public List<ToolDef> complianceTools() {
        return List.of(
                new ToolDef("function", new FunctionDef("searchPolicyDocuments",
                        "Search policy documents", searchSchema())),
                new ToolDef("function", new FunctionDef("getSARGuidance",
                        "Get SAR guidance", sarSchema()))
        );
    }

    public List<ToolDef> notificationTools() {
        return List.of(
                new ToolDef("function", new FunctionDef("sendAlert",
                        "Send compliance alert", alertSchema())),
                new ToolDef("function", new FunctionDef("createAuditLog",
                        "Create audit log entry", auditSchema()))
        );
    }

    public List<ToolDef> orchestratorTools() {
        Map<String, Object> taskSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "task", Map.of("type", "string",
                                "description", "The specific task to delegate to this agent")
                ),
                "required", List.of("task"),
                "additionalProperties", false
        );
        return List.of(
                new ToolDef("function", new FunctionDef("callCustomerAgent",
                        "Delegate customer screening, KYC and PEP checks", taskSchema)),
                new ToolDef("function", new FunctionDef("callFraudAgent",
                        "Delegate fraud scoring and transaction analysis", taskSchema)),
                new ToolDef("function", new FunctionDef("callPaymentAgent",
                        "Delegate payment screening and sanctions checks", taskSchema)),
                new ToolDef("function", new FunctionDef("callComplianceAgent",
                        "Delegate policy lookups and SAR guidance", taskSchema)),
                new ToolDef("function", new FunctionDef("callNotificationAgent",
                        "Delegate alerts and audit logging", taskSchema))
        );
    }

    // ── Message helpers ───────────────────────────────────────────────────────

    public ChatMessage userMsg(String content) {
        return new ChatMessage("user", content, null, null);
    }

    public ChatMessage toolResultMsg(String toolCallId, Object result) {
        try {
            String json = mapper.writeValueAsString(result);
            return new ChatMessage("tool", json, null, toolCallId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> parseArgs(String json) {
        try {
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid tool args: " + json, e);
        }
    }

    // ── Private schema helpers ────────────────────────────────────────────────

    private Map<String, Object> getProfileSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "customerId", Map.of("type", "string",
                                "description", "Unique customer ID")
                ),
                "required", List.of("customerId"),
                "additionalProperties", false
        );
    }

    private Map<String, Object> getFraudSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "customerId", Map.of("type", "string",
                                "description", "Customer ID to get fraud score for")
                ),
                "required", List.of("customerId"),
                "additionalProperties", false
        );
    }

    private Map<String, Object> searchSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "query", Map.of("type", "string",
                                "description", "Search query for policy documents")
                ),
                "required", List.of("query"),
                "additionalProperties", false
        );
    }

    private Map<String, Object> txnSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "customerId", Map.of("type", "string",
                                "description", "Customer ID to get transactions for")
                ),
                "required", List.of("customerId"),
                "additionalProperties", false
        );
    }

    private Map<String, Object> sanctionsSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "name", Map.of("type", "string",
                                "description", "Full name of person or entity to screen")
                ),
                "required", List.of("name"),
                "additionalProperties", false
        );
    }

    private Map<String, Object> paymentRiskSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "customerId", Map.of("type", "string",
                                "description", "Customer ID to get payments for")
                ),
                "required", List.of("customerId"),
                "additionalProperties", false
        );
    }

    private Map<String, Object> sarSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "scenario", Map.of("type", "string",
                                "description", "Description of the suspicious activity scenario")
                ),
                "required", List.of("scenario"),
                "additionalProperties", false
        );
    }

    private Map<String, Object> auditSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "eventType",   Map.of("type", "string"),
                        "customerId",  Map.of("type", "string"),
                        "performedBy", Map.of("type", "string"),
                        "outcome",     Map.of("type", "string"),
                        "details",     Map.of("type", "string")
                ),
                "required", List.of("eventType", "customerId"),
                "additionalProperties", false
        );
    }

    private Map<String, Object> alertSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "type",       Map.of("type", "string"),
                        "severity",   Map.of("type", "string"),
                        "customerId", Map.of("type", "string"),
                        "recipient",  Map.of("type", "string"),
                        "message",    Map.of("type", "string")
                ),
                "required", List.of("type", "severity", "message"),
                "additionalProperties", false
        );
    }
}