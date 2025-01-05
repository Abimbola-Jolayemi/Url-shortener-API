package africa.semicolon.urlShortener.services;

import africa.semicolon.urlShortener.dtos.GetOriginalUrlResponse;
import africa.semicolon.urlShortener.dtos.UrlRequest;
import africa.semicolon.urlShortener.dtos.UrlResponse;

public interface UrlServices {
    UrlResponse shortenUrl(UrlRequest request);
    GetOriginalUrlResponse getOriginalUrl(String shortUrl);
    String getOriginalUrlForRedirection(String shortenedUrl);
}
