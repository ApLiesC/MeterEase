package com.meterease.backend.service.impl;

import com.meterease.backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiServiceImpl implements GeminiService {

    private final RestClient restClient;
    private final String apiKey;

    public GeminiServiceImpl(
            @Value("${gemini.api.key}")
            String apiKey
    ) {
        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl(
                        "https://generativelanguage.googleapis.com"
                )
                .build();
    }

    @Override
    public String validateReading(
            MultipartFile image,
            String ocrText,
            List<Integer> detectedNumbers
    ) {
        try {
            String base64Image = Base64.getEncoder()
                    .encodeToString(image.getBytes());

            String mimeType = image.getContentType();

            if (mimeType == null) {
                mimeType = "image/jpeg";
            }

            String prompt = """
                    You are validating OCR results from a utility meter image.

                    OCR extracted the following text:

                    %s

                    Detected numbers:

                    %s

                    Identify the actual meter reading shown inside the
                    mechanical or digital meter display.

                    Ignore:
                    - serial numbers
                    - meter identification numbers
                    - manufacture years
                    - dates
                    - voltage values
                    - frequency values
                    - model numbers
                    - calibration values
                    - inspection numbers
                    - printed specifications

                    Use both the original image and the OCR results.

                    Return ONLY the meter reading as an integer.
                    Do not include an explanation.
                    Do not include markdown.
                    """
                    .formatted(
                            ocrText,
                            detectedNumbers
                    );

            Map<String, Object> textPart = Map.of(
                    "text",
                    prompt
            );

            Map<String, Object> imagePart = Map.of(
                    "inline_data",
                    Map.of(
                            "mime_type",
                            mimeType,
                            "data",
                            base64Image
                    )
            );

            Map<String, Object> requestBody = Map.of(
                    "contents",
                    List.of(
                            Map.of(
                                    "parts",
                                    List.of(
                                            textPart,
                                            imagePart
                                    )
                            )
                    )
            );

            GeminiResponse response = restClient
                    .post()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(
                                            "/v1beta/models/"
                                            + "gemini-3.1-flash-lite"
                                            + ":generateContent"
                                    )
                                    .queryParam(
                                            "key",
                                            apiKey
                                    )
                                    .build()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response == null
                    || response.candidates == null
                    || response.candidates.isEmpty()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Gemini returned no response"
                );
            }

            Candidate candidate =
                    response.candidates.getFirst();

            if (candidate.content == null
                    || candidate.content.parts == null
                    || candidate.content.parts.isEmpty()
                    || candidate.content.parts
                            .getFirst().text == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Gemini returned an invalid response"
                );
            }

            return candidate.content
                    .parts
                    .getFirst()
                    .text
                    .trim();

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to read the meter image",
                    exception
            );
        }
    }

    public static class GeminiResponse {
        public List<Candidate> candidates;
    }

    public static class Candidate {
        public Content content;
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }
}