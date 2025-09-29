package com.tejusko.user_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; 

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

//	 @Test
//	 void testExpiredToken() throws InterruptedException {
//	     // The @ActiveProfiles("test") annotation ensures the application-test.yml 
//	     // with the 10ms expiration is loaded.
//
//	     String token = jwtUtil.generateToken("expireduser");
//	     assertNotNull(token);
//
//	     // **Increasing the sleep time to 300ms.**
//	     // This drastically increases the chance that the 10ms token has expired, 
//	     // eliminating race conditions caused by thread scheduling.
//	     Thread.sleep(300); 
//
//	     // Should throw ExpiredJwtException
//	     assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateTokenAndGetUsername(token));
//	 }
}
