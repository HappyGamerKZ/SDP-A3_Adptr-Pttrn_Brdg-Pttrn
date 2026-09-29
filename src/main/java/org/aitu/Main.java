package org.aitu;

public class Main {

    static {
        PlatformFactory.register("bilim-land", data -> new BilimLandAPIClient(data));
        PlatformFactory.register("kundelik.kz", data -> new KundelikKzApiClient(data));
        PlatformFactory.register("aitu-lms", data -> new AituLmsApiAdapter(new AituLmsApiClient(data)));
    }

    public static void main(String[] args) {

        String student1 = """
                {
                    "platform": "bilim-land",
                    "student-id": "ID-2241",
                    "student-name": "John Pork",
                    "payload":{
                        "score": 95,
                        "assignment": "hero.moveRight()"
                    }
                }
                """;

        String student2 = """
                {
                    "platform": "kundelik.kz",
                    "student-id": "IIN-010101010101",
                    "student-name": "Genadiy Genadievich Golovkin",
                    "payload":{
                        "score": 4,
                        "home-task": "<html><body><h1>My First Page</h1></body></html>"
                    }
                }
                """;

        String student3 = """
                {
                    "platform":"aitu-lms",
                    "student-id":"UUID-777",
                    "student-name":"Michael Jackson",
                    "payload":"<xml><score>0</score><code>print('Hi')</code></xml>"
                }
                """;

        String[] incomingRequests = {student1, student2, student3};

        for (String rawData : incomingRequests) {
            // 1. Динамически получаем реализатор через фабрику (Dynamic implementor selection)
            PlatformImplementor implementor = PlatformFactory.createPlatformImplementor(rawData);

            // 2. Передаем его в мост-абстракции
            StudentTaskView quickView = new QuickSummaryView(implementor);
            StudentTaskView detailedView = new DetailedCodeReviewView(implementor);

            // 3. Отрисовываем
            quickView.render();
            detailedView.render();
        }
    }
}