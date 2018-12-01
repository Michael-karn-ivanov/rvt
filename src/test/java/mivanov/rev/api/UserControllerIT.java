package mivanov.rev.api;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import mivanov.rev.AppModule;
import mivanov.rev.api.model.PaymentOrder;
import mivanov.rev.model.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class UserControllerIT {
    static int port;
    static Injector injector;
    static ApiClient apiClient;

    @BeforeClass
    public static void setUp() throws SQLException {
        injector = Guice.createInjector(new AppModule());
        injector.getInstance(Router.class).Start(injector);
        port = injector.getInstance(Key.get(Integer.class, Names.named("port")));
        apiClient = new ApiClient(port);
    }

    @AfterClass
    public static void tearDown() {
        injector.getInstance(Router.class).Stop();
    }

    @Test
    public void testFirstCallToCreateUserGives201() throws SQLException, IOException {
        HttpResponse firstResponse = apiClient.CreateUser(UUID.randomUUID().toString());
        Assert.assertEquals(201, firstResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testSecondCallToCreateUserGives409() throws SQLException, IOException {
        String userId = UUID.randomUUID().toString();
        apiClient.CreateUser(userId);
        HttpResponse secondResponse = apiClient.CreateUser(userId);
        Assert.assertEquals(409, secondResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testBalanceOfJustCreatedAccountIs0() throws IOException {
        String userId = UUID.randomUUID().toString();
        apiClient.CreateUser(userId);
        User user = apiClient.GetUser(userId);
        Assert.assertEquals(Double.valueOf(0), user.Balance);
    }

    @Test
    public void testOneTopupProperlySetBalance() throws SQLException, IOException {
        String userId = UUID.randomUUID().toString();
        apiClient.CreateUser(userId);
        HttpResponse topupResponse = apiClient.Topup(userId, 1.234);
        User user = apiClient.GetUser(userId);
        Assert.assertEquals(Double.valueOf(1.234), user.Balance);
        Assert.assertEquals(200, topupResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testSeveralTopupsSummarizeInBalance() throws IOException {
        String userId = UUID.randomUUID().toString();
        apiClient.CreateUser(userId);
        apiClient.Topup(userId, 1.234);
        apiClient.Topup(userId, 2.345);
        apiClient.Topup(userId, 3.456);
        User user = apiClient.GetUser(userId);
        Assert.assertEquals(Double.valueOf(7.035), user.Balance);
    }
}
