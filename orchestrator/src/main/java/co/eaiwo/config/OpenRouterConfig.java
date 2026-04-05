package co.eaiwo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenRouterConfig {

    @Value("${openrouter.base-url}")
    private String baseUrl;

    @Value("${openrouter.api-key}")
    private String apiKey;

    @PostConstruct
    public void debug() {
        System.out.println("Loaded API key = " + apiKey);
    }


    @Bean
    public WebClient openRouterClient() {
        // Increase buffer if you expect long responses
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", "http://localhost:8080")
                .defaultHeader("X-Title", "EAIWO")
                .filter((request, next) -> {
                    System.out.println("=== OUTGOING HEADERS ===");
                    request.headers().forEach((k, v) -> System.out.println(k + " = " + v));
                    return next.exchange(request);
                })
                .build();



    }
}