
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            MatrizRMIServer server = new MatrizRMIServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("MatrixServer", server);
            System.out.println("Servidor de matrices listo");
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}