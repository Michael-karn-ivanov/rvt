package mivanov.rev;

import com.google.inject.Guice;
import com.google.inject.Injector;
import mivanov.rev.api.Router;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	    System.out.println("Starting...");
        Injector injector = Guice.createInjector(new AppModule());
        try {
            injector.getInstance(Router.class).Start(injector);
            System.out.print("Service started. Press any key to terminate >> ");
        } catch (SQLException e) {
            System.out.println("Failed to start service. Press any key to terminate >>");
        }
        Scanner scanner = new Scanner(System. in);
        scanner.nextLine();
        injector.getInstance(Router.class).Stop();
    }
}
