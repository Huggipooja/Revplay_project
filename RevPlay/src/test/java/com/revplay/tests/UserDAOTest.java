package com.revplay.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.revplay.dao.UserDAO;
import com.revplay.model.User;

public class UserDAOTest {

	@Test
	public void testRegisterAndReturnUser() {

	    UserDAO userDAO = new UserDAO();

	    String email = "junit_" + System.currentTimeMillis() + "@gmail.com";

	    User user = userDAO.registerAndReturnUser(
	            "JUnit User",
	            email,
	            "password123",
	            "USER",
	            "Your pet name?",
	            "tom"
	    );

	    assertNotNull(user);
	    assertEquals(email, user.getEmail());
	}


    @Test
    public void testLoginSuccess() {

        UserDAO userDAO = new UserDAO();

        User user = userDAO.login("junit_user@gmail.com", "password123");

        assertNotNull(user);
    }

    @Test
    public void testLoginFailure() {

        UserDAO userDAO = new UserDAO();

        User user = userDAO.login("invalid@gmail.com", "invalid");

        assertNull(user);
    }
}
