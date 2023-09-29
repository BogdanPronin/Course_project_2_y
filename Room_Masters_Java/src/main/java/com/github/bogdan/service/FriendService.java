package com.github.bogdan.service;

import com.github.bogdan.exception.WebException;
import com.github.bogdan.model.Friend;
import com.github.bogdan.model.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class FriendService {
    public static Friend getFriend(int user1Id, int user2Id, Dao<Friend, Integer> friendDao, Dao<User, Integer> userDao) throws SQLException {
        for(Friend f: friendDao.queryForAll()){
            if(f.getUser1().getId() == user1Id && f.getUser2().getId() == user2Id) {
                return f;
            }
        }
        throw new WebException("You didn't subscribe", 400);
    }
}
