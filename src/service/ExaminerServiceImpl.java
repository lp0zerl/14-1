package service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// === Реализация ExaminerServiceImpl ===
@Service
class ExaminerServiceImpl implements ExaminerService {
    private final QuestionService questionService;

    public ExaminerServiceImpl(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public Collection<Question> getQuestions(int amount) {
        Collection<Question> allQuestions = questionService.getAll();

        if (amount <= 0) {
            return Set.of();
        }

        if (amount > allQuestions.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Запрошено больше вопросов, чем имеется в базе"
            );
        }

        Set<Question> result = new HashSet<>();
        while (result.size() < amount) {
            result.add(questionService.getRandomQuestion());
        }
        return result;
    }
}
