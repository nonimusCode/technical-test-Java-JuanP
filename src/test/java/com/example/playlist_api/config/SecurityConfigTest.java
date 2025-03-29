package com.example.playlist_api.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accessProtectedResourceWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/lists"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedResourceWithValidCredentials_Ok() throws Exception {
        mockMvc.perform(get("/lists")
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void accessProtectedResourceWithInvalidCredentials_Unauthorized() throws Exception {
        mockMvc.perform(get("/lists")
                .with(httpBasic("user", "wrongpassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessH2Console_NotFound() throws Exception {
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isNotFound()); // Debería ser permitido pero la ruta exacta no existe
    }
}