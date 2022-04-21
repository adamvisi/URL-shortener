package com.example.URL_shortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.example.URL_shortener.service.URLServiceTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class URLControllerIntegrationTest {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;


    @Test
    public void createShortURL_then_handleRedirect() throws Exception {
        MvcResult postResult = mockMvc.perform(
                        post("/create-short")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createUrlShortenRequest(ORIGINAL_URL, EXPIRATION_DATE, null))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();


        assertEquals(6, postResult.getResponse().getContentAsString().length());

        String alias = postResult.getResponse().getContentAsString();

        MvcResult getResult = mockMvc.perform(
                        get("/" + alias)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.MOVED_PERMANENTLY.value()))
                .andReturn();

        assertEquals(ORIGINAL_URL, getResult.getResponse().getHeader("Location"));
    }

}