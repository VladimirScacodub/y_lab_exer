import org.assertj.core.api.Assertions;
import org.coworking.config.SpringConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тест методов из SpringConfig")
public class SpringConfigTest {

    public static final String COWORKING_SERVICE_TITLE = "Coworking Service";

    @Test
    @DisplayName("Тест на правильную устаноку данных для ApiConfig")
    void apiConfigShouldSetCorrectDateTest(){
        var apiInfo = new SpringConfig().apiInfo();

        Assertions.assertThat(apiInfo.getTitle()).isEqualTo(COWORKING_SERVICE_TITLE);
    }
}
