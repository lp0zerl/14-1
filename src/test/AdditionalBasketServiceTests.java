import service.BasketService;
import service.StorageService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AdditionalBasketServiceTests {

    @Mock
    private StorageService storageService;

    @Mock
    private ProductBasket productBasket;

    @InjectMocks
    private BasketService basketService;

    @Test
    void testEmptyBasketAfterClear() {
        when(productBasket.getProducts()).thenReturn(Collections.emptyMap());

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getItems()).isEmpty();
        assertThat(userBasket.getTotal()).isZero();
    }

    @Test
    void testProductWithDecimalPrice() {
        String productId = "decimal-product";
        Product product = new Product(productId, "Decimal Product", 19.99);

        Map<String, Integer> basketProducts = new HashMap<>();
        basketProducts.put(productId, 3);

        when(productBasket.getProducts()).thenReturn(basketProducts);
        when(storageService.getProductById(productId)).thenReturn(Optional.of(product));

        UserBasket userBasket = basketService.getUserBasket();

        assertThat(userBasket.getTotal()).isEqualTo(19.99 * 3);
    }

    @Test
    void testVerifyInteractionOrder() {
        String productId = "test-product";
        Product product = new Product(productId, "Test", 50.0);

        when(storageService.getProductById(productId)).thenReturn(Optional.of(product));

        basketService.addProduct(productId);

        InOrder inOrder = inOrder(storageService, productBasket);
        inOrder.verify(storageService).getProductById(productId);
        inOrder.verify(productBasket).addProduct(productId);
    }
}
