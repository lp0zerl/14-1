package service;

import model.Question;

import java.util.Collection;

// Интерфейс QuestionService
interface QuestionService {
    Question add(String question, String answer);

    Question add(Question question);

    Question remove(Question question);

    Collection<Question> getAll();

    Question getRandomQuestion();
}
