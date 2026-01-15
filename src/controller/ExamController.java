package controller;

import java.util.Collection;

// === Контроллер ExamController ===
@RestController
@RequestMapping("/exam")
class ExamController {
    private final ExaminerService examinerService;

    public ExamController(ExaminerService examinerService) {
        this.examinerService = examinerService;
    }

    @GetMapping("/get/{amount}")
    public ResponseEntity<Collection<Question>> getQuestions(@PathVariable int amount) {
        Collection<Question> questions = examinerService.getQuestions(amount);
        return ResponseEntity.ok(questions);
    }
}
