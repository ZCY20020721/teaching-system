package com.teaching.system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${ai-service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> generateQuestion(String ragContext) {
        return callAI("/ai/generate-question", Map.of("rag_context", ragContext));
    }

    public Map<String, Object> gradeAnswer(String ragContext, String answerPoints, String studentAnswer) {
        return callAI("/ai/grade-answer", Map.of(
            "rag_context", ragContext,
            "standard_answer_points", answerPoints,
            "student_answer", studentAnswer));
    }

    public String search(String query, int k) {
        Map<String, Object> result = callAI("/ai/search", Map.of("query", query, "k", k));
        Object results = result.get("results");
        return results != null ? results.toString() : "";
    }

    public String loadPdf(String filePath) {
        Map<String, Object> result = callAI("/ai/load-pdf", Map.of("pdf_path", filePath));
        Object chunks = result.get("chunks");
        return chunks != null ? chunks.toString() : "0";
    }

    private Map<String, Object> callAI(String path, Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(aiServiceUrl + path, request, Map.class);
            return resp.getBody();
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}
