package org.example.notificationservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.notificationservice.dto.UserEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    private KafkaConfig kafkaConfig = new KafkaConfig();

    @Test
    void consumerFactory_ShouldBeCreatedWithCorrectProperties() {
        ConsumerFactory<String, UserEventDto> consumerFactory = kafkaConfig.consumerFactory();

        assertNotNull(consumerFactory);

        Map<String, Object> configProps = consumerFactory.getConfigurationProperties();

        assertEquals("localhost:9092", configProps.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("notification-service", configProps.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals(StringDeserializer.class, configProps.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(JsonDeserializer.class, configProps.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals("org.example.notificationservice.dto", configProps.get(JsonDeserializer.TRUSTED_PACKAGES));
        assertEquals("org.example.dto.UserEventDto:org.example.notificationservice.dto.UserEventDto",
                configProps.get(JsonDeserializer.TYPE_MAPPINGS));
    }

    @Test
    void consumerFactory_ShouldReturnNewInstanceEachTime() {
        ConsumerFactory<String, UserEventDto> factory1 = kafkaConfig.consumerFactory();
        ConsumerFactory<String, UserEventDto> factory2 = kafkaConfig.consumerFactory();

        assertNotNull(factory1);
        assertNotNull(factory2);
        assertNotSame(factory1, factory2);
    }

    @Test
    void kafkaListenerContainerFactory_ShouldBeCreated() {
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory =
                kafkaConfig.kafkaListenerContainerFactory();

        assertNotNull(factory);
    }

    @Test
    void kafkaListenerContainerFactory_ShouldUseCorrectConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory =
                kafkaConfig.kafkaListenerContainerFactory();

        assertNotNull(factory.getConsumerFactory());

        ConsumerFactory<String, UserEventDto> expectedConsumerFactory = kafkaConfig.consumerFactory();

        Map<String, Object> factoryConfig = factory.getConsumerFactory().getConfigurationProperties();
        Map<String, Object> expectedConfig = expectedConsumerFactory.getConfigurationProperties();

        assertEquals(expectedConfig.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG),
                factoryConfig.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(expectedConfig.get(ConsumerConfig.GROUP_ID_CONFIG),
                factoryConfig.get(ConsumerConfig.GROUP_ID_CONFIG));
    }

    @Test
    void kafkaListenerContainerFactory_ShouldReturnNewInstanceEachTime() {
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory1 =
                kafkaConfig.kafkaListenerContainerFactory();
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory2 =
                kafkaConfig.kafkaListenerContainerFactory();

        assertNotNull(factory1);
        assertNotNull(factory2);
        assertNotSame(factory1, factory2);
    }

    @Test
    void kafkaListenerContainerFactory_ShouldHaveConsumerFactorySet() {
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory =
                kafkaConfig.kafkaListenerContainerFactory();

        ConsumerFactory<String, UserEventDto> consumerFactory = (ConsumerFactory<String, UserEventDto>) factory.getConsumerFactory();
        assertNotNull(consumerFactory);

        Map<String, Object> configProps = consumerFactory.getConfigurationProperties();
        assertTrue(configProps.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertTrue(configProps.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
    }
}