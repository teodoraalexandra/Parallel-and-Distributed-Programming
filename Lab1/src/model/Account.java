package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private int id;
    private int initialSum;
    private int actualSum;
    private ReentrantLock mutex;
    private List<Operation> listOfOperations;

    public Account(int id, int initialSum) {
        this.id = id;
        this.initialSum = initialSum;
        this.actualSum = initialSum;
        this.mutex = new ReentrantLock();
        this.listOfOperations = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInitialSum() {
        return initialSum;
    }

    public void setInitialSum(int initialSum) {
        this.initialSum = initialSum;
    }

    public int getActualSum() {
        return actualSum;
    }

    public void setActualSum(int actualSum) {
        this.actualSum = actualSum;
    }

    public List<Operation> getListOfOperations() {
        return listOfOperations;
    }

    public void setListOfOperations(List<Operation> listOfOperations) {
        this.listOfOperations = listOfOperations;
    }

    // Deposit
    public void deposit(int id, int sum) {
        actualSum += sum;
        listOfOperations.add(new Operation(id, "+", sum));
    }

    // Withdraw
    public void withdraw(int id, int sum) {
        actualSum -= sum;
        listOfOperations.add(new Operation(id, "-", sum));
    }

    public void transfer(int id, Account a, Account b, int sum) {
        a.mutex.lock();
        b.mutex.lock();
        if (a.actualSum < sum) {
            a.mutex.unlock();
        }
        a.actualSum -= sum;
        a.listOfOperations.add(new Operation(id, "-", sum));

        b.actualSum += sum;
        b.listOfOperations.add(new Operation(id, "+", sum));

        a.mutex.unlock();
        b.mutex.unlock();
    }

    @Override
    public String toString() {
        return "Account{" +
                "initialSum=" + initialSum +
                ", actualSum=" + actualSum +
                ", listOfOperations=" + listOfOperations +
                '}';
    }
}
