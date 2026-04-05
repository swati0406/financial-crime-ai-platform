package co.eaiwo.agent;

import co.eaiwo.model.LlmDtos.*;
import co.eaiwo.service.LlmService;
import co.eaiwo.service.ToolExecutorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FraudDetectionAgent {

    private final LlmService llm;
    private final ToolExecutorService executor;

    public FraudDetectionAgent(LlmService llm,
                               ToolExecutorService executor) {
        this.llm = llm;
        this.executor = executor;
    }

    public String handle(String task) {
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system",
                "You are a specialist Fraud Detection Agent for a UK bank. " +
                        "You analyse fraud risk and transaction patterns. " +
                        "Always use getFraudScore to get the risk score. " +
                        "Always use getTransactionHistory to identify suspicious patterns. " +
                        "Use searchPolicyDocuments to find relevant fraud policy rules. " +
                        "Give a clear risk assessment with: score, risk level, pattern analysis, and recommended action.",
                null, null));
        history.add(llm.userMsg(task));

        List<ToolDef> tools = llm.fraudTools();

        for (int turn = 0; turn < 5; turn++) {
            ChatCompletionResponse resp = llm.callWithTools(history, tools);
            var msg = resp.choices().get(0).message();

            System.out.println("[FraudAgent] TURN " + turn
                    + " | tools=" + msg.tool_calls());

            if (msg.tool_calls() == null || msg.tool_calls().isEmpty()) {
                if (msg.content() != null && !msg.content().isBlank())
                    return msg.content();
                history.add(new ChatMessage("assistant", msg.content(), null, null));
                history.add(new ChatMessage("user",
                        "Summarise the fraud risk findings clearly.", null, null));
                continue;
            }

            history.add(new ChatMessage("assistant",
                    msg.content(), msg.tool_calls(), null));

            for (ToolCall call : msg.tool_calls()) {
                System.out.println("[FraudAgent] TOOL: " + call.function().name());
                Map<String, Object> args = llm.parseArgs(call.function().arguments());
                Object result = executor.execute(call.function().name(), args);
                history.add(llm.toolResultMsg(call.id(), result));
            }
        }
        return "Fraud assessment could not be completed.";
    }
}