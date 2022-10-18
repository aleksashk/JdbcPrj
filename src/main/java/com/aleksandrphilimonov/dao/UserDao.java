package com.aleksandrphilimonov.dao;

import com.aleksandrphilimonov.exception.CustomException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private final DataSource dataSource;

    public UserDao() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("password");

        dataSource = new HikariDataSource(config);
    }

    public UserModel findByEmailAndHash(String email, String hash) {
        UserModel userModel = null;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from service_user " +
                            "where email = ? and password = ?"
            );
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, hash);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userModel = new UserModel();
                userModel.setId(resultSet.getLong("id"));
                userModel.setEmail(resultSet.getString("email"));
                userModel.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            throw new CustomException(e);
        }
        return userModel;
    }

    public UserModel insert(String email, String hash) {
        UserModel userModel = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into service_user(email, password) " +
                            "values (?, ?)"
            );
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, hash);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                userModel = new UserModel();
                userModel.setEmail(resultSet.getString("email"));
                userModel.setPassword(resultSet.getString("hash"));
                return userModel;
            } else {
                throw new CustomException("Invalid data. New ID didn't generate.");
            }
        } catch (SQLException e) {
            throw new CustomException("Invalid data. New ID didn't generate.");
        }
    }
}
