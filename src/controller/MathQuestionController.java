package controller;

import model.Question;
import service.MathQuestionService;

import java.util.Collection;

// Контроллер для математических вопросов
@RestController
@RequestMapping("/exam/math")
class MathQuestionController {
    private final MathQuestionService mathQuestionService;

    public MathQuestionController(MathQuestionService mathQuestionService) {
        this.mathQuestionService = mathQuestionService;
    }

    @PostMapping("/add")
    public ResponseEntity<Question> addMathQuestion(
            @RequestParam String question,
            @RequestParam String answer) {
        Question added = mathQuestionService.add(question, answer);
        return ResponseEntity.ok(added);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Question> removeMathQuestion(
            @RequestParam String question,
            @RequestParam String answer) {
        Question toRemove = new Question(question, answer);
        Question removed = mathQuestionService.remove(toRemove);

        if (removed != null) {
            return ResponseEntity.ok(removed);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<Collection<Question>> getAllMathQuestions() {
        return ResponseEntity.ok(mathQuestionService.getAll());
    }
}
