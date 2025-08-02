package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ErrorHandler.class, ErrorHandlerTest.TestController.class})
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TestController testController;

    @Test
    void whenThrowNotFoundException_thenReturns404AndCorrectMessage() throws Exception {
        doThrow(new NotFoundException("Объект не найден."))
                .when(testController).testMethod();
        mockMvc.perform(get("/test"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Искомый объект не найден."))
                .andExpect(jsonPath("$.message").value("Объект не найден."));
    }

    @Test
    void whenThrowValidationException_thenReturns400AndCorrectMessage() throws Exception {
        doThrow(new ValidationException("Проверка не пройдена."))
                .when(testController).testMethod();
        mockMvc.perform(get("/test"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка при валидации данных."))
                .andExpect(jsonPath("$.message").value("Проверка не пройдена."));
    }

    @Test
    void whenThrowConflictException_thenReturns409AndCorrectMessage() throws Exception {
        doThrow(new ConflictException("Конфликт данных."))
                .when(testController).testMethod();
        mockMvc.perform(get("/test"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Конфликт данных."))
                .andExpect(jsonPath("$.message").value("Конфликт данных."));
    }

    @Test
    void whenThrowAccessDeniedException_thenReturns403AndCorrectMessage() throws Exception {
        doThrow(new AccessDeniedException("Доступ запрещен."))
                .when(testController).testMethod();
        mockMvc.perform(get("/test"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Доступ запрещен."))
                .andExpect(jsonPath("$.message").value("Доступ запрещен."));
    }

    @Test
    void whenThrowGenericException_thenReturns500AndCorrectMessage() throws Exception {
        doThrow(new RuntimeException("Непредвиденная ошибка."))
                .when(testController).testMethod();
        mockMvc.perform(get("/test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Возникло исключение."))
                .andExpect(jsonPath("$.message").value("Непредвиденная ошибка."));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping
        public void testMethod() {
        }
    }
}