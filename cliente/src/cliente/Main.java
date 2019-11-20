package cliente;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        int op;

        try{
            Cliente socket = new Cliente();
            socket.startClient();
        } catch(IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}