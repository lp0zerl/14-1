package service;

import java.util.ArrayList;
import java.util.List;

// ==================== СЕРВИС ПОИСКА ====================
class SearchService {
    private final StorageService storageService;

    public SearchService(StorageService storageService) {
        this.storageService = storageService;
    }

    public List<Product> searchProducts(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> results = new ArrayList<>();
        String searchPattern = pattern.toLowerCase();

        for (Product product : storageService.getAllProducts()) {
            if (product.getName().toLowerCase().contains(searchPattern)) {
                results.add(product);
            }
        }

        return results;
    }
}
