package service;

import java.util.Collection;
import java.util.Optional;

// ==================== СЕРВИС ХРАНИЛИЩА ====================
interface StorageService {
    Collection<Product> getAllProducts();

    Optional<Product> getProductById(String id);
}
