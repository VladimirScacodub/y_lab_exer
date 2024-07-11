package org.coworking.repositories;

import lombok.AllArgsConstructor;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.coworking.Utils.JDBCUtils.rollback;
import static org.coworking.Utils.mappers.ResultSetMapper.mapBookedPlace;

/**
 * Реализация инерфейса BookedPlaceRepository, которая работает с БД
 */
@AllArgsConstructor
public class BookedPlaceRepositoryImpl implements BookedPlaceRepository {

    /**
     * Объект Connection, через которого происходит отправка запросов в БД
     */
    private Connection connection;

    /**
     * Репозиторий, позволяющий работать с хранилищем временых слотов
     */
    private SlotRepository slotRepository;

    /**
     * Сохраниение Записи о бронировании В БД
     *
     * @param place бронированое место
     * @param user  пользователь бронирующий место
     * @param from  дата начала бронирования
     * @param to    дата окончания бронирования
     * @return id записи бронирования в БД
     */
    @Override
    public int save(Place place, User user, LocalDateTime from, LocalDateTime to) {
        try {
            int slotId = slotRepository.save(from, to);
            final String preparedInsertionBookedQuery = "INSERT INTO coworking_schema.booked_places (user_id, place_id, slot_id)" +
                    " VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(preparedInsertionBookedQuery);
            statement.setInt(1, user.getId());
            statement.setInt(2, place.getId());
            statement.setInt(3, slotId);
            statement.executeUpdate();
            int savedId = getCurrentSequence();
            connection.commit();
            return savedId;
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Получение из БД последнего id
     *
     * @return id последнего сохраненного BookedPlace
     * @throws SQLException - если в ходе запроса возникла проблема
     */
    int getCurrentSequence() throws SQLException {
        var statement = connection.prepareStatement("select currval('coworking_schema.booked_places_id_seq')");
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    /**
     * Получения всех записей о бронированых местах из БД определенным пользователем
     *
     * @param user ползователь бронирующий места
     * @return список бронированых мест
     */
    @Override
    public List<BookedPlace> findAllByUser(User user) {
        final String preparedSelectQuery = getSelectBookedPlacesQuery() + " WHERE u.id = ?";
        List<BookedPlace> bookedPlaceList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(preparedSelectQuery);
            statement.setInt(1, user.getId());
            var resultSet = statement.executeQuery();
            fillResultList(resultSet, bookedPlaceList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return bookedPlaceList;
    }

    /**
     * Получения всех записей о бронированых местах из БД
     *
     * @return список бронированых мест
     */
    @Override
    public List<BookedPlace> findAll() {
        final String selectQuery = getSelectBookedPlacesQuery();
        List<BookedPlace> bookedPlaceList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            var resultSet = statement.executeQuery(selectQuery);
            fillResultList(resultSet, bookedPlaceList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return bookedPlaceList;
    }

    /**
     * Возвращает SQL запрос на получение всех связанных с бронированием мест данных
     *
     * @return строка - SQL запрос
     */
    private static String getSelectBookedPlacesQuery() {
        return "SELECT bp.id, u.id, u.name, u.password, u.role, pl.id, pl.place_name, pl.place_type, s.id, s.start_date, s.end_date" +
                " FROM coworking_schema.booked_places bp INNER JOIN coworking_schema.users u ON bp.user_id = u.id" +
                " INNER JOIN coworking_schema.places pl ON pl.id = bp.place_id" +
                " INNER JOIN coworking_schema.slots s ON s.id = bp.slot_id";
    }

    /**
     * Служебный метод мэппит ResultSet строки в список BookedPlace
     *
     * @param resultSet       объект ResultSet содержащий данные об бронировании
     * @param bookedPlaceList список в который будут заполнены данные
     * @throws SQLException в случае если какого-то поля в ResultSet нет
     */
    private void fillResultList(ResultSet resultSet, List<BookedPlace> bookedPlaceList) throws SQLException {
        while (resultSet.next()) {
            bookedPlaceList.add(mapBookedPlace(resultSet));
        }
    }

    /**
     * Получение записи о бронировании по id
     *
     * @param id id записи в БД
     * @return объект Optional, в который обернут BookedPlace объект
     */
    @Override
    public Optional<BookedPlace> findById(int id) {
        final String preparedSelectQuery = getSelectBookedPlacesQuery() + " WHERE bp.id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(preparedSelectQuery);
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.ofNullable(mapBookedPlace(resultSet)) : empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return empty();
        }
    }

    /**
     * Удаление записи о бронировании из БД по id
     *
     * @param id id записи в БД
     */
    @Override
    public void removeById(int id) {
        final String preparedDeleteQuery = "DELETE FROM coworking_schema.booked_places WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(preparedDeleteQuery);
            statement.setInt(1, id);
            int i = statement.executeUpdate();
            connection.commit();
            System.out.println(i);
        } catch (SQLException e) {
            rollback(connection);
            System.out.println(e.getMessage());
        }
    }
}
