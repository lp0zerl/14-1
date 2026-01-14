package type;

enum TransactionType {
    DEPOSIT, WITHDRAW;

    public static boolean isValid(String type) {
        for (TransactionType transactionType : values()) {
            if (transactionType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}
