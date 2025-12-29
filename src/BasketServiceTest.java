import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// ==================== ТЕСТЫ ДЛЯ СЕРВИСА КОРЗИНЫ ====================
@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private ProductBasket productBasket;

    @InjectMocks
    private BasketService basketService;

    @Test
    void testAddNonExistentProduct() {
        String invalidId = "non-existent";
        when(storageService.getProductById(invalidId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            basketService.addProduct(invalidId);
        });

        verify(storageService, times(1)).getProductById(invalidId);
        verify(productBasket, never()).addProduct(anyString());
    }

    @Test
    void testAddExistingProduct() {
        String validId = "product-123";
        Product product = new Product(validId, "Test Product", 99.99);

        when(storageService.getProductById(validId)).thenReturn(Optional.of(product));

        basketService.addProduct(validId);

        verify(storageService, times(1)).getProductById(validId);
        verify(productBasket, times(1)).addProduct(validId);
    }

    @Test
    void testGetEmptyUserBasket() {
        when(productBasket.getProducts()).thenReturn(Collections.emptyMap());

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getItems()).isEmpty();
        assertThat(userBasket.getTotal()).isZero();
        verify(productBasket, times(1)).getProducts();
        verify(storageService, never()).getProductById(anyString());
    }

    @Test
    void testGetUserBasketWithProducts() {
        String productId1 = "product-1";
        String productId2 = "product-2";

        Product product1 = new Product(productId1, "Product 1", 100.0);
        Product product2 = new Product(productId2, "Product 2", 50.0);

        Map<String, Integer> basketProducts = new HashMap<>();
        basketProducts.put(productId1, 2);
        basketProducts.put(productId2, 3);

        when(productBasket.getProducts()).thenReturn(basketProducts);
        when(storageService.getProductById(productId1)).thenReturn(Optional.of(product1));
        when(storageService.getProductById(productId2)).thenReturn(Optional.of(product2));

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getItems()).hasSize(2);
        assertThat(userBasket.getTotal()).isEqualTo(2 * 100.0 + 3 * 50.0);

        verify(productBasket, times(1)).getProducts();
        verify(storageService, times(1)).getProductById(productId1);
        verify(storageService, times(1)).getProductById(productId2);
    }

    @Test
    void testGetUserBasketWithMissingProduct() {
        String productId1 = "product-1";
        String productId2 = "missing-product";

        Product product1 = new Product(productId1, "Product 1", 100.0);

        Map<String, Integer> basketProducts = new HashMap<>();
        basketProducts.put(productId1, 2);
        basketProducts.put(productId2, 1);

        when(productBasket.getProducts()).thenReturn(basketProducts);
        when(storageService.getProductById(productId1)).thenReturn(Optional.of(product1));
        when(storageService.getProductById(productId2)).thenReturn(Optional.empty());

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getItems()).hasSize(1);
        assertThat(userBasket.getTotal()).isEqualTo(2 * 100.0);

        verify(productBasket, times(1)).getProducts();
        verify(storageService, times(1)).getProductById(productId1);
        verify(storageService, times(1)).getProductById(productId2);
    }

    @Test
    void testAddSameProductMultipleTimes() {
        String productId = "product-123";
        Product product = new Product(productId, "Test Product", 99.99);

        when(storageService.getProductById(productId)).thenReturn(Optional.of(product));

        basketService.addProduct(productId);
        basketService.addProduct(productId);
        basketService.addProduct(productId);

        verify(storageService, times(3)).getProductById(productId);
        verify(productBasket, times(3)).addProduct(productId);
    }

    @Test
    void testGetUserBasketWithZeroPriceProduct() {
        String productId = "free-product";
        Product freeProduct = new Product(productId, "Free Product", 0.0);

        Map<String, Integer> basketProducts = new HashMap<>();
        basketProducts.put(productId, 5);

        when(productBasket.getProducts()).thenReturn(basketProducts);
        when(storageService.getProductById(productId)).thenReturn(Optional.of(freeProduct));

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getItems()).hasSize(1);
        assertThat(userBasket.getTotal()).isZero();

        verify(productBasket, times(1)).getProducts();
        verify(storageService, times(1)).getProductById(productId);
    }

    @Test
    void testGetUserBasketWithLargeQuantities() {
        String productId = "bulk-product";
        Product product = new Product(productId, "Bulk Product", 10.0);

        Map<String, Integer> basketProducts = new HashMap<>();
        basketProducts.put(productId, 1000);

        when(productBasket.getProducts()).thenReturn(basketProducts);
        when(storageService.getProductById(productId)).thenReturn(Optional.of(product));

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getItems()).hasSize(1);
        assertThat(userBasket.getTotal()).isEqualTo(1000 * 10.0);

        verify(productBasket, times(1)).getProducts();
        verify(storageService, times(1)).getProductById(productId);
    }

    @Test
    void testExceptionMessageWhenAddingNonExistentProduct() {
        String invalidId = "non-existent-456";
        when(storageService.getProductById(invalidId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            basketService.addProduct(invalidId);
        });

        assertThat(exception.getMessage()).contains(invalidId);
        verify(storageService, times(1)).getProductById(invalidId);
        verify(productBasket, never()).addProduct(anyString());
    }

    @Test
    void testUserBasketItemProperties() {
        String productId = "test-product";
        Product product = new Product(productId, "Test Product", 25.5);

        Map<String, Integer> basketProducts = new HashMap<>();
        basketProducts.put(productId, 4);

        when(productBasket.getProducts()).thenReturn(basketProducts);
        when(storageService.getProductById(productId)).thenReturn(Optional.of(product));

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getItems()).hasSize(1);
        BasketItem item = userBasket.getItems().get(0);

        assertThat(item.getProduct()).isEqualTo(product);
        assertThat(item.getQuantity()).isEqualTo(4);
        assertThat(item.getTotal()).isEqualTo(25.5 * 4);

        verify(productBasket, times(1)).getProducts();
        verify(storageService, times(1)).getProductById(productId);
    }

    @Test
    void testMultipleProductAdditionsAndBasketRetrieval() {
        String productId1 = "prod-1";
        String productId2 = "prod-2";
        String productId3 = "prod-3";

        Product product1 = new Product(productId1, "Product 1", 10.0);
        Product product2 = new Product(productId2, "Product 2", 20.0);
        Product product3 = new Product(productId3, "Product 3", 30.0);

        when(storageService.getProductById(productId1)).thenReturn(Optional.of(product1));
        when(storageService.getProductById(productId2)).thenReturn(Optional.of(product2));
        when(storageService.getProductById(productId3)).thenReturn(Optional.of(product3));

        basketService.addProduct(productId1);
        basketService.addProduct(productId2);
        basketService.addProduct(productId1);
        basketService.addProduct(productId3);

        Map<String, Integer> basketProducts = new HashMap<>();
        basketProducts.put(productId1, 2);
        basketProducts.put(productId2, 1);
        basketProducts.put(productId3, 1);

        when(productBasket.getProducts()).thenReturn(basketProducts);

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getItems()).hasSize(3);
        assertThat(userBasket.getTotal()).isEqualTo(2 * 10.0 + 20.0 + 30.0);

        verify(storageService, times(4)).getProductById(anyString());
        verify(productBasket, times(4)).addProduct(anyString());
        verify(productBasket, times(1)).getProducts();
    }
}
