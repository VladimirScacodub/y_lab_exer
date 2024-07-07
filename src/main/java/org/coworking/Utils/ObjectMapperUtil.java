package org.coworking.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.annotations.Loggable;

import java.io.IOException;

/**
 * Класс экземпляры которого предоставляют возможность трасформировать объекты в JSON из HTTP запросов/ответов
 */
public class ObjectMapperUtil {

    /**
     * Объект трансформирующий другие объекты в JSON
     */
    private final ObjectMapper objectMapper;

    public ObjectMapperUtil (ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Получение объекта из JSON, содержащегося в HTTP запросе
     *
     * @param req HTTP запрос
     * @param clazz Ожидаемый класс объекта
     * @return объект класса
     * @param <T> дженерик тип, который принимает тип объекта
     * @throws IOException в случае если возникли проблемы с I/O
     */
    public <T> T getDto(HttpServletRequest req, Class<T> clazz) throws IOException {
        var node = objectMapper.readTree(req.getInputStream());
        return objectMapper.treeToValue(node, clazz);
    }

    /**
     * Записть объекта в HTTP ответ в виде JSON
     *
     * @param resp HTTP ответ
     * @param o объект для дазписи в JSON
     * @throws IOException в случае если есть проблемы с I/O
     */
    public void writeJson(HttpServletResponse resp, Object o) throws IOException {
        objectMapper.writeValue(resp.getOutputStream(), o);
    }
}
