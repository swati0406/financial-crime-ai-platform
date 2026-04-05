package co.eaiwo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

public class LlmDtos {

    // ----- request/response message union -----
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatMessage(
            String role,                    // "user", "assistant", or "tool"
            String content,                 // text or JSON for "tool"
            List<ToolCall> tool_calls,      // populated when assistant requests a tool
            String tool_call_id             // populated for role="tool" messages
    ) {}

    // assistant -> tool call instruction
    public record ToolCall(String id, String type, FunctionCall function) {}

    public record FunctionCall(String name, String arguments) {}

    // tool definitions you send along with the request
    public record ToolDef(String type, FunctionDef function) {}

    public record FunctionDef(String name, String description, Map<String, Object> parameters) {}

    // request to OpenRouter
    public record ChatCompletionRequest(
            String model,
            List<ChatMessage> messages,
            List<ToolDef> tools
    ) {}

    // response from OpenRouter
    public record ChatCompletionResponse(
            String id,
            List<Choice> choices
    ) {}

    public record Choice(Message message) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Message(
            String role,
            String content,
            List<ToolCall> tool_calls
    ) {}
}

