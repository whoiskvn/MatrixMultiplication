import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrizRMI extends Remote {
    float[][] multiplyMatrices(float[][] A, float[][] Bt) throws RemoteException;
}