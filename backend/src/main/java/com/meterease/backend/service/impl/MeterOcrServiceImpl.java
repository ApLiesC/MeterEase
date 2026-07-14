package com.meterease.backend.service.impl;

import com.meterease.backend.dto.MeterOcrResponse;
import com.meterease.backend.service.GeminiService;
import com.meterease.backend.service.MeterOcrService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MeterOcrServiceImpl implements MeterOcrService {

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("\\d+");

    private final RestClient restClient;
    private final String apiKey;
    private final GeminiService geminiService;

    public MeterOcrServiceImpl(
            @Value("${google.vision.api-key}")
            String apiKey,
            GeminiService geminiService
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(
                        "https://vision.googleapis.com/v1"
                )
                .build();

        this.apiKey = apiKey;
        this.geminiService = geminiService;
    }

    @Override
    public MeterOcrResponse scanMeterImage(
            MultipartFile image
    ) {
        validateImage(image);

        try {
            String base64Image = Base64.getEncoder()
                    .encodeToString(image.getBytes());

            Map<String, Object> requestBody = Map.of(
                    "requests",
                    List.of(
                            Map.of(
                                    "image",
                                    Map.of(
                                            "content",
                                            base64Image
                                    ),
                                    "features",
                                    List.of(
                                            Map.of(
                                                    "type",
                                                    "TEXT_DETECTION"
                                            )
                                    )
                            )
                    )
            );

            VisionResponse response = restClient
                    .post()
                    .uri(
                            uriBuilder -> uriBuilder
                                    .path("/images:annotate")
                                    .queryParam("key", apiKey)
                                    .build()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(VisionResponse.class);

            if (response == null
                    || response.responses() == null
                    || response.responses().isEmpty()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Google Vision returned no response"
                );
            }

            VisionAnnotationResponse annotationResponse =
                    response.responses().getFirst();

            if (annotationResponse.error() != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        annotationResponse.error().message()
                );
            }

            String extractedText =
                    extractText(annotationResponse);

            List<Integer> detectedNumbers =
                    extractNumbers(extractedText);

            Integer suggestedReading =
                    getSuggestedReading(
                            image,
                            extractedText,
                            detectedNumbers
                    );

            return MeterOcrResponse.builder()
                    .extractedText(extractedText)
                    .detectedNumbers(detectedNumbers)
                    .suggestedReading(suggestedReading)
                    .build();

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to read uploaded image",
                    exception
            );
        }
    }

    private Integer getSuggestedReading(
            MultipartFile image,
            String extractedText,
            List<Integer> detectedNumbers
    ) {
        try {
            String geminiResponse =
                    geminiService.validateReading(
                            image,
                            extractedText,
                            detectedNumbers
                    );

            String cleanedResponse =
                    geminiResponse.replaceAll(
                            "[^0-9]",
                            ""
                    );

            if (cleanedResponse.isBlank()) {
                throw new NumberFormatException(
                        "Gemini did not return a number"
                );
            }

            return Integer.parseInt(cleanedResponse);

        } catch (Exception exception) {
            return chooseSuggestedReading(
                    detectedNumbers
            );
        }
    }

    private void validateImage(
            MultipartFile image
    ) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Meter image is required"
            );
        }

        String contentType = image.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uploaded file must be an image"
            );
        }

        long maximumSize =
                10L * 1024L * 1024L;

        if (image.getSize() > maximumSize) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image cannot exceed 10 MB"
            );
        }
    }

    private String extractText(
            VisionAnnotationResponse response
    ) {
        if (response.fullTextAnnotation() != null
                && response.fullTextAnnotation().text() != null) {

            return response
                    .fullTextAnnotation()
                    .text();
        }

        if (response.textAnnotations() != null
                && !response.textAnnotations().isEmpty()
                && response.textAnnotations()
                        .getFirst()
                        .description() != null) {

            return response.textAnnotations()
                    .getFirst()
                    .description();
        }

        return "";
    }

    private List<Integer> extractNumbers(
            String text
    ) {
        List<Integer> numbers =
                new ArrayList<>();

        Matcher matcher =
                NUMBER_PATTERN.matcher(text);

        while (matcher.find()) {
            try {
                numbers.add(
                        Integer.parseInt(
                                matcher.group()
                        )
                );
            } catch (NumberFormatException ignored) {
                // Ignore numbers too large for Integer.
            }
        }

        return numbers;
    }

    private Integer chooseSuggestedReading(
            List<Integer> numbers
    ) {
        return numbers.stream()
                .filter(number -> number >= 100)
                .filter(number -> number <= 9_999_999)
                .findFirst()
                .orElse(null);
    }

    public record VisionResponse(
            List<VisionAnnotationResponse> responses
    ) {
    }

    public record VisionAnnotationResponse(
            List<TextAnnotation> textAnnotations,
            FullTextAnnotation fullTextAnnotation,
            VisionError error
    ) {
    }

    public record TextAnnotation(
            String description
    ) {
    }

    public record FullTextAnnotation(
            String text
    ) {
    }

    public record VisionError(
            Integer code,
            String message,
            String status
    ) {
    }
}