package com.aleksandrphilimonov.dao;

import com.aleksandrphilimonov.exception.CustomException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CategoryDao {
    private final DataSource dataSource;

    public CategoryDao() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("password");

        dataSource = new HikariDataSource(config);
    }

    public CategoryModel createCategory(String name, long userId) {
        CategoryModel categoryModel = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from category where name = ? and user_id = ?"
            );
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                try (Connection connection1 = dataSource.getConnection()) {
                    PreparedStatement preparedStatement1 = connection1.prepareStatement(
                            "insert into category(name, user_id) " +
                                    "values (?, ?)", Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement1.setString(1, name);
                    preparedStatement1.setLong(2, userId);

                    ResultSet resultSet1 = preparedStatement1.getGeneratedKeys();
                    if (resultSet1.next()) {
                        categoryModel = new CategoryModel();
                        categoryModel.setName(resultSet1.getString(1));
                        categoryModel.setUserId(resultSet1.getLong(2));
                    } else {
                        throw new CustomException("Invalid data. New ID didn't generate.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new CustomException(e);
        }
        return categoryModel;
    }

    public CategoryModel update(String name, String newName, long userId) {
        CategoryModel categoryModel;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("update category set name = ? " +
                     "where name = ? and user_id = ?")) {

            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, name);
            preparedStatement.setLong(3, userId);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                categoryModel = new CategoryModel();
                categoryModel.setName(resultSet.getString("name"));
                categoryModel.setUserId(resultSet.getLong("user_id"));
            } else {
                throw new CustomException("Invalid data. Category didn't update");
            }
        } catch (SQLException e) {
            throw new CustomException(e);
        }

        return categoryModel;
    }

    public void delete(String name) {
        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from category " +
                            "where name = ?"
            );
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                try (Connection connection1 = dataSource.getConnection()) {
                    PreparedStatement preparedStatement1 = connection1.prepareStatement(
                            "delete from category " +
                                    "where name = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement1.setString(1, name);
                    preparedStatement1.executeUpdate();
                }
            } else {
                throw new CustomException("Invalid request. Category didn't delete.");
            }

        } catch (SQLException e) {
            throw new CustomException(e);
        }
    }
}
