package co.eaiwo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {

    private final JdbcTemplate jdbc;
    private final EmbeddingService embeddingService;

    public VectorSearchService(JdbcTemplate jdbc, EmbeddingService embeddingService) {
        this.jdbc = jdbc;
        this.embeddingService = embeddingService;
    }

    public String search(String query, int topK) {

        // Step 1 — The user's question gets converted to 1536 numbers — exactly the same process as when we embedded the PDF chunks during ingestion
        List<Double> queryEmbedding = embeddingService.embed(query);

        // Step 2 — Convert to Postgres vector format
        String vectorStr = queryEmbedding.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));

        // Step 3 — Find most similar chunks in the database
        // The <=> operator is PGVector's cosine distance operator. It returns a value between 0 and 2 where 0 means identical
        // We do 1 - distance to convert it to a similarity score where 1 means identical and 0 means completely different. So a score of 0.92 means very relevant.
        List<Map<String, Object>> results = jdbc.queryForList(
                "SELECT doc_name, content, " +
                        "1 - (embedding <=> ?::vector) AS similarity " +
                        "FROM document_chunks " +
                        "ORDER BY embedding <=> ?::vector " + // Sorts all chunks by how close they are to the query vector and returns only the top K most similar ones. This is the vector similarity search — PGVector uses the HNSW index we created to do this extremely fast.
                        "LIMIT ?",
                vectorStr, vectorStr, topK
        );

        // Step 4 — If nothing found, say so
        if (results.isEmpty()) {
            return "No relevant documents found.";
        }

        // Step 5 — Format results for the LLM prompt
        // Formats the retrieved chunks for the LLM
        // Each chunk is labelled with its source document and relevance score so the LLM knows where the information came from. This is what gets injected into the prompt as context.
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> row : results) {
            double similarity = (double) row.get("similarity");
            sb.append("[Source: ").append(row.get("doc_name"))
                    .append(" | Relevance: ")
                    .append(String.format("%.2f", similarity))
                    .append("]\n")
                    .append(row.get("content"))
                    .append("\n\n");
        }

        return sb.toString();
    }
}