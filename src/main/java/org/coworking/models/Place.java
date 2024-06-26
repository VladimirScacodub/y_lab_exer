package org.coworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.coworking.models.enums.PlaceType;

import java.util.Objects;

/**
 * Сущность отвечающая за данные о рабочих местах и конференц залов
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
public class Place implements Comparable<Place>{

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
    private PlaceType placeType;

    /**
     * Отображение объекта Place в String
     * @return Отображение Place
     */
    @Override
    public String toString() {
        return "placeName='" + placeName + '\'' +
                ", placeType=" + placeType;
    }

    /**
     * Метод сравнивающий два объекта на равность
     * @param o - другой объект
     * @return true если объекты равны, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;
        return id == place.id && Objects.equals(placeName, place.placeName) && placeType == place.placeType;
    }

    /**
     * Отображеие текущего объекта в int
     * @return hashcode текущего объекта
     */
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + Objects.hashCode(placeName);
        result = 31 * result + Objects.hashCode(placeType);
        return result;
    }

    /**
     * Метод стравнения двух объектов Place
     * @param o объект для сравнения.
     * @return 0 если два объекта равны, положительное число
     * если вызывающий объект больше объекта, переданного в качестве параметра
     * отрицательное, если вызывающий объект меньше объекта, переданного в качестве параметра
     */
    @Override
    public int compareTo(Place o) {
        return placeName.compareTo(o.placeName);
    }
}

