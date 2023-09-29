package com.github.bogdan.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.bogdan.model.User;

import java.io.IOException;

public class UserGetSerializer extends StdSerializer<User> {
    public UserGetSerializer() {
        super(User.class);
    }

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id",user.getId());
        jsonGenerator.writeStringField("email",user.getEmail());
        jsonGenerator.writeStringField("phone",user.getNickName());
        jsonGenerator.writeStringField("role",user.getRole().toString());
        jsonGenerator.writeStringField("updatedAt",user.getUpdatedAt());
        jsonGenerator.writeStringField("createdAt",user.getCreatedAt());

        jsonGenerator.writeEndObject();
    }
}
