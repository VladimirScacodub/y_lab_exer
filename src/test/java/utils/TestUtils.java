package utils;

import liquibase.exception.LiquibaseException;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.models.User;
import org.coworking.models.enums.PlaceType;
import org.coworking.models.enums.Role;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

public class TestUtils {

    public static final String EXISTENT_NAME = "testName";

    public static final Place PLACE_TEST_OBJECT = new Place(1, EXISTENT_NAME, PlaceType.WORKPLACE);

    public static final String NEW_NAME_STRING = "newName";

    public static final List<Place> TEST_LIST_OF_PLACE = List.of(new Place(2, EXISTENT_NAME, PlaceType.WORKPLACE));

    public static final String EMPTY_STRING = "";

    public static final User USER_TEST_OBJECT = User.builder()
            .id(2)
            .name(EXISTENT_NAME)
            .password(EXISTENT_NAME)
            .role(Role.USER)
            .build();

    public static final List<User> USER_TEST_LIST = List.of(USER_TEST_OBJECT);

    public static final String ADMIN_LOGIN = "admin";

    public static final String ADMIN_PASSWORD = "admin";

    public static final User ADMIN_TEST_OBJECT = User.builder()
            .id(1)
            .name(ADMIN_LOGIN)
            .password(ADMIN_PASSWORD)
            .role(Role.ADMIN)
            .build();


    public static final String TEST_PLACE_NAME_0 = "Workplace 0";

    public static final String TEST_PLACE_NAME_1 = "Workplace 1";

    public static final String TEST_PLACE_NAME_2 = "Workplace 2";


    public static final Place PLACE_TEST_OBJECT_FOR_BOOKING = Place.builder().id(1)
            .placeName(TEST_PLACE_NAME_0)
            .placeType(PlaceType.WORKPLACE)
            .build();

    public static final String TEST_DATE = "21/06/2024";

    public static final Slot TEST_SLOT = Slot.builder()
            .start(LocalDateTime.MIN)
            .end(LocalDateTime.MAX)
            .build();

    public static final BookedPlace TEST_BOOKED_PLACE_OBJECT = BookedPlace.builder()
            .id(1)
            .place(PLACE_TEST_OBJECT)
            .user(ADMIN_TEST_OBJECT)
            .slot(TEST_SLOT)
            .build();

    public static final String TEST_PLACE_TYPE_STRING = "WORKPLACE";

    public static final PlaceType TEST_PLACE_TYPE = PlaceType.WORKPLACE;

    public static final List<BookedPlace> TEST_BOOKED_PLACE_LIST = List.of(TEST_BOOKED_PLACE_OBJECT);

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer;


    public static String startTestContainer() throws SQLException, LiquibaseException {
        final String db_name = "coworking_service";
        final String db_user = "user";
        final String db_password = "password";

        postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
                .withUsername(db_user)
                .withPassword(db_password)
                .withDatabaseName(db_name);
        postgreSQLContainer.start();
        return postgreSQLContainer.getJdbcUrl() + "&user=" + postgreSQLContainer.getUsername() + "&password=" + postgreSQLContainer.getPassword();
    }

    public static void stopTestContainers() {
        if (postgreSQLContainer != null)
            postgreSQLContainer.stop();
    }

    public static final LocalDateTime TEST_LOCAL_DATE_TIME = parse("2024-06-22 11:31", ofPattern("yyyy-MM-dd HH:mm"));

}
