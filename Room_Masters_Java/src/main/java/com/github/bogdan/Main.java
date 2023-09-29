package com.github.bogdan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.bogdan.controller.MainController;
import com.github.bogdan.databaseConfiguration.DatabaseConfiguration;
import com.github.bogdan.exception.WebException;
import com.github.bogdan.model.*;
import com.github.bogdan.serializer.WebExceptionSerializer;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import io.javalin.Javalin;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {


        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.enableDevLogging();
            javalinConfig.enableCorsForAllOrigins();
            javalinConfig.defaultContentType = "application/json";
        }).start(22867);


        String jdbcConnectionString = "jdbc:sqlite:/Users/bogdanpronin/Documents/Room_Masters_Java/roomMasters.db";
        DatabasePath.setPath(jdbcConnectionString);
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(jdbcConnectionString);

        Dao<User, Integer> userDao = DaoManager.createDao(databaseConfiguration.getConnectionSource(), User.class);
        Dao<Friend, Integer> friendDao = DaoManager.createDao(databaseConfiguration.getConnectionSource(), Friend.class);

        app.post("/users", ctx -> MainController.add(ctx,userDao,User.class));
        app.get("/users", ctx -> MainController.get(ctx,userDao, User.class));
        app.get("/users/:id", ctx -> MainController.getById(ctx,userDao, User.class));
        app.patch("/users/:id", ctx -> MainController.change(ctx,userDao,User.class));
        app.delete("/users/:id",ctx -> MainController.delete(ctx,userDao,User.class));

        app.post("/friends", ctx -> MainController.add(ctx,friendDao,Friend.class));
        app.get("/friends", ctx -> MainController.get(ctx,friendDao,Friend.class));
        app.get("/friends/:id", ctx -> MainController.getById(ctx,friendDao,Friend.class));
        app.delete("/friends/:id",ctx -> MainController.delete(ctx,friendDao,Friend.class));

        app.get("/authorized", MainController::getAuthorized);


        app.exception(IllegalArgumentException.class,(e, ctx) ->{
            WebException w = new WebException("Such enum constant doesn't exist: "+e.getMessage(),400);
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(WebException.class,new WebExceptionSerializer());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(simpleModule);
            try {
                ctx.result(objectMapper.writeValueAsString(w));
                ctx.status(w.getStatus());
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        });
        app.exception(WebException.class, (e, ctx) -> {
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(WebException.class,new WebExceptionSerializer());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(simpleModule);
            try {
                ctx.result(objectMapper.writeValueAsString(e));
                ctx.status(e.getStatus());
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        });
    }
}
