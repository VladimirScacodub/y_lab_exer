import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.http.HttpServlet;
import liquibase.exception.LiquibaseException;
import org.coworking.servlets.config.ServletInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("Тест инициализации сервлетов")
public class ServletInitializerTest {

    @Spy
    private ServletInitializer servletInitializer;
    @Mock
    private ServletContextEvent servletContextEvent;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletRegistration.Dynamic dynamic;

    @BeforeEach
    void setUp() throws SQLException, LiquibaseException {
        MockitoAnnotations.openMocks(this);
        servletInitializer = Mockito.spy(new ServletInitializer());
        Mockito.doNothing().when(servletInitializer).startLiquibaseDb();
    }

    @Test
    @DisplayName("Тест на вызов установки всех сервлетов")
    void servletInitializerShouldSetupAllServlets(){
        Mockito.when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.addServlet(anyString(), any(HttpServlet.class))).thenReturn(dynamic);
        servletInitializer.contextInitialized(servletContextEvent);

        Mockito.verify(servletContext,Mockito.times(11)).addServlet(anyString(), any(HttpServlet.class));
    }
}
