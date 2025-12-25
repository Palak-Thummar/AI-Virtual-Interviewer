package com.virtualinterviewer.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private OpenAiService getOpenAiService() {
        return new OpenAiService(apiKey);
    }

    public String generateQuestion(String jobRole, String domain, String difficulty, String resumeContent) {
        try {
            OpenAiService service = getOpenAiService();

            String prompt = String.format(
                    "Generate a challenging %s interview question for a %s position in the %s domain. " +
                    "Difficulty level: %s. " +
                    (resumeContent != null ? "Consider the candidate's resume: %s. " : "") +
                    "Provide only the question without any numbering or extra text.",
                    domain, jobRole, domain, difficulty, resumeContent != null ? resumeContent : ""
            );

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                    .temperature(0.7)
                    .maxTokens(200)
                    .build();

            String question = service.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            service.shutdownExecutor();
            return question;
        } catch (Exception e) {
            System.err.println("OpenAI API error in generateQuestion: " + e.getMessage());
            throw new RuntimeException("Failed to generate question via AI", e);
        }
    }

    public String evaluateAnswer(String question, String userAnswer, String domain) {
        OpenAiService service = getOpenAiService();

        String prompt = String.format(
                "You are an expert interview evaluator. Evaluate the following answer to an interview question.\n\n" +
                "Question: %s\n\n" +
                "Domain: %s\n\n" +
                "Candidate's Answer: %s\n\n" +
                "Please provide:\n" +
                "1. Score (0-100)\n" +
                "2. Strengths of the answer\n" +
                "3. Weaknesses\n" +
                "4. Suggestions for improvement\n" +
                "5. Correct answer (if applicable)\n" +
                "Format your response clearly with sections.",
                question, domain, userAnswer
        );

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                .temperature(0.5)
                .build();

        String evaluation = service.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();

        service.shutdownExecutor();
        return evaluation;
    }

    public String generateFeedback(List<String> strengths, List<String> weaknesses, double score) {
        OpenAiService service = getOpenAiService();

        String prompt = String.format(
                "Based on an interview performance with the following metrics:\n" +
                "Overall Score: %.2f/100\n" +
                "Strengths: %s\n" +
                "Weaknesses: %s\n\n" +
                "Generate comprehensive feedback for the candidate including:\n" +
                "1. Overall assessment\n" +
                "2. Key strengths to leverage\n" +
                "3. Areas for improvement\n" +
                "4. Actionable recommendations\n" +
                "5. Preparation tips for next interview",
                score, String.join(", ", strengths), String.join(", ", weaknesses)
        );

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                .temperature(0.6)
                .build();

        String feedback = service.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();

        service.shutdownExecutor();
        return feedback;
    }

    public String transcribeAudio(String audioPath) {
        // This would require Whisper API or similar
        // For now, returning placeholder
        return "Transcribed audio content from: " + audioPath;
    }

    public String generateSpeech(String text) {
        // This would require Text-to-Speech API
        // For now, returning placeholder
        return "Speech generated for: " + text;
    }
}
