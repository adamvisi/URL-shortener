package com.example.URL_shortener.controller;

import com.example.URL_shortener.dto.URLShortenRequest;
import com.example.URL_shortener.service.URLService;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;

@RestController
@RequestMapping
public class URLController {

    private URLService urlService;

    @Autowired
    public URLController(URLService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/create-short")
    public ResponseEntity<?> createShortURL(@Valid @RequestBody URLShortenRequest request) {
        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(request.getUrl())) {
            return ResponseEntity.ok(urlService.convertToShortUrl(request));
        } else {
            throw new IllegalArgumentException("Entered URL is invalid");
        }
    }

    @GetMapping("/{alias}")
    public ResponseEntity<?> handleRedirect(@PathVariable String alias)
            throws URISyntaxException, EntityNotFoundException, EntityExistsException {
        URI uri = new URI(urlService.getOriginalUrl(alias)).parseServerAuthority();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);

        return new ResponseEntity<>(httpHeaders, MOVED_PERMANENTLY);
    }

}
