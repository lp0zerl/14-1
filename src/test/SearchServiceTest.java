package test;

import service.SearchService;
import service.StorageService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// ==================== ТЕСТЫ ДЛЯ СЕРВИСА ПОИСКА ====================
@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private SearchService searchService;

    @Test
    void testSearchWithEmptyStorage() {
        when(storageService.getAllProducts()).thenReturn(Collections.emptyList());

        List<Product> results = searchService.searchProducts("Test");

        assertThat(results).isEmpty();
        verify(storageService, times(1)).getAllProducts();
    }

    @Test
    void testSearchWithNoMatchingProducts() {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple iPhone", 999.99),
                new Product("2", "Samsung Galaxy", 899.99),
                new Product("3", "Google Pixel", 799.99)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results = searchService.searchProducts("Xiaomi");

        assertThat(results).isEmpty();
        verify(storageService, times(1)).getAllProducts();
    }

    @Test
    void testSearchWithMatchingProduct() {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple iPhone", 999.99),
                new Product("2", "Samsung Galaxy", 899.99),
                new Product("3", "Google Pixel", 799.99)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results = searchService.searchProducts("Apple");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Apple iPhone");
        verify(storageService, times(1)).getAllProducts();
    }

    @Test
    void testSearchWithMultipleMatchingProducts() {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple iPhone 13", 999.99),
                new Product("2", "Apple MacBook Pro", 1999.99),
                new Product("3", "Samsung Galaxy", 899.99),
                new Product("4", "Apple Watch", 399.99)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results = searchService.searchProducts("Apple");

        assertThat(results).hasSize(3);
        verify(storageService, times(1)).getAllProducts();
    }

    @Test
    void testSearchWithEmptyPattern() {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple iPhone", 999.99),
                new Product("2", "Samsung Galaxy", 899.99)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results = searchService.searchProducts("");

        assertThat(results).isEmpty();
        verify(storageService, never()).getAllProducts();
    }

    @Test
    void testSearchWithNullPattern() {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple iPhone", 999.99),
                new Product("2", "Samsung Galaxy", 899.99)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results = searchService.searchProducts(null);

        assertThat(results).isEmpty();
        verify(storageService, never()).getAllProducts();
    }

    @Test
    void testSearchCaseInsensitive() {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple iPhone", 999.99),
                new Product("2", "Samsung Galaxy", 899.99)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results1 = searchService.searchProducts("apple");
        List<Product> results2 = searchService.searchProducts("APPLE");
        List<Product> results3 = searchService.searchProducts("Apple");

        assertThat(results1).hasSize(1);
        assertThat(results2).hasSize(1);
        assertThat(results3).hasSize(1);
        verify(storageService, times(3)).getAllProducts();
    }

    @Test
    void testSearchWithPartialMatch() {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple iPhone 13 Pro Max", 1299.99),
                new Product("2", "Samsung Galaxy S22 Ultra", 1199.99)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results = searchService.searchProducts("Pro");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Apple iPhone 13 Pro Max");
        verify(storageService, times(1)).getAllProducts();
    }
}
