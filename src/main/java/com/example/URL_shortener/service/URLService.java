package com.example.URL_shortener.service;

import com.example.URL_shortener.dto.URLShortenRequest;
import com.example.URL_shortener.entity.URLEntity;
import com.example.URL_shortener.repository.URLRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Date;

@Service
public class URLService {

    private final URLRepository urlRepository;
    private final ShorteningService shorteningService;

    public URLService(URLRepository urlRepository, ShorteningService shorteningService) {
        this.urlRepository = urlRepository;
        this.shorteningService = shorteningService;
    }

    public String convertToShortUrl(URLShortenRequest request) {

        URLEntity url = new URLEntity();
        url.setOriginalUrl(request.getUrl());
        url.setExpiresDate(request.getExpirationDate());
        url.setCreationDate(new Date());
        generateAliasIfNoSeoKeyWord(request, url);

        if (urlRepository.existsByAlias(url.getAlias())) {
            throw new EntityExistsException("Shortened URL already exists!");
        }

        urlRepository.save(url);

        return url.getAlias();
    }

    private void generateAliasIfNoSeoKeyWord(URLShortenRequest request, URLEntity url) {
        if (null == request.getSeoKeyword()) {
            url.setAlias(shorteningService.shortenURL(request.getUrl()));
        } else {
            url.setAlias(request.getSeoKeyword());
        }
    }

    public String getOriginalUrl(String alias) {
        URLEntity entity = urlRepository.findByAlias(alias)
                .orElseThrow(() -> new EntityNotFoundException("There is no entity with " + alias));

        if (entity.getExpiresDate() != null && entity.getExpiresDate().before(new Date())){
            urlRepository.delete(entity);
            throw new EntityNotFoundException("Link expired! A new shortened URL must be created!");
        }

        return entity.getOriginalUrl();
    }

}
