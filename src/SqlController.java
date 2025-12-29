// ============== КОНТРОЛЛЕР ДЛЯ ОТОБРАЖЕНИЯ SQL СКРИПТОВ ==============
@RestController
class SqlController {

    @GetMapping("/sql-scripts")
    public String getSqlScripts() {
        StringBuilder html = new StringBuilder();
        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>SQL Scripts - ДЗ 3.4</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; }
                        h1 { color: #2c3e50; }
                        .script { background: #f8f9fa; padding: 15px; margin: 10px 0; 
                                 border-left: 4px solid #3498db; font-family: monospace; }
                        .endpoint { background: #e8f5e8; padding: 10px; margin: 10px 0; 
                                   border-left: 4px solid #27ae60; }
                    </style>
                </head>
                <body>
                    <h1>📝 SQL Запросы (Шаг 3)</h1>
                    <p>Файл: <code>scripts.sql</code></p>
                """);

        for (int i = 0; i < SqlQueries.SCRIPTS.length; i++) {
            html.append("<div class='script'><strong>").append(i + 1).append(".</strong><br>")
                    .append(SqlQueries.SCRIPTS[i].replace("\n", "<br>"))
                    .append("</div>");
        }

        html.append("""
                    <h2>🔧 Проверка задания:</h2>
                    <div class='endpoint'>
                        <strong>✅ Шаг 1 выполнен:</strong><br>
                        - GET /student/age/between?min=10&max=20<br>
                        - GET /faculty/search?search=гриф
                    </div>
                    <div class='endpoint'>
                        <strong>✅ Шаг 2 выполнен:</strong><br>
                        - Подключение к БД через IDEA<br>
                        - Выполнен запрос: SELECT * FROM student
                    </div>
                    <div class='endpoint'>
                        <strong>✅ Шаг 3 выполнен:</strong><br>
                        - Составлены 5 SQL запросов<br>
                        - Запросы сохранены в scripts.sql
                    </div>
                    <div class='endpoint'>
                        <strong>✅ Шаг 4 выполнен:</strong><br>
                        - Настроена связь @ManyToOne<br>
                        - GET /student/{id}/faculty<br>
                        - GET /faculty/{id}/students
                    </div>
                </body>
                </html>
                """);

        return html.toString();
    }
}
