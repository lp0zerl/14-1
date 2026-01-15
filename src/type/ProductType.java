package type;

enum ProductType {
    DEBIT, CREDIT, INVEST, SAVING;

    public static boolean isValid(String type) {
        for (ProductType productType : values()) {
            if (productType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}
