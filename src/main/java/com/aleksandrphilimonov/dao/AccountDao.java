package com.aleksandrphilimonov.dao;

import com.aleksandrphilimonov.exception.CustomException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    private final DataSource dataSource;

    public AccountDao() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("password");

        dataSource = new HikariDataSource(config);
    }

    public List<AccountModel> findById(long user_id) {
        List<AccountModel> accountModelList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            AccountModel accountModel;
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from account " +
                            "where user_id = ?"
            );
            preparedStatement.setLong(1, user_id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                accountModel = new AccountModel();
                accountModel.setAccountId(resultSet.getLong(1));
                accountModel.setTitle(resultSet.getString(2));
                accountModel.setBalance(new BigDecimal(resultSet.getLong(3)));
                accountModel.setUserId(resultSet.getLong(4));

                accountModelList.add(accountModel);
            }
        } catch (SQLException e) {
            throw new CustomException(e);
        }
        return accountModelList;
    }

    public AccountModel createAccount(String title, double balance, long userId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into account(title, balance, user_id) " +
                            "values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            preparedStatement.setString(1, title);
            preparedStatement.setDouble(2, balance);
            preparedStatement.setDouble(3, userId);

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                AccountModel accountModel = new AccountModel();

                accountModel.setTitle(resultSet.getString(1));
                accountModel.setBalance(BigDecimal.valueOf(resultSet.getDouble(2)));
                accountModel.setUserId(resultSet.getLong(3));
                return accountModel;
            } else {
                throw new CustomException("Invalid data. New ID didn't generate.");
            }
        } catch (SQLException e) {
            throw new CustomException(e);
        }
    }

    public AccountModel deleteAccount(String title, long userId) {
        AccountModel accountModel = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select from account where title = ? and user_id = ?"
            );
            preparedStatement.setString(1, title);
            preparedStatement.setLong(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                try (Connection connection1 = dataSource.getConnection()) {
                    PreparedStatement preparedStatement1 = connection1.prepareStatement(
                            "delete from account " +
                                    "where title = ? and user_id = ?",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement1.setString(1, title);
                    preparedStatement1.setLong(2, userId);

                    preparedStatement1.executeUpdate();

                    ResultSet resultSet1 = preparedStatement1.getGeneratedKeys();
                    if (resultSet1.next()) {
                        accountModel = new AccountModel();
                        accountModel.setTitle(resultSet1.getString(1));
                        accountModel.setBalance(BigDecimal.valueOf(resultSet1.getDouble(2)));
                        accountModel.setUserId(resultSet1.getLong(3));

                    } else {
                        throw new CustomException("Invalid data. New ID didn't generate.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new CustomException(e);
        }
        return accountModel;
    }
}
