package org.example.notificationservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.notificationservice.dto.UserEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@TestConfiguration
@Profile("test")
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

    @Bean
    public ProducerFactory<String, UserEventDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, UserEventDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, UserEventDto> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service-test");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "org.example.notificationservice.dto");
        configProps.put(JsonDeserializer.TYPE_MAPPINGS, "userEventDto:org.example.notificationservice.dto.UserEventDto");
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(),
                new JsonDeserializer<>(UserEventDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEventDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}