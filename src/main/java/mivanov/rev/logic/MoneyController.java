package mivanov.rev.logic;

import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import mivanov.rev.logic.status.AddMoneyStatus;
import mivanov.rev.logic.status.DeductMoneyStatus;
import mivanov.rev.model.MoneyBucket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class MoneyController {
    private static final Logger logger = LogManager.getLogger("controller");
    Dao<MoneyBucket, UUID> moneyBucketDao;
    @Inject JdbcPooledConnectionSource connectionSource;

    @Inject public MoneyController(JdbcPooledConnectionSource connectionSource) throws SQLException {
        moneyBucketDao = DaoManager.createDao(connectionSource, MoneyBucket.class);
    }

    public DeductMoneyStatus DeductMoney(String user, Double amount) {
        return DeductMoney(user, amount, true);
    }

    private DeductMoneyStatus DeductMoney(String user, Double amount, Boolean tryToWipeBuckets) {
        try {
            int rowsUpdated = moneyBucketDao.executeRaw(
                    "update moneybucket " +
                            "set amount = amount - ? " +
                            "where user = ? and amount >= ? " +
                            "limit 1", amount.toString(), user, amount.toString());
            if (rowsUpdated == 1) {
                return DeductMoneyStatus.SUCCESS;
            } else if (rowsUpdated == 0) {
                if (tryToWipeBuckets) {
                    TransactionManager.callInTransaction(connectionSource, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            moneyBucketDao.executeRaw("update moneybucket set user = ? where user = ?",
                                    String.format("%s.sum", user), user);
                            Double wipedBalance = GetBalance(String.format("%s.sum", user));
                            AddMoney(user, wipedBalance);
                            moneyBucketDao.executeRaw("delete moneybucket where user = ?",
                                    String.format("%s.sum", user));
                            return true;
                        }
                    });
                    return DeductMoney(user, amount, false);
                } else {
                    return DeductMoneyStatus.NOT_ENOUGH_BALANCE;
                }
            } else {
                logger.error("updated more than one bucket");
                return DeductMoneyStatus.DEDUCTION_ERROR;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return DeductMoneyStatus.DEDUCTION_ERROR;
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
