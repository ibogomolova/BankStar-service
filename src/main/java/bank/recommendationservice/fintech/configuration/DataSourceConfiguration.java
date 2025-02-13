package bank.recommendationservice.fintech.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    // Бин для первой базы данных (H2)
    @Bean(name = "recommendationsServiceDataSource")
    public DataSource recommendationsServiceDataSource(
            @Value("${application.fintech_service-db.url}") String serviceRecommendationsUrl) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceRecommendationsUrl);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setReadOnly(true);
        return dataSource;
    }

    // Бин для второй базы данных (PostgreSQL)
    @Primary
    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource defaultDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    // JdbcTemplate для первой базы данных
    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(
            @Qualifier("recommendationsServiceDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

