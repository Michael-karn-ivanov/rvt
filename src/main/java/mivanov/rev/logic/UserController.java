package mivanov.rev.logic;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import mivanov.rev.logic.status.CreateUserStatus;
import mivanov.rev.model.User;

import java.sql.SQLException;

public class UserController {
    Dao<User, String> userDao;

    @Inject public UserController(JdbcPooledConnectionSource connectionSource) throws SQLException {
        userDao = DaoManager.createDao(connectionSource, User.class);
    }

    public CreateUserStatus CreateUser(String name) {
        try {
            userDao.create(new User(name));
            return CreateUserStatus.CREATED;
        } catch (SQLException exception) {
            return CreateUserStatus.ALREADY_EXISTS;
        }
    }

    public User GetUser(String name) {
        try {
            return userDao.queryForId(name);
        } catch (SQLException exception) {
            return null;
        }
    }
}
