import java.util.List;

// СЕРВИСЫ
@Service
class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Long id, Student student) {
        return studentRepository.findById(id)
                .map(existing -> {
                    existing.setName(student.getName());
                    existing.setAge(student.getAge());
                    if (student.getFaculty() != null) {
                        existing.setFaculty(student.getFaculty());
                    }
                    return studentRepository.save(existing);
                })
                .orElse(null);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }
}
