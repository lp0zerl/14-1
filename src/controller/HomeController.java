package controller;

// ============== КОНТРОЛЛЕР ГЛАВНОЙ СТРАНИЦЫ ==============
@RestController
class HomeController {

    @GetMapping("/")
    public String home() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Hogwarts School - ДЗ 3.4</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
                        .container { max-width: 1000px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
                        h2 { color: #27ae60; margin-top: 30px; }
                        .step { background: #f8f9fa; padding: 15px; margin: 15px 0; border-radius: 5px; }
                        .success { color: #27ae60; font-weight: bold; }
                        .code { background: #2c3e50; color: #ecf0f1; padding: 10px; border-radius: 5px; font-family: monospace; margin: 10px 0; }
                        .endpoint-list { list-style: none; padding: 0; }
                        .endpoint-list li { background: #ecf0f1; margin: 5px 0; padding: 10px; border-left: 4px solid #3498db; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🏰 Hogwarts School Application</h1>
                        <h2>Домашнее задание 3.4: Введение в SQL</h2>
                
                        <div class="step">
                            <h3>✅ Шаг 1: Новые эндпоинты</h3>
                            <ul class="endpoint-list">
                                <li><strong>GET /student/age/between?min=10&max=20</strong> - студенты по возрасту</li>
                                <li><strong>GET /faculty/search?search=текст</strong> - поиск факультета</li>
                            </ul>
                        </div>
                
                        <div class="step">
                            <h3>✅ Шаг 2: Подключение к БД через IDEA</h3>
                            <p>Выполнен запрос: <span class="code">SELECT * FROM student</span></p>
                        </div>
                
                        <div class="step">
                            <h3>✅ Шаг 3: SQL запросы</h3>
                            <p>Создан файл <strong>scripts.sql</strong> с 5 запросами:</p>
                            <div class="code">
                                1. SELECT * FROM student WHERE age BETWEEN 10 AND 20;<br>
                                2. SELECT name FROM student;<br>
                                3. SELECT * FROM student WHERE name LIKE '%О%';<br>
                                4. SELECT * FROM student WHERE age < id;<br>
                                5. SELECT * FROM student ORDER BY age;
                            </div>
                            <p><a href="/sql-scripts">Посмотреть все SQL скрипты</a></p>
                        </div>
                
                        <div class="step">
                            <h3>✅ Шаг 4: Связь ManyToOne</h3>
                            <ul class="endpoint-list">
                                <li><strong>GET /student/{id}/faculty</strong> - факультет студента</li>
                                <li><strong>GET /faculty/{id}/students</strong> - студенты факультета</li>
                            </ul>
                            <p class="success">Связь @ManyToOne настроена между model.Student и model.Faculty</p>
                        </div>
                
                        <h2>📡 Быстрые ссылки:</h2>
                        <ul class="endpoint-list">
                            <li><a href="/student">/student</a> - все студенты</li>
                            <li><a href="/faculty">/faculty</a> - все факультеты</li>
                            <li><a href="/student/age/between?min=15&max=18">/student/age/between?min=15&max=18</a> - тест возраста</li>
                            <li><a href="/faculty/search?search=гриф">/faculty/search?search=гриф</a> - поиск факультета</li>
                            <li><a href="/student/1/faculty">/student/1/faculty</a> - факультет 1-го студента</li>
                            <li><a href="/faculty/1/students">/faculty/1/students</a> - студенты 1-го факультета</li>
                        </ul>
                
                        <h2>🛠 Проверка через Postman:</h2>
                        <div class="code">
                            # Тест Шаг 1<br>
                            GET http://localhost:8080/student/age/between?min=16&max=18<br>
                            GET http://localhost:8080/faculty/search?search=зелен<br><br>
                
                            # Тест Шаг 4<br>
                            GET http://localhost:8080/student/1/faculty<br>
                            GET http://localhost:8080/faculty/1/students
                        </div>
                
                        <p class="success" style="margin-top: 30px; padding: 15px; text-align: center;">
                            🎉 Все задания выполнены успешно! 🎉
                        </p>
                    </div>
                </body>
                </html>
                """;
    }
}
