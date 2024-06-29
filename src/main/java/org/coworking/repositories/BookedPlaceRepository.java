package org.coworking.repositories;

import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Интефрейс для репозиториев работающих с бронированием места
 */
public interface BookedPlaceRepository {

    /**
     * Сохраниение Записи о бронировании
     *
     * @param place бронированое место
     * @param user  пользователь бронирующий место
     * @param from  дата начала бронирования
     * @param to    дата окончания бронирования
     * @return id записи бронирования в хранилище
     */
    int save(Place place, User user, LocalDateTime from, LocalDateTime to);

    /**
     * Получения всех записей о бронированых местах из хранилища определенным пользователем
     *
     * @param user ползователь бронирующий места
     * @return список бронированых мест
     */
    List<BookedPlace> findAllByUser(User user);

    /**
     * Получения всех записей о бронированых местах из хранилища
     *
     * @return список бронированых мест
     */
    List<BookedPlace> findAll();

    /**
     * Получение записи о бронировании по id
     *
     * @param id id записи в хранилище
     * @return объект Optional, в который обернут BookedPlace объект
     */
    Optional<BookedPlace> findById(int id);

    /**
     * Удаление записи о бронировании из хранилища по id
     *
     * @param id id записи в хранилища
     */
    void removeById(int id);

}
