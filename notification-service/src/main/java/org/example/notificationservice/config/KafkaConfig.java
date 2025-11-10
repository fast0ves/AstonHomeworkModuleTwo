package org.example.notificationservice.config;

import org.example.notificationservice.dto.UserEventDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, UserEventDto> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // Разрешаем все пакеты или добавляем оба пакета
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        // ИЛИ конкретные пакеты:
        // configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "org.example.dto,org.example.notificationservice.dto");

        // Явно указываем тип для десериализации
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.example.notificationservice.dto.UserEventDto");

        // Добавляем маппинг типов
        configProps.put(JsonDeserializer.TYPE_MAPPINGS,
                "org.example.dto.UserEventDto:org.example.notificationservice.dto.UserEventDto," +
                "userEventDto:org.example.notificationservice.dto.UserEventDto");

        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEventDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Добавляем обработку ошибок десериализации
        factory.setCommonErrorHandler(new CommonLoggingErrorHandler());

        return factory;
    }
}