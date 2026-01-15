package test;

import service.BasketService;
import service.InMemoryStorageService;
import service.SearchService;
import service.StorageService;

import java.util.List;

// ==================== ИНТЕГРАЦИОННЫЙ ТЕСТ ====================
class IntegrationTest {

    @Test
    void testCompleteScenario() {
        StorageService storageService = new InMemoryStorageService();
        ProductBasket productBasket = new InMemoryProductBasket();

        storageService.addProduct(new Product("1", "Apple iPhone", 999.99));
        storageService.addProduct(new Product("2", "Samsung Galaxy", 899.99));
        storageService.addProduct(new Product("3", "Google Pixel", 799.99));

        SearchService searchService = new SearchService(storageService);
        BasketService basketService = new BasketService(storageService, productBasket);

        List<Product> appleProducts = searchService.searchProducts("Apple");
        assertThat(appleProducts).hasSize(1);
        assertThat(appleProducts.get(0).getName()).isEqualTo("Apple iPhone");

        basketService.addProduct("1");
        basketService.addProduct("2");
        basketService.addProduct("1");

        UserBasket userBasket = basketService.getUserBasket();
        assertThat(userBasket.getItems()).hasSize(2);
        assertThat(userBasket.getTotal()).isEqualTo(2 * 999.99 + 899.99);

        assertThrows(IllegalArgumentException.class, () -> {
            basketService.addProduct("non-existent");
        });
    }
}
