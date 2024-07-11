package org.coworking.Utils.mappers;

import org.coworking.annotations.Loggable;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.models.User;
import org.coworking.models.enums.PlaceType;
import org.coworking.models.enums.Role;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс, который предоставляет возможности по мэппингу строк из БД в конкретные объекты
 */
@Loggable
public class ResultSetMapper {

    /**
     * Производит мэппинг строки из ResultSet в объект User
     *
     * @param resultSet - объект ResultSet содержащий данные
     * @return - Объект User
     * @throws SQLException - выбрасывается если поля в ResultSet не существует
     */
    public static User mapUserRow(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .password(resultSet.getString("password"))
                .role(Role.valueOf(resultSet.getString("role")))
                .build();
    }

    /**
     * Производит мэппинг строки из ResultSet в объект Place
     *
     * @param resultSet - объект ResultSet содержащий данные
     * @return - Объект Place
     * @throws SQLException - выбрасывается если поля в ResultSet не существует
     */
    public static Place mapPlaceRow(ResultSet resultSet) throws SQLException {
        return Place.builder()
                .id(resultSet.getInt("id"))
                .placeName(resultSet.getString("place_name"))
                .placeType(PlaceType.valueOf(resultSet.getString("place_type")))
                .build();
    }

    /**
     * Производит мэппинг строки из ResultSet в объект Slot
     *
     * @param resultSet - объект ResultSet содержащий данные
     * @return - Объект Slot
     * @throws SQLException - выбрасывается если поля в ResultSet не существует
     */
    public static Slot mapSlotRow(ResultSet resultSet) throws SQLException {
        return Slot.builder()
                .id(resultSet.getInt("id"))
                .start(resultSet.getTimestamp("start_date").toLocalDateTime())
                .end(resultSet.getTimestamp("end_date").toLocalDateTime())
                .build();
    }

    /**
     * Производит мэппинг строки из ResultSet, начиная с startColumn, в объект User.
     * Часто используется для меппинга объекта из JOIN запросов.
     *
     * @param resultSet   - объект ResultSet содержащий данные
     * @param startColumn - индекс поля в ResultSet с которого начинается меппинг
     * @return - User объект
     * @throws SQLException - выбрасывается если поля в ResultSet не существует
     */
    private static User mapUserRow(ResultSet resultSet, int startColumn) throws SQLException {
        return User.builder()
                .id(resultSet.getInt(startColumn))
                .name(resultSet.getString(startColumn + 1))
                .password(resultSet.getString(startColumn + 2))
                .role(Role.valueOf(resultSet.getString(startColumn + 3)))
                .build();
    }

    /**
     * Производит мэппинг строки из ResultSet, начиная с startColumn, в объект Place.
     * Часто используется для меппинга объекта из JOIN запросов.
     *
     * @param resultSet   - объект ResultSet содержащий данные
     * @param startColumn - индекс поля в ResultSet с которого начинается меппинг
     * @return - Place объект
     * @throws SQLException - выбрасывается если поля в ResultSet не существует
     */
    private static Place mapPlaceRow(ResultSet resultSet, int startColumn) throws SQLException {
        return Place.builder()
                .id(resultSet.getInt(startColumn))
                .placeName(resultSet.getString(startColumn + 1))
                .placeType(PlaceType.valueOf(resultSet.getString(startColumn + 2)))
                .build();
    }

    /**
     * Производит мэппинг строки из ResultSet, начиная с startColumn, в объект Slot.
     * Часто используется для меппинга объекта из JOIN запросов.
     *
     * @param resultSet   - объект ResultSet содержащий данные
     * @param startColumn - индекс поля в ResultSet с которого начинается меппинг
     * @return - Slot объект
     * @throws SQLException - выбрасывается если поля в ResultSet не существует
     */
    private static Slot mapSlotRow(ResultSet resultSet, int startColumn) throws SQLException {
        return Slot.builder()
                .id(resultSet.getInt(startColumn))
                .start(resultSet.getTimestamp(startColumn + 1).toLocalDateTime())
                .end(resultSet.getTimestamp(startColumn + 2).toLocalDateTime())
                .build();
    }

    /**
     * Производит мэппинг строки из ResultSet в объект BookedPlace
     *
     * @param resultSet - объект ResultSet содержащий данные
     * @return - Объект BookedPlace
     * @throws SQLException - выбрасывается если поля в ResultSet не существует
     */
    public static BookedPlace mapBookedPlace(ResultSet resultSet) throws SQLException {
        User user = mapUserRow(resultSet, 2);
        Place place = mapPlaceRow(resultSet, 6);
        Slot slot = mapSlotRow(resultSet, 9);
        return BookedPlace.builder()
                .id(resultSet.getInt(1))
                .user(user)
                .place(place)
                .slot(slot)
                .build();
    }
}
