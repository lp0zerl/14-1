// ============== SQL ЗАПРОСЫ (ШАГ 3) ==============
class SqlQueries {
    public static final String[] SCRIPTS = {
            // 1. Получить всех студентов, возраст которых находится между 10 и 20
            "SELECT * FROM student WHERE age BETWEEN 10 AND 20;",

            // 2. Получить всех студентов, но отобразить только список их имен
            "SELECT name FROM student;",

            // 3. Получить всех студентов, у которых в имени присутствует буква О
            "SELECT * FROM student WHERE name LIKE '%О%' OR name LIKE '%o%';",

            // 4. Получить всех студентов, у которых возраст меньше идентификатора
            "SELECT * FROM student WHERE age < id;",

            // 5. Получить всех студентов упорядоченных по возрасту
            "SELECT * FROM student ORDER BY age;",

            // Дополнительные полезные запросы
            "SELECT COUNT(*) as total_students FROM student;",
            "SELECT AVG(age) as average_age FROM student;",
            "SELECT * FROM student WHERE faculty_id IS NOT NULL;",
            "SELECT f.name as faculty_name, COUNT(s.id) as student_count " +
                    "FROM faculty f LEFT JOIN student s ON f.id = s.faculty_id " +
                    "GROUP BY f.id, f.name ORDER BY student_count DESC;"
    };
}
