package test;

import java.util.Collection;

// Тесты для JavaQuestionService
class JavaQuestionServiceTest {
    private JavaQuestionService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new JavaQuestionService();
    }

    @org.junit.jupiter.api.Test
    void testAddQuestionWithStrings() {
        // Проверка добавления вопроса через строки
        Question question = service.add("Что такое Java?", "Язык программирования");

        assert question != null;
        assert question.getQuestion().equals("Что такое Java?");
        assert question.getAnswer().equals("Язык программирования");

        assert service.getAll().size() == 1;
    }

    @org.junit.jupiter.api.Test
    void testAddQuestionWithObject() {
        // Проверка добавления объекта Question
        Question question = new Question("Что такое ООП?", "Объектно-ориентированное программирование");
        Question added = service.add(question);

        assert added.equals(question);
        assert service.getAll().size() == 1;
    }

    @org.junit.jupiter.api.Test
    void testAddDuplicateQuestion() {
        // Проверка добавления дубликата
        service.add("Вопрос", "Ответ");
        service.add("Вопрос", "Ответ");

        // HashSet не должен добавлять дубликаты
        assert service.getAll().size() == 1;
    }

    @org.junit.jupiter.api.Test
    void testRemoveQuestion() {
        // Проверка удаления вопроса
        service.add("Вопрос", "Ответ");
        Question toRemove = new Question("Вопрос", "Ответ");
        Question removed = service.remove(toRemove);

        assert removed != null;
        assert service.getAll().isEmpty();
    }

    @org.junit.jupiter.api.Test
    void testRemoveNonExistentQuestion() {
        // Попытка удалить несуществующий вопрос
        Question toRemove = new Question("Несуществующий", "Вопрос");
        Question removed = service.remove(toRemove);

        assert removed == null;
    }

    @org.junit.jupiter.api.Test
    void testGetRandomQuestionFromEmptySet() {
        // Попытка получить случайный вопрос из пустой коллекции
        try {
            service.getRandomQuestion();
            assert false : "Должно было выброситься исключение";
        } catch (NoQuestionsException e) {
            assert true;
        }
    }

    @org.junit.jupiter.api.Test
    void testGetRandomQuestion() {
        // Проверка получения случайного вопроса
        service.add("Вопрос 1", "Ответ 1");
        service.add("Вопрос 2", "Ответ 2");

        Question random = service.getRandomQuestion();
        assert random != null;
        assert random.getQuestion() != null;
        assert random.getAnswer() != null;
    }

    @org.junit.jupiter.api.Test
    void testGetAllQuestions() {
        // Проверка получения всех вопросов
        service.add("Вопрос 1", "Ответ 1");
        service.add("Вопрос 2", "Ответ 2");

        Collection<Question> all = service.getAll();
        assert all.size() == 2;
    }
}
