package com.revplay.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.revplay.model.User;
import com.revplay.service.UserService;

public class UserServiceTest {

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserService();
    }

    @Test
    public void testLoginSuccess() {
        User user = userService.login("nazmeen111@gmail.com", "nazmeen124");
        assertNotNull(user);
    }

    @Test
    public void testLoginFailure() {
        User user = userService.login("wrong@gmail.com", "wrong");
        assertNull(user);
    }
}
