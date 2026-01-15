package controller;

import entity.DynamicRuleEntity;
import service.DynamicRuleService;
import service.ValidationService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rules")
class RuleController {
    @Autowired
    private DynamicRuleService ruleService;
    @Autowired
    private ValidationService validationService;

    @PostMapping
    public ResponseEntity<?> createRule(@RequestBody DynamicRuleEntity rule) {
        try {
            DynamicRuleEntity createdRule = ruleService.createRule(rule);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRule);
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

    @GetMapping
    public ResponseEntity<?> getAllRules(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String productName,
            Pageable pageable) {
        try {
            if (active != null && active) {
                List<DynamicRuleEntity> activeRules = ruleService.getAllActiveRules();
                return ResponseEntity.ok(Map.of(
                        "data", activeRules,
                        "total", activeRules.size()
                ));
            } else if (productName != null && !productName.trim().isEmpty()) {
                Page<DynamicRuleEntity> rulesPage = ruleService.getRulesPage(pageable);
                return ResponseEntity.ok(Map.of(
                        "data", rulesPage.getContent(),
                        "total", rulesPage.getTotalElements()
                ));
            } else {
                Page<DynamicRuleEntity> rulesPage = ruleService.getRulesPage(pageable);
                return ResponseEntity.ok(Map.of(
                        "data", rulesPage.getContent(),
                        "total", rulesPage.getTotalElements()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRuleById(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            return ruleService.getRuleById(ruleId)
                    .map(rule -> ResponseEntity.ok(rule))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            Map.of("error", "Правило не найдено", "message", "Правило с ID " + id + " не существует")
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRule(@PathVariable String id,
                                        @RequestBody DynamicRuleEntity updatedRule) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            DynamicRuleEntity rule = ruleService.updateRule(ruleId, updatedRule);
            return ResponseEntity.ok(rule);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            ruleService.deleteRule(ruleId);
            return ResponseEntity.noContent().build();
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

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateRule(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            ruleService.activateRule(ruleId);
            return ResponseEntity.ok().build();
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

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateRule(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            ruleService.deactivateRule(ruleId);
            return ResponseEntity.ok().build();
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

    @GetMapping("/statistics")
    public ResponseEntity<?> getRuleStatistics() {
        try {
            DynamicRuleService.RuleStatistics stats = ruleService.getRuleStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Внутренняя ошибка сервера", "message", e.getMessage())
            );
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<?> checkRuleExists(@PathVariable String id) {
        try {
            UUID ruleId = validationService.validateAndParseUuid(id);
            boolean exists = ruleService.ruleExists(ruleId);
            if (exists) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
