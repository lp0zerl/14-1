import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// ==================== ПОМОЩНИКИ ДЛЯ ТЕСТОВ ====================
class TestHelper {

    static Product createTestProduct(String name, double price) {
        return new Product(UUID.randomUUID().toString(), name, price);
    }

    static List<Product> createProductList(int count) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            products.add(new Product("id-" + i, "Product " + i, i * 10.0));
        }
        return products;
    }
}
