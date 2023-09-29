package com.github.bogdan.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.bogdan.model.Role;
import com.github.bogdan.model.User;
import com.google.i18n.phonenumbers.NumberParseException;
import com.j256.ormlite.dao.Dao;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.github.bogdan.service.ContactService.*;
import static com.github.bogdan.service.DeserializerService.*;
import static com.github.bogdan.service.UserService.*;

public class DeserializerForAddUser extends StdDeserializer<User> {
    public DeserializerForAddUser(Dao<User,Integer> userDao) {
        super(User.class);
        this.userDao = userDao;
    }
    private final Dao<User,Integer> userDao;

    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            User u = new User();

            String email = getOldStringFieldValue(node,"email",null);
            u.setEmail(email);
            if(email!=null) {
                checkValidateEmail(email);
                checkIsEmailAlreadyInUse(email,userDao);
            }

            String nickName = getOldStringFieldValue(node,"nickName",null);
            u.setNickName(nickName);
            if(nickName!=null){
                checkIsNickNameAlreadyInUse(nickName,userDao);
            }

            checkIsEmailPhoneNull(nickName,email);

            LocalDate localDate = LocalDate.now();
            u.setCreatedAt(localDate.toString());

            u.setUpdatedAt(localDate.toString());

            u.setRole(Role.USER);

            String password = getStringFieldValue(node,"password");
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            u.setPassword(hashedPassword);

            return u;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
