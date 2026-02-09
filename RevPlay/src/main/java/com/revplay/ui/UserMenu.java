package com.revplay.ui;

import java.util.Scanner;

import com.revplay.dao.ArtistDAO;
import com.revplay.dao.UserDAO;
import com.revplay.model.Artist;
import com.revplay.model.User;
import com.revplay.service.UserService;

public class UserMenu {

    private UserService userService = new UserService();
    private Scanner sc = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n====== RevPlay ======");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                    
                case 3:
                	forgotPassword();
                	break;
                case 4:
                    System.out.println("Thank you for using RevPlay 🎵");
                    return;
                default:
                    System.out.println("❌ Invalid choice!");
            }
        }
    }

    // ================= REGISTER =================
    private void register() {

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();
        
        System.out.print("Security Question (eg: Your favourite color?): ");
        String question = sc.nextLine();

        System.out.print("Security Answer: ");
        String answer = sc.nextLine();

        System.out.print("Role (USER/ARTIST): ");
        String role = sc.nextLine().trim().toUpperCase();

        // ✅ Role validation
        if (!role.equals("USER") && !role.equals("ARTIST")) {
            System.out.println("❌ Invalid role! Must be USER or ARTIST");
            return;
        }

        User user = userService.registerAndReturnUser(username, email, password, role, question, answer);

        if (user == null) {
            System.out.println("❌ Registration failed!");
            return;
        }

        System.out.println("✅ Registration successful!");
		System.out.println("🆔 Your User ID is: " + user.getUserId());
        System.out.println("⚠ Please save this ID for reference.");

        // ===== ARTIST FLOW =====
        if (role.equals("ARTIST")) {

            ArtistDAO artistDAO = new ArtistDAO();

            // Create artist profile ONLY if not exists
            if (!artistDAO.isArtist(user.getUserId())) {

                System.out.println("\n🎤 Create your artist profile");

                System.out.print("Enter bio: ");
                String bio = sc.nextLine();

                System.out.print("Enter genre: ");
                String genre = sc.nextLine();

                System.out.print("Enter social links: ");
                String links = sc.nextLine();

                Artist artist = new Artist();
                artist.setUserId(user.getUserId());
                artist.setBio(bio);
                artist.setGenre(genre);
                artist.setSocialLinks(links);

                artistDAO.createArtist(artist);
            }

            // Go to Artist Dashboard
            new ArtistDashboard(user).start();

        } else {
            // USER Dashboard
            new UserDashboard(user).start();
        }
    }

    // ================= LOGIN =================
    private void login() {

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = userService.login(email, password);

        if (user == null) {
            System.out.println("❌ Invalid email or password");
            return;
        }

        System.out.println("✅ Login successful!");
        System.out.println("Welcome " + user.getUsername() + " (" + user.getRole() + ")");
        System.out.println("🆔 Your User ID: " + user.getUserId());

        // 🔥 THIS WAS MISSING BEFORE
        if (user.getRole().equals("ARTIST")) {
            new ArtistDashboard(user).start();
        } else {
            new UserDashboard(user).start();
        }
    }
    
    private void forgotPassword() {

        System.out.print("Enter registered email: ");
        String email = sc.nextLine();

        UserDAO dao = new UserDAO();

        String question = dao.getSecurityQuestionByEmail(email);

        if (question == null) {
            System.out.println("❌ Email not found");
            return;
        }

        System.out.println("Security Question:");
        System.out.println(question);

        System.out.print("Answer: ");
        String answer = sc.nextLine();

        if (!dao.validateSecurityAnswer(email, answer)) {
            System.out.println("❌ Incorrect answer");
            return;
        }

        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();

        System.out.print("Confirm password: ");
        String confirm = sc.nextLine();

        if (!newPass.equals(confirm)) {
            System.out.println("❌ Passwords do not match");
            return;
        }

        if (dao.updatePassword(email, newPass)) {
            System.out.println("✅ Password updated successfully!");
        } else {
            System.out.println("❌ Failed to update password");
        }
    }

}
