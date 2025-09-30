package resources;

public class ConfigDB {
    private String URL ="jdbc:mysql://localhost:3306/credit_scoring";
    private String root = "root";
    private String password = "";
    private String driver = "com.mysql.cj.jdbc.Driver";

    public ConfigDB() {};

    public ConfigDB( String URL,String root,String password,String driver){
        this.URL=URL;
        this.root= root;
        this.password=password;
        this.driver=driver;
    };

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
