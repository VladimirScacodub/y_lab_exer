package org.coworking.repositories;

import lombok.AllArgsConstructor;
import org.coworking.models.Place;
import org.coworking.models.enums.PlaceType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.coworking.Utils.JDBCUtils.rollback;
import static org.coworking.Utils.mappers.ResultSetMapper.mapPlaceRow;

/**
 * Реализация интерфейса PlaceRepository, которая хранит данные в БД
 */
@AllArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {

    /**
     * Связь с БД, через которую будет происходить CRUD операции над данными о местах
     */
    private Connection connection;

    /**
     * Сохранение нового места в БД
     *
     * @param placeName имя места
     * @param placeType тип места
     */
    @Override
    public void save(String placeName, PlaceType placeType) {
        PreparedStatement insertStatement;
        try {
            String preparedSql = "INSERT INTO coworking_schema.places (place_name, place_type) " +
                    "VALUES (?,?);";
            insertStatement = connection.prepareStatement(preparedSql);
            insertStatement.setString(1, placeName);
            insertStatement.setString(2, placeType.name());
            insertStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Получение существующего места из БД по имени
     *
     * @param placeName имя места
     * @return Optional объект, в который обернут Place объект
     */
    @Override
    public Optional<Place> findByName(String placeName) {
        String selectQuery = "SELECT * FROM coworking_schema.places WHERE place_name = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setString(1, placeName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.ofNullable(mapPlaceRow(resultSet)) : empty();
        } catch (SQLException e) {
            return empty();
        }
    }

    /**
     * Получение всей информации о местах из БД
     *
     * @return спиок обектов Place
     */
    @Override
    public List<Place> findAll() {
        String selectQuery = "SELECT * FROM coworking_schema.places";
        Statement statement;
        List<Place> placeList = new ArrayList<>();
        try {
            statement = connection.createStatement();
            var resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()){
                placeList.add(mapPlaceRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return placeList;
    }

    /**
     * Удаление места по его имени из БД
     *
     * @param placeName имя удаляемого места
     */
    @Override
    public void removeByName(String placeName) {
        String deleteQuery = "DELETE FROM coworking_schema.places WHERE place_name = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, placeName);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Обновление данных об месте по имени в БД
     *
     * @param oldPlaceName имя обновляемого места
     * @param newPlaceName новое имя для места
     * @param newPlaceType новый тип для места
     */
    @Override
    public void updatePlace(String oldPlaceName, String newPlaceName, PlaceType newPlaceType) {
        String deleteQuery = "UPDATE coworking_schema.places SET place_name=?, place_type=? WHERE place_name = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, newPlaceName);
            statement.setString(2, newPlaceType.name());
            statement.setString(3, oldPlaceName);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            System.out.println(e.getMessage());
        }
    }
}
