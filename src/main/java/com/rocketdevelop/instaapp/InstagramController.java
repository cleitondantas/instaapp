package com.rocketdevelop.instaapp;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instagram")
public class InstagramController {

    private final InstagramService instagramService;

    public InstagramController(InstagramService instagramService) {
        this.instagramService = instagramService;
    }
    @GetMapping("/")
    public ResponseEntity<?> path() {
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }


    @GetMapping("/auth")
    public ResponseEntity<?> auth() {
        String response = instagramService.getAuthUrl();

        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).body(response);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code) {
        String shortToken = instagramService.exchangeShortLivedToken(code);
        String longToken = instagramService.exchangeLongLivedToken(shortToken);
        return "Long-lived token: " + longToken;
    }

    @GetMapping("/medias")
    public JsonNode medias(@RequestParam String token) {
        return instagramService.getRecentMedia(token);
    }

    @GetMapping("/mediaid")
    public JsonNode media(@RequestParam String mediaid, @RequestParam String token) {
        return instagramService.getMediaById(mediaid,token);
    }
}
