package com.library.user;

import com.library.common.DBUtil;
import com.library.common.model.User;
import com.library.common.operations.UserOperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserOperationsImpl implements UserOperations {
    private static final Logger LOG = Logger.getLogger(UserOperationsImpl.class.getName());

    @Override
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, password, email) VALUES (?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Exception while adding user: ", e);
            return false;
        }
    }

    @Override
    public boolean checkUser(User user) {
        String sql = "SELECT id FROM users WHERE username = ? OR email = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Exception while searching user", e);
            return false;  // Return false if any error occurs, indicating user doesn't exist
        }
    }

    @Override
    public boolean updateUser(User user) {
        String query = "UPDATE users SET name = ?, email = ?, password = ? WHERE user_id = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setInt(4, user.getUserid());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error updating user profile", e);
            return false;
        }
    }
}
