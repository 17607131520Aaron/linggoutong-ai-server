package com.linggoutong.server.module.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linggoutong.server.app.dto.ChatMessage;
import com.linggoutong.server.app.dto.ChatRequest;
import com.linggoutong.server.common.config.OpenAiConfig;
import com.linggoutong.server.module.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final WebClient openAiWebClient;
    private final OpenAiConfig openAiConfig;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public String chat(ChatRequest request) {
        Map<String, Object> requestBody = buildRequestBody(request, false);
        log.debug("OpenAI request body: {}", requestBody);

        String response = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.debug("OpenAI response: {}", response);
        return extractContent(response);
    }

    @Override
    public SseEmitter chatStream(ChatRequest request) {
        SseEmitter emitter = new SseEmitter(300000L);

        emitter.onCompletion(() -> log.debug("SSE completed"));
        emitter.onTimeout(() -> log.debug("SSE timeout"));
        emitter.onError(e -> log.error("SSE error", e));

        executorService.execute(() -> {
            try {
                Map<String, Object> requestBody = buildRequestBody(request, true);

                Flux<ServerSentEvent<String>> eventStream = openAiWebClient.post()
                        .uri("/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {});

                eventStream.subscribe(
                        event -> {
                            try {
                                Object dataObj = event.data();
                                String data = dataObj != null ? dataObj.toString() : null;
                                if (data != null && !data.equals("[DONE]")) {
                                    String content = extractStreamContent(data);
                                    if (content != null && !content.isEmpty()) {
                                        emitter.send(SseEmitter.event()
                                                .data(content));
                                    }
                                }
                                if (data != null && data.equals("[DONE]")) {
                                    emitter.send(SseEmitter.event()
                                            .data("[DONE]"));
                                    emitter.complete();
                                }
                            } catch (IOException e) {
                                log.error("Error sending SSE event", e);
                                emitter.completeWithError(e);
                            }
                        },
                        error -> {
                            log.error("Error in SSE stream", error);
                            emitter.completeWithError(error);
                        },
                        () -> {
                            try {
                                emitter.send(SseEmitter.event().data("[DONE]"));
                                emitter.complete();
                            } catch (IOException e) {
                                log.error("Error completing SSE", e);
                            }
                        }
                );
            } catch (Exception e) {
                log.error("Error initiating SSE stream", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private Map<String, Object> buildRequestBody(ChatRequest request, boolean stream) {
        Map<String, Object> requestBody = new HashMap<>();
        String modelName = request.getModel() != null ? request.getModel() : openAiConfig.getModel();
        String imageFormat = openAiConfig.getImageFormat(modelName);
        
        requestBody.put("model", modelName);
        requestBody.put("messages", processMessages(request.getMessages(), modelName));
        requestBody.put("stream", stream);

        if (request.getTemperature() != null) {
            requestBody.put("temperature", request.getTemperature());
        }
        if (request.getMaxTokens() != null) {
            if ("mimo".equals(imageFormat)) {
                requestBody.put("max_completion_tokens", request.getMaxTokens());
            } else {
                requestBody.put("max_tokens", request.getMaxTokens());
            }
        }

        return requestBody;
    }

    private List<Map<String, Object>> processMessages(List<ChatMessage> messages, String modelName) {
        List<Map<String, Object>> processedMessages = new ArrayList<>();
        String imageFormat = openAiConfig.getImageFormat(modelName);

        for (ChatMessage message : messages) {
            Map<String, Object> processedMessage = new HashMap<>();
            processedMessage.put("role", message.getRole());

            if (message.getImages() != null && !message.getImages().isEmpty()) {
                processedMessage.put("content", buildContentWithImages(message, imageFormat, modelName));
            } else {
                processedMessage.put("content", message.getContent());
            }

            processedMessages.add(processedMessage);
        }

        return processedMessages;
    }

    private Object buildContentWithImages(ChatMessage message, String imageFormat, String modelName) {
        if ("mimo".equals(imageFormat)) {
            return buildMiMoVisionContent(message, modelName);
        }
        return buildOpenAiVisionContent(message);
    }

    private List<Map<String, Object>> buildMiMoVisionContent(ChatMessage message, String modelName) {
        List<Map<String, Object>> contentList = new ArrayList<>();
        boolean stripPrefix = openAiConfig.isStripDataUriPrefix(modelName);

        for (String imageUrl : message.getImages()) {
            String processedUrl = imageUrl;
            
            // 如果配置了去掉data URI前缀
            if (stripPrefix && imageUrl.startsWith("data:") && imageUrl.contains(";base64,")) {
                processedUrl = imageUrl.substring(imageUrl.indexOf(";base64,") + 8);
            }
            
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, Object> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", processedUrl);
            imageContent.put("image_url", imageUrlMap);
            contentList.add(imageContent);
        }

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", message.getContent().toString());
        contentList.add(textContent);

        return contentList;
    }

    private List<Map<String, Object>> buildOpenAiVisionContent(ChatMessage message) {
        List<Map<String, Object>> contentList = new ArrayList<>();

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", message.getContent().toString());
        contentList.add(textContent);

        for (String imageUrl : message.getImages()) {
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, Object> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", imageUrl);
            imageContent.put("image_url", imageUrlMap);
            contentList.add(imageContent);
        }

        return contentList;
    }

    private String extractContent(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
        } catch (Exception e) {
            log.error("Error extracting content from response", e);
        }
        return "";
    }

    private String extractStreamContent(String data) {
        try {
            JsonNode root = objectMapper.readTree(data);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode delta = choices.get(0).path("delta");
                if (delta.has("content")) {
                    return delta.path("content").asText();
                }
            }
        } catch (Exception e) {
            log.error("Error extracting stream content", e);
        }
        return null;
    }
}
