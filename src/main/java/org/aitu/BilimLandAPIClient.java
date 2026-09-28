package org.aitu;

public class BilimLandAPIClient implements PlatformImplementor {
    private final String student;

    public BilimLandAPIClient(String student) {
        this.student = student;
    }

    protected String extractJsonField(String fieldName) {
        try {
            String searchKey = "\"" + fieldName + "\":";
            String rightPart = this.student.split(searchKey)[1];

            return rightPart.split("[,\\n]")[0]
                    .replaceAll("[\"\\{\\}]", "")
                    .trim();
        } catch (Exception e) {
            return "Not found";
        }
    }

    @Override
    public String getPlatformName() {
        return "BilimLand";
    }

    @Override
    public String getStudentId() {
        return extractJsonField("student-id");
    }

    @Override
    public String getStudentName() {
        return extractJsonField("student-name");
    }

    @Override
    public int getScore() {
        try {
            return Integer.parseInt(extractJsonField("score"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String getPayload() {
        return extractJsonField("code");
    }
}
