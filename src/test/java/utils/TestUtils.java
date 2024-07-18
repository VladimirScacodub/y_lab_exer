package utils;


import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.coworking.dtos.BookedPlaceDTO;
import org.coworking.dtos.PlaceDTO;
import org.coworking.dtos.SlotDTO;
import org.coworking.dtos.UserDTO;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.models.User;
import org.coworking.models.enums.PlaceType;
import org.coworking.models.enums.Role;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

public class TestUtils {

    public static final String EXISTENT_NAME = "testName";

    public static final Place PLACE_TEST_OBJECT = new Place(1, EXISTENT_NAME, PlaceType.WORKPLACE);

    public static final String ADMIN_BASIC_AUTH_HEADER_VALUE = "Basic YWRtaW46YWRtaW4=";

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
    public static final LocalDateTime TEST_LOCAL_DATE_TIME = parse("2024-06-22 11:31", ofPattern("yyyy-MM-dd HH:mm"));

    public static final String TEST_DATE = "21/06/2024";

    public static final Slot TEST_SLOT = Slot.builder()
            .start(TEST_LOCAL_DATE_TIME)
            .end(TEST_LOCAL_DATE_TIME.plusHours(2))
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


    public static final ServletInputStream SERVLET_INPUT_STREAM = new ServletInputStream() {
        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return 0;
        }
    };
    public static final ServletOutputStream SERVLET_OUTPUT_STREAM = new ServletOutputStream() {
        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int b) throws IOException {

        }
    };

    public static final UserDTO TEST_ADMIN_DTO = UserDTO.builder()
            .id(1)
            .name(ADMIN_LOGIN)
            .password(ADMIN_PASSWORD)
            .role(Role.ADMIN)
            .build();

    public static final PlaceDTO TEST_PLACE_DTO = PlaceDTO.builder()
            .id(1)
            .placeName(TEST_PLACE_NAME_0)
            .placeType("WORKPLACE")
            .build();

    public static final SlotDTO TEST_SLOT_DTO = SlotDTO.builder()
            .id(1)
            .start("2024-09-09 11:00")
            .end("2024-09-09 15:00")
            .build();

    public static final BookedPlaceDTO TEST_BOOKED_PLACE_DTO = BookedPlaceDTO.builder()
            .id(1)
            .userDTO(TEST_ADMIN_DTO)
            .placeDTO(TEST_PLACE_DTO)
            .slotDTO(TEST_SLOT_DTO)
            .build();

    /**
     * Подготовка и запуск всех Liquibase скриптов
     *
     * @param connection Connection объект связанный с БД
     * @throws SQLException       - если есть проблемы с БД
     * @throws LiquibaseException если есть проблемы с liquibase скриптами
     */
    public static void startLiquibase(Connection connection) throws SQLException, LiquibaseException {
        var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        String liquibaseSchemaName = "liquibase_schema";
        createSchemaForLiquibaseLogs(connection, liquibaseSchemaName);
        database.setLiquibaseSchemaName(liquibaseSchemaName);
        Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);

        liquibase.update();
    }

    /**
     * Создание отдельной схемы в БД для служебных таблиц liquibase
     *
     * @param connection          Connection объект связанный с БД
     * @param liquibaseSchemaName имя новой liquibase схемы
     * @throws SQLException в случае если есть проблемы с БД
     */
    private static void createSchemaForLiquibaseLogs(Connection connection, String liquibaseSchemaName) throws SQLException {
        Statement statement = connection.createStatement();
        String schemaCreationQuery = "CREATE SCHEMA IF NOT EXISTS " + liquibaseSchemaName;
        statement.executeUpdate(schemaCreationQuery);
        connection.commit();
    }

    private static String getTestUserJson(String name, String password, Role role) {
        return "{\n" +
                "    \"name\" : \"" + name + "\",\n" +
                "    \"password\" : \"" + password + "\",\n" +
                "    \"role\" : \"" + role.name() + "\"\n" +
                "}";
    }

    public static final String TEST_USER_JSON = getTestUserJson(ADMIN_LOGIN, ADMIN_PASSWORD, Role.USER);

    private static String getTestPlaceJson(String name, PlaceType placeType) {
        return "{\n" +
                "    \"placeName\" : \"" + name + "\",\n" +
                "    \"placeType\" : \"" + placeType.name() + "\"\n" +
                "}";
    }

    public static final String TEST_PLACE_JSON = getTestPlaceJson(TEST_PLACE_NAME_0 + "s", PlaceType.WORKPLACE);

    private static String getTestBookedPlaceJson(PlaceDTO placeDTO, SlotDTO slotDTO) {
        return "{\n" +
                "        \"placeDTO\": {\n" +
                "            \"id\" : " + placeDTO.getId() + ",\n" +
                "            \"placeName\": \"" + placeDTO.getPlaceName() + "\",\n" +
                "            \"placeType\": \"" + placeDTO.getPlaceType() + "\"\n" +
                "        },\n" +
                "        \"slotDTO\": {\n" +
                "            \"start\": \"" + slotDTO.getStart() + "\",\n" +
                "            \"end\": \"" + slotDTO.getEnd() + "\"\n" +
                "        }\n" +
                "}";
    }

    public static String TEST_BOOKED_PLACE_JSON = getTestBookedPlaceJson(TEST_PLACE_DTO, TEST_SLOT_DTO);
}
