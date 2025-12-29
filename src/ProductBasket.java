import java.util.Map;

// ==================== КОРЗИНА ПОКУПОК ====================
interface ProductBasket {
    void addProduct(String productId);

    Map<String, Integer> getProducts();

    void clear();
}
