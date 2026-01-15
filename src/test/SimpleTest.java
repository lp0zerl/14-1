package test;

import model.Faculty;
import model.Student;

// КЛАСС ДЛЯ БЫСТРОГО ТЕСТИРОВАНИЯ БЕЗ БАЗЫ ДАННЫХ
class SimpleTest {
    public static void main(String[] args) {
        System.out.println("Тестирование моделей:");

        Student student = new Student("Test model.Student", 20);
        Faculty faculty = new Faculty("Test model.Faculty", "purple");

        student.setFaculty(faculty);

        System.out.println(student);
        System.out.println(faculty);
        System.out.println("Тест завершен успешно!");
    }
}
