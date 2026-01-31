package service;

import model.Question;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Реализация ExaminerServiceImpl
@org.springframework.stereotype.Service
class ExaminerServiceImpl implements ExaminerService {
    private final QuestionService questionService;

    public ExaminerServiceImpl(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public Collection<Question> getQuestions(int amount) {
        Collection<Question> allQuestions = questionService.getAll();

        if (amount > allQuestions.size()) {
            throw new TooManyQuestionsException(
                    "Запрошено " + amount + " вопросов, но доступно только " + allQuestions.size()
            );
        }

        if (amount <= 0) {
            return Collections.emptySet();
        }

        Set<Question> result = new HashSet<>();

        while (result.size() < amount) {
            try {
                Question randomQuestion = questionService.getRandomQuestion();
                result.add(randomQuestion);
            } catch (NoQuestionsException e) {
                // В теории здесь не должно возникать, так как мы уже проверили количество
                throw new TooManyQuestionsException("Не удалось получить вопросы");
            }
        }

        return result;
    }
}
