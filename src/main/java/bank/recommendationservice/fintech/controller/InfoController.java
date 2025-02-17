package bank.recommendationservice.fintech.controller;


import bank.recommendationservice.fintech.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
@Tag(name = "Management", description = "Эндпоинт для получение информации о системе и сброса кэша")
public class InfoController {
    private final BuildProperties buildProperties;
    private final CacheService cacheService;

    public InfoController(BuildProperties buildProperties, CacheService cacheService) {
        this.buildProperties = buildProperties;
        this.cacheService = cacheService;
    }

    /**
     * @return строка, содержащая имя и версию приложения
     */
    @GetMapping("/info")
    @Operation(summary = "Получение названия и версии системы", description = "Возвращает название и версию системы")
    public String info() {
        return "name = " + buildProperties.getName() + ", version = " + buildProperties.getVersion();
    }

    /**
     * Сбрасывает все кэши
     */
    @PostMapping("/clear-caches")
    @Operation(summary = "Сброс кеша всех запросов", description = "Сбрасывается кеш всех запросов в системе ")
    public ResponseEntity<String> clearCaches() {
        cacheService.clearCaches();
        return new ResponseEntity<>("Кеш успешно очищен.", HttpStatus.OK);
    }
}