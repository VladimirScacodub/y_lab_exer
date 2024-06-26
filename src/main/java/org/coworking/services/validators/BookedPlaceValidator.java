package org.coworking.services.validators;

import lombok.AllArgsConstructor;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.services.BookedPlaceService;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс бля валидации данных бронирования мест
 */
@AllArgsConstructor
public class BookedPlaceValidator {

    /**
     * Зависимость используемая для получения данных об бронировании мест
     */
    BookedPlaceService bookedPlaceService;

    /**
     * Валидация данных для нового бронирования доступного места
     * @param place - Место, которое нужно забронировать
     * @param from - дата начала бронирования
     * @param to - дата окончания бронирования
     * @throws BookedPlaceConflictsException если возникает конфликт бронирования
     */
    public void validateBookingPlace(Place place, LocalDateTime from, LocalDateTime to) throws BookedPlaceConflictsException {
        if (from.isAfter(to) || from.isEqual(to)) {
            throw new BookedPlaceConflictsException("Дата старта должна быть до даты конца букинга!");
        }

        if (someBookingConflicts(place, new Slot(from, to))) {
            throw new BookedPlaceConflictsException("Конфликт бронирования. Данное рабочее место уже было забронированно в данный промежуток времени!");
        }
    }

    /**
     * Определяет наличие конфликтов бронирования
     * @param place - место
     * @param slot - временой слот, включающий даты старта и окончания бронирования
     * @return true если есть конфликт для указанного место, иначе false
     */
    private boolean someBookingConflicts(Place place, Slot slot) {
        return bookedPlaceService.getAllBookedPlaces().stream()
                .anyMatch(bookedPlace -> Objects.equals(place, bookedPlace.getPlace()) &&
                        isSlotConflict(slot, bookedPlace.getSlot()));
    }

    /**
     * Определяет пересечение временых слотов
     * @param newSlot - слот, для нового бронирования
     * @param existingSlot - забронированный слот
     * @return true - если конфликт существует, иначе false
     */
    private boolean isSlotConflict(Slot newSlot, Slot existingSlot) {
        return isStartDateInExistingSlot(newSlot, existingSlot) ||
                isEndDateInExistingSlot(newSlot, existingSlot) ||
                isNewSlotEqualToExistingSlot(newSlot, existingSlot) ||
                isExistingSlotInNewSlot(newSlot, existingSlot);
    }

    /**
     * Проверяет если временый границы нового слота содержат в себе все временные границы старого слота
     * @param newSlot - новый слот
     * @param existingSlot - уже существующий слот
     * @return true - если существующий слот это часть нового, иначе false
     */
    private boolean isExistingSlotInNewSlot(Slot newSlot, Slot existingSlot) {
        return existingSlot.getStart().isAfter(newSlot.getStart()) && existingSlot.getStart().isBefore(newSlot.getEnd());
    }

    /**
     * Проверяет, впадает ли конечная дата нового слота в существующий слот
     * @param newSlot - новый слот
     * @param existingSlot - уже существующий слот
     * @return true - если впадает, иначе false
     */
    private boolean isEndDateInExistingSlot(Slot newSlot, Slot existingSlot) {
        return newSlot.getEnd().isAfter(existingSlot.getStart()) && newSlot.getEnd().isBefore(existingSlot.getEnd());
    }

    /**
     * Проверяет, впадает ли начальная дата нового слота в существующий слот
     * @param newSlot - новый слот
     * @param existingSlot - уже существующий слот
     * @return true - если впадает, иначе false
     */
    private boolean isStartDateInExistingSlot(Slot newSlot, Slot existingSlot) {
        return newSlot.getStart().isAfter(existingSlot.getStart()) && newSlot.getStart().isBefore(existingSlot.getEnd());
    }

    /**
     * Првоеряет, совпадают ли начальные и конечные даты у нового и существующего слотов
     * @param newSlot - новый слот
     * @param existingSlot - - уже существующий слот
     * @return true - если они одинаковые, иначе false
     */
    private boolean isNewSlotEqualToExistingSlot (Slot newSlot, Slot existingSlot){
        return newSlot.getStart().isEqual(existingSlot.getStart()) || newSlot.getEnd().isEqual(existingSlot.getEnd());
    }
}