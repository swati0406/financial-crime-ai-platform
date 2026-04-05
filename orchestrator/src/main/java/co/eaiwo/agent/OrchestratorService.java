package co.eaiwo.agent;

import co.eaiwo.model.LlmDtos.*;
import co.eaiwo.service.LlmService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrchestratorService {

    private final LlmService llm;
    private final CustomerScreeningAgent customerAgent;
    private final FraudDetectionAgent fraudAgent;
    private final PaymentScreeningAgent paymentAgent;
    private final ComplianceAgent complianceAgent;
    private final NotificationAgent notificationAgent;

    public OrchestratorService(LlmService llm,
                               CustomerScreeningAgent customerAgent,
                               FraudDetectionAgent fraudAgent,
                               PaymentScreeningAgent paymentAgent,
                               ComplianceAgent complianceAgent,
                               NotificationAgent notificationAgent) {
        this.llm = llm;
        this.customerAgent = customerAgent;
        this.fraudAgent = fraudAgent;
        this.paymentAgent = paymentAgent;
        this.complianceAgent = complianceAgent;
        this.notificationAgent = notificationAgent;
    }

    public String handle(String userMessage) {
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system",
                "You are the Orchestrator of a Financial Crime AI Platform for a UK bank. " +
                        "You coordinate specialist agents to fulfil financial crime requests. " +
                        "You have 5 specialist agents available as tools: " +
                        "1. callCustomerAgent — for customer profile, KYC, PEP screening, customer risk. " +
                        "2. callFraudAgent — for fraud scoring, transaction history, structuring detection. " +
                        "3. callPaymentAgent — for payment screening, sanctions checks, payment risk. " +
                        "4. callComplianceAgent — for policy lookups, SAR guidance, regulatory questions. " +
                        "5. callNotificationAgent — for sending alerts and creating audit logs. " +
                        "Break complex tasks into sub-tasks and delegate each to the right agent. " +
                        "For a full AML check use multiple agents. " +
                        "Synthesise all results into one clear final answer.",
                null, null));
        history.add(llm.userMsg(userMessage));

        List<ToolDef> tools = llm.orchestratorTools();

        for (int turn = 0; turn < 8; turn++) {
            ChatCompletionResponse resp = llm.callWithTools(history, tools);
            var msg = resp.choices().get(0).message();

            System.out.println("[Orchestrator] TURN " + turn
                    + " | tools=" + msg.tool_calls());

            if (msg.tool_calls() == null || msg.tool_calls().isEmpty()) {
                if (msg.content() != null && !msg.content().isBlank())
                    return msg.content();
                history.add(new ChatMessage("assistant", msg.content(), null, null));
                history.add(new ChatMessage("user",
                        "Synthesise all findings into a clear final answer.", null, null));
                continue;
            }

            history.add(new ChatMessage("assistant",
                    msg.content(), msg.tool_calls(), null));

            for (ToolCall call : msg.tool_calls()) {
                Map<String, Object> args = llm.parseArgs(call.function().arguments());
                String task = (String) args.get("task");

                System.out.println("[Orchestrator] DELEGATING to: "
                        + call.function().name() + " | task: " + task);

                String result = switch (call.function().name()) {
                    case "callCustomerAgent"     -> customerAgent.handle(task);
                    case "callFraudAgent"        -> fraudAgent.handle(task);
                    case "callPaymentAgent"      -> paymentAgent.handle(task);
                    case "callComplianceAgent"   -> complianceAgent.handle(task);
                    case "callNotificationAgent" -> notificationAgent.handle(task);
                    default -> "Unknown agent: " + call.function().name();
                };

                System.out.println("[Orchestrator] RESULT from "
                        + call.function().name() + ": " + result);

                history.add(llm.toolResultMsg(call.id(), result));
            }
        }
        return "I could not complete the request.";
    }
}