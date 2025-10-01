import repository.DatabaseConnection;
import resources.ConfigDB;
import ui.MenuClient;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args){
        try {
            new MenuClient().start();

        } catch (Exception e) {
            System.out.println("erooooor");
            e.printStackTrace();
        }
    }
}
