package bank.recommendationservice.fintech.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("${spring.application.name}")
    private String appName;

    @Value("${build.version}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()

                        .title("${spring.application.name}")
                        .version("${build.version}")
                        .description("API для управления новым сервисом Fintech - Recommended Program for SkyPro School"))

                .addTagsItem(new Tag().name("Health Check").description("Эндпоинты для проверки работоспособности приложения"))
                .addTagsItem(new Tag().name("Recommendation").description("Эндпоинты для управления рекомендациями"))
                .addTagsItem(new Tag().name("Dynamic Rule").description("Эндпоинты для управления динамическими правилами"));

    }
}