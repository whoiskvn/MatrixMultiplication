import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatrizRMIServer extends UnicastRemoteObject implements MatrizRMI {

    protected MatrizRMIServer() throws RemoteException {
        super();
    }

    @Override
    public float[][] multiplyMatrices(float[][] A, float[][] Bt) throws RemoteException {
        int N = A.length;
        int M = Bt[0].length;
        float[][] C = new float[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < M; k++) {
                    C[i][j] += A[i][k] * Bt[j][k];
                }
            }
        }

        return C;
    }
}