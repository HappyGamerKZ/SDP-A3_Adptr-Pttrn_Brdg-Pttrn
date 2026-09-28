package org.aitu;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProjectArchitectureTest {
    // 1. Тестируем нормальную JSON-платформу
    @Test
    void testBilimLandClientParsing() {
        // Arrange (Дано)
        String json = """
                {
                    "platform": "bilim-land",
                    "student-id": "ID-2241",
                    "student-name": "John Pork",
                    "payload": {
                        "score": 95,
                        "code": "hero.moveRight()"
                    }
                }
                """;

        // Act (Действие)
        BilimLandAPIClient client = new BilimLandAPIClient(json);

        // Assert (Проверка)
        Assertions.assertEquals("BilimLand", client.getPlatformName());
        Assertions.assertEquals("ID-2241", client.getStudentId());
        Assertions.assertEquals("John Pork", client.getStudentName());
        Assertions.assertEquals(95, client.getScore());
        Assertions.assertEquals("hero.moveRight()", client.getPayload());
    }

    // 2. Тестируем работу Фабрики (PlatformFactory)
    @Test
    void testPlatformFactorySelection() {
        // Arrange
        String json = """
                {
                    "platform": "bilim-land",
                    "student-id": "ID-100"
                }
                """;

        // Act
        PlatformImplementor implementor = PlatformFactory.createPlatformImplementor(json);

        // Assert
        Assertions.assertNotNull(implementor);
        Assertions.assertInstanceOf(BilimLandAPIClient.class, implementor);
    }

    // 3. Тестируем обработку ошибки на Фабрике
    @Test
    void testFactoryThrowsExceptionOnBadInput() {
        // Assert & Act
        Assertions.assertThrows(IllegalArgumentException.class, () -> PlatformFactory.createPlatformImplementor("invalid data"));
    }

    // 4. Тестируем работу Адаптера с XML (AituLmsApiAdapter)
    @Test
    void testAituLmsAdapterParsing() {
        // Arrange
        String xmlData = """
                {
                    "platform": "aitu-lms",
                    "student-id": "UUID-777",
                    "student-name": "Michael Jackson",
                    "payload": "<xml><score>80</score><code>print('Hello')</code></xml>"
                }
                """;

        // Act
        AituLmsApiClient client = new AituLmsApiClient(xmlData);
        AituLmsApiAdapter adapter = new AituLmsApiAdapter(client);

        // Assert
        Assertions.assertEquals("aitu-lms", adapter.getPlatformName());
        Assertions.assertEquals("UUID-777", adapter.getStudentId());
        Assertions.assertEquals("Michael Jackson", adapter.getStudentName());
        Assertions.assertEquals(80, adapter.getScore());
        Assertions.assertEquals("print('Hello')", adapter.getPayload());
    }
}
