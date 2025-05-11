package africa.semicolon.urlShortener.services;

import africa.semicolon.urlShortener.data.models.Url;
import africa.semicolon.urlShortener.data.repositories.UrlRepository;
import africa.semicolon.urlShortener.dtos.GetOriginalUrlResponse;
import africa.semicolon.urlShortener.dtos.UrlRequest;
import africa.semicolon.urlShortener.dtos.UrlResponse;
import africa.semicolon.urlShortener.exceptions.UrlNotFoundException;
import africa.semicolon.urlShortener.services.UrlServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.Optional;

@Service
public class UrlServicesImpl implements UrlServices {

    @Autowired
    private UrlRepository urlRepository;

    private static final String BITLY_ACCESS_TOKEN = "dcc0aaf9ad6841d14821198a6b4844c5590bb636";
    private static final String BITLY_API_URL = "https://api-ssl.bitly.com/v4/shorten";

    @Override
    public UrlResponse shortenUrl(UrlRequest request) {
        UrlResponse response = new UrlResponse();

        Optional<Url> existingUrl = urlRepository.findByOriginalUrl(request.getOriginalUrl());
        if (existingUrl.isPresent()) {
            response.setOriginalUrl(existingUrl.get().getOriginalUrl());
            response.setShortenedUrl(existingUrl.get().getShortenedUrl());
            return response;
        }

        String shortenedUrl = callBitlyApi(request.getOriginalUrl());
        Url url = new Url();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortenedUrl(shortenedUrl);

        urlRepository.save(url);

        response.setOriginalUrl(url.getOriginalUrl());
        response.setShortenedUrl(url.getShortenedUrl());

        return response;
    }

    private String callBitlyApi(String longUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + BITLY_ACCESS_TOKEN);
            headers.set("Content-Type", "application/json");

            JSONObject payload = new JSONObject();
            payload.put("long_url", longUrl);

            HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(BITLY_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject responseBody = new JSONObject(response.getBody());
                return responseBody.getString("id");
            } else {
                throw new RuntimeException("Failed to shorten URL: " + response.getStatusCode());
            }
        } catch (Exception exception) {
            throw new RuntimeException("Error during Bitly API call", exception);
        }
    }

    @Override
    public GetOriginalUrlResponse getOriginalUrl(String shortenedUrl) {
        Optional<Url> foundUrl = urlRepository.findByShortenedUrl(shortenedUrl);
        Url url = foundUrl.orElseThrow(() -> new UrlNotFoundException("Shortened URL not found: " + shortenedUrl));

        GetOriginalUrlResponse response = new GetOriginalUrlResponse();
        response.setOriginalUrl(url.getOriginalUrl());
        return response;
    }

    @Override
    public String getOriginalUrlForRedirection(String shortenedUrl) {
        Url url = urlRepository.findByShortenedUrl(shortenedUrl)
                .orElseThrow(() -> new UrlNotFoundException("Shortened URL not found"));
        return url.getOriginalUrl();
    }
}
