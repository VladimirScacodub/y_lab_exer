package org.coworking.Utils.mappers;

import javax.annotation.processing.Generated;
import org.coworking.dtos.PlaceDTO;
import org.coworking.models.Place;
import org.coworking.models.enums.PlaceType;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-14T15:35:48+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
public class PlaceMapperImpl implements PlaceMapper {

    @Override
    public PlaceDTO placeToPlaceDto(Place place) {
        if ( place == null ) {
            return null;
        }

        PlaceDTO.PlaceDTOBuilder placeDTO = PlaceDTO.builder();

        placeDTO.id( place.getId() );
        placeDTO.placeName( place.getPlaceName() );
        if ( place.getPlaceType() != null ) {
            placeDTO.placeType( place.getPlaceType().name() );
        }

        return placeDTO.build();
    }

    @Override
    public Place placeDtoToPlace(PlaceDTO placeDTO) {
        if ( placeDTO == null ) {
            return null;
        }

        Place.PlaceBuilder place = Place.builder();

        place.id( placeDTO.getId() );
        place.placeName( placeDTO.getPlaceName() );
        if ( placeDTO.getPlaceType() != null ) {
            place.placeType( Enum.valueOf( PlaceType.class, placeDTO.getPlaceType() ) );
        }

        return place.build();
    }
}
