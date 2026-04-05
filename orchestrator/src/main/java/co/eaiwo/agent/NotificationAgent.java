package co.eaiwo.agent;

import co.eaiwo.model.LlmDtos.*;
import co.eaiwo.service.LlmService;
import co.eaiwo.service.ToolExecutorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NotificationAgent {

    private final LlmService llm;
    private final ToolExecutorService executor;

    public NotificationAgent(LlmService llm,
                             ToolExecutorService executor) {
        this.llm = llm;
        this.executor = executor;
    }

    public String handle(String task) {
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system",
                "You are a specialist Notification and Audit Agent for a UK bank. " +
                        "You send compliance alerts and create audit log entries. " +
                        "Always use sendAlert to send compliance notifications. " +
                        "Always use createAuditLog to record significant actions. " +
                        "Confirm clearly what alert was sent and what was logged.",
                null, null));
        history.add(llm.userMsg(task));

        List<ToolDef> tools = llm.notificationTools();

        for (int turn = 0; turn < 5; turn++) {
            ChatCompletionResponse resp = llm.callWithTools(history, tools);
            var msg = resp.choices().get(0).message();

            System.out.println("[NotificationAgent] TURN " + turn
                    + " | tools=" + msg.tool_calls());

            if (msg.tool_calls() == null || msg.tool_calls().isEmpty()) {
                if (msg.content() != null && !msg.content().isBlank())
                    return msg.content();
                history.add(new ChatMessage("assistant", msg.content(), null, null));
                history.add(new ChatMessage("user",
                        "Confirm what was sent and logged.", null, null));
                continue;
            }

            history.add(new ChatMessage("assistant",
                    msg.content(), msg.tool_calls(), null));

            for (ToolCall call : msg.tool_calls()) {
                System.out.println("[NotificationAgent] TOOL: " + call.function().name());
                Map<String, Object> args = llm.parseArgs(call.function().arguments());
                Object result = executor.execute(call.function().name(), args);
                history.add(llm.toolResultMsg(call.id(), result));
            }
        }
        return "Notification could not be sent.";
    }
}