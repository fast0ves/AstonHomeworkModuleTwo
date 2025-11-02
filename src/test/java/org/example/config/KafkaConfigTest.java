package org.example.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.dto.UserEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    private final KafkaConfig kafkaConfig = new KafkaConfig();

    @Test
    void producerFactory_ShouldBeCreatedWithCorrectProperties() {
        ProducerFactory<String, UserEventDto> producerFactory = kafkaConfig.producerFactory();

        assertNotNull(producerFactory);

        Map<String, Object> configProps = producerFactory.getConfigurationProperties();

        assertEquals("localhost:9092", configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, configProps.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(JsonSerializer.class, configProps.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    void producerFactory_ShouldReturnNewInstanceEachTime() {
        ProducerFactory<String, UserEventDto> factory1 = kafkaConfig.producerFactory();
        ProducerFactory<String, UserEventDto> factory2 = kafkaConfig.producerFactory();

        assertNotNull(factory1);
        assertNotNull(factory2);
        assertNotSame(factory1, factory2); // Должны быть разные инстансы
    }

    @Test
    void kafkaTemplate_ShouldBeCreated() {
        KafkaTemplate<String, UserEventDto> kafkaTemplate = kafkaConfig.kafkaTemplate();

        assertNotNull(kafkaTemplate);
    }

    @Test
    void kafkaTemplate_ShouldUseCorrectProducerFactory() {
        KafkaTemplate<String, UserEventDto> kafkaTemplate = kafkaConfig.kafkaTemplate();

        assertNotNull(kafkaTemplate.getProducerFactory());

        ProducerFactory<String, UserEventDto> expectedProducerFactory = kafkaConfig.producerFactory();

        // Проверяем, что KafkaTemplate использует producerFactory с правильными настройками
        Map<String, Object> templateConfig = kafkaTemplate.getProducerFactory().getConfigurationProperties();
        Map<String, Object> expectedConfig = expectedProducerFactory.getConfigurationProperties();

        assertEquals(expectedConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG),
                templateConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(expectedConfig.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG),
                templateConfig.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(expectedConfig.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG),
                templateConfig.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    void kafkaTemplate_ShouldReturnNewInstanceEachTime() {
        KafkaTemplate<String, UserEventDto> template1 = kafkaConfig.kafkaTemplate();
        KafkaTemplate<String, UserEventDto> template2 = kafkaConfig.kafkaTemplate();

        assertNotNull(template1);
        assertNotNull(template2);
        assertNotSame(template1, template2); // Должны быть разные инстансы
    }

    @Test
    void kafkaTemplate_ShouldHaveProducerFactorySet() {
        KafkaTemplate<String, UserEventDto> kafkaTemplate = kafkaConfig.kafkaTemplate();

        ProducerFactory<String, UserEventDto> producerFactory = kafkaTemplate.getProducerFactory();
        assertNotNull(producerFactory);

        // Проверяем, что producerFactory правильно настроен
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        assertTrue(configProps.containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    void producerFactory_Configuration_ShouldContainAllRequiredProperties() {
        ProducerFactory<String, UserEventDto> producerFactory = kafkaConfig.producerFactory();
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();

        assertEquals(3, configProps.size(), "Should contain exactly 3 configuration properties");
        assertTrue(configProps.containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }
}