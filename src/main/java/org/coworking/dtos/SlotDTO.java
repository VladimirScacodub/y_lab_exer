package org.coworking.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @ApiModelProperty(example = "2024-06-26 12:00")
    private String start;

    /**
     * Конец бронирования
     */
    @ApiModelProperty(example = "2024-06-26 14:00")
    private String end;

}
