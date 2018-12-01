package mivanov.rev.api;

import com.google.gson.Gson;
import mivanov.rev.api.model.PaymentOrder;
import mivanov.rev.model.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ApiClient {
    private int port;

    public ApiClient(int port) {
        this.port = port;
    }

    public HttpResponse CreateUser(String user) throws IOException {
        HttpUriRequest createRequest = new HttpPut(String.format("http://localhost:%d/user/%s", port, user));
        return HttpClientBuilder.create().build().execute( createRequest );
    }

    public User GetUser(String user) throws IOException {
        HttpUriRequest getRequest = new HttpGet(String.format("http://localhost:%d/user/%s", port, user));
        HttpResponse getResponse = HttpClientBuilder.create().build().execute(getRequest);
        return new Gson().fromJson(EntityUtils.toString(getResponse.getEntity()), User.class);
    }

    public HttpResponse Topup(String user, Double amount) throws IOException {
        HttpPut topupRequest = new HttpPut(String.format("http://localhost:%d/topup", port));
        topupRequest.addHeader("content-type", "application/json");
        PaymentOrder order = new PaymentOrder();
        order.setToUser(user);
        order.setAmount(amount);
        topupRequest.setEntity(new StringEntity(new Gson().toJson(order)));
        return HttpClientBuilder.create().build().execute( topupRequest );
    }

    public HttpResponse Deduct(String user, Double amount) throws IOException {
        HttpPut spendRequest = new HttpPut(String.format("http://localhost:%d/spend", port));
        spendRequest.addHeader("content-type", "application/json");
        PaymentOrder order = new PaymentOrder();
        order.setToUser(user);
        order.setAmount(amount);
        spendRequest.setEntity(new StringEntity(new Gson().toJson(order)));
        return HttpClientBuilder.create().build().execute( spendRequest );
    }
}
