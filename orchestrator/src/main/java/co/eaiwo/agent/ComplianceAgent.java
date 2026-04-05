package co.eaiwo.agent;

import co.eaiwo.model.LlmDtos.*;
import co.eaiwo.service.LlmService;
import co.eaiwo.service.ToolExecutorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ComplianceAgent {

    private final LlmService llm;
    private final ToolExecutorService executor;

    public ComplianceAgent(LlmService llm,
                           ToolExecutorService executor) {
        this.llm = llm;
        this.executor = executor;
    }

    public String handle(String task) {
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system",
                "You are a specialist Compliance Agent for a UK bank. " +
                        "You answer questions about compliance policy, regulations and SAR reporting. " +
                        "Always use searchPolicyDocuments to find relevant policy content. " +
                        "Always use getSARGuidance when asked about suspicious activity reporting. " +
                        "Base your answers strictly on the retrieved policy documents. " +
                        "Cite the policy source in your answer. " +
                        "Give clear actionable compliance guidance.",
                null, null));
        history.add(llm.userMsg(task));

        List<ToolDef> tools = llm.complianceTools();

        for (int turn = 0; turn < 5; turn++) {
            ChatCompletionResponse resp = llm.callWithTools(history, tools);
            var msg = resp.choices().get(0).message();

            System.out.println("[ComplianceAgent] TURN " + turn
                    + " | tools=" + msg.tool_calls());

            if (msg.tool_calls() == null || msg.tool_calls().isEmpty()) {
                if (msg.content() != null && !msg.content().isBlank())
                    return msg.content();
                history.add(new ChatMessage("assistant", msg.content(), null, null));
                history.add(new ChatMessage("user",
                        "Summarise the compliance guidance clearly.", null, null));
                continue;
            }

            history.add(new ChatMessage("assistant",
                    msg.content(), msg.tool_calls(), null));

            for (ToolCall call : msg.tool_calls()) {
                System.out.println("[ComplianceAgent] TOOL: " + call.function().name());
                Map<String, Object> args = llm.parseArgs(call.function().arguments());
                Object result = executor.execute(call.function().name(), args);
                history.add(llm.toolResultMsg(call.id(), result));
            }
        }
        return "Compliance guidance could not be retrieved.";
    }
}