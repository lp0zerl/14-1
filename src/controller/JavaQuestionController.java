package controller;

import model.Question;
import service.JavaQuestionService;

import java.util.Collection;

// Контроллер для работы с Java вопросами
@RestController
@RequestMapping("/exam/java")
class JavaQuestionController {
    private final JavaQuestionService javaQuestionService;

    public JavaQuestionController(JavaQuestionService javaQuestionService) {
        this.javaQuestionService = javaQuestionService;
    }

    @PostMapping("/add")
    public ResponseEntity<Question> addQuestion(
            @RequestParam String question,
            @RequestParam String answer) {
        Question added = javaQuestionService.add(question, answer);
        return ResponseEntity.ok(added);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Question> removeQuestion(
            @RequestParam String question,
            @RequestParam String answer) {
        Question toRemove = new Question(question, answer);
        Question removed = javaQuestionService.remove(toRemove);

        if (removed != null) {
            return ResponseEntity.ok(removed);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<Collection<Question>> getAllQuestions() {
        return ResponseEntity.ok(javaQuestionService.getAll());
    }
}
