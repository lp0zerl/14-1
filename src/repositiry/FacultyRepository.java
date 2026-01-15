package repositiry;

import model.Faculty;

import java.util.List;

@Repository
interface FacultyRepository extends JpaRepository<Faculty, Long> {
    // Метод для шага 1: поиск факультета по имени или цвету (регистронезависимый)
    List<Faculty> findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(String name, String color);
}
