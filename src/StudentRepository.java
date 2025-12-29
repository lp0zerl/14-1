import java.util.List;

// РЕПОЗИТОРИИ
@Repository
interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int minAge, int maxAge);

    List<Student> findByFacultyId(Long facultyId);
}
