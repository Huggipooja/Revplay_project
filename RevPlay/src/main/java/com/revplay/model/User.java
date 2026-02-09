package com.revplay.model;

public class User {

    private int userId;
    private String username;
    private String email;
    private String password;
    private String role;
	private String security_question;
	private String security_answer; 

    public User() {}

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


public String getSecurityQuestion() {
    return getSecurityQuestion();
}

public void setSecurityQuestion(String securityQuestion) {
    this.security_question = securityQuestion;
}

public String getSecurityAnswer() {
    return getSecurityAnswer();
}

public void setSecurityAnswer(String securityAnswer) {
    this.security_answer = securityAnswer;
}

}
