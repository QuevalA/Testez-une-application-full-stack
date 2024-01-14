package com.openclassrooms.starterjwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public abstract class BaseIntegrationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    protected Environment environment;

    public ResultActions performLogin() throws Exception {
        String email = environment.getProperty("TEST_ADMIN_USER_EMAIL");
        String password = environment.getProperty("TEST_ADMIN_USER_PASSWORD");

        // Prepare login request payload
        String loginPayload = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        // Perform authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType("application/json")
                .content(loginPayload));
    }
}
