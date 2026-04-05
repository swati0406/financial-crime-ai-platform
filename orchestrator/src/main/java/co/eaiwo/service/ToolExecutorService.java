package co.eaiwo.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ToolExecutorService {

    private final VectorSearchService vectorSearch;
    private final MicroserviceClient client;

    public ToolExecutorService(VectorSearchService vectorSearch,
                               MicroserviceClient client) {
        this.vectorSearch = vectorSearch;
        this.client = client;
    }

    public Object execute(String toolName, Map<String, Object> args) {
        return switch (toolName) {

            case "getCustomerProfile" -> {
                String id = (String) args.get("customerId");
                yield client.getCustomerProfile(id);
            }

            case "getFraudScore" -> {
                String id = (String) args.get("customerId");
                yield client.getFraudScore(id);
            }

            case "getTransactionHistory" -> {
                String id = (String) args.get("customerId");
                yield client.getTransactionHistory(id);
            }

            case "sanctionsCheck" -> {
                String name = (String) args.get("name");
                yield client.sanctionsCheck(name);
            }

            case "checkPaymentRisk" -> {
                String customerId = (String) args.get("customerId");
                yield client.getPaymentsByCustomer(customerId);
            }

            case "searchPolicyDocuments" -> {
                String query = (String) args.get("query");
                yield vectorSearch.search(query, 3);
            }

            case "getSARGuidance" -> {
                String scenario = (String) args.getOrDefault(
                        "scenario", "suspicious activity");
                yield vectorSearch.search(
                        "SAR reporting " + scenario, 3);
            }

            case "createAuditLog" -> {
                String eventType  = (String) args.getOrDefault("eventType", "AGENT_ACTION");
                String customerId = (String) args.getOrDefault("customerId", "UNKNOWN");
                String performedBy = (String) args.getOrDefault("performedBy", "AI Agent");
                String outcome    = (String) args.getOrDefault("outcome", "COMPLETED");
                String details    = (String) args.getOrDefault("details", "");
                yield client.createAuditLog(eventType, customerId,
                        performedBy, outcome, details);
            }

            case "sendNotification", "sendAlert" -> {
                String alertType  = (String) args.getOrDefault("type", "GENERAL");
                String severity   = (String) args.getOrDefault("severity", "MEDIUM");
                String customerId = (String) args.getOrDefault("customerId", "UNKNOWN");
                String recipient  = (String) args.getOrDefault("recipient", "compliance@bank.com");
                String message    = (String) args.getOrDefault("message", "");
                yield client.createAlert(alertType, severity,
                        customerId, recipient, message);
            }

            default -> throw new IllegalArgumentException(
                    "Unknown tool: " + toolName);
        };
    }
}