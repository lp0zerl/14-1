import java.util.Arrays;
import java.util.List;

// ==================== ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ ====================
@ExtendWith(MockitoExtension.class)
class AdditionalSearchServiceTests {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private SearchService searchService;

    @Test
    void testSearchWithSpecialCharacters() {
        List<Product> products = Arrays.asList(
                new Product("1", "Product#Special", 100.0),
                new Product("2", "Normal Product", 200.0)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results = searchService.searchProducts("#Special");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Product#Special");
    }

    @Test
    void testSearchWithSpacesInPattern() {
        List<Product> products = Arrays.asList(
                new Product("1", "Apple iPhone 13", 999.99),
                new Product("2", "Samsung Galaxy S22", 899.99)
        );

        when(storageService.getAllProducts()).thenReturn(products);

        List<Product> results = searchService.searchProducts("iPhone 13");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Apple iPhone 13");
    }
}
