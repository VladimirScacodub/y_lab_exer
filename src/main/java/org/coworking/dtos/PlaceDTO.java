package org.coworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coworking.models.enums.PlaceType;

/**
 * Клас объектов для передачи данных о местах
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDTO {

    /**
     * Идентификатор места
     */
    private int id;

    /**
     * имя места
     */
    private String placeName;

    /**
     * Тип места
     */
    private String placeType;

}