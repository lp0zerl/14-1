import java.util.List;

@Repository
interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findByColor(String color);

    List<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);
}
