package com.github.bogdan.service;

import com.github.bogdan.exception.WebException;
import com.github.bogdan.model.User;
import com.j256.ormlite.dao.Dao;
import org.apache.commons.validator.routines.EmailValidator;

import java.sql.SQLException;

public class ContactService {

    public static void checkValidateEmail(String email){
        if(!doesEmailAvailable(email)){
            throw new WebException("This mail does not exist",400);
        }
    }
    public static boolean doesEmailAvailable(String email){
        email = email.trim();
        EmailValidator eValidator = EmailValidator.getInstance();
        if(eValidator.isValid(email)){
            return true;
        }else{
            return false;
        }
    }

    public static void checkIsNickNameAlreadyInUse(String phone, Dao<User, Integer> userDao) throws SQLException {
        for(User user: userDao.queryForAll()){
            if(user.getNickName()!= null) {
                if (user.getNickName().equals(phone)) {
                    throw new WebException("This nickname is already in use", 400);
                }
            }
        }
    }
    public static void checkIsNickNameAlreadyInUse(String phone, int userId, Dao<User, Integer> userDao) throws SQLException {
        for(User user: userDao.queryForAll()){
            if(user.getNickName()!= null){
                if(user.getNickName().equals(phone) && user.getId() != userId){
                    throw new WebException("This nickname is already in use",400);
                }
            }
        }
    }

    public static void checkIsEmailAlreadyInUse(String email,Dao<User, Integer> userDao) throws SQLException {
        for(User user: userDao.queryForAll()){
            if(user.getEmail()!= null){
                if(user.getEmail().equals(email)){
                    throw new WebException("This email is already in use",400);
                }
            }

        }
    }
    public static void checkIsEmailAlreadyInUse(String email,int userId,Dao<User, Integer> userDao) throws SQLException {
        for(User user: userDao.queryForAll()){
            if(user.getEmail()!= null) {
                if (user.getEmail().equals(email) && user.getId() != userId) {
                    throw new WebException("This email is already in use", 400);
                }
            }
        }
    }


}
