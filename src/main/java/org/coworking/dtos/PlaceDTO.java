package org.coworking.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @ApiModelProperty(example = "Workplace_example")
    private String placeName;

    /**
     * Тип места
     */
    @ApiModelProperty(example = "WORKPLACE")
    private String placeType;

}