package org.coworking.Utils.mappers;

import org.coworking.annotations.Loggable;
import org.coworking.dtos.BookedPlaceDTO;
import org.coworking.dtos.PlaceDTO;
import org.coworking.dtos.SlotDTO;
import org.coworking.dtos.UserDTO;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Класс Mapper, который переводит DTO в BookedPlace и обратно
 */
@Loggable
@Mapper
public interface BookedPlaceMapper {

    /**
     * Экземпляр данного Mapper
     */
    BookedPlaceMapper INSTANCE = Mappers.getMapper(BookedPlaceMapper.class);

    /**
     * Трансформация BookedPlace в BookedPlaceDTO
     *
     * @param bookedPlace объект содержащй данные о бронировании
     * @return BookedPlaceDTO объект
     */
    default BookedPlaceDTO bookedPlaceToBookedPlaceDto(BookedPlace bookedPlace) {
        UserDTO userDTO = UserMapper.INSTANCE.userToUserDto(bookedPlace.getUser());
        PlaceDTO placeDTO = PlaceMapper.INSTANCE.placeToPlaceDto(bookedPlace.getPlace());
        SlotDTO slotDTO = SlotMapper.INSTANCE.slotToSlotDto(bookedPlace.getSlot());
        return BookedPlaceDTO.builder()
                .id(bookedPlace.getId())
                .userDTO(userDTO)
                .placeDTO(placeDTO)
                .slotDTO(slotDTO)
                .build();
    }

    /**
     * Трансформация BookedPlaceDTO в BookedPlace
     *
     * @param bookedPlaceDTO DTO объект содержащй данные о бронировании
     * @return BookedPlace объект
     */
    default BookedPlace bookedPlaceDtoToBookedPlace(BookedPlaceDTO bookedPlaceDTO) {
        User user = UserMapper.INSTANCE.userDtoToUser(bookedPlaceDTO.getUserDTO());
        Place place = PlaceMapper.INSTANCE.placeDtoToPlace(bookedPlaceDTO.getPlaceDTO());
        Slot slot = SlotMapper.INSTANCE.slotDtoToSlot(bookedPlaceDTO.getSlotDTO());
        return BookedPlace.builder()
                .id(bookedPlaceDTO.getId())
                .user(user)
                .place(place)
                .slot(slot)
                .build();
    }

}
