package org.aitu;

import java.util.HashMap;
import java.util.Map;

public class PlatformFactory {
    // Реестр (словарь) платформ
    private static final Map<String, PlatformCreator> creators = new HashMap<>();

    // Метод для регистрации платформ извне. OCP соблюден: класс открыт для расширения!
    public static void register(String platformName, PlatformCreator creator) {
        creators.put(platformName, creator);
    }

    public static PlatformImplementor createPlatformImplementor(String studentJson) {
        if (studentJson == null || studentJson.isBlank()) {
            throw new IllegalArgumentException("Student JSON cannot be null or blank");
        }

        String platformName = extractPlatformName(studentJson);

        if (platformName == null || !creators.containsKey(platformName)) {
            throw new IllegalArgumentException("Unknown or missing platform type: " + platformName);
        }

        // Достаем нужного создателя из словаря и создаем реализацию
        return creators.get(platformName).create(studentJson);
    }

    // Вспомогательный метод: вытаскивает значение поля "platform" даже если там разные пробелы
    private static String extractPlatformName(String json) {
        try {
            String[] parts = json.split("\"platform\"\\s*:\\s*\"");
            if (parts.length > 1) {
                return parts[1].split("\"")[0];
            }
        } catch (Exception e) {
            // Если парсинг упал, вернем null, и фабрика выбросит исключение
        }
        return null;
    }
}