package com.rocketdevelop.instaapp;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/instagram")
public class InstagramController {

    private final InstagramService instagramService;
    private String token= null;

    public InstagramController(InstagramService instagramService) {
        this.instagramService = instagramService;
    }
    @GetMapping("/")
    public ResponseEntity<?> path() {
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }


    @GetMapping("/auth")
    public RedirectView auth() {
        System.out.println("Redirecting to Instagram authorization URL");
        String authUrl = instagramService.getAuthUrl();
        return new RedirectView(authUrl);
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam String code) {
        String shortToken = instagramService.exchangeShortLivedToken(code);
        String longToken = instagramService.exchangeLongLivedToken(shortToken);
        System.out.println("Long-lived token: " + longToken);
        return new RedirectView("https://28c0ae96528b.ngrok-free.app/instagram/");
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
