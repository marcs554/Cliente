package cliente;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private final String SERVERIP = "127.0.0.1"; //Dirección IP del servidor
    private final int PORT = 9999; //Puerto
    private Socket socket;
    private DataInputStream streamReceiver;
    private DataOutputStream streamSender;
    private BufferedReader bufferReceiver;

    /*Preparación y activación del socket*/
    public void client() throws IOException {
        socket = new Socket(SERVERIP, PORT);
    }

    public void startClient() throws IOException {
        Scanner kb = new Scanner(System.in);
        String op;
        /*
         * La variable breaker se encarga de decidir si el bucle while continua repitiendose o no
         * true = el bucle repite el ciclo y el servidor socket sigue en linea
         * false = sale del bucle para proceder a cerrar el socket y finalizar el programa
         */
        boolean breaker = true;
        System.out.println("Iniciando conexión con el servidor...");

        try {
            while (breaker) {
                client();

                //Se abre el canal de entrada y salida de datos
                streamSender = new DataOutputStream(socket.getOutputStream());
                streamReceiver = new DataInputStream(socket.getInputStream());

                /*El cliente recibirá confirmación del servidor de que la conexión
                * se ha realizado con éxito
                */
                System.out.println(streamReceiver.readUTF());

                System.out.print("1. Introducir nueva tortuga \n2. Eliminar una tortuga \n" +
                        "3. Mostrar tortugas \n4. Iniciar carrera \n5. Salir \nIntroduzca una opción: ");
                op =kb.nextLine();
                //Se envia la opción escogida al servidor y al menú del cliente
                streamSender.writeUTF(op);

                breaker = menu(Integer.parseInt(op));
                streamReceiver.close();
                streamSender.close();
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Cerrando la conexión");
            socket.close();
        }
    }

    /*
     * En la función "menu" se llevarán a cabo las opciones que elija el cliente:
     * 1º Añadir una tortuga
     * 2º Borrar una tortuga
     * 3º Listar todas las tortugas almacenadas
     * 4º Inicia la carrera
     * 5º Apaga el servidor
     * La funcion "menu" retornará un valor booleano que será almacenada dentro de la variable breaker
     * de la función "StartClient"
     */
    public boolean menu(int op) throws IOException {
        Scanner kb = new Scanner(System.in);
        boolean breaker = true;
        switch(op){
            case 1:
                //El cliente introducirá el nombre de la tortuga
                System.out.print("Introduce el nombre de la tortuga: ");
                streamSender.writeUTF(kb.nextLine());

                //El cliente introducirá el número del dorsal
               System.out.print("Número del dorsal: ");
                streamSender.writeUTF(kb.nextLine());

                System.out.println(streamReceiver.readUTF());
                System.out.print("Presione intro tecla para continuar...");
                kb.nextLine();
                break;

            case 2:
                //Borra la tortuga selecionada por el cliente en el arraylist del servidor
                System.out.print("Tortuga que quieres eliminar:");
                streamSender.writeUTF(kb.nextLine());
                System.out.println(streamReceiver.readUTF());
                break;

            case 3:
                /*
                * El cliente recibirá un listado de todas las tortugas almacenadas en el
                * servidor
                */
                System.out.println("TORTUGAS:");
                System.out.println("-----------------------------------------------------");
                bufferReceiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg;
                while((msg = bufferReceiver.readLine()) != null){
                    System.out.println(msg);
                }
                bufferReceiver.close();
                System.out.println("-----------------------------------------------------");
                System.out.print("Presione intro tecla para continuar...");
                kb.nextLine();
                break;

            case 4:
                bufferReceiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String puestos;
                while((puestos = bufferReceiver.readLine()) != null) {
                	System.out.println(puestos);
                }
                bufferReceiver.close();
                break;

            case 5:
                /*
                 * Si el cliente decide desconectar el servidor la variable booleana "breaker"
                 * se modificará su valor true a false, cerrando así tanto el cliente como el servidor
                 */
                System.out.println("Saliendo...");
                breaker = false;
                break;

            default:
                break;
        }
        return breaker;
    }
}
