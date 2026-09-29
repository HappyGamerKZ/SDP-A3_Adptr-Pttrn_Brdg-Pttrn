package org.aitu;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ProjectArchitectureTest {

    // Инициализируем реестр фабрики перед всеми тестами
    @BeforeAll
    static void setupRegistry() {
        PlatformFactory.register("bilim-land", BilimLandAPIClient::new);
        PlatformFactory.register("kundelik.kz", KundelikKzApiClient::new);
        PlatformFactory.register("aitu-lms", data -> new AituLmsApiAdapter(new AituLmsApiClient(data)));
    }

    // =========================================================================
    // 1. ТЕСТИРОВАНИЕ АБСТРАКЦИЙ (BRIDGE DELEGATION) - Требование из п. 3.6
    // =========================================================================

    @Test
    void testQuickSummaryViewDelegation() {
        // Arrange: Создаем мок (манекен) интерфейса Implementor
        PlatformImplementor mockPlatform = Mockito.mock(PlatformImplementor.class);

        // Учим мок возвращать тестовые данные
        Mockito.when(mockPlatform.getPlatformName()).thenReturn("Test-LMS");
        Mockito.when(mockPlatform.getStudentName()).thenReturn("Ivan Ivanov");
        Mockito.when(mockPlatform.getStudentId()).thenReturn("ID-999");
        Mockito.when(mockPlatform.getScore()).thenReturn(85);

        // Передаем мок в первую Refined Abstraction
        StudentTaskView quickView = new QuickSummaryView(mockPlatform);

        // Act: Вызываем метод отрисовки
        quickView.render();

        // Assert: Проверяем, что вьюшка действительно обращалась к методам Implementor (делегирование)
        Mockito.verify(mockPlatform, Mockito.times(1)).getPlatformName();
        Mockito.verify(mockPlatform, Mockito.times(1)).getStudentName();
        Mockito.verify(mockPlatform, Mockito.times(1)).getStudentId();
        Mockito.verify(mockPlatform, Mockito.times(1)).getScore();

        // В кратком отчете payload не нужен, проверяем, что его не вызывали
        Mockito.verify(mockPlatform, Mockito.never()).getPayload();
    }

    @Test
    void testDetailedCodeReviewViewDelegation() {
        // Arrange: Создаем мок для второй абстракции
        PlatformImplementor mockPlatform = Mockito.mock(PlatformImplementor.class);
        Mockito.when(mockPlatform.getPlatformName()).thenReturn("Test-LMS");
        Mockito.when(mockPlatform.getStudentName()).thenReturn("Petr Petrov");
        Mockito.when(mockPlatform.getStudentId()).thenReturn("ID-777");
        Mockito.when(mockPlatform.getScore()).thenReturn(100);
        Mockito.when(mockPlatform.getPayload()).thenReturn("System.out.println('Mocked');");

        // Передаем мок во вторую Refined Abstraction
        StudentTaskView detailedView = new DetailedCodeReviewView(mockPlatform);

        // Act
        detailedView.render();

        // Assert: Проверяем, что в детальном отчете вызываются ВСЕ методы, включая getPayload()
        Mockito.verify(mockPlatform, Mockito.times(1)).getPlatformName();
        Mockito.verify(mockPlatform, Mockito.times(1)).getStudentName();
        Mockito.verify(mockPlatform, Mockito.times(1)).getStudentId();
        Mockito.verify(mockPlatform, Mockito.times(1)).getScore();
        Mockito.verify(mockPlatform, Mockito.times(1)).getPayload();
    }


    // =========================================================================
    // 2. ТЕСТИРОВАНИЕ АДАПТЕРА (ADAPTER FAILURE TRANSLATION) - Требование из п. 3.3 и 3.6
    // =========================================================================

    @Test
    void testAdapterTranslatesExceptionCorrectly() {
        // Arrange: Создаем мок несовместимого класса (AituLmsApiClient)
        AituLmsApiClient mockBadClient = Mockito.mock(AituLmsApiClient.class);

        // Симулируем ситуацию, когда легаси-клиент падает с ошибкой (например, битый XML)
        Mockito.when(mockBadClient.fetchXmlResponse())
                .thenThrow(new RuntimeException("FatalXmlException: Failed to extract XML payload"));

        // Оборачиваем сломанный клиент в Адаптер
        PlatformImplementor adapter = new AituLmsApiAdapter(mockBadClient);

        // Act & Assert: Ожидаем, что Адаптер перехватит RuntimeException
        // и выбросит доменное исключение PlatformSyncException
        Assertions.assertThrows(PlatformSyncException.class, () -> {
            adapter.getScore();
        }, "Адаптер должен транслировать ошибку в PlatformSyncException");

        Assertions.assertThrows(PlatformSyncException.class, () -> {
            adapter.getPayload();
        }, "Адаптер должен транслировать ошибку в PlatformSyncException");
    }

    @Test
    void testAdapterSuccessPath() {
        // Arrange: Проверяем успешный сценарий работы Адаптера
        String xmlData = """
                {
                    "platform": "aitu-lms",
                    "student-id": "UUID-777",
                    "student-name": "Michael Jackson",
                    "payload": "<xml><score>80</score><code>print('Hello')</code></xml>"
                }
                """;

        AituLmsApiClient realClient = new AituLmsApiClient(xmlData);
        PlatformImplementor adapter = new AituLmsApiAdapter(realClient);

        // Act & Assert
        Assertions.assertEquals("aitu-lms", adapter.getPlatformName());
        Assertions.assertEquals("UUID-777", adapter.getStudentId());
        Assertions.assertEquals("Michael Jackson", adapter.getStudentName());
        Assertions.assertEquals(80, adapter.getScore());
        Assertions.assertEquals("print('Hello')", adapter.getPayload());
    }


    // =========================================================================
    // 3. ТЕСТИРОВАНИЕ ФАБРИКИ (DYNAMIC SELECTION / OCP) - Требование из п. 5
    // =========================================================================

    @Test
    void testFactoryDynamicSelection() {
        // Arrange
        String jsonBilim = """
                {
                    "platform": "bilim-land",
                    "student-id": "ID-100",
                    "student-name": "Test",
                    "payload": { "score": 90, "assignment": "test()" }
                }
                """;

        // Act
        PlatformImplementor implementor = PlatformFactory.createPlatformImplementor(jsonBilim);

        // Assert: Проверяем, что фабрика через Реестр правильно выбрала создателя
        Assertions.assertNotNull(implementor);
        Assertions.assertInstanceOf(BilimLandAPIClient.class, implementor);
    }

    @Test
    void testFactoryThrowsExceptionOnUnknownPlatform() {
        // Arrange: JSON с незарегистрированной платформой
        String jsonUnknown = """
                {
                    "platform": "unknown-lms",
                    "student-id": "ID-100"
                }
                """;

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PlatformFactory.createPlatformImplementor(jsonUnknown);
        }, "Фабрика должна выбрасывать ошибку, если платформа не найдена в реестре");
    }
}