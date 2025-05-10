package com.library.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserService {

    // Simple method to hash the password (you can use a more secure approach like bcrypt in real-world apps)
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
