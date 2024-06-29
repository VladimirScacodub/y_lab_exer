package org.coworking.repositories;

import org.coworking.models.Place;
import org.coworking.models.enums.PlaceType;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с хранилищем инфыормации об рабочих местах и конференц залов
 */
public interface PlaceRepository {

    /**
     * Сохранение нового места
     *
     * @param placeName имя места
     * @param placeType тип места
     */
    void save(String placeName, PlaceType placeType);

    /**
     * Получение существующего места из хранилища по имени
     *
     * @param placeName имя места
     * @return Optional объект, в который обернут Place объект
     */
    Optional<Place> findByName(String placeName);

    /**
     * Получение всей информации о местах
     *
     * @return спиок обектов Place
     */
    List<Place> findAll();

    /**
     * Удаление места по его имени
     *
     * @param placeName имя удаляемого места
     */
    void removeByName(String placeName);

    /**
     * Обновление данных об месте по имени
     *
     * @param oldPlaceName имя обновляемого места
     * @param newPlaceName новое имя для места
     * @param newPlaceType новый тип для места
     */
    void updatePlace(String oldPlaceName, String newPlaceName, PlaceType newPlaceType);
}
