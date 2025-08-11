package com.rocketdevelop.instaapp;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class InstagramService {

    @Value("${instagram.client.id}")
    private String clientId;

    @Value("${instagram.client.secret}")
    private String clientSecret;

    @Value("${instagram.redirect.uri}")
    private String redirectUri;

    @Value("${instagram.scope}")
    private String scope;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAuthUrl() {
        return UriComponentsBuilder
                .fromUriString("https://api.instagram.com/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", scope)
                .queryParam("response_type", "code")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }

    public String exchangeShortLivedToken(String code) {
        String url = "https://api.instagram.com/oauth/access_token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        return response.getBody().get("access_token").asText();
    }

    public String exchangeLongLivedToken(String shortToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://graph.instagram.com/access_token")
                .queryParam("grant_type", "ig_exchange_token")
                .queryParam("client_secret", clientSecret)
                .queryParam("access_token", shortToken)
                .build()
                .toUri();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(uri, JsonNode.class);

        return response.getBody().get("access_token").asText();
    }

    public JsonNode getRecentMedia(String longToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://graph.instagram.com/me/media")
                .queryParam("fields", "id,caption,media_type,media_url,permalink,thumbnail_url,timestamp")
                .queryParam("access_token", longToken)
                .build()
                .toUri();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(uri, JsonNode.class);

        return response.getBody().get("data");
    }

    public JsonNode getMediaById(String mediaid, String longToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://graph.instagram.com/" + mediaid)
                .queryParam("fields","id,media_type,media_url,caption,like_count,video_views")
                .queryParam("access_token", longToken)
                .build()
                .toUri();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(uri, JsonNode.class);

        return response.getBody();
    }
}