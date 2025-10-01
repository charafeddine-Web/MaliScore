import ui.MenuClient;

public class Main {

    public static void main(String[] args){
        try {
            new MenuClient().start();

        } catch (Exception e) {
            System.out.println("Errour" + e);
            e.printStackTrace();
        }
    }
}
