// ==================== ТЕСТОВЫЙ КЛАСС ДЛЯ ЗАПУСКА ====================
class HomeworkTestRunner {

    public static void runAllTests() {
        System.out.println("Запуск всех тестов...");

        SearchServiceTest searchServiceTest = new SearchServiceTest();
        BasketServiceTest basketServiceTest = new BasketServiceTest();

        try {
            System.out.println("Тестирование SearchService:");
            searchServiceTest.testSearchWithEmptyStorage();
            searchServiceTest.testSearchWithNoMatchingProducts();
            searchServiceTest.testSearchWithMatchingProduct();
            searchServiceTest.testSearchWithMultipleMatchingProducts();
            searchServiceTest.testSearchWithEmptyPattern();
            searchServiceTest.testSearchWithNullPattern();
            searchServiceTest.testSearchCaseInsensitive();
            searchServiceTest.testSearchWithPartialMatch();

            System.out.println("Тестирование BasketService:");
            basketServiceTest.testAddNonExistentProduct();
            basketServiceTest.testAddExistingProduct();
            basketServiceTest.testGetEmptyUserBasket();
            basketServiceTest.testGetUserBasketWithProducts();
            basketServiceTest.testGetUserBasketWithMissingProduct();
            basketServiceTest.testAddSameProductMultipleTimes();
            basketServiceTest.testGetUserBasketWithZeroPriceProduct();
            basketServiceTest.testGetUserBasketWithLargeQuantities();
            basketServiceTest.testExceptionMessageWhenAddingNonExistentProduct();
            basketServiceTest.testUserBasketItemProperties();
            basketServiceTest.testMultipleProductAdditionsAndBasketRetrieval();

            System.out.println("Интеграционный тест:");
            new IntegrationTest().testCompleteScenario();

            System.out.println("Все тесты прошли успешно!");
        } catch (Exception e) {
            System.err.println("Ошибка при выполнении тестов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runAllTests();
    }
}
