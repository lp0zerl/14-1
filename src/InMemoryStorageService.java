import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class InMemoryStorageService implements StorageService {
    private final Map<String, Product> products = new HashMap<>();

    @Override
    public Collection<Product> getAllProducts() {
        return products.values();
    }

    @Override
    public Optional<Product> getProductById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }
}
