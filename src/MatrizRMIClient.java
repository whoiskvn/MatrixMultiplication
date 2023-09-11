import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MatrizRMIClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Debe ingresar 2 argumentos: N y M");
            System.exit(1);
        }

        int N = Integer.parseInt(args[0]);
        int M = Integer.parseInt(args[1]);

        // Initialize matrices A and B
        float[][] A = new float[N][M];
        float[][] B = new float[M][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                A[i][j] = 2 * i + 3 * j;
            }
        }
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                B[i][j] = 3 * i - 2 * j;
            }
        }

        // Transpose Matrix B in Bt
        float[][] Bt = new float[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                Bt[i][j] = B[j][i];
            }
        }

        // Divide matrices A and Bt into 9 equal parts
        int parts = 3;
        float[][][] subMatricesA = divideMatrix(A, parts);
        float[][][] subMatricesBt = divideMatrix(Bt, parts);


        // Create threads to call remote method multiplyMatrices()
        Thread[] threads = new Thread[parts * parts];
        float[][][] subMatricesC = new float[parts * parts][][];

        for (int i = 0; i < parts; i++) {
            for (int j = 0; j < parts; j++) {
                final int finalI = i;
                final int finalJ = j;
                int index = i * parts + j;
                threads[index] = new Thread(() -> {
                    try {
                        Registry registry = LocateRegistry.getRegistry("localhost");
                        MatrizRMI server = (MatrizRMI) registry.lookup("MatrixServer");
                        subMatricesC[index] = server.multiplyMatrices(subMatricesA[finalI], subMatricesBt[finalJ]);
                    } catch (Exception e) {
                        System.err.println("Error en el cliente: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
                threads[index].start();
            }
        }


// Join threads and merge subMatricesC into matrix C
        float[][] C = new float[N][N];
        for (int i = 0; i < parts * parts; i++) {
            try {
                threads[i].join();
                mergeSubMatrix(C, subMatricesC[i], i / parts, i % parts);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Calculate checksum of matrix C
        double checksum = 0.0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }

        // Print checksum of matrix C
        System.out.println("Checksum: " + checksum);
    }

    private static float[][][] divideMatrix(float[][] matrix, int parts) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int subRows = rows / parts;
        int subCols = cols / parts;
        float[][][] subMatrices = new float[parts * parts][][];

        for (int i = 0; i < parts; i++) {
            for (int j = 0; j < parts; j++) {
                int index = i * parts + j;
                subMatrices[index] = new float[subRows][subCols];
                for (int x = 0; x < subRows; x++) {
                    for (int y = 0; y < subCols; y++) {
                        subMatrices[index][x][y] = matrix[i * subRows + x][j * subCols + y];
                    }
                }
            }
        }

        return subMatrices;
    }

    private static void mergeSubMatrix(float[][] matrix, float[][] subMatrix, int rowOffset, int colOffset) {
        int subRows = subMatrix.length;
        int subCols = subMatrix[0].length;

        for (int i = 0; i < subRows; i++) {
            for (int j = 0; j < subCols; j++) {
                matrix[rowOffset * subRows + i][colOffset * subCols + j] = subMatrix[i][j];
            }
        }
    }
}

