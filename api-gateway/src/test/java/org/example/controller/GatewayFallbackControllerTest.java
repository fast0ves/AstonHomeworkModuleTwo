package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GatewayFallbackControllerUnitTest {

    private final GatewayFallbackController controller = new GatewayFallbackController();

    @Test
    void userServiceFallback_ShouldReturnCorrectResponse() {
        ResponseEntity<Map<String, String>> response = controller.userServiceFallback();

        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, String> body = response.getBody();
        assertEquals("User service is temporarily unavailable", body.get("message"));
        assertEquals("SERVICE_UNAVAILABLE", body.get("status"));
        assertEquals(2, body.size());
    }

    @Test
    void notificationServiceFallback_ShouldReturnCorrectResponse() {
        ResponseEntity<Map<String, String>> response = controller.notificationServiceFallback();

        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, String> body = response.getBody();
        assertEquals("Notification service is temporarily unavailable", body.get("message"));
        assertEquals("SERVICE_UNAVAILABLE", body.get("status"));
        assertEquals(2, body.size());
    }

    @Test
    void userServiceFallback_ShouldHaveServiceUnavailableStatus() {
        ResponseEntity<Map<String, String>> response = controller.userServiceFallback();

        assertTrue(response.getStatusCode().is5xxServerError());
        assertEquals(503, response.getStatusCodeValue());
    }

    @Test
    void notificationServiceFallback_ShouldHaveServiceUnavailableStatus() {
        ResponseEntity<Map<String, String>> response = controller.notificationServiceFallback();

        assertTrue(response.getStatusCode().is5xxServerError());
        assertEquals(503, response.getStatusCodeValue());
    }

    @Test
    void fallbackResponses_ShouldContainExpectedFields() {
        ResponseEntity<Map<String, String>> userResponse = controller.userServiceFallback();
        Map<String, String> userBody = userResponse.getBody();

        assertTrue(userBody.containsKey("message"));
        assertTrue(userBody.containsKey("status"));
        assertEquals("User service is temporarily unavailable", userBody.get("message"));

        ResponseEntity<Map<String, String>> notificationResponse = controller.notificationServiceFallback();
        Map<String, String> notificationBody = notificationResponse.getBody();

        assertTrue(notificationBody.containsKey("message"));
        assertTrue(notificationBody.containsKey("status"));
        assertEquals("Notification service is temporarily unavailable", notificationBody.get("message"));
    }
}