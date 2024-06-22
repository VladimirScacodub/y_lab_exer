package org.coworking.services;

import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.models.Place;
import org.coworking.models.enums.PlaceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для работы с местами и хранящий Place в памяти, в которой по умолчанию создает рад объектов Place
 */
public class PlaceService {

    /**
     * Хранилище для данных о рабочих местах и конференц залах
     */
    private static List<Place> places = new ArrayList<>();

    /**
     * Генератор идентификаторов для объектов Place
     */
    private static int lastId = 1;

    public PlaceService() {
    }

    /**
     * Возвращает список всех мест, хранящиеся в памяти
     * @return список Place объектов
     */
    public List<Place> getAllPlaces() {
        return places;
    }

    /**
     * Поиск Place по имени
     * @param placeName - имя Place
     * @return Optional, содержащий Place объект или null
     */
    public Optional<Place> findByName(String placeName) {
        return places.stream()
                .filter(place -> Objects.equals(place.getPlaceName(), placeName))
                .findAny();
    }

    /**
     * Создает новый Place
     * @param placeName - имя для нового Place
     * @param placeType - тип нового Place
     * @return созданный и добавленный в память Place объект
     */
    public Place createNewPlace(String placeName, PlaceType placeType) {
        Place place = Place.builder()
                .id(lastId++)
                .placeName(placeName)
                .placeType(placeType)
                .build();
        places.add(place);
        return place;
    }

    /**
     * Удаляет Place объект из хранилища в памяти
     * @param placeName - имя Place, который юудет удален
     * @throws PlaceNamingException в случае если Place с таким именем нет
     */
    public void removePlace(String placeName) throws PlaceNamingException {
        Place place = findByName(placeName)
                .orElseThrow(()-> new PlaceNamingException("Рабочего места с таким именем не существует"));
        places.remove(place);
    }

    /**
     * Обновляет данные уже существующего Place
     * @param oldPlaceName - имя существующего Place, данные которого будут изменены
     * @param newPlaceName - новое имя для существующего Place
     * @param newPlaceType - новый тип для существующего Place
     */
    public void updatePlace(String oldPlaceName, String newPlaceName, PlaceType newPlaceType) throws PlaceNamingException {
        Place place = findByName(oldPlaceName).orElseThrow(()->new PlaceNamingException("Place name should not be null"));
        place.setPlaceName(newPlaceName);
        place.setPlaceType(newPlaceType);
    }
}
