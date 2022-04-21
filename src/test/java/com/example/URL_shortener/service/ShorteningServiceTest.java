package com.example.URL_shortener.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.example.URL_shortener.service.URLServiceTest.ORIGINAL_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(PowerMockRunner.class)
class ShorteningServiceTest {

    @Test
    public void shortenURL() {
        ShorteningService shorteningService = new ShorteningService();

        assertEquals(6, shorteningService.shortenURL(ORIGINAL_URL).length());
    }

    @Test
    public void shortenURL_keyAlreadyExists() {
        ShorteningService shorteningService = new ShorteningService();

        String expected = shorteningService.shortenURL(ORIGINAL_URL);

        assertEquals(expected, shorteningService.shortenURL(ORIGINAL_URL));
    }

}