package com.github.bogdan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.bogdan.databaseConfiguration.DatabaseConfiguration;
import com.github.bogdan.deserializer.*;
import com.github.bogdan.exception.WebException;
import com.github.bogdan.model.*;
import com.github.bogdan.serializer.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static com.github.bogdan.service.AuthService.checkAuthorization;
import static com.github.bogdan.service.CtxService.*;
import static com.github.bogdan.service.FriendService.getFriend;
import static com.github.bogdan.service.SortingService.getByQueryParams;
import static com.github.bogdan.service.UserService.*;

public class MainController {
    static Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(DatabasePath.getPath());

    private static Dao<User, Integer> userDao;
    private static Dao<Friend, Integer> friendDao;

    static {
        try {
            userDao = DaoManager.createDao(databaseConfiguration.getConnectionSource(), User.class);
            friendDao = DaoManager.createDao(databaseConfiguration.getConnectionSource(), Friend.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static <T> void add(Context ctx, Dao<T, Integer> dao, Class<T> clazz) throws JsonProcessingException, SQLException {
        ctx.header("content-type:app/json");
        SimpleModule simpleModule = new SimpleModule();
        ObjectMapper objectMapper = new ObjectMapper();

        if (clazz == User.class) {
            simpleModule.addDeserializer(User.class, new DeserializerForAddUser(userDao));
        } else {
            checkDoesBasicAuthEmpty(ctx);
            checkAuthorization(ctx, userDao);
        }

        if (clazz == Friend.class){
            int userId = getUserByLogin(ctx.basicAuthCredentials().getUsername(),userDao).getId();
            simpleModule.addDeserializer(Friend.class, new DeserializerForAddFriend(userId,userDao));
        }

        checkBodyRequestIsEmpty(ctx);
        String body = ctx.body();
        objectMapper.registerModule(simpleModule);
        Object obj = objectMapper.readValue(body, clazz);
        dao.create((T) obj);
            created(ctx);

    }

    public static <T> void get(Context ctx, Dao<T, Integer> dao, Class<T> clazz) throws JsonProcessingException, SQLException, NoSuchFieldException, IllegalAccessException, UnsupportedEncodingException {
        SimpleModule simpleModule = new SimpleModule();
        ObjectMapper objectMapper = new ObjectMapper();
        int userId = 0;
        if (ctx.basicAuthCredentialsExist()) {
            if (getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao) != null) {
                userId = Objects.requireNonNull(getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao)).getId();
            }
        }
        simpleModule.addSerializer(User.class, new UserGetSerializer());

        objectMapper.registerModule(simpleModule);

        ArrayList<String> params = new ArrayList<>();
        if (clazz == User.class) {
            User u = new User();
            params.addAll(u.getQueryParams());
        }
        if (clazz == Friend.class){
            Friend f = new Friend();
            params.addAll(f.getQueryParams());
        }

        String serialized;


        if(ctx.queryString() != null) {
            serialized = objectMapper.writeValueAsString(getByQueryParams(userId, userDao, dao, clazz, params, ctx));
        } else serialized = objectMapper.writeValueAsString(userDao.queryForAll());
        ctx.result(serialized);
    }

    public static <T> void getById(Context ctx, Dao<T, Integer> dao, Class<T> clazz) throws SQLException, JsonProcessingException {
        SimpleModule simpleModule = new SimpleModule();
        ObjectMapper objectMapper = new ObjectMapper();
        int userId = 0;
        if (ctx.basicAuthCredentialsExist()) {
            if (getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao) != null) {
                userId = getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao).getId();
            }
        }
        simpleModule.addSerializer(User.class, new UserGetSerializer());

        objectMapper.registerModule(simpleModule);
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (dao.queryForId(id) == null) {
            throw new WebException("Такого не существует", 404);
        }
        String serialized = objectMapper.writeValueAsString(dao.queryForId(id));

        ctx.result(serialized);

    }

    public static <T> void change(Context ctx, Dao<T, Integer> dao, Class<T> clazz) throws JsonProcessingException, SQLException {
        checkDoesBasicAuthEmpty(ctx);

        ctx.header("content-type", "app/json");
        SimpleModule simpleModule = new SimpleModule();
        ObjectMapper objectMapper = new ObjectMapper();
        int id = Integer.parseInt(ctx.pathParam("id"));
        checkBodyRequestIsEmpty(ctx);
        String body = ctx.body();

        checkAuthorization(ctx, userDao);
        if (clazz == User.class) {
            if (getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao).getRole() != Role.ADMIN) {
                if (id != getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao).getId()) {
                    youAreNotAdmin(ctx);
                }
            }
            checkDoesSuchUserExist(id, userDao);
            simpleModule.addDeserializer(User.class, new DeserializerForChangeUser(id, userDao));
        }


        objectMapper.registerModule(simpleModule);
        Object obj = objectMapper.readValue(body, clazz);

        dao.update((T) obj);
        updated(ctx);
    }

    public static <T> void delete(Context ctx, Dao<T, Integer> dao, Class<T> clazz) throws JsonProcessingException, SQLException {
        checkDoesBasicAuthEmpty(ctx);
        ctx.header("content-type:app/json");
        SimpleModule simpleModule = new SimpleModule();
        ObjectMapper objectMapper = new ObjectMapper();
        int id = Integer.parseInt(ctx.pathParam("id"));
        checkAuthorization(ctx, userDao);
        if (clazz == User.class) {
            if (getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao).getRole() != Role.ADMIN) {
                if (id != getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao).getId()) {
                    youAreNotAdmin(ctx);
                }
            }
            checkDoesSuchUserExist(id, userDao);
        }

        if (clazz == Friend.class){
            int userId = getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao).getId();
            id = getFriend(userId,id,friendDao,userDao).getId();

            checkDoesSuchUserExist(id, userDao);
        }

        objectMapper.registerModule(simpleModule);
        Object obj = dao.queryForId(id);
        dao.delete((T) obj);
        deleted(ctx);
    }

    public static void getAuthorized(Context ctx) throws JsonProcessingException, SQLException {
        ObjectMapper objectMapper = new ObjectMapper();

        checkDoesBasicAuthEmpty(ctx);
        checkAuthorization(ctx, userDao);
        SimpleModule simpleModule = new SimpleModule();

        simpleModule.addSerializer(User.class, new UserGetSerializer());
        objectMapper.registerModule(simpleModule);

        ctx.result(objectMapper.writeValueAsString(getUserByLogin(ctx.basicAuthCredentials().getUsername(), userDao)));
    }
}