package com.example.URL_shortener.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
public class ShorteningService {

    private final HashMap<String, String> keyMap;
    private final HashMap<String, String> valueMap;
    private final char[] myChars;
    private final Random myRand;
    private final int keyLength;


    ShorteningService() {
        keyMap = new HashMap<>();
        valueMap = new HashMap<>();
        myRand = new Random();
        keyLength = 6;
        myChars = new char[62];
        for (int i = 0; i < 62; i++) {
            int j;
            if (i < 10) {
                j = i + 48;
            } else if (i <= 35) {
                j = i + 55;
            } else {
                j = i + 61;
            }
            myChars[i] = (char) j;
        }
    }


    public String shortenURL(String longURL) {
        String alias;
        longURL = sanitizeURL(longURL);

        if (valueMap.containsKey(longURL)) {
            alias = valueMap.get(longURL);
        } else {
            alias = getKey(longURL);
        }


        return alias;
    }

    String sanitizeURL(String url) {
        if (url.startsWith("http://"))
            url = url.substring(7);

        if (url.startsWith("https://"))
            url = url.substring(8);

        if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);
        return url;
    }

    private String getKey(String longURL) {
        String key;
        key = generateKey();
        keyMap.put(key, longURL);
        valueMap.put(longURL, key);
        return key;
    }

    private String generateKey() {
        StringBuilder key = new StringBuilder();
        boolean flag = true;
        while (flag) {
            key = new StringBuilder();
            for (int i = 0; i < keyLength; i++) {
                key.append(myChars[myRand.nextInt(62)]);
            }
            if (!keyMap.containsKey(key.toString())) {
                flag = false;
            }
        }
        return key.toString();
    }
}
