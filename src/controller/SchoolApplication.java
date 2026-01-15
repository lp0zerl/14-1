package controller;

import model.Faculty;
import model.Student;
import repositiry.FacultyRepository;
import repositiry.StudentRepository;

import java.util.Arrays;
import java.util.List;

// ============== ГЛАВНЫЙ КЛАСС ПРИЛОЖЕНИЯ ==============
@SpringBootApplication
@EntityScan(basePackages = "ru.hogwarts.school")
@EnableJpaRepositories(basePackages = "ru.hogwarts.school")
public class SchoolApplication {

    @Bean
    public CommandLineRunner initDataAndShowSql(StudentRepository studentRepo,
                                                FacultyRepository facultyRepo) {
        return args -> {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("           HOGWARTS SCHOOL APPLICATION");
            System.out.println("           Домашнее задание 3.4: Введение в SQL");
            System.out.println("=".repeat(60));

            // Создаем тестовые данные
            if (facultyRepo.count() == 0) {
                Faculty gryffindor = facultyRepo.save(new Faculty("Гриффиндор", "красный"));
                Faculty slytherin = facultyRepo.save(new Faculty("Слизерин", "зеленый"));
                Faculty ravenclaw = facultyRepo.save(new Faculty("Когтевран", "синий"));
                Faculty hufflepuff = facultyRepo.save(new Faculty("Пуффендуй", "желтый"));

                System.out.println("✅ Созданы факультеты:");
                System.out.println("   - " + gryffindor.getName() + " (" + gryffindor.getColor() + ")");
                System.out.println("   - " + slytherin.getName() + " (" + slytherin.getColor() + ")");
                System.out.println("   - " + ravenclaw.getName() + " (" + ravenclaw.getColor() + ")");
                System.out.println("   - " + hufflepuff.getName() + " (" + hufflepuff.getColor() + ")");
            }

            if (studentRepo.count() == 0) {
                List<Faculty> faculties = facultyRepo.findAll();

                // Создаем студентов
                Student harry = new Student("Гарри Поттер", 17);
                Student hermione = new Student("Гермиона Грейнджер", 17);
                Student ron = new Student("Рон Уизли", 17);
                Student draco = new Student("Драко Малфой", 17);
                Student luna = new Student("Луна Лавгуд", 16);
                Student neville = new Student("Невилл Долгопупс", 17);
                Student ginny = new Student("Джинни Уизли", 15);
                Student cedric = new Student("Седрик Диггори", 17);
                Student cho = new Student("Чжоу Чанг", 16);
                Student oliver = new Student("Оливер Вуд", 18);

                // Распределяем по факультетам (ManyToOne связь)
                if (!faculties.isEmpty()) {
                    harry.setFaculty(faculties.get(0));     // Гриффиндор
                    hermione.setFaculty(faculties.get(0));  // Гриффиндор
                    ron.setFaculty(faculties.get(0));       // Гриффиндор
                    neville.setFaculty(faculties.get(0));   // Гриффиндор
                    ginny.setFaculty(faculties.get(0));     // Гриффиндор
                    oliver.setFaculty(faculties.get(0));    // Гриффиндор

                    draco.setFaculty(faculties.get(1));     // Слизерин

                    luna.setFaculty(faculties.get(2));      // Когтевран
                    cho.setFaculty(faculties.get(2));       // Когтевран

                    cedric.setFaculty(faculties.get(3));    // Пуффендуй
                }

                studentRepo.saveAll(Arrays.asList(harry, hermione, ron, draco, luna,
                        neville, ginny, cedric, cho, oliver));
                System.out.println("\n✅ Созданы 10 тестовых студентов");
            }

            System.out.println("\n📊 Статистика базы данных:");
            System.out.println("   Всего студентов: " + studentRepo.count());
            System.out.println("   Всего факультетов: " + facultyRepo.count());

            // Показываем SQL запросы из шага 3
            System.out.println("\n" + "=".repeat(60));
            System.out.println("           SQL ЗАПРОСЫ (ШАГ 3)");
            System.out.println("=".repeat(60));

            for (int i = 0; i < SqlQueries.SCRIPTS.length; i++) {
                System.out.println("\n" + (i + 1) + ". " + SqlQueries.SCRIPTS[i]);
            }

            // Демонстрация работы эндпоинтов
            System.out.println("\n" + "=".repeat(60));
            System.out.println("           ДОСТУПНЫЕ ЭНДПОИНТЫ");
            System.out.println("=".repeat(60));

            System.out.println("\n📝 СТУДЕНТЫ:");
            System.out.println("   GET  /student                    - все студенты");
            System.out.println("   GET  /student/{id}               - студент по ID");
            System.out.println("   POST /student                    - создать студента");
            System.out.println("   PUT  /student/{id}               - обновить студента");
            System.out.println("   DELETE /student/{id}             - удалить студента");
            System.out.println("   GET  /student/age/between?min=X&max=Y - студенты по возрасту (ШАГ 1)");
            System.out.println("   GET  /student/{id}/faculty       - факультет студента (ШАГ 4)");
            System.out.println("   GET  /student/faculty/{id}       - студенты факультета (ШАГ 4)");

            System.out.println("\n🏛️ ФАКУЛЬТЕТЫ:");
            System.out.println("   GET  /faculty                    - все факультеты");
            System.out.println("   GET  /faculty/{id}               - факультет по ID");
            System.out.println("   POST /faculty                    - создать факультет");
            System.out.println("   PUT  /faculty/{id}               - обновить факультет");
            System.out.println("   DELETE /faculty/{id}             - удалить факультет");
            System.out.println("   GET  /faculty/search?search=текст - поиск факультета (ШАГ 1)");
            System.out.println("   GET  /faculty/{id}/students      - студенты факультета (ШАГ 4)");

            System.out.println("\n🔍 SQL ЗАПРОСЫ ЧЕРЕЗ API:");
            System.out.println("   GET  /student/name-contains/{буква} - студенты с буквой в имени");
            System.out.println("   GET  /student/age-less-than-id   - возраст < ID");
            System.out.println("   GET  /student/ordered-by-age     - студенты по возрасту");

            System.out.println("\n" + "=".repeat(60));
            System.out.println("   Приложение запущено! Откройте браузер:");
            System.out.println("   🌐 http://localhost:8080");
            System.out.println("=".repeat(60) + "\n");

            // Демонстрация работы новых методов
            System.out.println("🎯 ДЕМОНСТРАЦИЯ РАБОТЫ НОВЫХ МЕТОДОВ:");

            // 1. Студенты в возрасте между 15 и 18
            List<Student> studentsBetween = studentRepo.findByAgeBetween(15, 18);
            System.out.println("\n1. Студенты в возрасте 15-18 лет: " + studentsBetween.size() + " чел.");

            // 2. Поиск факультета по строке "гриф" (регистронезависимый)
            List<Faculty> foundFaculties = facultyRepo.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase("гриф", "гриф");
            System.out.println("2. Найденные факультеты по 'гриф': " + foundFaculties.size());

            // 3. Студенты с буквой 'о' в имени
            List<Student> studentsWithO = studentRepo.findByNameContaining("о");
            System.out.println("3. Студенты с буквой 'о' в имени: " + studentsWithO.size() + " чел.");

            // 4. Студенты, у которых возраст меньше ID
            List<Student> ageLessThanId = studentRepo.findStudentsWhereAgeLessThanId();
            System.out.println("4. Студенты, где возраст < ID: " + ageLessThanId.size() + " чел.");

            // 5. Связь ManyToOne: факультеты со студентами
            System.out.println("\n🎓 СВЯЗЬ МНОГИЕ-К-ОДНОМУ (ManyToOne):");
            faculties = facultyRepo.findAll();
            for (Faculty f : faculties) {
                System.out.println("   " + f.getName() + ": " + f.getStudents().size() + " студентов");
            }
        };
    }

    public static void main(String[] args) {
        // Настройка подключения к PostgreSQL
        System.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/hogwarts");
        System.setProperty("spring.datasource.username", "student");
        System.setProperty("spring.datasource.password", "chocolatefrog");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        System.setProperty("spring.jpa.show-sql", "true");
        System.setProperty("spring.jpa.properties.hibernate.format_sql", "true");
        System.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        SpringApplication.run(SchoolApplication.class, args);
    }
}
