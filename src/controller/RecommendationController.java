package controller;

import service.RecommendationService;
import service.ValidationService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private ValidationService validationService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRecommendations(
            @PathVariable String userId,
            @RequestParam(required = false) Boolean debug) {
        try {
            UUID userUuid = validationService.validateAndParseUuid(userId);
            if (Boolean.TRUE.equals(debug)) {
                return ResponseEntity.ok(recommendationService.getRecommendations(userUuid));
            } else {
                return ResponseEntity.ok(recommendationService.getRecommendations(userUuid));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<?> getBatchRecommendations(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<String> userIds = (java.util.List<String>) request.get("userIds");
            if (userIds == null || userIds.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Ошибка валидации", "message", "Список userIds не может быть пустым")
                );
            }
            java.util.List<UUID> userUuids = userIds.stream()
                    .map(validationService::validateAndParseUuid)
                    .toList();
            Map<UUID, RecommendationService.RecommendationResult> results =
                    recommendationService.getBatchRecommendations(userUuids);
            return ResponseEntity.ok(Map.of(
                    "data", results,
                    "total", results.size()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Ошибка валидации", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "service", "recommendation-service",
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", "DOWN", "error", e.getMessage())
            );
        }
    }
}
