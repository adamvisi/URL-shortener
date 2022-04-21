package com.example.URL_shortener.service;

import com.example.URL_shortener.dto.URLShortenRequest;
import com.example.URL_shortener.entity.URLEntity;
import com.example.URL_shortener.repository.URLRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@ExtendWith(MockitoExtension.class)
public class URLServiceTest {

    public static final String ORIGINAL_URL = "https://www.youtube.com";
    public static final String ALIAS = "myLovelyPage";
    public static final Date EXPIRATION_DATE = new Date(129, 10, 10, 10, 10, 10);

    @Mock
    URLRepository urlRepository;

    @Mock
    ShorteningService shorteningService;

    @InjectMocks
    private URLService urlService;

    @Before("")
    public void setup() {
        urlService = new URLService(urlRepository, shorteningService);
    }

    @Test
    public void convertToShortUrl_noSeoKeywordProvided() {
        URLShortenRequest urlShortenRequest = createUrlShortenRequest(
                ORIGINAL_URL,
                EXPIRATION_DATE,
                null);

        when(urlRepository.existsByAlias(anyString())).thenReturn(false);
        when(urlRepository.save(any())).thenReturn(null);
        when(shorteningService.shortenURL(anyString())).thenReturn("asdfgh");

        String actual = urlService.convertToShortUrl(urlShortenRequest);

        assertEquals("asdfgh", actual);
    }

    public static URLShortenRequest createUrlShortenRequest(String url, Date expirationDate, String seoKeyword) {
        URLShortenRequest urlShortenRequest = new URLShortenRequest();

        urlShortenRequest.setUrl(url);
        urlShortenRequest.setExpirationDate(expirationDate);
        urlShortenRequest.setSeoKeyword(seoKeyword);

        return urlShortenRequest;
    }

    @Test
    public void convertToShortUrl_seoKeywordProvided() {
        URLShortenRequest urlShortenRequest = createUrlShortenRequest(
                ORIGINAL_URL,
                EXPIRATION_DATE,
                ALIAS);

        when(urlRepository.existsByAlias(anyString())).thenReturn(false);
        when(urlRepository.save(any())).thenReturn(null);

        String actual = urlService.convertToShortUrl(urlShortenRequest);

        assertEquals(ALIAS, actual);

        verify(shorteningService, times(0)).shortenURL(any());
    }

    @Test
    public void convertToShortUrl_seoKeywordAlreadyExists() {
        when(urlRepository.existsByAlias(anyString())).thenReturn(true);

        Exception exception = assertThrows(EntityExistsException.class, () -> {
            URLShortenRequest urlShortenRequest = createUrlShortenRequest(
                    ORIGINAL_URL,
                    EXPIRATION_DATE,
                    ALIAS);

            urlService.convertToShortUrl(urlShortenRequest);
        });

        String expectedMessage = "Shortened URL already exists!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getOriginalUrl() {
        URLEntity urlEntity = new URLEntity();
        urlEntity.setOriginalUrl(ORIGINAL_URL);
        urlEntity.setAlias(ALIAS);
        urlEntity.setExpiresDate(EXPIRATION_DATE);

        when(urlRepository.findByAlias(any())).thenReturn(Optional.of(urlEntity));

        assertEquals(ORIGINAL_URL, urlService.getOriginalUrl(ALIAS));
    }

    @Test
    public void getOriginalUrl_linkExpired() {
        URLEntity urlEntity = new URLEntity();
        urlEntity.setOriginalUrl(ORIGINAL_URL);
        urlEntity.setAlias(ALIAS);
        urlEntity.setExpiresDate(new Date(11, 10, 10, 10, 10, 10));

        when(urlRepository.findByAlias(any())).thenReturn(Optional.of(urlEntity));
        doNothing().when(urlRepository).delete(any());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            urlService.getOriginalUrl(ALIAS);
        });

        String expectedMessage = "Link expired! A new shortened URL must be created!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));


    }

}