package org.coworking.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Клас объектов для передачи данных о бронировании
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedPlaceDTO {

    /**
     * id бронирования
     */
    private int id;

    /**
     * Данные о месте бронирования
     */
    private PlaceDTO placeDTO;

    /**
     * Данные о пользователе
     */
    @ApiModelProperty(hidden = true)
    private UserDTO userDTO;

    /**
     * Данные о временом слоте бронирования
     */
    private SlotDTO slotDTO;

}
