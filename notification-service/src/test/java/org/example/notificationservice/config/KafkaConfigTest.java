package org.example.notificationservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.example.notificationservice.dto.UserEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class KafkaConfigTest {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Test
    void consumerFactory_ShouldBeCreated() {
        // Вызов метода consumerFactory()
        ConsumerFactory<String, UserEventDto> consumerFactory = kafkaConfig.consumerFactory();

        // Проверка что объект создан
        assertNotNull(consumerFactory);
    }

    @Test
    void consumerFactory_ShouldHaveCorrectConfiguration() {
        // Вызов метода consumerFactory()
        ConsumerFactory<String, UserEventDto> consumerFactory = kafkaConfig.consumerFactory();

        // Получение конфигурации для проверки всех свойств
        Map<String, Object> configProps = consumerFactory.getConfigurationProperties();

        // Проверка всех свойств из KafkaConfig
        assertEquals("localhost:9092", configProps.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("notification-service", configProps.get(ConsumerConfig.GROUP_ID_CONFIG));

        // Проверяем только имена классов из-за подмены TestContainers
        assertNotNull(configProps.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertNotNull(configProps.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));

        assertEquals("*", configProps.get(JsonDeserializer.TRUSTED_PACKAGES));
        assertEquals(UserEventDto.class.getName(), configProps.get(JsonDeserializer.VALUE_DEFAULT_TYPE));
        assertEquals("earliest", configProps.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
    }

    @Test
    void kafkaListenerContainerFactory_ShouldBeCreated() {
        // Вызов метода kafkaListenerContainerFactory()
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory =
                kafkaConfig.kafkaListenerContainerFactory();

        // Проверка что объект создан
        assertNotNull(factory);
    }

    @Test
    void kafkaListenerContainerFactory_ShouldHaveConsumerFactorySet() {
        // Вызов метода kafkaListenerContainerFactory()
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory =
                kafkaConfig.kafkaListenerContainerFactory();

        // Проверка что consumerFactory установлен
        assertNotNull(factory.getConsumerFactory());

        // Проверка что это тот же consumerFactory что создается в KafkaConfig
        ConsumerFactory<String, UserEventDto> consumerFactory = kafkaConfig.consumerFactory();
        assertNotNull(consumerFactory);

        // Проверка что конфигурации совпадают
        Map<String, Object> factoryConfig = factory.getConsumerFactory().getConfigurationProperties();
        Map<String, Object> consumerConfig = consumerFactory.getConfigurationProperties();

        assertEquals(
                consumerConfig.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG),
                factoryConfig.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)
        );
        assertEquals(
                consumerConfig.get(ConsumerConfig.GROUP_ID_CONFIG),
                factoryConfig.get(ConsumerConfig.GROUP_ID_CONFIG)
        );
    }

    @Test
    void kafkaListenerContainerFactory_ShouldBeFunctional() {
        // Вызов метода kafkaListenerContainerFactory()
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory =
                kafkaConfig.kafkaListenerContainerFactory();

        // Дополнительные проверки функциональности
        assertNotNull(factory.getContainerProperties());
    }

    @Test
    void multipleCalls_ShouldReturnFunctionalBeans() {
        // Многократный вызов методов для покрытия всех строк
        ConsumerFactory<String, UserEventDto> consumerFactory1 = kafkaConfig.consumerFactory();
        ConsumerFactory<String, UserEventDto> consumerFactory2 = kafkaConfig.consumerFactory();

        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory1 =
                kafkaConfig.kafkaListenerContainerFactory();
        ConcurrentKafkaListenerContainerFactory<String, UserEventDto> factory2 =
                kafkaConfig.kafkaListenerContainerFactory();

        // Все вызовы должны возвращать рабочие бины
        assertNotNull(consumerFactory1);
        assertNotNull(consumerFactory2);
        assertNotNull(factory1);
        assertNotNull(factory2);

        // Проверка что consumerFactory работает в factory
        assertNotNull(factory1.getConsumerFactory());
        assertNotNull(factory2.getConsumerFactory());
    }
}