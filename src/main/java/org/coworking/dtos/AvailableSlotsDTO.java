package org.coworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Клас объектов для передачи данных о доступных слотах
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableSlotsDTO {

    /**
     * DTO места
     */
    private PlaceDTO placeDTO;

    /**
     * Список доступных слотов для одного места
     */
    private List<SlotDTO> slotDTOS;

}
