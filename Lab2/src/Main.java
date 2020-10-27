import javafx.util.Pair;
import model.ProducerConsumer;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the length of the array: ");
        int length = s.nextInt();

        int [] a = new int[length];
        int [] b = new int[length];

        int min = -15;
        int max = 15;

        //System.out.println("Enter the elements of the array a: ");
        System.out.println("Generate the elements from array a.");
        for(int i = 0; i < length; i++) {
            // Keyboard
            //a[i] = s.nextInt();
            // Random
            a[i] = (int)(Math.random() * (max - min + 1) + min);
        }

        //System.out.println("Enter the elements of the array b: ");
        System.out.println("Generate the elements from array b.");
        for(int i = 0; i < length; i++) {
            // Keyboard
            //b[i] = s.nextInt();
            // Random
            b[i] = (int)(Math.random() * (max - min + 1) + min);
        }

        System.out.println("\n" + Arrays.toString(a) + Arrays.toString(b) + "\n");

        int MAX_SIZE = 3;
        ProducerConsumer PC = new ProducerConsumer(MAX_SIZE);

        Runnable producer = () -> {
            for(int i = 0; i < length; i++){
                boolean isLast = i + 1 == length;
                PC.produce(a[i] * b[i], isLast);
            }
        };

        Runnable consumer = () ->{
            int sum = 0;
            boolean isLast = false;
            while (!isLast){
                Pair<Integer, Boolean> received = PC.consume();
                if(received != null){
                    sum += received.getKey();
                    isLast = received.getValue();
                }
            }
            System.out.println("The scalar product is: " + sum);
        };

        Thread consumerThread = new Thread(consumer);
        Thread producerThread = new Thread(producer);
        consumerThread.start();
        producerThread.start();
        consumerThread.join();
        producerThread.join();
    }
}
