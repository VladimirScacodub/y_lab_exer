package org.coworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность отвечающие за данные об бронированнии разных мест
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookedPlace {

    /**
     * идентификатор места
     */
    private int id;

    /**
     * Конкретное место
     */
    private Place place;

    /**
     * Конкретный пользователь
     */
    private User user;

    /**
     * Занятый временой слот
     */
    Slot slot;

    /**
     * Отображение объекта BookedPlace в String
     * @return String Отображение BookedPlace
     */

    @Override
    public String toString() {
        return "BookedPlace{" +
                "Time slot = " + slot +
                ", user=" + user +
                ", place=" + place +
                '}';
    }
}
