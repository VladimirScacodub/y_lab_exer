package org.coworking.Utils.mappers;

import org.coworking.annotations.Loggable;
import org.coworking.dtos.SlotDTO;
import org.coworking.models.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Класс Mapper, который переводит DTO в Slot и обратно
 */
@Loggable
@Mapper
public interface SlotMapper {

    /**
     * Экземпляр данного Mapper
     */
    SlotMapper INSTANCE = Mappers.getMapper(SlotMapper.class);

    /**
     * Трансформация SlotDTO в Slot
     *
     * @param slotDTO DTO, содержащий данные о временном слоте
     * @return Slot объект
     */
    @Mapping(target = "start", source = "slotDTO.start", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "end", source = "slotDTO.end", dateFormat = "yyyy-MM-dd HH:mm")
    Slot slotDtoToSlot(SlotDTO slotDTO);

    /**
     * Трансформация Slot в SlotDTO
     *
     * @param slot объект, содержащий данные о временном слоте
     * @return SlotDTO объект
     */
    @Mapping(target = "start", source = "slot.start", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "end", source = "slot.end", dateFormat = "yyyy-MM-dd HH:mm")
    SlotDTO slotToSlotDto(Slot slot);

}
