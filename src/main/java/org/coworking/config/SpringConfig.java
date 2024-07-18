package org.coworking.config;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.coworking.annotations.Loggable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Конфигурация Spring
 */
@Loggable
@Configuration
@EnableWebMvc
@EnableSwagger2
@EnableAspectJAutoProxy
@ComponentScan("org.coworking")
@PropertySource(value = "classpath:application.yml", factory = YmlPropertySourceFactory.class)
public class SpringConfig implements WebMvcConfigurer {

    /**
     * URL базы данных
     */
    @Value("${url}")
    private String url;

    /**
     * Имя пользователя для БД
     */
    @Value("${user}")
    private String user;

    /**
     * Пароль для бд
     */
    @Value("${password}")
    private String password;

    /**
     * Имя схемы, где будут хранится служебные таблицы
     */
    @Value("${liquibaseSchemaName}")
    private String liquibaseSchemaName;

    /**
     * Путь к liquibase changelog файлу
     */
    @Value("${changeLogFile}")
    private String changeLogFileName;

    /**
     * Установка Object mapper в HttpMessageConverter
     *
     * @param converters initially an empty list of converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true);

        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
    }

    /**
     * Создание связи с БД с выключенным авто-коммитов!
     *
     * @return Connection объект через, который можно связаться с БД
     * @throws SQLException В случае если есть проблема с БД
     * @throws ClassNotFoundException если есть проблемы с подгрузкой Driver
     */
    @Bean
    public Connection connection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }

    /**
     * Конфигурация springfox-swagger2
     *
     * @return Docket объект
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("org.coworking.controllers"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(new BasicAuth("basicAuth")))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    /**
     * Создание SecurityContext для swagger2
     *
     * @return Объект SecurityContext
     */
    private SecurityContext securityContext(){
        return SecurityContext.builder()
                .securityReferences(Arrays.asList(new SecurityReference("basicAuth", new AuthorizationScope[0])))
                .forPaths(path -> !Objects.equals(path, "/register-user"))
                .build();
    }

    /**
     * Создание объекта, который содержит информацию об приложении для swagger2
     *
     * @return объект ApiInfo
     */
    public ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Coworking Service")
                .description("Сервис для бронирования мест")
                .version("1.0.0")
                .build();
    }

    /**
     * Конфигурация статических ресурсов для swagger ui
     *
     * @param registry - объект, который хранит регистрации обработчиков ресурсов для обслуживания статических ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    /**
     * Конфигурация Liquibase и запуск liquibase скриптов
     *
     * @param connection Связь с БД
     * @return объект Liquibase
     * @throws LiquibaseException В случае есил есть проблемы со скриптами
     * @throws SQLException в случае если есть проблемы с БД
     */
    @Bean
    public Liquibase liquibase(Connection connection) throws LiquibaseException, SQLException {
        var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        createSchemaForLiquibaseLogs(connection, liquibaseSchemaName);
        database.setLiquibaseSchemaName(liquibaseSchemaName);
        Liquibase liquibase = new Liquibase(changeLogFileName, new ClassLoaderResourceAccessor(), database);

        liquibase.update();
        return liquibase;
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
}

