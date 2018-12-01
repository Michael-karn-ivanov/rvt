package mivanov.rev.logic;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import mivanov.rev.logic.status.AddMoneyStatus;
import mivanov.rev.logic.status.DeductMoneyStatus;
import mivanov.rev.model.MoneyBucket;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MoneyController {
    Dao<MoneyBucket, UUID> moneyBucketDao;

    @Inject public MoneyController(JdbcPooledConnectionSource connectionSource) throws SQLException {
        moneyBucketDao = DaoManager.createDao(connectionSource, MoneyBucket.class);
    }

    public DeductMoneyStatus DeductMoney(String user, Double amount) {
        try {
            int rawsUpdated = moneyBucketDao.executeRaw(
                    "update moneybucket " +
                            "set amount = amount - ? " +
                            "where user = ? and amount >= ? " +
                            "limit 1", amount.toString(), user, amount.toString());
            return DeductMoneyStatus.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
            return DeductMoneyStatus.NOT_ENOUGH_BALANCE;
        }
    }

    public AddMoneyStatus AddMoney(String user, Double amount) throws SQLException {
        moneyBucketDao.create(new MoneyBucket(user, amount));
        return AddMoneyStatus.SUCCESS;
    }

    public Double GetBalance(String user) {
        try {
            GenericRawResults<Object[]> rawResults = moneyBucketDao.queryRaw(
                    "select sum(amount) from moneybucket where user = ?",
                    new DataType[]{DataType.DOUBLE},
                    user
            );
            List<Object[]> results = rawResults.getResults();
            if (results.isEmpty()) return Double.valueOf(0);
            else return (Double) results.get(0)[0];
        } catch (SQLException exception) {
            return null;
        }
    }
}
