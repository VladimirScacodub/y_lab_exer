package org.coworking.dtos;

/**
 * Класс, который используется для передачи сообщения через HTTP ответ
 *
 * @param message сообщение
 */
public record MessageDTO(String message) {
}
