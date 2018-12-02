package mivanov.rev.api;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import mivanov.rev.api.model.PaymentOrder;
import mivanov.rev.api.model.Utils;
import mivanov.rev.logic.MoneyController;
import mivanov.rev.logic.UserController;
import mivanov.rev.logic.status.GetUserStatus;
import mivanov.rev.model.MoneyBucket;
import mivanov.rev.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import static spark.Spark.*;

public class Router {
    private static final Logger logger = LogManager.getLogger("api");
    @Inject
    @Named("port")
    int portNumber;
    @Inject
    @Named("jdbc")
    String jdbcConnectionString;
    @Inject
    JdbcPooledConnectionSource connectionSource;

    public void Start(Injector injector) throws SQLException {
        startDB(injector);
        startApi(injector);
    }

    private void startDB(Injector injector) throws SQLException {
        connectionSource.setUrl(jdbcConnectionString);
        connectionSource.initialize();
        try {
            TableUtils.createTableIfNotExists(
                    injector.getInstance(JdbcPooledConnectionSource.class), User.class);
            TableUtils.createTableIfNotExists(
                    injector.getInstance(JdbcPooledConnectionSource.class), MoneyBucket.class);
        } catch (SQLException exception) {
            logger.error(exception);
            throw exception;
        }
    }

    private void startApi(Injector injector) {
        port(portNumber);
        logger.debug(String.format("Port set to %d", portNumber));
        put("/user/:name", (request, response) -> {
            try {
                response.status(
                        injector.getInstance(UserController.class).CreateUser(request.params(":name")).getValue()
                );
                return "";
            } catch (Exception exception) {
                logger.error(exception);
                throw exception;
            }
        });
        get("/user/:name", (request, response) -> {
            try {
                CompletableFuture<User> user =
                        CompletableFuture.supplyAsync(() -> injector.getInstance(UserController.class).GetUser(request.params(":name")));
                CompletableFuture<Double> balance =
                        CompletableFuture.supplyAsync(() -> injector.getInstance(MoneyController.class).GetBalance(request.params(":name")));
                return user.thenCombine(balance, (u, b) -> {
                    if (u == null) {
                        response.status(GetUserStatus.USER_DOESNT_EXIST.getValue());
                        return "";
                    } else {
                        u.Balance = Utils.decorateDouble(b);
                        response.status(GetUserStatus.USER_FOUND.getValue());
                        response.type("application/json");
                        return new Gson().toJson(u);
                    }
                }).join();
            } catch (Exception exception) {
                logger.error(exception);
                throw exception;
            }
        });
        put("/topup", (request, response) -> {
            try {
                PaymentOrder order = new Gson().fromJson(request.body(), PaymentOrder.class);
                response.status(
                        injector.getInstance(MoneyController.class).AddMoney(order.getToUser(), order.getAmount()).getValue()
                );
                return "";
            } catch (Exception exception) {
                logger.error(exception);
                throw exception;
            }
        });
        put("/spend", ((request, response) -> {
            try {
                PaymentOrder order = new Gson().fromJson(request.body(), PaymentOrder.class);
                response.status(
                        injector.getInstance(MoneyController.class).DeductMoney(order.getToUser(), order.getAmount()).getValue()
                );
                return "";
            } catch (Exception exception) {
                logger.error(exception);
                throw exception;
            }
        }));
    }

    public void Stop() {
        stop();
    }
}
