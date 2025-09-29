package com.tejusko.user_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.ExpiredJwtException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class JwtUtilTest {

	 @Autowired
	    private JwtUtil jwtUtil; // Spring injects this bean

	    @Test
	    void testGenerateAndValidateToken() {
	        String username = "springuser";

	        // Generate token
	        String token = jwtUtil.generateToken(username);
	        assertNotNull(token);

	        // Validate token
	        String extractedUsername = jwtUtil.validateTokenAndGetUsername(token);
	        assertEquals(username, extractedUsername);

	        // Extract username
	        String extracted2 = jwtUtil.extractUsername(token);
	        assertEquals(username, extracted2);
	    }

	    @Test
	    void testExpiredToken() throws InterruptedException {
	        // For testing expired token, temporarily override expiration in application-test.yml
	        // Or use a separate JwtUtil bean with short expiration
	        // Example: Here we assume jwt.expiration-ms is set very low in application-test.yml

	        String token = jwtUtil.generateToken("expireduser");
	        assertNotNull(token);

	        // Wait to ensure token expires
	        Thread.sleep(5);

	        // Should throw exception
	        assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateTokenAndGetUsername(token));
	    }
}
