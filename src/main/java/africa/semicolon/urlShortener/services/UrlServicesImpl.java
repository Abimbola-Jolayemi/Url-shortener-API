package africa.semicolon.urlShortener.services;

import africa.semicolon.urlShortener.data.models.Url;
import africa.semicolon.urlShortener.data.repositories.UrlRepository;
import africa.semicolon.urlShortener.dtos.GetOriginalUrlResponse;
import africa.semicolon.urlShortener.dtos.UrlRequest;
import africa.semicolon.urlShortener.dtos.UrlResponse;
import africa.semicolon.urlShortener.exceptions.UrlNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UrlServicesImpl implements UrlServices {

    @Autowired
    private UrlRepository urlRepository;

    private static final String BASE_URL = "https://snip.com/";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORTENED_URL_LENGTH = 5;

    private final Random random = new Random();

    @Override
    public UrlResponse shortenUrl(UrlRequest request) {

        UrlResponse response = new UrlResponse();

        Optional<Url> existingUrl = urlRepository.findByOriginalUrl(request.getOriginalUrl());

        if (existingUrl.isPresent()) {
            response.setOriginalUrl(existingUrl.get().getOriginalUrl());
            response.setShortenedUrl(existingUrl.get().getShortenedUrl());
            return response;
        }

        String shortenedUrl = generateUniqueShortenedUrl();
        Url url = new Url();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortenedUrl(BASE_URL + shortenedUrl);

        urlRepository.save(url);

        response.setOriginalUrl(url.getOriginalUrl());
        response.setShortenedUrl(url.getShortenedUrl());

        return response;
    }

    @Override
    public GetOriginalUrlResponse getOriginalUrl(String shortenedUrl){
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

    private String generateUniqueShortenedUrl() {
        String shortenedUrl;
        do {
            shortenedUrl = generateShortenedUrl();
        } while (urlRepository.findByShortenedUrl(shortenedUrl).isPresent());
        return shortenedUrl;
    }

    private String generateShortenedUrl() {
        String randomString = generateRandomString(SHORTENED_URL_LENGTH);
        return formatShortenedUrl(randomString);
    }

    private String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            int characterIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(characterIndex));
        }
        return stringBuilder.toString();
    }

    private String formatShortenedUrl(String generatedString) {
        return "snip-" + generatedString;
    }
}
