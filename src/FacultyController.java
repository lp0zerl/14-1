import java.util.List;

@RestController
@RequestMapping("/faculty")
class FacultyController {
    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @GetMapping("/{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return facultyService.getFacultyById(id);
    }

    @PutMapping("/{id}")
    public Faculty updateFaculty(@PathVariable Long id, @RequestBody Faculty faculty) {
        return facultyService.updateFaculty(id, faculty);
    }

    @DeleteMapping("/{id}")
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    @GetMapping
    public List<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    // Шаг 1: Эндпоинт для поиска факультета по имени или цвету
    @GetMapping("/search")
    public List<Faculty> findFacultiesByNameOrColor(@RequestParam String search) {
        return facultyService.findFacultiesByNameOrColor(search);
    }

    // Шаг 4: Эндпоинт для получения студентов факультета
    @GetMapping("/{id}/students")
    public List<Student> getFacultyStudents(@PathVariable Long id) {
        return facultyService.getFacultyStudents(id);
    }
}
