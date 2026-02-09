package com.revplay.service;

import com.revplay.dao.UserDAO;
import com.revplay.model.User;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public User registerAndReturnUser(String name, String email, String password, String role, String question, String answer) {
        return userDAO.registerAndReturnUser(name, email, password, role, question, answer);
    }


    public User login(String email, String password) {
        return userDAO.login(email, password);
    }
}

