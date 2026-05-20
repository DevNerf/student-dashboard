package com.studentdashboard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //обработка для стандартных запросов
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception ex) {
        logger.error("=== НЕОБРАБОТАННАЯ ОШИБКА ===");
        logger.error("URL: {}", request.getRequestURL());
        logger.error("Метод: {}", request.getMethod());
        logger.error("Сообщение: {}", ex.getMessage());
        logger.error("Тип ошибки: {}", ex.getClass().getName());
        logger.error("Стек вызовов:", ex);  // ← полный стек ошибки

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "Внутренняя ошибка сервера");
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    //обработка для ajax-запросов
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleAjaxException(HttpServletRequest request, RuntimeException ex) {
        String requestedWith = request.getHeader("X-Requested-With");

        if ("XMLHttpRequest".equals(requestedWith)) {
            logger.error("AJAX ОШИБКА");
            logger.error("URL: {}", request.getRequestURL());
            logger.error("Сообщение: {}", ex.getMessage());
            logger.error("Стек:", ex);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка сервера: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        throw ex;
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ModelAndView handleAccessDenied(HttpServletRequest request, Exception ex) {
        logger.warn("ДОСТУП ЗАПРЕЩЁН!");
        logger.warn("URL: {}", request.getRequestURL());
        logger.warn("Пользователь: {}", request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "неизвестен");

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "У вас нет доступа к этой странице");
        return mav;
    }

    //обработчик для 404
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ModelAndView handleNotFound(HttpServletRequest request, Exception ex) {
        logger.warn("СТРАНИЦА НЕ НАЙДЕНА!");
        logger.warn("URL: {}", request.getRequestURL());

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "Страница не найдена");
        return mav;
    }
}