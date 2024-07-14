package org.coworking.Utils.mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.coworking.dtos.SlotDTO;
import org.coworking.models.Slot;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-14T15:35:47+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
public class SlotMapperImpl implements SlotMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_01172057030 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm" );

    @Override
    public Slot slotDtoToSlot(SlotDTO slotDTO) {
        if ( slotDTO == null ) {
            return null;
        }

        Slot.SlotBuilder slot = Slot.builder();

        if ( slotDTO.getStart() != null ) {
            slot.start( LocalDateTime.parse( slotDTO.getStart(), dateTimeFormatter_yyyy_MM_dd_HH_mm_01172057030 ) );
        }
        if ( slotDTO.getEnd() != null ) {
            slot.end( LocalDateTime.parse( slotDTO.getEnd(), dateTimeFormatter_yyyy_MM_dd_HH_mm_01172057030 ) );
        }
        slot.id( slotDTO.getId() );

        return slot.build();
    }

    @Override
    public SlotDTO slotToSlotDto(Slot slot) {
        if ( slot == null ) {
            return null;
        }

        SlotDTO.SlotDTOBuilder slotDTO = SlotDTO.builder();

        if ( slot.getStart() != null ) {
            slotDTO.start( dateTimeFormatter_yyyy_MM_dd_HH_mm_01172057030.format( slot.getStart() ) );
        }
        if ( slot.getEnd() != null ) {
            slotDTO.end( dateTimeFormatter_yyyy_MM_dd_HH_mm_01172057030.format( slot.getEnd() ) );
        }
        slotDTO.id( slot.getId() );

        return slotDTO.build();
    }
}
