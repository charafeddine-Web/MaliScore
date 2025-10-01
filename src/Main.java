import ui.MenuClient;
import ui.MenuPranc;

public class Main {

    public static void main(String[] args){
        try {
            new MenuPranc().start();
        } catch (Exception e) {
            System.out.println("Errour" + e);
            e.printStackTrace();
        }
    }
}
