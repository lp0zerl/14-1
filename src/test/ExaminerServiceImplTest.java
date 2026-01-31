package test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// Тесты для ExaminerServiceImpl
class ExaminerServiceImplTest {
    private ExaminerServiceImpl examinerService;
    private JavaQuestionService questionService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        questionService = new JavaQuestionService();
        examinerService = new ExaminerServiceImpl(questionService);
    }

    @org.junit.jupiter.api.Test
    void testGetQuestionsWithValidAmount() {
        // Подготовка тестовых данных
        questionService.add("Вопрос 1", "Ответ 1");
        questionService.add("Вопрос 2", "Ответ 2");
        questionService.add("Вопрос 3", "Ответ 3");

        // Запрос допустимого количества вопросов
        Collection<Question> questions = examinerService.getQuestions(2);

        assert questions.size() == 2;
        assert areAllQuestionsUnique(questions);
    }

    @org.junit.jupiter.api.Test
    void testGetQuestionsWithZeroAmount() {
        // Запрос 0 вопросов
        Collection<Question> questions = examinerService.getQuestions(0);

        assert questions.isEmpty();
    }

    @org.junit.jupiter.api.Test
    void testGetQuestionsWithTooManyRequested() {
        // Попытка запросить больше вопросов, чем есть
        questionService.add("Вопрос 1", "Ответ 1");
        questionService.add("Вопрос 2", "Ответ 2");

        try {
            examinerService.getQuestions(5);
            assert false : "Должно было выброситься исключение";
        } catch (TooManyQuestionsException e) {
            assert true;
        }
    }

    @org.junit.jupiter.api.Test
    void testGetQuestionsWithEmptyQuestionService() {
        // Попытка получить вопросы, когда сервис пуст
        try {
            examinerService.getQuestions(1);
            assert false : "Должно было выброситься исключение";
        } catch (TooManyQuestionsException e) {
            assert true;
        }
    }

    @org.junit.jupiter.api.Test
    void testGetQuestionsReturnsUniqueQuestions() {
        // Проверка, что все возвращаемые вопросы уникальны
        questionService.add("Вопрос 1", "Ответ 1");
        questionService.add("Вопрос 2", "Ответ 2");
        questionService.add("Вопрос 3", "Ответ 3");

        // Запускаем несколько раз, чтобы убедиться в уникальности
        for (int i = 0; i < 10; i++) {
            Collection<Question> questions = examinerService.getQuestions(3);
            assert questions.size() == 3;
            assert areAllQuestionsUnique(questions);
        }
    }

    private boolean areAllQuestionsUnique(Collection<Question> questions) {
        Set<Question> uniqueQuestions = new HashSet<>(questions);
        return uniqueQuestions.size() == questions.size();
    }
}
