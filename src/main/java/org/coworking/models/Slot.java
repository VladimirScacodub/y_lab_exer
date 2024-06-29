package org.coworking.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Временой слот, который содержит временные границы бранирования места
 */
@Getter
@Builder
@AllArgsConstructor
public class Slot implements Comparable<Slot> {

    /**
     * Идентификатор слота
     */
    private int id;

    /**
     * Начало бронирования
     */
    private LocalDateTime start;

    /**
     * Конец бронирования
     */
    private LocalDateTime end;

    /**
     * Отображение объекта Slot в String
     *
     * @return String Отображение Slot
     */
    @Override
    public String toString() {
        return "From " + start.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")) + " to " + end.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    /**
     * Метод сравнивающий два объекта на равность
     *
     * @param o другой объект
     * @return true если объекты равны, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slot slot = (Slot) o;
        return Objects.equals(start, slot.start) && Objects.equals(end, slot.end);
    }

    /**
     * Отображеие текущего объекта в int
     *
     * @return hashcode текущего объекта
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(start);
        result = 31 * result + Objects.hashCode(end);
        return result;
    }

    /**
     * Метод стравнения двух объектов Slot
     *
     * @param o объект для сравнения.
     * @return 0 если два объекта равны, положительное число
     * если вызывающий объект больше объекта, переданного в качестве параметра
     * отрицательное, если вызывающий объект меньше объекта, переданного в качестве параметра
     */
    @Override
    public int compareTo(Slot o) {
        return start.compareTo(o.getStart());
    }
}
