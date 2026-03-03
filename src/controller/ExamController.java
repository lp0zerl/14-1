package controller;

import model.Question;
import service.ExaminerService;

import java.util.Collection;

// Контроллер для получения экзаменационных вопросов
@RestController
@RequestMapping("/exam")
class ExamController {
    private final ExaminerService examinerService;

    public ExamController(ExaminerService examinerService) {
        this.examinerService = examinerService;
    }

    @GetMapping("/get/{amount}")
    public ResponseEntity<Collection<Question>> getQuestions(@PathVariable int amount) {
        try {
            Collection<Question> questions = examinerService.getQuestions(amount);
            return ResponseEntity.ok(questions);
        } catch (TooManyQuestionsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
