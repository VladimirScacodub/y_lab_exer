import liquibase.exception.LiquibaseException;
import org.coworking.Utils.JDBCUtils;
import org.coworking.models.enums.PlaceType;
import org.coworking.repositories.BookedPlaceRepository;
import org.coworking.repositories.BookedPlaceRepositoryImpl;
import org.coworking.repositories.PlaceRepositoryImpl;
import org.coworking.repositories.SlotRepository;
import org.coworking.repositories.SlotRepositoryImpl;
import org.coworking.repositories.UserRepository;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static utils.TestUtils.TEST_LOCAL_DATE_TIME;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@DisplayName("Тест Валидатора бронированых мест")
public class BookedPlaceValidatorTest {

    PlaceService placeService;

    UserService userService;

    BookedPlaceRepository bookedPlaceRepository;

    BookedPlaceService bookedPlaceService;

    BookedPlaceValidator bookedPlaceValidator;

    UserRepository userRepository;

    private static Connection connection;

    @BeforeAll
    static void setDatabase() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection(startTestContainer());
        JDBCUtils.startLiquibase(connection);
    }

    @AfterAll
    static void setDown() {
        stopTestContainers();
    }


    @BeforeEach
    public void fillData() {
        placeService = new PlaceService(new PlaceRepositoryImpl(connection));

        SlotRepository slotRepository = new SlotRepositoryImpl(connection);
        BookedPlaceRepository bookedPlaceRepository = new BookedPlaceRepositoryImpl(connection, slotRepository);
        bookedPlaceService = new BookedPlaceService(placeService, bookedPlaceRepository);
        userService = new UserService(userRepository);
        bookedPlaceService = new BookedPlaceService(placeService, new BookedPlaceRepositoryImpl(connection, slotRepository));

        bookedPlaceValidator = new BookedPlaceValidator(bookedPlaceService);
    }

    @Test
    @DisplayName("Тест на обнаружение конфликов бронирования")
    void validateBookingPlaceShouldDetectBookingConflictsTest() {

        assertThatThrownBy(() -> bookedPlaceValidator.validateBookingPlace(placeService.getAllPlaces().get(0), LocalDateTime.MAX, LocalDateTime.MIN))
                .isInstanceOf(BookedPlaceConflictsException.class);
        assertThatThrownBy(() -> bookedPlaceValidator.validateBookingPlace(placeService.getAllPlaces().get(0), LocalDateTime.MIN, LocalDateTime.MAX))
                .isInstanceOf(BookedPlaceConflictsException.class);
        assertThatThrownBy(() -> bookedPlaceValidator.validateBookingPlace(placeService.getAllPlaces().get(0), TEST_LOCAL_DATE_TIME, TEST_LOCAL_DATE_TIME.plusHours(1)))
                .isInstanceOf(BookedPlaceConflictsException.class);
        assertThatThrownBy(() -> bookedPlaceValidator.validateBookingPlace(placeService.getAllPlaces().get(0), TEST_LOCAL_DATE_TIME, TEST_LOCAL_DATE_TIME))
                .isInstanceOf(BookedPlaceConflictsException.class);
    }
}
