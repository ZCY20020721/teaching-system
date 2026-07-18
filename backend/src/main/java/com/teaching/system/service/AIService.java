package com.teaching.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class AIService {

    @Value("${ai-service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate;

    public AIService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(120000);
        this.restTemplate = new RestTemplate(factory);
    }

    public Map<String, Object> generateQuestion(String ragContext, String customRequirement) {
        Map<String, Object> body = new HashMap<>();
        body.put("rag_context", ragContext);
        if (customRequirement != null && !customRequirement.isBlank()) {
            body.put("requirement", customRequirement);
        }
        return callAI("/ai/generate-question", body);
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

    public String loadPdf(byte[] pdfBytes, String filename) {
        String b64 = Base64.getEncoder().encodeToString(pdfBytes);
        Map<String, Object> result = callAI("/ai/load-pdf", Map.of("filename", filename, "data_b64", b64));
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
