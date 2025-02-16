package bank.recommendationservice.fintech.controller;

import bank.recommendationservice.fintech.service.CacheService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InfoController.class)
public class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CacheService cacheService;

    @MockBean
    private BuildProperties buildProperties;

    @Test
    void testInfo() throws Exception {
        // Настройка мок-объекта для buildProperties
        when(buildProperties.getName()).thenReturn("fintech");
        when(buildProperties.getVersion()).thenReturn("0.0.1-SNAPSHOT");

        // Выполнение GET-запроса и проверка ответа
        mockMvc.perform(get("/management/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("name = fintech, version = 0.0.1-SNAPSHOT"));
    }

    @Test
    void testClearCaches() throws Exception {
        // Выполнение POST-запроса и проверка ответа
        mockMvc.perform(post("/management/clear-caches")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Кеш успешно очищен."));

        // Проверка, что метод clearCaches был вызван
        Mockito.verify(cacheService).clearCaches();
    }
}
