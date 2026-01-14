package repositiry;

import model.Student;

import java.util.List;

// ============== РЕПОЗИТОРИИ ==============
@Repository
interface StudentRepository extends JpaRepository<Student, Long> {
    // Метод для шага 1: получение студентов по возрасту в промежутке
    List<Student> findByAgeBetween(int minAge, int maxAge);

    // Методы для SQL запросов (шаг 3)
    List<Student> findByNameContaining(String letter);

    // Метод для SQL запроса: студенты, у которых возраст меньше ID
    @Query("SELECT s FROM model.Student s WHERE s.age < s.id")
    List<Student> findStudentsWhereAgeLessThanId();

    // Метод для получения студентов факультета (шаг 4)
    List<Student> findByFacultyId(Long facultyId);
}
