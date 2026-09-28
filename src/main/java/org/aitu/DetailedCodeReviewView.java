package org.aitu;

public class DetailedCodeReviewView extends StudentTaskView {
    public DetailedCodeReviewView(PlatformImplementor platform) {
        super(platform);
    }

    @Override
    public void render() {
        System.out.println("==========================================");
        System.out.println("ПОДРОБНЫЙ РЕЗЮМЕ-ОТЧЕТ ПО КОДУ");
        System.out.println("Платформа: " + platform.getPlatformName());
        System.out.println("Студент:   " + platform.getStudentName() + " [" + platform.getStudentId() + "]");
        System.out.println("Балл:      " + platform.getScore() + " из 100");
        System.out.println("Отправленный код:");
        System.out.println("------------------------------------------");
        System.out.println(platform.getPayload());
        System.out.println("==========================================\n");
    }
}