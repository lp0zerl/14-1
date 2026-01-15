package basket;

import java.util.HashMap;
import java.util.Map;

class InMemoryProductBasket implements ProductBasket {
    private final Map<String, Integer> products = new HashMap<>();

    @Override
    public void addProduct(String productId) {
        products.put(productId, products.getOrDefault(productId, 0) + 1);
    }

    @Override
    public Map<String, Integer> getProducts() {
        return new HashMap<>(products);
    }

    @Override
    public void clear() {
        products.clear();
    }
}
