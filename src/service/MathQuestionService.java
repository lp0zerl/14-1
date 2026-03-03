package service;

import model.Question;

import java.util.*;

// Дополнительная реализация для математических вопросов (опционально)
@org.springframework.stereotype.Service
class MathQuestionService implements QuestionService {
    private final Set<Question> questions = new HashSet<>();
    private final Random random = new Random();

    public MathQuestionService() {
        // Добавляем некоторые математические вопросы по умолчанию
        add("Сколько будет 2+2?", "4");
        add("Что такое квадратный корень из 9?", "3");
        add("Чему равен синус 90 градусов?", "1");
    }

    @Override
    public Question add(String question, String answer) {
        Question newQuestion = new Question(question, answer);
        return add(newQuestion);
    }

    @Override
    public Question add(Question question) {
        questions.add(question);
        return question;
    }

    @Override
    public Question remove(Question question) {
        if (questions.remove(question)) {
            return question;
        }
        return null;
    }

    @Override
    public Collection<Question> getAll() {
        return new HashSet<>(questions);
    }

    @Override
    public Question getRandomQuestion() {
        if (questions.isEmpty()) {
            throw new NoQuestionsException("Нет доступных математических вопросов");
        }

        int index = random.nextInt(questions.size());
        Iterator<Question> iterator = questions.iterator();

        for (int i = 0; i < index; i++) {
            iterator.next();
        }

        return iterator.next();
    }
}
