package com.github.bogdan.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.bogdan.model.User;
import com.google.i18n.phonenumbers.NumberParseException;
import com.j256.ormlite.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.github.bogdan.service.ContactService.*;
import static com.github.bogdan.service.ContactService.checkIsNickNameAlreadyInUse;
import static com.github.bogdan.service.DeserializerService.*;

public class DeserializerForChangeUser extends StdDeserializer<User> {
    static Logger LOGGER = LoggerFactory.getLogger(DeserializerForChangeUser.class);
    public DeserializerForChangeUser(int id,Dao<User, Integer> userDao) {
        super (User.class);
        this.id = id;
        this.userDao = userDao;
    }
    private final Dao<User, Integer> userDao;
    private int id;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            User userBase = userDao.queryForId(id);
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            User u = new User();

            String email = getOldStringFieldValue(node,"email",userBase.getEmail());
            u.setEmail(email);

            if(email != null) {
                checkValidateEmail(email);
                checkIsEmailAlreadyInUse(email, id, userDao);
            }

            String nickName = getOldStringFieldValue(node,"nickName",userBase.getNickName());
            u.setNickName(nickName);
            if(nickName != null) {
                checkIsNickNameAlreadyInUse(nickName, id, userDao);
            }


            u.setCreatedAt(userBase.getCreatedAt());

            LocalDate localDate = LocalDate.now();
            u.setUpdatedAt(localDate.toString());

            u.setRole(userBase.getRole());

            String password = getOldPasswordFieldValue(node,"password",userBase.getPassword());

            u.setPassword(password);
            u.setId(id);
            return u;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
