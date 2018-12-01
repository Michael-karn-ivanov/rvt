package mivanov.rev;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import mivanov.rev.api.Router;
import mivanov.rev.logic.UserController;

public class AppModule extends AbstractModule {
    protected void configure() {
        bindConstant().annotatedWith(Names.named("port")).to(8080);
        bindConstant().annotatedWith(Names.named("jdbc")).to("jdbc:h2:mem:account");
        bind(Router.class).in(Singleton.class);
        bind(UserController.class);
        bind(JdbcPooledConnectionSource.class).in(Singleton.class);
    }
}
