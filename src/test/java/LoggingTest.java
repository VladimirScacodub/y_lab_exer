import org.aspectj.lang.ProceedingJoinPoint;
import org.coworking.aspects.LoggableAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

@DisplayName("Тест логирования")
public class LoggingTest {

    @Mock
    ProceedingJoinPoint proceedingJoinPoint;

    LoggableAspect loggableAspect;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        loggableAspect = new LoggableAspect();
    }

    @Test
    @DisplayName("Тест на вызов логирования методов")
    void loggingShouldReturnObject() throws Throwable {
        loggableAspect.logging(proceedingJoinPoint);
        verify(proceedingJoinPoint).proceed();
    }
}
