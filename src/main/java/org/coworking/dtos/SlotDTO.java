package org.coworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Клас объектов для передачи данных о временных слотах
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlotDTO {

    /**
     * Идентификатор Слота
     */
    private int id;

    /**
     * Начало бронирования
     */
    private String start;

    /**
     * Конец бронирования
     */
    private String end;

}
