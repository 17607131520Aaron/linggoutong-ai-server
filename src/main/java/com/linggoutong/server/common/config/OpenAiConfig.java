package com.linggoutong.server.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {

    private String apiKey;
    private String model;
    private String baseUrl;
    private String authHeaderType = "api-key"; // "api-key" 或 "bearer"
    private VisionConfig vision = new VisionConfig();

    @Data
    public static class VisionConfig {
        private boolean enabled = true;
        private Map<String, ModelVisionConfig> models = new HashMap<>();
    }

    @Data
    public static class ModelVisionConfig {
        private String imageFormat = "openai";
        private boolean supportBase64 = true;
        private boolean stripDataUriPrefix = false;
    }

    @Bean
    public WebClient openAiWebClient() {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024));

        // 根据 authHeaderType 选择认证头格式
        if ("api-key".equalsIgnoreCase(authHeaderType)) {
            // MiMo 风格：api-key 头
            builder.defaultHeader("api-key", apiKey);
        } else {
            // OpenAI 风格：Authorization Bearer 头
            builder.defaultHeader("Authorization", "Bearer " + apiKey);
        }

        return builder.build();
    }

    public String getImageFormat(String modelName) {
        if (vision.getModels().containsKey(modelName)) {
            return vision.getModels().get(modelName).getImageFormat();
        }
        return "openai";
    }

    public boolean isSupportBase64(String modelName) {
        if (vision.getModels().containsKey(modelName)) {
            return vision.getModels().get(modelName).isSupportBase64();
        }
        return true;
    }

    public boolean isStripDataUriPrefix(String modelName) {
        if (vision.getModels().containsKey(modelName)) {
            return vision.getModels().get(modelName).isStripDataUriPrefix();
        }
        return false;
    }
}
