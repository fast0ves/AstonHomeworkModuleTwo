package org.example.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEventDtoTest {

    @Test
    void noArgsConstructor_CreatesObjectWithNullFields() {
        UserEventDto dto = new UserEventDto();

        assertNotNull(dto);
        assertNull(dto.getOperation());
        assertNull(dto.getEmail());
        assertNull(dto.getUserName());
    }

    @Test
    void allArgsConstructor_CreatesObjectWithProvidedValues() {
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
    void allArgsConstructor_WithNullValues_CreatesObject() {
        UserEventDto dto = new UserEventDto(null, null, null);

        // Then
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
    void setOperation_UpdatesOperationField() {
        UserEventDto dto = new UserEventDto();
        String operation = "DELETE";

        dto.setOperation(operation);

        assertEquals(operation, dto.getOperation());
    }

    @Test
    void setOperation_WithNullValue_UpdatesOperationField() {
        UserEventDto dto = new UserEventDto("CREATE", "test@example.com", "John");

        dto.setOperation(null);

        assertNull(dto.getOperation());
    }

    @Test
    void setEmail_UpdatesEmailField() {
        UserEventDto dto = new UserEventDto();
        String email = "new@example.com";

        dto.setEmail(email);

        assertEquals(email, dto.getEmail());
    }

    @Test
    void setEmail_WithNullValue_UpdatesEmailField() {
        UserEventDto dto = new UserEventDto("CREATE", "test@example.com", "John");

        dto.setEmail(null);

        assertNull(dto.getEmail());
    }

    @Test
    void setUserName_UpdatesUserNameField() {
        UserEventDto dto = new UserEventDto();
        String userName = "Jane Smith";

        dto.setUserName(userName);

        assertEquals(userName, dto.getUserName());
    }

    @Test
    void setUserName_WithNullValue_UpdatesUserNameField() {
        UserEventDto dto = new UserEventDto("CREATE", "test@example.com", "John");

        dto.setUserName(null);

        assertNull(dto.getUserName());
    }

    @Test
    void getOperation_ReturnsSetValue() {
        UserEventDto dto = new UserEventDto();
        String operation = "UPDATE";
        dto.setOperation(operation);

        String result = dto.getOperation();

        assertEquals(operation, result);
    }

    @Test
    void getEmail_ReturnsSetValue() {
        UserEventDto dto = new UserEventDto();
        String email = "user@domain.com";
        dto.setEmail(email);

        String result = dto.getEmail();

        assertEquals(email, result);
    }

    @Test
    void getUserName_ReturnsSetValue() {
        UserEventDto dto = new UserEventDto();
        String userName = "Alice Johnson";
        dto.setUserName(userName);

        String result = dto.getUserName();

        assertEquals(userName, result);
    }

    @Test
    void objectStateCanBeFullyModified() {
        UserEventDto dto = new UserEventDto("CREATE", "old@example.com", "Old Name");

        assertEquals("CREATE", dto.getOperation());
        assertEquals("old@example.com", dto.getEmail());
        assertEquals("Old Name", dto.getUserName());

        dto.setOperation("DELETE");
        dto.setEmail("new@example.com");
        dto.setUserName("New Name");

        assertEquals("DELETE", dto.getOperation());
        assertEquals("new@example.com", dto.getEmail());
        assertEquals("New Name", dto.getUserName());
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
    void fieldsCanBeSetIndependently() {
        UserEventDto dto = new UserEventDto();

        dto.setOperation("CREATE");

        assertEquals("CREATE", dto.getOperation());
        assertNull(dto.getEmail());
        assertNull(dto.getUserName());

        dto.setEmail("test@example.com");

        assertEquals("CREATE", dto.getOperation());
        assertEquals("test@example.com", dto.getEmail());
        assertNull(dto.getUserName());

        dto.setUserName("John Doe");

        assertEquals("CREATE", dto.getOperation());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("John Doe", dto.getUserName());
    }
}