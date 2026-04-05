package co.eaiwo.service;

import co.eaiwo.model.LlmDtos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentService {

    private final LlmService llm;
    private final ToolExecutorService executor;
    private final ObjectMapper mapper = new ObjectMapper();

    // ✅ ADD THIS — one map holds all active sessions
    private final Map<String, List<ChatMessage>> sessions = new ConcurrentHashMap<>();

    public AgentService(LlmService llm, ToolExecutorService executor) {
        this.llm = llm;
        this.executor = executor;
    }

    // ✅ CHANGE signature — now accepts sessionId
    public String handle(String sessionId, String userMessage) {

        // ✅ Get existing history for this session, or start a new one
        List<ChatMessage> history = sessions.computeIfAbsent(sessionId, k -> {
            List<ChatMessage> fresh = new ArrayList<>();
            fresh.add(new ChatMessage("system",
                    "You are a Financial Crime AI Assistant for a UK bank. " +
                            "You have access to the following tools — ALWAYS use them when relevant: " +

                            "1. getCustomerProfile(customerId) — get customer details, segment, risk rating. " +
                            "2. getFraudScore(customerId) — get fraud risk score and reasons. " +
                            "3. getTransactionHistory(customerId) — get recent transactions and patterns. " +
                            "4. sanctionsCheck(name) — screen any person or entity name against OFAC, UK, UN and EU sanctions lists. ALWAYS use this when asked to screen someone. " +
                            "5. checkPaymentRisk(customerId) — get payments and risk assessments for a customer. " +
                            "6. searchPolicyDocuments(query) — search internal compliance policy documents. Use this for ANY question about policies, procedures, thresholds, regulations, EDD, CDD, PEPs, SARs, AML, fraud rules, or sanctions policy. " +
                            "7. getSARGuidance(scenario) — get SAR submission guidance and legal requirements. " +
                            "8. createAuditLog(eventType, customerId, performedBy, outcome, details) — log any significant action. " +
                            "9. sendAlert(type, severity, customerId, recipient, message) — send compliance alerts. " +

                            "Rules: " +
                            "- NEVER say you cannot do something without first trying the relevant tool. " +
                            "- For sanctions screening ALWAYS call sanctionsCheck with the person or entity name. " +
                            "- For flagged payments ALWAYS call checkPaymentRisk. " +
                            "- For policy questions ALWAYS call searchPolicyDocuments. " +
                            "- After getting tool results give a clear direct answer. " +
                            "- Never return empty responses.",
                    null, null));
            return fresh;
        });

        // ✅ Add the new user message to existing history
        history.add(llm.userMsg(userMessage));

        List<ToolDef> tools = llm.defaultTools();

        for (int turn = 0; turn < 5; turn++) {
            ChatCompletionResponse resp = llm.callWithTools(history, tools);
            var msg = resp.choices().get(0).message();

            System.out.println("=== TURN " + turn
                    + " | content=" + msg.content()
                    + " | tool_calls=" + msg.tool_calls());

            if (msg.tool_calls() == null || msg.tool_calls().isEmpty()) {
                if (msg.content() != null && !msg.content().isBlank()) {
                    // ✅ Save the assistant reply into history before returning
                    history.add(new ChatMessage("assistant", msg.content(), null, null));
                    return msg.content();
                }
                history.add(new ChatMessage("assistant", msg.content(), null, null));
                history.add(new ChatMessage("user",
                        "Based on the tool results above, give a clear friendly summary.",
                        null, null));
                continue;
            }

            history.add(new ChatMessage("assistant", msg.content(), msg.tool_calls(), null));

            for (ToolCall call : msg.tool_calls()) {
                System.out.println("=== TOOL CALL: " + call.function().name()
                        + " args=" + call.function().arguments());
                Map<String, Object> args = parseArgs(call.function().arguments());
                Object toolResult = executor.execute(call.function().name(), args);
                System.out.println("=== TOOL RESULT: " + toolResult);
                history.add(llm.toolResultMsg(call.id(), toolResult));
            }
        }

        return "Sorry, I could not complete the request.";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseArgs(String json) {
        try {
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid tool args JSON: " + json, e);
        }
    }
}