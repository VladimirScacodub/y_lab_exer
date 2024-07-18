package Repositories;

import liquibase.exception.LiquibaseException;
import org.coworking.Utils.TimeUtils;
import org.coworking.repositories.SlotRepository;
import org.coworking.repositories.impl.SlotRepositoryImpl;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import utils.TestUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestUtils.TEST_LOCAL_DATE_TIME;
import static utils.TestUtils.TEST_SLOT;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@Testcontainers
@DisplayName("Тесты для проверки реализации SlotRepository")
class SlotRepositoryImplTest {
    private static Connection connection;

    private SlotRepository slotRepository;

    @BeforeAll
    static void setDatabase() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection(startTestContainer());
        TestUtils.startLiquibase(connection);
    }

    @AfterAll
    static void setDown() {
        stopTestContainers();
    }

    @BeforeEach
    void setUp() {
        slotRepository = new SlotRepositoryImpl(connection);
    }

    @Test
    @DisplayName("Тест на сохранение нового слота")
    void saveShouldSaveNewSlotTest(){
        int actualId = slotRepository.save(LocalDateTime.now(), LocalDateTime.now().plusHours(4));
        var optionalSavedSlot = slotRepository.findById(actualId);

        assertThat(actualId).isGreaterThan(0);
        assertThat(optionalSavedSlot).isNotEmpty();
    }

    @Test
    @DisplayName("Тест на удаление слота из БД по его id")
    void removeSlotShouldRemoveSlotByIdTest(){
        int savedId = slotRepository.save(LocalDateTime.now(), LocalDateTime.now().plusHours(4));

        slotRepository.removeSlot(savedId);
        var optionalSavedSlot = slotRepository.findById(savedId);

        assertThat(savedId).isGreaterThan(0);
        assertThat(optionalSavedSlot).isEmpty();
    }

    @Test
    @DisplayName("Тест на получение форматированой строки со временем из LocalDateTime")
    void getFormatedTimeShouldReturnCorrectStringTest(){
        final String EXPECTED_DATA = "11:31";

        String actualData = TimeUtils.getFormatedTime(TEST_LOCAL_DATE_TIME);

        assertThat(actualData).isEqualTo(EXPECTED_DATA);
    }

    @Test
    @DisplayName("Тест на проверку хешкода слотов")
    void hashCodeShouldBeTheSameTest(){
        assertThat(TEST_SLOT.hashCode()).isEqualTo(TEST_SLOT.hashCode());
    }

}