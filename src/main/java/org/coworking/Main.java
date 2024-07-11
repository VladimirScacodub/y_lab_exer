package org.coworking;

import liquibase.exception.LiquibaseException;
import org.coworking.Utils.mappers.UserMapper;
import org.coworking.dtos.UserDTO;
import org.coworking.repositories.BookedPlaceRepositoryImpl;
import org.coworking.repositories.PlaceRepositoryImpl;
import org.coworking.repositories.SlotRepositoryImpl;
import org.coworking.repositories.UserRepositoryImpl;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.services.validators.PlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.coworking.services.BookedPlaceService;
import org.coworking.outputs.MenuOutput;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;

import static org.coworking.Utils.JDBCUtils.getConnection;
import static org.coworking.Utils.JDBCUtils.startLiquibase;

/**
 * Класс запускающий весь рабочий поток приложения
 */
public class Main {
    /**
     * метод апускающий весь рабочий поток приложения
     *
     * @param args - аргументы, которые в данном приложении никак не обрабатываются
     */
    public static void main(String[] args) throws SQLException, LiquibaseException {
        Connection connection = getConnection();
        startLiquibase(connection);
        System.out.println("Подключение было совершенно");

        UserDTO userDTO = new UserDTO();

        UserMapper.INSTANCE.userDtoToUser(userDTO);

        MenuOutput menuOutput = getMenuOutput(connection);

        menuOutput.performMenu();
    }

    /**
     * Метод возвращающий MenuOutput объект со всеми зависимостями
     *
     * @param connection - Объект java.sql.Connection
     * @return Объект MenuOutput
     */
    private static MenuOutput getMenuOutput(Connection connection) {
        UserService userService = new UserService(new UserRepositoryImpl(connection));
        PlaceService placeService = new PlaceService(new PlaceRepositoryImpl(connection));
        UserValidator userValidator = new UserValidator(userService);
        PlaceValidator placeValidator = new PlaceValidator(placeService);
        BookedPlaceService bookedPlaceService = new BookedPlaceService(placeService, new BookedPlaceRepositoryImpl(connection, new SlotRepositoryImpl(connection)));
        BookedPlaceValidator bookedPlaceValidator = new BookedPlaceValidator(bookedPlaceService);
        return new MenuOutput(userValidator, userService, placeService, placeValidator, bookedPlaceService, bookedPlaceValidator, null);
    }
}