package org.coworking.services;

import lombok.AllArgsConstructor;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.annotations.Loggable;
import org.coworking.models.Place;
import org.coworking.models.enums.PlaceType;
import org.coworking.repositories.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с местами и рабоющий с репозиторием Place, в которой по умолчанию создает ряд объектов Place
 */
@Loggable
@AllArgsConstructor
@Service
public class PlaceService {

    /**
     * Репозиторий Place
     */
    private PlaceRepository placeRepository;

    /**
     * Возвращает список всех мест, хранящиеся в памяти
     *
     * @return список Place объектов
     */
    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    /**
     * Поиск Place по имени
     *
     * @param placeName имя Place
     * @return Optional, содержащий Place объект или null
     */
    public Optional<Place> findByName(String placeName) {
        return placeRepository.findByName(placeName);
    }

    /**
     * Создает новый Place в БД
     *
     * @param placeName имя для нового Place
     * @param placeType тип нового Place
     */
    public void createNewPlace(String placeName, PlaceType placeType) {
        placeRepository.save(placeName, placeType);
    }

    /**
     * Создает новый Place в БД
     *
     * @param place содержит данноые для нового места
     */
    public void createNewPlace(Place place) {
        createNewPlace(place.getPlaceName(), place.getPlaceType());
    }

    /**
     * Удаляет Place объект из хранилища в памяти
     *
     * @param placeName имя Place, который юудет удален
     * @throws PlaceNamingException в случае если Place с таким именем нет
     */
    public void removePlace(String placeName) throws PlaceNamingException {
        findByName(placeName)
                .orElseThrow(() -> new PlaceNamingException("Рабочего места с таким именем не существует"));
        placeRepository.removeByName(placeName);
    }

    /**
     * Обновляет данные уже существующего Place
     *
     * @param oldPlaceName имя существующего Place, данные которого будут изменены
     * @param newPlaceName новое имя для существующего Place
     * @param newPlaceType новый тип для существующего Place
     * @throws PlaceNamingException В случае если Place null
     */
    public void updatePlace(String oldPlaceName, String newPlaceName, PlaceType newPlaceType) throws PlaceNamingException {
        findByName(oldPlaceName).orElseThrow(() -> new PlaceNamingException("Place name should not be null"));
        placeRepository.updatePlace(oldPlaceName, newPlaceName, newPlaceType);
    }

    /**
     * Обновляет данные уже существующего Place
     *
     * @param oldPlaceName имя существующего Place, данные которого будут изменены
     * @param place        новые данные для существующего Place
     * @throws PlaceNamingException В случае если Place null
     */
    public void updatePlace(String oldPlaceName, Place place) throws PlaceNamingException {
        updatePlace(oldPlaceName, place.getPlaceName(), place.getPlaceType());
    }
}
