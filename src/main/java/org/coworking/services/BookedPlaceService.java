package org.coworking.services;

import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.models.User;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для хранения и работы с бронированием мест
 */
public class BookedPlaceService {

    /**
     * Сервис для работы с данными рабочих мест и конференц залов
     */
    PlaceService placeService;

    /**
     * Хранилище для данных бронирования рабочих мест и конференц-залов
     */
    private static List<BookedPlace> bookedPlaces = new ArrayList<>();

    /**
     * Генератор идентификаторов для объектов BookedPlace
     */
    private static int lastId = 1;

    public BookedPlaceService(PlaceService placeService, UserService userService) {
        this.placeService = placeService;
    }

    /**
     * Возвращает все данные о бронировании мест
     * @return сипок BookedPlace
     */
    public List<BookedPlace> getAllBookedPlaces() {
        return bookedPlaces;
    }

    /**
     * Возвращает все данные о бронированных местах связанных с указанным пользователем
     * @param user - пользователь по которому будет происходить поиск
     * @return список BookedPlace пользователя
     */
    public List<BookedPlace> getAllBookedPlacesByUser(User user) {
        return getAllBookedPlaces().stream()
                .filter(bookedPlace -> Objects.equals(bookedPlace.getUser(), user))
                .collect(Collectors.toList());
    }

    /**
     * Отмена бронирования
     * @param id - id бронирования
     * @throws BookedPlaceConflictsException если бронирования с таким id не существует
     */
    public void cancelBooking(int id) throws BookedPlaceConflictsException {
        bookedPlaces.remove(findById(id));
    }

    /**
     * ищет бронирование по указанному ID
     * @param id - id, для поиска бронирования
     * @return BookedPlace объект с указныым ID
     * @throws BookedPlaceConflictsException bookedPlace с таким ID не существует
     */
    public BookedPlace findById(int id) throws BookedPlaceConflictsException {
        return bookedPlaces.stream()
                .filter(bookedPlace -> bookedPlace.getId() == id)
                .findAny()
                .orElseThrow(() -> new BookedPlaceConflictsException("Бронированый слот с таким ID не существует"));
    }

    /**
     * Бронирует конкретное место для пользователя начиная с определенной даты и заканчивая с другой определенной даты
     * @param place - конкретное место
     * @param user - пользователь, бронирующий место
     * @param from - дата начала бронирования
     * @param to - дата окончания бронирования
     */
    public void bookPlace(Place place, User user, LocalDateTime from, LocalDateTime to) {
        var bookedPlace = BookedPlace.builder()
                .id(lastId++)
                .user(user)
                .place(place)
                .slot(new Slot(from, to))
                .build();
        bookedPlaces.add(bookedPlace);
    }


    /**
     * Удаляет Place и все связанные с ним BookedPlace
     * @param nameOfPlace - имя удаляемого Place
     * @throws PlaceNamingException если такого Place не существует
     */
    public void cancelBookingWithRemovingPlace(String nameOfPlace) throws PlaceNamingException {
        placeService.removePlace(nameOfPlace);
        bookedPlaces.removeIf(bookedPlace -> Objects.equals(bookedPlace.getPlace().getPlaceName(), nameOfPlace));
    }

    /**
     * Вычисляет доступные слоты для определенного рабочего места по определенной дате
     * @param place - Рабочее место или конференц зал
     * @param date  - Дата для которой будет вычесленн список свободных мест
     * @return Спиок свободных слотов для определенного рабочего места за определенную дату
     */
    public List<Slot> getAvailableSlots(Place place, LocalDateTime date) {
        List<BookedPlace> bookingsOfPlace = bookedPlaces.stream()
                .filter(bookedPlace -> bookedPlace.getPlace().equals(place))
                .toList();
        List<Slot> availableSlots = new ArrayList<>();
        final LocalTime startOfDay = LocalTime.of(8, 0);
        final LocalTime endOfDay = LocalTime.of(20, 0);

        LocalDateTime currentStart = date.with(startOfDay);
        LocalDateTime currentEnd = date.with(endOfDay);

        for (BookedPlace booking : bookingsOfPlace) {
            if (booking.getSlot().getStart().toLocalDate().equals(date.toLocalDate())) {
                if (currentStart.isBefore(booking.getSlot().getStart())) {
                    availableSlots.add(new Slot(currentStart, booking.getSlot().getStart()));
                }
                currentStart = booking.getSlot().getEnd();
            }
        }

        if (currentStart.isBefore(currentEnd)) {
            availableSlots.add(new Slot(currentStart, currentEnd));
        }

        return availableSlots;
    }

    /**
     * Возращает список всех бронирований сортированный по параметрам:
     * 1 - имя места
     * 2 - имя пользователя
     * 3 - слот
     * @param indexOfField индекс параметра, по которому будет проиходить сортировка
     * @return сортированный список BookedPlace
     * @throws BookedPlaceConflictsException если параметр был введен неправильно
     */
    public List<BookedPlace> getAllBookedPlacesSortedBy(String indexOfField) throws BookedPlaceConflictsException {
        Comparator<BookedPlace> bookedPlaceComparator;

        switch (indexOfField) {
            case "1": {
                bookedPlaceComparator = Comparator.comparing(BookedPlace::getPlace);
                break;
            }
            case "2": {
                bookedPlaceComparator = Comparator.comparing(BookedPlace::getUser);
                break;
            }
            case "3": {
                bookedPlaceComparator = Comparator.comparing(BookedPlace::getSlot);
                break;
            }
            default: {
                throw new BookedPlaceConflictsException("Такого параметра не существует!");
            }
        }

        return getAllBookedPlaces().stream().sorted(bookedPlaceComparator).collect(Collectors.toList());
    }
}
