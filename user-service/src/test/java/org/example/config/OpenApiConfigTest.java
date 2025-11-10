package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class OpenApiConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void openApiBeanShouldBeCreated() {
        OpenAPI openAPI = applicationContext.getBean(OpenAPI.class);
        assertThat(openAPI).isNotNull();

        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("User Service API");
        assertThat(openAPI.getInfo().getDescription()).contains("REST API для управления пользователями");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }
}
