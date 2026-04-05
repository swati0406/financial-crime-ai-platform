package co.eaiwo.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngestionService {

    private final JdbcTemplate jdbc;
    private final EmbeddingService embeddingService;

    public IngestionService(JdbcTemplate jdbc, EmbeddingService embeddingService) {
        this.jdbc = jdbc;
        this.embeddingService = embeddingService;
    }

    public void ingestPdf(String filePath, String docName) throws Exception {

        // Step 1 — Read the PDF and extract all text
        PDDocument pdf = Loader.loadPDF(new File(filePath));
        PDFTextStripper stripper = new PDFTextStripper();
        String fullText = stripper.getText(pdf);
        pdf.close();
        System.out.println("Extracted text from: " + docName);

        // Step 2 — Split the text into overlapping chunks of 500 characters, each overlapping the previous by 50 characters
        List<String> chunks = chunk(fullText, 500, 50);
        System.out.println("Total chunks created: " + chunks.size());

        // Step 3 — Embed each chunk and store in PGVector
        for (int i = 0; i < chunks.size(); i++) {
            String content = chunks.get(i);

            // Call embedding model for each chunk — sends the chunk text to OpenRouter,get back 1536 numbers
            List<Double> embedding = embeddingService.embed(content);

            // Postgres expects vectors in this exact format: [0.023,-0.847,0.312,...]
            // This line converts our Java List<Double> into that string format
            String vectorStr = embedding.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(",", "[", "]"));

            // Saves the chunk into the database
            // ?::vector is a Postgres cast — it tells Postgres to treat the string as a vector type
            // The ? placeholders are filled in safely by JdbcTemplate, preventing SQL injection.
            jdbc.update(
                    "INSERT INTO document_chunks " +
                            "(doc_name, chunk_index, content, embedding) " +
                            "VALUES (?, ?, ?, ?::vector)",
                    docName, i, content, vectorStr
            );

            System.out.println("Stored chunk " + (i + 1) + " of " + chunks.size());
        }

        System.out.println("Ingestion complete for: " + docName);
    }

    /**
     * The chunking logic
     * Moves a sliding window through the text
     * size - overlap means each step moves forward 450 characters (500 - 50), keeping 50 characters of the previous chunk at the start of the next one.
     *
     * @param text
     * @param size
     * @param overlap
     * @return
     */
    private List<String> chunk(String text, int size, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + size, text.length());
            chunks.add(text.substring(start, end).trim());
            start += (size - overlap); // move forward but keep overlap
        }
        return chunks;
    }
}