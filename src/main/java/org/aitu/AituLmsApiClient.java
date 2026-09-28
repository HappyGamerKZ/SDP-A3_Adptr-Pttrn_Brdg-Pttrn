package org.aitu;

public class AituLmsApiClient {
    private final String student;

    public AituLmsApiClient(String student) {
        this.student = student;
    }

    public String getData() {
        return student;
    }

    // Возвращает чистую XML-строку из payload: "<xml><score>0</score><code>print('Hi')</code></xml>"
    public String fetchXmlResponse() {
        if (student == null || student.isBlank()) {
            throw new RuntimeException("FatalXmlException: Raw input data is missing or empty");
        }
        try {
            String xmlPart = student.split("\"payload\":")[1];
            // Отрезаем кавычки и закрывающую фигурные скобки JSON
            return xmlPart.split("[\"\\}]")[1].trim();
        } catch (Exception e) {
            throw new RuntimeException("FatalXmlException: Failed to extract XML payload from input", e);
        }
    }
}