package org.coworking.services.validators;

import lombok.AllArgsConstructor;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.annotations.Loggable;
import org.coworking.dtos.BookedPlaceDTO;
import org.coworking.dtos.PlaceDTO;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.models.User;
import org.coworking.models.enums.Role;
import org.coworking.services.BookedPlaceService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * Класс бля валидации данных бронирования мест
 */
@Loggable
@AllArgsConstructor
@Component
public class BookedPlaceValidator {

    /**
     * Зависимость используемая для получения данных об бронировании мест
     */
    private BookedPlaceService bookedPlaceService;

    /**
     * Проверка на доступность пользователю удаления бронирования
     *
     * @param id   браонирования, которая должна быть удаленна
     * @param user пользователб желающий удалить запись
     * @throws BookedPlaceConflictsException если пользователю не разрещено удалять запись
     */
    public void validateCancelingBookedPlaceByNonAdmin(int id, User user) throws BookedPlaceConflictsException {
        if (!isBookedPlaceOfUser(id, user) && !Objects.equals(user.getRole(), Role.ADMIN)) {
            throw new BookedPlaceConflictsException("Удаление удаление данного BookedPlace невозможно из-за отсувствия записи или доступа к данной функции");
        }
    }

    /**
     * Проверяет если пользователб ответственен за бронирование места
     *
     * @param id   браонирования
     * @param user пользователь
     * @return true если пользователь ответственнен, false если нет
     */
    private boolean isBookedPlaceOfUser(int id, User user) {
        return bookedPlaceService.getAllBookedPlacesByUser(user).stream().anyMatch(bookedPlace -> bookedPlace.getId() == id);
    }

    /**
     * Валидация данных для нового бронирования доступного места
     *
     * @param place Место, которое нужно забронировать
     * @param from  дата начала бронирования
     * @param to    дата окончания бронирования
     * @throws BookedPlaceConflictsException если возникает конфликт бронирования
     */
    public void validateBookingPlace(Place place, LocalDateTime from, LocalDateTime to) throws BookedPlaceConflictsException {
        if (from.isAfter(to) || from.isEqual(to)) {
            throw new BookedPlaceConflictsException("Дата старта должна быть до даты конца букинга!");
        }

        if (someBookingConflicts(place, Slot.builder().start(from).end(to).build())) {
            throw new BookedPlaceConflictsException("Конфликт бронирования. Данное рабочее место уже было забронированно в данный промежуток времени!");
        }
    }

    /**
     * Валидация данных для нового бронирования доступного места
     *
     * @param bookedPlace
     * @throws BookedPlaceConflictsException если возникает конфликт бронирования
     */
    public void validateBookingPlace(BookedPlace bookedPlace) throws BookedPlaceConflictsException {
        Place place = bookedPlace.getPlace();
        LocalDateTime start = bookedPlace.getSlot().getStart();
        LocalDateTime end = bookedPlace.getSlot().getEnd();
        validateBookingPlace(place, start, end);
    }

    /**
     * Валидация временого формата для dateTime слота
     * @param dateTime строка содержащая время и дату
     * @throws BookedPlaceConflictsException если строка содержит неправильное время и дату
     */
    public void validateDateTimeFormat(String dateTime) throws BookedPlaceConflictsException {
        try {
            LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            throw new BookedPlaceConflictsException("Дата была введена не правильно");
        }
    }

    /**
     * Валидания полей из BookedPlaceDTO
     * @param bookedPlaceDTO объект содержащий данные об бронировании
     * @throws BookedPlaceConflictsException если есть конфликт бронирования
     */
    public void validateExistingBookedDtoFields(BookedPlaceDTO bookedPlaceDTO) throws BookedPlaceConflictsException {
        if (isNull(bookedPlaceDTO.getPlaceDTO())) {
            throw new BookedPlaceConflictsException("Требуетя указать placeDTO");
        }
        validateExistingPlaceDTOFields(bookedPlaceDTO.getPlaceDTO());
        if (isNull(bookedPlaceDTO.getSlotDTO())) {
            throw new BookedPlaceConflictsException("Требуется укзать временой слот slotDTO");
        }
    }

    /**
     * Валидация полей PlaceDTO
     * @param placeDTO объект содержащий данные о местах
     * @throws BookedPlaceConflictsException если возникает проблема с полями PlaceDTO
     */
    private static void validateExistingPlaceDTOFields(PlaceDTO placeDTO) throws BookedPlaceConflictsException {
        if (placeDTO.getId() == 0) {
            throw new BookedPlaceConflictsException("Требуетя указать id для placeDTO");
        }
        if (isNull(placeDTO.getPlaceName())) {
            throw new BookedPlaceConflictsException("Требуетя указать placeName для placeDTO");
        }
        if (isNull(placeDTO.getPlaceType())) {
            throw new BookedPlaceConflictsException("Требуетя указать placeType для placeDTO");
        }
    }

    /**
     * Определяет наличие конфликтов бронирования
     *
     * @param place место
     * @param slot  временой слот, включающий даты старта и окончания бронирования
     * @return true если есть конфликт для указанного место, иначе false
     */
    private boolean someBookingConflicts(Place place, Slot slot) {
        return bookedPlaceService.getAllBookedPlaces().stream()
                .anyMatch(bookedPlace -> Objects.equals(place, bookedPlace.getPlace()) &&
                        isSlotConflict(slot, bookedPlace.getSlot()));
    }

    /**
     * Определяет пересечение временых слотов
     *
     * @param newSlot      слот, для нового бронирования
     * @param existingSlot забронированный слот
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
     *
     * @param newSlot      новый слот
     * @param existingSlot уже существующий слот
     * @return true - если существующий слот это часть нового, иначе false
     */
    private boolean isExistingSlotInNewSlot(Slot newSlot, Slot existingSlot) {
        return existingSlot.getStart().isAfter(newSlot.getStart()) && existingSlot.getStart().isBefore(newSlot.getEnd());
    }

    /**
     * Проверяет, впадает ли конечная дата нового слота в существующий слот
     *
     * @param newSlot      новый слот
     * @param existingSlot уже существующий слот
     * @return true - если впадает, иначе false
     */
    private boolean isEndDateInExistingSlot(Slot newSlot, Slot existingSlot) {
        return newSlot.getEnd().isAfter(existingSlot.getStart()) && newSlot.getEnd().isBefore(existingSlot.getEnd());
    }

    /**
     * Проверяет, впадает ли начальная дата нового слота в существующий слот
     *
     * @param newSlot      новый слот
     * @param existingSlot уже существующий слот
     * @return true - если впадает, иначе false
     */
    private boolean isStartDateInExistingSlot(Slot newSlot, Slot existingSlot) {
        return newSlot.getStart().isAfter(existingSlot.getStart()) && newSlot.getStart().isBefore(existingSlot.getEnd());
    }

    /**
     * Првоеряет, совпадают ли начальные и конечные даты у нового и существующего слотов
     *
     * @param newSlot      новый слот
     * @param existingSlot уже существующий слот
     * @return true - если они одинаковые, иначе false
     */
    private boolean isNewSlotEqualToExistingSlot(Slot newSlot, Slot existingSlot) {
        return newSlot.getStart().isEqual(existingSlot.getStart()) || newSlot.getEnd().isEqual(existingSlot.getEnd());
    }
}