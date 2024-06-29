package org.coworking.repositories;

import lombok.AllArgsConstructor;
import org.coworking.Utils.JDBCUtils;
import org.coworking.Utils.Mapper;
import org.coworking.models.Slot;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Stack;

import static java.sql.Timestamp.valueOf;
import static java.util.Optional.empty;
import static org.coworking.Utils.JDBCUtils.rollback;
import static org.coworking.Utils.Mapper.mapSlotRow;

/**
 * Реализация SlotRepository хранящая слоты в БД
 */
@AllArgsConstructor
public class SlotRepositoryImpl implements SlotRepository {

    /**
     * Связь с БД, через которую будет происходить CRUD операции над данными о слотах
     */
    private Connection connection;

    /**
     * Созранение нового временого слота в БД
     *
     * @param start начальная дата слота
     * @param end   конечная дата слота
     * @return id сохраненного слота из БД
     */
    @Override
    public int save(LocalDateTime start, LocalDateTime end) {
        try {
            createOrReplaceInsertionFunction();
            String callableSql = "{? = call slot_insertion(?, ?)}";
            CallableStatement insertStatement = connection.prepareCall(callableSql);
            insertStatement.registerOutParameter(1, Types.INTEGER);
            insertStatement.setTimestamp(2, valueOf(start));
            insertStatement.setTimestamp(3, valueOf(end));
            insertStatement.executeUpdate();
            return insertStatement.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Создание или замена функции в БД, которая при создании слота возвращает его id
     * @throws SQLException если создание провалилось
     */
    private void createOrReplaceInsertionFunction() throws SQLException {
        String createFunctionQuery = "CREATE OR REPLACE FUNCTION slot_insertion (start_d TIMESTAMP, end_d TIMESTAMP) " +
                "RETURNS integer AS $$ " +
                "BEGIN " +
                " INSERT INTO coworking_schema.slots (id, start_date, end_date) " +
                " VALUES (nextval('coworking_schema.slot_id_seq'), start_d, end_d); " +
                " RETURN currval('coworking_schema.slot_id_seq'); " +
                "END; " +
                "$$ LANGUAGE plpgsql";
        Statement statement = connection.createStatement();
        statement.executeUpdate(createFunctionQuery);
    }

    /**
     * Удаление слота по id из БД
     *
     * @param id id слота
     */
    @Override
    public void removeSlot(int id) {
        String deleteQuery = "DELETE FROM coworking_schema.slots WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setInt(1, id);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Поулчение слота из БД по его id
     *
     * @param id id слота
     * @return Optional объект, в который завернут объект временного слота
     */
    @Override
    public Optional<Slot> findById(int id) {
        String selectQuery = "SELECT * FROM coworking_schema.slots WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.ofNullable(mapSlotRow(resultSet)) : empty();
        } catch (SQLException e) {
            return empty();
        }
    }
}
