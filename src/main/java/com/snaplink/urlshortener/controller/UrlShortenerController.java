package com.snaplink.urlshortener.controller;

import com.snaplink.urlshortener.model.ShortUrl;
import com.snaplink.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @Autowired
    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    // Shorten URL - Accept JSON Body
    @PostMapping("/shorten")
    public ResponseEntity<ShortUrl> shortenUrl(@RequestBody ShortUrl request) {
        if (request.getLongUrl() == null || request.getUserId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        ShortUrl shortUrl = urlShortenerService.createShortUrl(
                request.getLongUrl(),
                request.getUserId(),
                request.isOneTime(),
                request.getExpirationDate()
        );

        return ResponseEntity.ok(shortUrl);
    }

    // Retrieve Shortened URL
//    @GetMapping("/{shortCode}")
//    public ResponseEntity<ShortUrl> getShortUrl(@PathVariable String shortCode) {
//        ShortUrl url = urlShortenerService.getShortUrl(shortCode);
//        if (url == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(url);
//    }
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortCode) {
        ShortUrl url = urlShortenerService.getShortUrl(shortCode);

        if (url == null || !url.isActive()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(302)
                .header("Location", url.getLongUrl())
                .build();
    }


    // Delete Shortened URL
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<String> deleteShortUrl(@PathVariable String shortCode) {
        urlShortenerService.deleteShortUrl(shortCode);
        return ResponseEntity.ok("Short URL deleted successfully.");
    }
}
