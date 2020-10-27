package controller;

import model.Account;
import model.Operation;
import model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller {
    private List<Account> listOfAccounts;

    public Controller(List<Account> listOfAccounts) {
        this.listOfAccounts = listOfAccounts;
    }

    public void printAccounts() {
        for (Account account : listOfAccounts) {
            System.out.println(account.toString());
        }
    }

    public boolean checkAccounts() {
        for (Account account : listOfAccounts) {
            int initialSum = account.getInitialSum();
            for (Operation operation : account.getListOfOperations()) {
                if (operation.getSign().equals("+"))
                    initialSum += operation.getSum();
                else if (operation.getSign().equals("-"))
                    initialSum -= operation.getSum();
            }
            if (initialSum != account.getActualSum())
                return false;
        }
        return true;
    }

    public boolean checkPairTransactions() {
        List<Integer> ids = new ArrayList<>();
        List<Integer> duplicates = new ArrayList<>();
        for (Account account : listOfAccounts) {
            for (Operation operation: account.getListOfOperations()) {
                if (ids.contains(operation.getId())) {
                    duplicates.add(operation.getId());
                } else {
                    ids.add(operation.getId());
                }
            }
        }

        Collections.sort(ids);
        Collections.sort(duplicates);
        return ids.equals(duplicates);
    }

    public boolean beginTransaction(Transaction transaction) throws InterruptedException {
        Account sender = null;
        Account receiver = null;

        for (Account account : listOfAccounts) {
            if (account.getId() == transaction.getSender()) {
                sender = account;
            }
            if (account.getId() == transaction.getReceiver()) {
                receiver = account;
            }
        }

        System.out.println(transaction.getId() + " --- "
                            + "Transfer from " + sender.getId()
                            + " to " + receiver.getId()
                            + " the sum of " + transaction.getSum());
        if (sender.getActualSum() < transaction.getSum()) {
            return false;
        } else {
            //sender.withdraw(transaction.getId(), transaction.getSum());
            //receiver.deposit(transaction.getId(), transaction.getSum());

            sender.transfer(transaction.getId(), sender, receiver, transaction.getSum());
            Thread.sleep(1000);
            return true;
        }
    }
}
