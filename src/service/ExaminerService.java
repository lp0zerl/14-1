package service;

import java.util.Collection;

// === Интерфейс ExaminerService ===
interface ExaminerService {
    Collection<Question> getQuestions(int amount);
}
