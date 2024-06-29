package org.coworking.services;

import lombok.AllArgsConstructor;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.models.User;
import org.coworking.repositories.BookedPlaceRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для работы с бронированием мест
 */
@AllArgsConstructor
public class BookedPlaceService {

    /**
     * Сервис для работы с данными рабочих мест и конференц залов
     */
    private PlaceService placeService;

    /**
     * Репозиторий через который происходит работа с БД
     */
    private BookedPlaceRepository bookedPlaceRepository;

    /**
     * Возвращает все данные о бронировании мест
     *
     * @return сипок BookedPlace
     */
    public List<BookedPlace> getAllBookedPlaces() {
        return bookedPlaceRepository.findAll();
    }

    /**
     * Возвращает все данные о бронированных местах связанных с указанным пользователем
     *
     * @param user пользователь по которому будет происходить поиск
     * @return список BookedPlace пользователя
     */
    public List<BookedPlace> getAllBookedPlacesByUser(User user) {
        return bookedPlaceRepository.findAllByUser(user);
    }

    /**
     * Отмена бронирования
     *
     * @param id id бронирования
     * @throws BookedPlaceConflictsException если бронирования с таким id не существует
     */
    public void cancelBooking(int id) throws BookedPlaceConflictsException {
        findById(id);
        bookedPlaceRepository.removeById(id);
    }

    /**
     * ищет бронирование по указанному ID
     *
     * @param id id, для поиска бронирования
     * @return BookedPlace объект с указныым ID
     * @throws BookedPlaceConflictsException bookedPlace с таким ID не существует
     */
    public BookedPlace findById(int id) throws BookedPlaceConflictsException {
        return bookedPlaceRepository.findById(id)
                .orElseThrow(() -> new BookedPlaceConflictsException("Бронированый слот с таким ID не существует"));
    }

    /**
     * Бронирует конкретное место для пользователя начиная с определенной даты и заканчивая с другой определенной даты
     *
     * @param place конкретное место
     * @param user  пользователь, бронирующий место
     * @param from  дата начала бронирования
     * @param to    дата окончания бронирования
     */
    public void bookPlace(Place place, User user, LocalDateTime from, LocalDateTime to) {
        bookedPlaceRepository.save(place, user, from, to);
    }


    /**
     * Удаляет Place и все связанные с ним BookedPlace
     *
     * @param nameOfPlace имя удаляемого Place
     * @throws PlaceNamingException если такого Place не существует
     */
    public void cancelBookingWithRemovingPlace(String nameOfPlace) throws PlaceNamingException {
        placeService.removePlace(nameOfPlace);
    }

    /**
     * Вычисляет доступные слоты для определенного рабочего места по определенной дате
     *
     * @param place Рабочее место или конференц зал
     * @param date  Дата для которой будет вычесленн список свободных мест
     * @return Спиок свободных слотов для определенного рабочего места за определенную дату
     */
    public List<Slot> getAvailableSlots(Place place, LocalDateTime date) {
        List<BookedPlace> bookingsOfPlace = getAllBookedPlaces().stream()
                .filter(bookedPlace -> bookedPlace.getPlace().equals(place))
                .sorted(Comparator.comparing(bp -> bp.getSlot().getStart()))
                .toList();
        List<Slot> availableSlots = new ArrayList<>();
        final LocalTime startOfDay = LocalTime.of(8, 0);
        final LocalTime endOfDay = LocalTime.of(20, 0);

        LocalDateTime currentStart = date.with(startOfDay);
        LocalDateTime currentEnd = date.with(endOfDay);

        for (BookedPlace booking : bookingsOfPlace) {
            if (booking.getSlot().getStart().toLocalDate().equals(date.toLocalDate())) {
                if (currentStart.isBefore(booking.getSlot().getStart())) {
                    var newSlot = Slot.builder()
                            .start(currentStart)
                            .end(booking.getSlot().getStart())
                            .build();
                    availableSlots.add(newSlot);
                }
                currentStart = booking.getSlot().getEnd();
            }
        }

        if (currentStart.isBefore(currentEnd)) {
            availableSlots.add(Slot.builder().start(currentStart).end(currentEnd).build());
        }

        return availableSlots;
    }

    /**
     * Возращает список всех бронирований сортированный по параметрам:
     * 1 - имя места
     * 2 - имя пользователя
     * 3 - слот
     *
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
