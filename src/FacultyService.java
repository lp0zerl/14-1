import java.util.Collections;
import java.util.List;

@Service
class FacultyService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        return facultyRepository.findById(id)
                .map(existing -> {
                    existing.setName(faculty.getName());
                    existing.setColor(faculty.getColor());
                    return facultyRepository.save(existing);
                })
                .orElse(null);
    }

    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    // Шаг 1: поиск факультета по имени или цвету
    public List<Faculty> findFacultiesByNameOrColor(String searchString) {
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(
                searchString, searchString);
    }

    // Шаг 4: получение студентов факультета
    public List<Student> getFacultyStudents(Long facultyId) {
        Faculty faculty = getFacultyById(facultyId);
        return faculty != null ? faculty.getStudents() : Collections.emptyList();
    }
}
