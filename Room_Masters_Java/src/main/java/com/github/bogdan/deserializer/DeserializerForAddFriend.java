package com.github.bogdan.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.bogdan.model.Friend;
import com.github.bogdan.model.User;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;

import static com.github.bogdan.service.DeserializerService.getIntFieldValue;
import static com.github.bogdan.service.UserService.getUserById;

public class DeserializerForAddFriend extends StdDeserializer<Friend> {
    public DeserializerForAddFriend(int userId,Dao<User, Integer> userDao) {
        super(Friend.class);
        this.userDao = userDao;
        this.userId = userId;
    }

    private final Dao<User, Integer> userDao;
    private final int userId;
    @Override
    public Friend deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {

            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            Friend friend = new Friend();

            friend.setUser1(getUserById(userId,userDao));

            int user2Id = getIntFieldValue(node, "user2");
            friend.setUser2(getUserById(user2Id,userDao));

            return friend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
