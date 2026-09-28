package org.aitu;

public class AituLmsApiAdapter implements PlatformImplementor {
    private final AituLmsApiClient client;

    public AituLmsApiAdapter(AituLmsApiClient client) {
        this.client = client;
    }

    // Хелпер 1: Для чтения верхнего уровня JSON (student-id, student-name)
    private String extractJsonField(String fieldName) {
        try {
            String searchKey = "\"" + fieldName + "\":";
            String rightPart = client.getData().split(searchKey)[1];
            return rightPart.split("[,\\n]")[0]
                    .replaceAll("[\"\\{\\}]", "")
                    .trim();
        } catch (Exception e) {
            return "Not found";
        }
    }

    // Хелпер 2: Для чтения внутри XML-тегов (<score>, <code>)
    private String extractXmlTag(String xml, String tagName) {
        try {
            String openTag = "<" + tagName + ">";
            String closeTag = "</" + tagName + ">";
            return xml.split(openTag)[1].split(closeTag)[0].trim();
        } catch (Exception e) {
            return "Not found";
        }
    }

    @Override
    public String getPlatformName() {
        return extractJsonField("platform"); // Вернет "AITU-LMS"
    }

    @Override
    public String getStudentId() {
        return extractJsonField("student-id"); // Вернет "UUID-777"
    }

    @Override
    public String getStudentName() {
        return extractJsonField("student-name"); // Вернет "Michael Jackson"
    }

    @Override
    public int getScore() {
        try {
            String xml = client.fetchXmlResponse();
            String scoreStr = extractXmlTag(xml, "score");
            return Integer.parseInt(scoreStr); // Вернет 0
        } catch (Exception e) {
            // Трансляция ошибки легаси-системы по ТЗ!
            throw new PlatformSyncException("Failed to sync score from AITU-LMS XML payload", e);
        }
    }

    @Override
    public String getPayload() {
        try {
            String xml = client.fetchXmlResponse();
            return extractXmlTag(xml, "code"); // Вернет "print('Hi')"
        } catch (Exception e) {
            throw new PlatformSyncException("Failed to sync code payload from AITU-LMS XML", e);
        }
    }
}