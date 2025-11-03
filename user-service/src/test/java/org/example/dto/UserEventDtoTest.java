package org.example.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEventDtoTest {

    @Test
    void noArgsConstructor_CreatesObjectWithNullFields() {
        // When
        UserEventDto dto = new UserEventDto();

        // Then
        assertNotNull(dto);
        assertNull(dto.getOperation());
        assertNull(dto.getEmail());
        assertNull(dto.getUserName());
    }

    @Test
    void allArgsConstructor_CreatesObjectWithProvidedValues() {
        // Given
        String operation = "CREATE";
        String email = "test@example.com";
        String userName = "John Doe";

        // When
        UserEventDto dto = new UserEventDto(operation, email, userName);

        // Then
        assertNotNull(dto);
        assertEquals(operation, dto.getOperation());
        assertEquals(email, dto.getEmail());
        assertEquals(userName, dto.getUserName());
    }

    @Test
    void allArgsConstructor_WithNullValues_CreatesObject() {
        // When
        UserEventDto dto = new UserEventDto(null, null, null);

        // Then
        assertNotNull(dto);
        assertNull(dto.getOperation());
        assertNull(dto.getEmail());
        assertNull(dto.getUserName());
    }

    @Test
    void allArgsConstructor_WithEmptyStrings_CreatesObject() {
        // When
        UserEventDto dto = new UserEventDto("", "", "");

        // Then
        assertNotNull(dto);
        assertEquals("", dto.getOperation());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getUserName());
    }

    @Test
    void setOperation_UpdatesOperationField() {
        // Given
        UserEventDto dto = new UserEventDto();
        String operation = "DELETE";

        // When
        dto.setOperation(operation);

        // Then
        assertEquals(operation, dto.getOperation());
    }

    @Test
    void setOperation_WithNullValue_UpdatesOperationField() {
        // Given
        UserEventDto dto = new UserEventDto("CREATE", "test@example.com", "John");

        // When
        dto.setOperation(null);

        // Then
        assertNull(dto.getOperation());
    }

    @Test
    void setEmail_UpdatesEmailField() {
        // Given
        UserEventDto dto = new UserEventDto();
        String email = "new@example.com";

        // When
        dto.setEmail(email);

        // Then
        assertEquals(email, dto.getEmail());
    }

    @Test
    void setEmail_WithNullValue_UpdatesEmailField() {
        // Given
        UserEventDto dto = new UserEventDto("CREATE", "test@example.com", "John");

        // When
        dto.setEmail(null);

        // Then
        assertNull(dto.getEmail());
    }

    @Test
    void setUserName_UpdatesUserNameField() {
        // Given
        UserEventDto dto = new UserEventDto();
        String userName = "Jane Smith";

        // When
        dto.setUserName(userName);

        // Then
        assertEquals(userName, dto.getUserName());
    }

    @Test
    void setUserName_WithNullValue_UpdatesUserNameField() {
        // Given
        UserEventDto dto = new UserEventDto("CREATE", "test@example.com", "John");

        // When
        dto.setUserName(null);

        // Then
        assertNull(dto.getUserName());
    }

    @Test
    void getOperation_ReturnsSetValue() {
        // Given
        UserEventDto dto = new UserEventDto();
        String operation = "UPDATE";
        dto.setOperation(operation);

        // When
        String result = dto.getOperation();

        // Then
        assertEquals(operation, result);
    }

    @Test
    void getEmail_ReturnsSetValue() {
        // Given
        UserEventDto dto = new UserEventDto();
        String email = "user@domain.com";
        dto.setEmail(email);

        // When
        String result = dto.getEmail();

        // Then
        assertEquals(email, result);
    }

    @Test
    void getUserName_ReturnsSetValue() {
        // Given
        UserEventDto dto = new UserEventDto();
        String userName = "Alice Johnson";
        dto.setUserName(userName);

        // When
        String result = dto.getUserName();

        // Then
        assertEquals(userName, result);
    }

    @Test
    void objectStateCanBeFullyModified() {
        // Given
        UserEventDto dto = new UserEventDto("CREATE", "old@example.com", "Old Name");

        // Verify initial state
        assertEquals("CREATE", dto.getOperation());
        assertEquals("old@example.com", dto.getEmail());
        assertEquals("Old Name", dto.getUserName());

        // When - modify all fields
        dto.setOperation("DELETE");
        dto.setEmail("new@example.com");
        dto.setUserName("New Name");

        // Then - verify new state
        assertEquals("DELETE", dto.getOperation());
        assertEquals("new@example.com", dto.getEmail());
        assertEquals("New Name", dto.getUserName());
    }

    @Test
    void setters_WithEmptyStrings_WorkCorrectly() {
        // Given
        UserEventDto dto = new UserEventDto();

        // When
        dto.setOperation("");
        dto.setEmail("");
        dto.setUserName("");

        // Then
        assertEquals("", dto.getOperation());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getUserName());
    }

    @Test
    void fieldsCanBeSetIndependently() {
        // Given
        UserEventDto dto = new UserEventDto();

        // When - set only operation
        dto.setOperation("CREATE");

        // Then
        assertEquals("CREATE", dto.getOperation());
        assertNull(dto.getEmail());
        assertNull(dto.getUserName());

        // When - set only email
        dto.setEmail("test@example.com");

        // Then
        assertEquals("CREATE", dto.getOperation());
        assertEquals("test@example.com", dto.getEmail());
        assertNull(dto.getUserName());

        // When - set only userName
        dto.setUserName("John Doe");

        // Then
        assertEquals("CREATE", dto.getOperation());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("John Doe", dto.getUserName());
    }
}