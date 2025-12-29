import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.*;
import java.util.*;

// ============== СЕРВИСЫ ==============
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

    // Шаг 1: получение студентов по возрасту в промежутке
    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    // Шаг 3: SQL запросы
    public List<Student> getStudentsWithNameContaining(String letter) {
        return studentRepository.findByNameContaining(letter);
    }

    public List<Student> getStudentsWhereAgeLessThanId() {
        return studentRepository.findStudentsWhereAgeLessThanId();
    }

    public List<Student> getStudentsOrderedByAge() {
        return studentRepository.findAll(Sort.by("age"));
    }

    // Шаг 4: получение факультета студента
    public Faculty getStudentFaculty(Long studentId) {
        Student student = getStudentById(studentId);
        return student != null ? student.getFaculty() : null;
    }

    // Шаг 4: получение студентов факультета
    public List<Student> getStudentsByFaculty(Long facultyId) {
        return studentRepository.findByFacultyId(facultyId);
    }
}

