package excertion;

// Исключение для случаев, когда нет доступных вопросов
class NoQuestionsException extends RuntimeException {
    public NoQuestionsException(String message) {
        super(message);
    }
}
