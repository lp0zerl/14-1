package controller;

import java.util.Collection;

// === Контроллер JavaQuestionController ===
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
        return removed != null ?
                ResponseEntity.ok(removed) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Question>> getAllQuestions() {
        Collection<Question> questions = javaQuestionService.getAll();
        return ResponseEntity.ok(questions);
    }
}
