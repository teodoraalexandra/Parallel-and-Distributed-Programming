import mpi.*;

public class Main {

    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        System.out.println("CPU: <" + rank + "> of size <" + size + ">");
        MPI.Finalize();
    }
}
