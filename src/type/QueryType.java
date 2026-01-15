package type;

enum QueryType {
    USER_OF("USER_OF", 1, 1),
    ACTIVE_USER_OF("ACTIVE_USER_OF", 1, 1),
    TRANSACTION_SUM_COMPARE("TRANSACTION_SUM_COMPARE", 4, 4),
    TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW", 2, 2);
    private final String code;
    private final int minArguments;
    private final int maxArguments;

    QueryType(String code, int minArguments, int maxArguments) {
        this.code = code;
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
    }

    public String getCode() {
        return code;
    }

    public int getMinArguments() {
        return minArguments;
    }

    public int getMaxArguments() {
        return maxArguments;
    }

    public static QueryType fromCode(String code) {
        for (QueryType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Неизвестный тип запроса: " + code);
    }

    public static boolean isValid(String code) {
        for (QueryType type : values()) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
