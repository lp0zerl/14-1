package excertion;

// Исключение для случаев, когда запрошено слишком много вопросов
class TooManyQuestionsException extends RuntimeException {
    public TooManyQuestionsException(String message) {
        super(message);
    }
}
