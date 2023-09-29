package com.github.bogdan.service;

import com.github.bogdan.model.User;
import com.j256.ormlite.dao.Dao;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;

public class SortingService {
    static Logger LOGGER = LoggerFactory.getLogger(SortingService.class);

    public static <T> ArrayList<T> getByQueryParams(int userId, Dao<User, Integer> userDao, Dao<T, Integer> dao, Class<T> tClass, ArrayList<String> queryParams, Context ctx) throws NoSuchFieldException, SQLException, IllegalAccessException, UnsupportedEncodingException {
        ArrayList<T> objects = new ArrayList<>();
        for (String s : queryParams) {
            Field field = tClass.getDeclaredField(s);
            LOGGER.info("Field " + field.getName());
            String currentParam = ctx.queryParam(s);
            LOGGER.info("CurrentParam " + currentParam);
            if (currentParam != null) {
                field.setAccessible(true);
                for (T obj : dao.queryForAll()) {
                    Object value = field.get(obj);
                    String valueString = null;
                    if (value != null) {
                        valueString = value.toString();
                    }
                    if (URLDecoder.decode(currentParam, StandardCharsets.UTF_8.toString()).equals(valueString)) {
                        objects.add(obj);
                    }
                }
                field.setAccessible(false);
            }
        }
        return objects;
    }

}
