import repository.DatabaseConnection;
import resources.ConfigDB;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args){
        try {
            ConfigDB configDB=new ConfigDB();
            DatabaseConnection db =  DatabaseConnection.getInstance(configDB);
            Connection conn=db.getConnection();

            if (conn != null) {
                System.out.println("Connecté !!!");
            } else {
                System.out.println("Échec de connexion !!!");
            }

        } catch ( SQLException | ClassNotFoundException e) {

            System.out.println("erooooor");
            e.printStackTrace();
        }
    }
}
