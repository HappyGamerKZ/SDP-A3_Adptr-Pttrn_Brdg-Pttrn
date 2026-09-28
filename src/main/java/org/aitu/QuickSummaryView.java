package org.aitu;

public class QuickSummaryView extends StudentTaskView{
    public QuickSummaryView(PlatformImplementor platform) {
        super(platform);
    }

    @Override
    public void render() {
        System.out.println("=== КРАТКИЙ ОТЧЕТ ===");
        System.out.printf("[%s] Студент: %s (%s) | Оценка: %d/100%n%n",
                platform.getPlatformName(),
                platform.getStudentName(),
                platform.getStudentId(),
                platform.getScore());
    }
}
