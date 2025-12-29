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

    public List<Faculty> getFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public List<Faculty> findFacultiesByNameOrColor(String name, String color) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public List<Student> getStudentsByFaculty(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
        return faculty != null ? faculty.getStudents() : Collections.emptyList();
    }
}
