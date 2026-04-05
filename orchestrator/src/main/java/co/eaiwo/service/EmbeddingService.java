package co.eaiwo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 *  you give this class a string, it calls OpenRouter, and gets back 1536 numbers that represent the meaning of that string
 *  Every chunk of your PDF and every user query will go through this class.
 */
@Service
public class EmbeddingService {

    /**
     * The same WebClient we already configured in OpenRouterConfig
     * it already has API key and base URL set. We reuse it here instead of creating a new HTTP client.
     */
    private final WebClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openrouter.embedding-model}")
    private String embeddingModel;

    public EmbeddingService(WebClient openRouterClient) {
        this.client = openRouterClient;
    }

    // Takes any piece of text and returns a list of numbers (the vector)
    @SuppressWarnings("unchecked")
    public List<Double> embed(String text) {
        Map<String, Object> body = Map.of(
                "model", embeddingModel,
                "input", text
        );
/**
 * Calls the OpenRouter /embeddings endpoint. Same pattern as existing LLM calls in LlmService
 * POST the body, get back a response, block until it arrives
 * The response is a raw Map because we just need to dig into it for the vector
 */
        Map response = client.post()
                .uri("/embeddings")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Response: { "data": [ { "embedding": [0.1, 0.2, ...] } ] }
        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.get("data");
        return (List<Double>) data.get(0).get("embedding");
    }
}