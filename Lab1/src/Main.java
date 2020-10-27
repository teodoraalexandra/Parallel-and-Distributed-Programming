import controller.Controller;
import model.Account;
import model.Counter;
import model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        //Creating the accounts
        List<Account> listOfAccounts = new ArrayList<>();
        listOfAccounts.add(new Account(1,100));
        listOfAccounts.add(new Account(2,200));
        listOfAccounts.add(new Account(3,300));
        listOfAccounts.add(new Account(4,400));
        listOfAccounts.add(new Account(5,500));

        System.out.println("Program started... \n");
        AtomicBoolean isRunning = new AtomicBoolean(true);

        /*int NUMBER_OF_THREADS = 5;
        int NUMBER_OF_TRANSACTIONS = 10;*/

        Scanner input_threads = new Scanner(System.in);
        System.out.println("Enter number of threads: ");
        int NUMBER_OF_THREADS = Integer.parseInt(input_threads.nextLine());

        Scanner input_transactions = new Scanner(System.in);
        System.out.println("Enter number of transactions: ");
        int NUMBER_OF_TRANSACTIONS = Integer.parseInt(input_transactions.nextLine());

        AtomicInteger correct_transactions = new AtomicInteger();

        ExecutorService executorService = null;
        Counter counter = new Counter();
        Controller controller = new Controller(listOfAccounts);

        try {
            executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

            Runnable main_task = () -> {
                while (correct_transactions.get() < NUMBER_OF_TRANSACTIONS) {
                    //Time for make random transactions
                    Random rand = new Random();
                    int sender = rand.nextInt(5) + 1;
                    int receiver = rand.nextInt(5) + 1;

                    while (sender == receiver) {
                        sender = rand.nextInt(5) + 1;
                        receiver = rand.nextInt(5) + 1;
                    }

                    int min = 50;
                    int max = 250;
                    int sum = (int) (Math.random() * (max - min + 1) + min);
                    Transaction transaction = new Transaction(counter.getValue(), sender, receiver, sum);
                    try {
                        if (controller.beginTransaction(transaction)) {
                            System.out.println("Operation has been done successfully");
                            //Increment the id of the transaction
                            counter.increment();
                            correct_transactions.addAndGet(1);
                        } else {
                            System.out.println("Transaction has been canceled due to insufficient funds");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isRunning.set(false);
            };

            Runnable check_task = () -> {
                while (isRunning.get()) {
                    try {
                        Thread.sleep(2000);
                        if (controller.checkAccounts() && controller.checkPairTransactions())
                            System.out.println("  Checker ---> GOOD");
                        else {
                            System.out.println("CHECKER FAILED");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            executorService.submit(main_task);
            executorService.submit(check_task);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (executorService != null) {
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.MINUTES);
                System.out.println("\nProgram finished. Final list of accounts:");
                controller.printAccounts();
                System.out.println("\nFinal check.");
                if (controller.checkAccounts() && controller.checkPairTransactions())
                    System.out.println("  Checker ---> GOOD");
                else {
                    System.out.println("CHECKER FAILED");
                }
            }
        }
    }
}

