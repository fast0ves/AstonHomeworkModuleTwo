package org.example.notificationservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEventDtoTest {

    @Test
    void noArgsConstructor_CreatesEmptyObject() {
        UserEventDto dto = new UserEventDto();

        assertNotNull(dto);
        assertNull(dto.getOperation());
        assertNull(dto.getEmail());
        assertNull(dto.getUserName());
    }

    @Test
    void allArgsConstructor_CreatesObjectWithValues() {
        String operation = "CREATE";
        String email = "test@example.com";
        String userName = "John Doe";

        UserEventDto dto = new UserEventDto(operation, email, userName);

        assertNotNull(dto);
        assertEquals(operation, dto.getOperation());
        assertEquals(email, dto.getEmail());
        assertEquals(userName, dto.getUserName());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        UserEventDto dto = new UserEventDto();

        String operation = "DELETE";
        String email = "user@test.com";
        String userName = "Jane Smith";

        dto.setOperation(operation);
        dto.setEmail(email);
        dto.setUserName(userName);

        assertEquals(operation, dto.getOperation());
        assertEquals(email, dto.getEmail());
        assertEquals(userName, dto.getUserName());
    }

    @Test
    void settersAndGetters_WithNullValues_WorkCorrectly() {
        UserEventDto dto = new UserEventDto("CREATE", "test@example.com", "John Doe");

        dto.setOperation(null);
        dto.setEmail(null);
        dto.setUserName(null);

        assertNull(dto.getOperation());
        assertNull(dto.getEmail());
        assertNull(dto.getUserName());
    }

    @Test
    void allArgsConstructor_WithNullValues_CreatesObject() {
        UserEventDto dto = new UserEventDto(null, null, null);

        assertNotNull(dto);
        assertNull(dto.getOperation());
        assertNull(dto.getEmail());
        assertNull(dto.getUserName());
    }

    @Test
    void allArgsConstructor_WithEmptyStrings_CreatesObject() {
        UserEventDto dto = new UserEventDto("", "", "");

        assertNotNull(dto);
        assertEquals("", dto.getOperation());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getUserName());
    }

    @Test
    void setters_WithEmptyStrings_WorkCorrectly() {
        UserEventDto dto = new UserEventDto();

        dto.setOperation("");
        dto.setEmail("");
        dto.setUserName("");

        assertEquals("", dto.getOperation());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getUserName());
    }

    @Test
    void objectCanBeReusedWithDifferentValues() {
        UserEventDto dto = new UserEventDto("CREATE", "old@example.com", "Old Name");

        assertEquals("CREATE", dto.getOperation());
        assertEquals("old@example.com", dto.getEmail());
        assertEquals("Old Name", dto.getUserName());

        dto.setOperation("UPDATE");
        dto.setEmail("new@example.com");
        dto.setUserName("New Name");

        assertEquals("UPDATE", dto.getOperation());
        assertEquals("new@example.com", dto.getEmail());
        assertEquals("New Name", dto.getUserName());
    }
}