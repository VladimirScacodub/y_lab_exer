import org.assertj.core.api.Assertions;
import org.coworking.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static utils.TestUtils.USER_TEST_OBJECT;

@DisplayName("Тест пользователького класса")
public class UserTest {

    @Test
    @DisplayName("Проверка метода equals на true при сравнении объектов с одним ID")
    void equalsShouldReturnTrueWithSameIDUserObjectUsers(){
        final User USER_WITH_SAME_ID = User.builder()
                .id(USER_TEST_OBJECT.getId())
                .build();

        Assertions.assertThat(USER_TEST_OBJECT.equals(USER_WITH_SAME_ID)).isTrue();
    }
}
