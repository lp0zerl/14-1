import java.util.List;

// ============== КОНТРОЛЛЕРЫ ==============
@RestController
@RequestMapping("/student")
class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // Шаг 1: Эндпоинт для получения студентов по возрасту в промежутке
    @GetMapping("/age/between")
    public List<Student> getStudentsByAgeBetween(
            @RequestParam int min,
            @RequestParam int max) {
        return studentService.getStudentsByAgeBetween(min, max);
    }

    // Шаг 4: Эндпоинт для получения факультета студента
    @GetMapping("/{id}/faculty")
    public Faculty getStudentFaculty(@PathVariable Long id) {
        return studentService.getStudentFaculty(id);
    }

    // Шаг 4: Эндпоинт для получения студентов факультета
    @GetMapping("/faculty/{facultyId}")
    public List<Student> getStudentsByFaculty(@PathVariable Long facultyId) {
        return studentService.getStudentsByFaculty(facultyId);
    }

    // Шаг 3: Дополнительные эндпоинты для SQL запросов
    @GetMapping("/name-contains/{letter}")
    public List<Student> getStudentsWithNameContaining(@PathVariable String letter) {
        return studentService.getStudentsWithNameContaining(letter);
    }

    @GetMapping("/age-less-than-id")
    public List<Student> getStudentsWhereAgeLessThanId() {
        return studentService.getStudentsWhereAgeLessThanId();
    }

    @GetMapping("/ordered-by-age")
    public List<Student> getStudentsOrderedByAge() {
        return studentService.getStudentsOrderedByAge();
    }
}
