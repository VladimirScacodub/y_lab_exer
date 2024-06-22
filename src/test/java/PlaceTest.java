import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static utils.TestUtils.PLACE_TEST_OBJECT;

@DisplayName("Тест для рабочех мест или конференц залов")
public class PlaceTest {

    @Test
    @DisplayName("Проверка метода hashCode на возвращеннии одинакого hashCode для однакого объекта")
    void hashCodeShouldReturnSameCodeToSameObject(){
        final Integer EXPECTED_HASH_CODE = PLACE_TEST_OBJECT.hashCode();

        Assertions.assertThat(PLACE_TEST_OBJECT.hashCode()).isEqualTo(EXPECTED_HASH_CODE);
    }
}
