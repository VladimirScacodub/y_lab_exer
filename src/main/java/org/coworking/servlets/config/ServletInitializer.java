package org.coworking.servlets.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import liquibase.exception.LiquibaseException;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.annotations.Loggable;
import org.coworking.repositories.BookedPlaceRepositoryImpl;
import org.coworking.repositories.PlaceRepositoryImpl;
import org.coworking.repositories.SlotRepositoryImpl;
import org.coworking.repositories.UserRepositoryImpl;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.services.validators.PlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.coworking.servlets.booking.BookPlaceServlet;
import org.coworking.servlets.booking.DeleteBookedPlaceServlet;
import org.coworking.servlets.booking.GetAllAvailableSlotsServlet;
import org.coworking.servlets.booking.GetAllBookedPlaceServlet;
import org.coworking.servlets.booking.GetAllUserBookedPlaceServlet;
import org.coworking.servlets.place.DeletePlaceServlet;
import org.coworking.servlets.place.GelAllPlaceServlet;
import org.coworking.servlets.place.SaveNewPlaceServlet;
import org.coworking.servlets.place.UpdatePlaceServlet;
import org.coworking.servlets.user.AuthoriseUserServlet;
import org.coworking.servlets.user.RegisterUserServlet;

import java.sql.Connection;
import java.sql.SQLException;

import static org.coworking.Utils.JDBCUtils.getConnection;
import static org.coworking.Utils.JDBCUtils.startLiquibase;

/**
 * Клас исользующийся для инициализации контекста и предворительной настройки БД
 */
@Loggable
@WebListener
public class ServletInitializer implements ServletContextListener {

    /**
     * Объект отвечающий за связь с БД
     */
    private Connection connection;

    /**
     * Метод реализующий все конфигурацию контекста и сервлетов
     * @param sce объект, использующийся для инициализации контекста
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            startLiquibaseDb();
            var context = sce.getServletContext();

            ObjectMapperUtil objectMapperUtil = new ObjectMapperUtil(new ObjectMapper());
            UserService userService = new UserService(new UserRepositoryImpl(connection));
            UserValidator userValidator = new UserValidator(userService);
            PlaceRepositoryImpl placeRepository = new PlaceRepositoryImpl(connection);
            PlaceService placeService = new PlaceService(placeRepository);
            PlaceValidator placeValidator = new PlaceValidator(placeService);
            SlotRepositoryImpl slotRepository = new SlotRepositoryImpl(connection);
            BookedPlaceRepositoryImpl bookedPlaceRepository = new BookedPlaceRepositoryImpl(connection, slotRepository);
            BookedPlaceService bookedPlaceService = new BookedPlaceService(placeService, bookedPlaceRepository);
            BookedPlaceValidator bookedPlaceValidator = new BookedPlaceValidator(bookedPlaceService);

            context.addServlet("RegisterUserServlet", new RegisterUserServlet(userService, userValidator, objectMapperUtil))
                    .addMapping("/register-user");
            context.addServlet("AuthoriseUserServlet", new AuthoriseUserServlet(userValidator, objectMapperUtil))
                    .addMapping("/authorise-user");
            context.addServlet("UpdatePlaceServlet", new UpdatePlaceServlet(placeService, placeValidator, objectMapperUtil))
                    .addMapping("/update-place");
            context.addServlet("SaveNewPlaceServlet", new SaveNewPlaceServlet(placeService,placeValidator, objectMapperUtil))
                    .addMapping("/save-new-place");
            context.addServlet("GelAllPlaceServlet", new GelAllPlaceServlet(placeService, objectMapperUtil))
                    .addMapping("/get-all-places");
            context.addServlet("DeletePlaceServlet", new DeletePlaceServlet(placeService,placeValidator))
                    .addMapping("/delete-place");
            context.addServlet("GetAllUserBookedPlaceServlet", new GetAllUserBookedPlaceServlet(bookedPlaceService, objectMapperUtil))
                    .addMapping("/get-current-user-booked-place");
            context.addServlet("GetAllBookedPlaceServlet", new GetAllBookedPlaceServlet(bookedPlaceService, objectMapperUtil))
                    .addMapping("/get-all-booking");
            context.addServlet("GetAllAvailableSlotsServlet", new GetAllAvailableSlotsServlet(bookedPlaceService, placeService, objectMapperUtil))
                    .addMapping("/get-available-slots");
            context.addServlet("DeleteBookedPlaceServlet", new DeleteBookedPlaceServlet(bookedPlaceService, bookedPlaceValidator))
                    .addMapping("/delete-booked-place");
            context.addServlet("BookPlaceServlet", new BookPlaceServlet(bookedPlaceService, bookedPlaceValidator, objectMapperUtil))
                    .addMapping("/book-place");
        } catch (SQLException | LiquibaseException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Запуск скриптов Liquibase
     * @throws SQLException если возникла проблема с БД
     * @throws LiquibaseException если возникла проблема с Liquibase скриптами
     */
    public void startLiquibaseDb() throws SQLException, LiquibaseException {
        connection = getConnection();
        startLiquibase(connection);
    }
}
