package model;

import repository.FacultyRepository;
import repository.StudentRepository;

import java.util.Arrays;
import java.util.List;

// ГЛАВНЫЙ КЛАСС ПРИЛОЖЕНИЯ
@SpringBootApplication
@EntityScan(basePackages = "ru.hogwarts.school")
@EnableJpaRepositories(basePackages = "ru.hogwarts.school")
public class SchoolApplication {

    @Bean
    public CommandLineRunner initData(StudentRepository studentRepo, FacultyRepository facultyRepo) {
        return args -> {
            System.out.println("=================================");
            System.out.println("   Hogwarts School Application   ");
            System.out.println("=================================");
            System.out.println("База данных: PostgreSQL");
            System.out.println("Имя БД: hogwarts");
            System.out.println("Пользователь: student");
            System.out.println("Пароль: chocolatefrog");
            System.out.println("Порт: 8080");
            System.out.println("=================================");

            // Создаем тестовые данные при запуске
            if (facultyRepo.count() == 0) {
                Faculty gryffindor = new Faculty("Gryffindor", "red");
                Faculty slytherin = new Faculty("Slytherin", "green");
                Faculty ravenclaw = new Faculty("Ravenclaw", "blue");
                Faculty hufflepuff = new Faculty("Hufflepuff", "yellow");

                facultyRepo.saveAll(Arrays.asList(gryffindor, slytherin, ravenclaw, hufflepuff));
                System.out.println("Созданы тестовые факультеты");
            }

            if (studentRepo.count() == 0) {
                List<Faculty> faculties = facultyRepo.findAll();

                Student harry = new Student("Harry Potter", 17);
                Student hermione = new Student("Hermione Granger", 17);
                Student ron = new Student("Ron Weasley", 17);
                Student draco = new Student("Draco Malfoy", 17);
                Student luna = new Student("Luna Lovegood", 16);

                if (!faculties.isEmpty()) {
                    harry.setFaculty(faculties.get(0)); // Gryffindor
                    hermione.setFaculty(faculties.get(0));
                    ron.setFaculty(faculties.get(0));
                    draco.setFaculty(faculties.get(1)); // Slytherin
                    luna.setFaculty(faculties.get(2)); // Ravenclaw
                }

                studentRepo.saveAll(Arrays.asList(harry, hermione, ron, draco, luna));
                System.out.println("Созданы тестовые студенты");
            }

            System.out.println("Всего студентов: " + studentRepo.count());
            System.out.println("Всего факультетов: " + facultyRepo.count());
            System.out.println("=================================");
            System.out.println("Откройте в браузере: http://localhost:8080/");
            System.out.println("=================================");
        };
    }

    public static void main(String[] args) {
        // Устанавливаем свойства для базы данных
        System.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/hogwarts");
        System.setProperty("spring.datasource.username", "student");
        System.setProperty("spring.datasource.password", "chocolatefrog");
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        System.setProperty("spring.jpa.show-sql", "true");
        System.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        System.setProperty("spring.jpa.properties.hibernate.format_sql", "true");

        SpringApplication.run(SchoolApplication.class, args);
    }
}
