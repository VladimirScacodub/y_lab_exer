package org.coworking.Utils.mappers;

import org.coworking.annotations.Loggable;
import org.coworking.dtos.PlaceDTO;
import org.coworking.models.Place;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Класс Mapper, который переводит DTO в Place и обратно
 */
@Loggable
@Mapper
public interface PlaceMapper {
    /**
     * Экземпляр данного Mapper
     */
    PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);

    /**
     * Трансформация Place в PlaceDTO
     *
     * @param place объект содержащий информацию о месте
     * @return PlaceDTO объект
     */
    PlaceDTO placeToPlaceDto (Place place);

    /**
     * Трансформация PlaceDTO в Place
     *
     * @param placeDTO DTO, содержащее данное о месте
     * @return Place объект
     */
    Place placeDtoToPlace (PlaceDTO placeDTO);
}
