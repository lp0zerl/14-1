package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// ==================== СЕРВИС КОРЗИНЫ ====================
class BasketService {
    private final StorageService storageService;
    private final ProductBasket productBasket;

    public BasketService(StorageService storageService, ProductBasket productBasket) {
        this.storageService = storageService;
        this.productBasket = productBasket;
    }

    public void addProduct(String productId) {
        Optional<Product> product = storageService.getProductById(productId);
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Товар с ID " + productId + " не найден");
        }
        productBasket.addProduct(productId);
    }

    public UserBasket getUserBasket() {
        Map<String, Integer> basketProducts = productBasket.getProducts();
        List<BasketItem> items = new ArrayList<>();
        double total = 0.0;

        for (Map.Entry<String, Integer> entry : basketProducts.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();

            Optional<Product> productOpt = storageService.getProductById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BasketItem item = new BasketItem(product, quantity);
                items.add(item);
                total += product.getPrice() * quantity;
            }
        }

        return new UserBasket(items, total);
    }
}
