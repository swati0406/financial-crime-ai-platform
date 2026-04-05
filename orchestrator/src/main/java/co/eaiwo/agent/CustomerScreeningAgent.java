package co.eaiwo.agent;

import co.eaiwo.model.LlmDtos.*;
import co.eaiwo.service.LlmService;
import co.eaiwo.service.ToolExecutorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomerScreeningAgent {

    private final LlmService llm;
    private final ToolExecutorService executor;

    public CustomerScreeningAgent(LlmService llm,
                                  ToolExecutorService executor) {
        this.llm = llm;
        this.executor = executor;
    }

    public String handle(String task) {
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system",
                "You are a specialist Customer Screening Agent for a UK bank. " +
                        "You handle customer due diligence, KYC verification and PEP screening. " +
                        "Always use getCustomerProfile to retrieve customer details. " +
                        "Always use sanctionsCheck to screen the customer name against sanctions lists. " +
                        "Give a clear structured assessment covering: customer details, sanctions status, and risk rating. " +
                        "If the customer is HIGH or CRITICAL risk flag it clearly.",
                null, null));
        history.add(llm.userMsg(task));

        List<ToolDef> tools = llm.customerTools();

        for (int turn = 0; turn < 5; turn++) {
            ChatCompletionResponse resp = llm.callWithTools(history, tools);
            var msg = resp.choices().get(0).message();

            System.out.println("[CustomerAgent] TURN " + turn
                    + " | tools=" + msg.tool_calls());

            if (msg.tool_calls() == null || msg.tool_calls().isEmpty()) {
                if (msg.content() != null && !msg.content().isBlank())
                    return msg.content();
                history.add(new ChatMessage("assistant", msg.content(), null, null));
                history.add(new ChatMessage("user",
                        "Summarise the customer screening findings clearly.", null, null));
                continue;
            }

            history.add(new ChatMessage("assistant",
                    msg.content(), msg.tool_calls(), null));

            for (ToolCall call : msg.tool_calls()) {
                System.out.println("[CustomerAgent] TOOL: " + call.function().name());
                Map<String, Object> args = llm.parseArgs(call.function().arguments());
                Object result = executor.execute(call.function().name(), args);
                history.add(llm.toolResultMsg(call.id(), result));
            }
        }
        return "Customer screening could not be completed.";
    }
}