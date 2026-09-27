package org.aitu;

public class Main {
    static void main() {

        String Student1 = """
                {
                    "platform": "bilim-land",
                    "student-id": "ID-2241",
                    "student-name": "John Pork",
                    "payload":{
                    "score": 95,
                    "code": "hero.moveRight()"
                    }
                }
                """;

        String Student2 = """
                {
                    "platform": "kundelik.kz",
                    "student-id": "IIN-010101010101",
                    "student-name": "Genadiy Genadievich Golovkin",
                    "payload":{
                    "score": 50,
                    "code": "<html><body><h1>My First Page</h1></body></html>"
                    }
                }
                """;

        String Student3 = """
                {
                    "platform":"AITU-LMS",
                    "student-id":"UUID-777",
                    "student-name":"Michael Jackson",
                    "payload":"<xml><score>0</score><code>print('Hi')</code></xml>",
                }
                """;

        }
    }
}
