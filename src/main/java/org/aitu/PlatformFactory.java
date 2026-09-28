package org.aitu;

public class PlatformFactory {
    public static PlatformImplementor createPlatformImplementor(String student) {
        if (student == null || student.isBlank()) {
            throw new IllegalArgumentException("Student cannot be null or blank");
        }

        if (student.contains("\"platform\": \"bilim-land\"")){
            return new BilimLandAPIClient(student);
        } else if (student.contains("\"platform\": \"AITU-LMS\"")){
            AituLmsApiClient client = new AituLmsApiClient(student);
            return new AituLmsApiAdapter(client);
        } else if (student.contains("\"platform\": \"kundelik.kz\"")){
            return new KundelikKzApiClient(student);
        }

        throw new IllegalArgumentException("Unknown platform type in raw data");
    }
}
