package com.revplay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revplay.model.User;
import com.revplay.util.DBConnection;

public class UserDAO {

    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    /* ===================== Register & return User ===================== */
    public User registerAndReturnUser(String name, String email, String password, String role, String securityQuestion,
            String securityAnswer) {

        logger.info("Registering user with email: {}", email);

        String sql =
        	    "INSERT INTO users (user_id, username, email, password, role, security_question, security_answer) " +
        	    "VALUES (USERS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	ps.setString(1, name);
        	ps.setString(2, email);
        	ps.setString(3, password);
        	ps.setString(4, role);
        	ps.setString(5, securityQuestion);
        	ps.setString(6, securityAnswer);


            ps.executeUpdate();
            logger.info("User inserted successfully for email: {}", email);

            return getUserByEmail(email);

        } catch (Exception e) {
            logger.error("Error registering user with email: " + email, e);
            return null;
        }
    }

    /* ===================== Get User by Email ===================== */
    private User getUserByEmail(String email) {

        logger.info("Fetching user by email: {}", email);

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                return user;
            }

            logger.warn("No user found for email: {}", email);

        } catch (Exception e) {
            logger.error("Error fetching user by email: " + email, e);
        }

        return null;
    }

    /* ===================== Register user & return ID ===================== */
    public int registerUser(User user) {

        logger.info("Registering user: {}", user.getEmail());

        String sql =
            "INSERT INTO users (user_id, username, email, password, role) " +
            "VALUES (USERS_SEQ.NEXTVAL, ?, ?, ?, ?)";

        String idSql = "SELECT USERS_SEQ.CURRVAL FROM dual";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             Statement st = conn.createStatement()) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            ps.executeUpdate();

            ResultSet rs = st.executeQuery(idSql);
            if (rs.next()) {
                int userId = rs.getInt(1);
                logger.info("User registered successfully with ID: {}", userId);
                return userId;
            }

        } catch (Exception e) {
            logger.error("Error registering user: " + user.getEmail(), e);
        }

        return -1;
    }

    /* ===================== Login ===================== */
    public User login(String email, String password) {

        logger.info("Login attempt for email: {}", email);

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));

                logger.info("Login successful for user ID: {}", user.getUserId());
                return user;
            }

            logger.warn("Invalid login attempt for email: {}", email);

        } catch (Exception e) {
            logger.error("Login error for email: " + email, e);
        }

        return null;
    }
    
    
    public String getSecurityQuestionByEmail(String email) {

        logger.info("Fetching security question for email={}", email);

        String sql = "SELECT security_question FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.info("Security question found for email={}", email);
                return rs.getString("security_question");
            }

            logger.warn("No security question found for email={}", email);

        } catch (Exception e) {
            logger.error("Error fetching security question for email={}",
                    email, e);
        }
        return null;
    }


    
    public boolean validateSecurityAnswer(String email, String answer) {

        logger.info("Validating security answer for email={}", email);

        String sql =
            "SELECT COUNT(*) FROM users WHERE email = ? AND security_answer = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, answer);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean valid = rs.getInt(1) > 0;
                logger.info("Security answer validation result for email={}: {}",
                        email, valid);
                return valid;
            }

        } catch (Exception e) {
            logger.error("Error validating security answer for email={}",
                    email, e);
        }
        return false;
    }

    
    public boolean updatePassword(String email, String newPassword) {

        logger.warn("Updating password for email={}", email);

        String sql = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);

            boolean updated = ps.executeUpdate() > 0;

            logger.info("Password update status for email={}: {}",
                    email, updated);

            return updated;

        } catch (Exception e) {
            logger.error("Error updating password for email={}",
                    email, e);
        }
        return false;
    }



}
