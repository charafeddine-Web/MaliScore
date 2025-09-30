package repository;

import resources.ConfigDB;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

public class DatabaseConnection {

    private static DatabaseConnection instence;
    private Connection connection;
    private ConfigDB configDB ;

    private DatabaseConnection(ConfigDB configDB) throws ClassNotFoundException, SQLException {
        this.configDB= new ConfigDB();
        Class.forName(configDB.getDriver());
        this.connection = DriverManager.getConnection(
                configDB.getURL(),
                configDB.getRoot(),
                configDB.getPassword()
        );
    }

    public static DatabaseConnection getInstance(ConfigDB configDB) throws SQLException , ClassNotFoundException{
        if(instence == null){
            instence= new DatabaseConnection(configDB);
        }
        return instence;
    }

    public Connection getConnection (){
        return connection;
    }

}
