package africa.semicolon.urlShortener.controllers;

import africa.semicolon.urlShortener.dtos.GetOriginalUrlResponse;
import africa.semicolon.urlShortener.dtos.UrlRequest;
import africa.semicolon.urlShortener.dtos.UrlResponse;
import africa.semicolon.urlShortener.exceptions.UrlNotFoundException;
import africa.semicolon.urlShortener.services.UrlServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/url")
public class UrlController {

    private final UrlServices urlServices;

    @Autowired
    public UrlController(UrlServices urlServices) {
        this.urlServices = urlServices;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody UrlRequest request) {
        try {
            UrlResponse urlResponse = urlServices.shortenUrl(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(urlResponse);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @GetMapping("/original")
    public ResponseEntity<?> getOriginalUrl(@RequestParam("shortenedUrl") String shortenedUrl) {
        try {
            GetOriginalUrlResponse response = urlServices.getOriginalUrl(shortenedUrl);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UrlNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @GetMapping("/{shortenedUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortenedUrl, HttpServletResponse response) {
        try {
            String originalUrl = urlServices.getOriginalUrlForRedirection(shortenedUrl);

            if (originalUrl != null) {
                response.setHeader("Location", originalUrl);
                return ResponseEntity.status(HttpStatus.FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

