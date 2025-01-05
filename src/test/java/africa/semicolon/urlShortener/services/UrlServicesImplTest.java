package africa.semicolon.urlShortener.services;

import africa.semicolon.urlShortener.data.models.Url;
import africa.semicolon.urlShortener.data.repositories.UrlRepository;
import africa.semicolon.urlShortener.dtos.GetOriginalUrlResponse;
import africa.semicolon.urlShortener.dtos.UrlRequest;
import africa.semicolon.urlShortener.dtos.UrlResponse;
import africa.semicolon.urlShortener.exceptions.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UrlServicesImplTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServicesImpl urlServices;

    private UrlRequest urlRequest;
    private Url existingUrl;
    private UrlResponse urlResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        urlRequest = new UrlRequest();
        urlRequest.setOriginalUrl("http://example.com");

        existingUrl = new Url();
        existingUrl.setId(1L); // Mock the primary key
        existingUrl.setOriginalUrl("http://example.com");
        existingUrl.setShortenedUrl("http://snip.com/snip-12345");

        urlResponse = new UrlResponse();
        urlResponse.setOriginalUrl(existingUrl.getOriginalUrl());
        urlResponse.setShortenedUrl(existingUrl.getShortenedUrl());
    }

    @Test
    void shortenUrl_ShouldReturnExistingShortenedUrl_WhenUrlAlreadyExists() {
        when(urlRepository.findByOriginalUrl(urlRequest.getOriginalUrl())).thenReturn(Optional.of(existingUrl));
        UrlResponse response = urlServices.shortenUrl(urlRequest);

        assertEquals(existingUrl.getOriginalUrl(), response.getOriginalUrl());
        assertEquals(existingUrl.getShortenedUrl(), response.getShortenedUrl());
    }

    @Test
    void shortenUrl_ShouldGenerateNewShortenedUrl_WhenUrlIsNew() {
        when(urlRepository.findByOriginalUrl(urlRequest.getOriginalUrl())).thenReturn(Optional.empty());

        UrlResponse response = urlServices.shortenUrl(urlRequest);

        assertNotNull(response.getShortenedUrl());
        assertNotEquals(urlRequest.getOriginalUrl(), response.getShortenedUrl());
    }

    @Test
    void getOriginalUrl_ShouldReturnOriginalUrl_WhenShortenedUrlExists() {
        when(urlRepository.findByShortenedUrl("http://snip.com/snip-12345")).thenReturn(Optional.of(existingUrl));

        GetOriginalUrlResponse response = urlServices.getOriginalUrl("http://snip.com/snip-12345");

        assertEquals(existingUrl.getOriginalUrl(), response.getOriginalUrl());
    }

    @Test
    void getOriginalUrl_ShouldThrowException_WhenShortenedUrlDoesNotExist() {
        when(urlRepository.findByShortenedUrl("http://snip.com/snip-12345")).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> urlServices.getOriginalUrl("http://snip.com/snip-12345"));
    }
}
