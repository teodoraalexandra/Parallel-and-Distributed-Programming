package model;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer {
    private Queue<Pair<Integer, Boolean>> queue;
    private Integer size;

    // Protect the access for the queue
    private ReentrantLock lock;

    // See if the queue is full or empty
    private Condition cv;

    public ProducerConsumer(int size) {
        this.queue = new LinkedList<>();
        this.size = size;
        this.lock = new ReentrantLock();
        this.cv = lock.newCondition();
    }

    public void produce(Integer product, Boolean isLast){
        lock.lock();
        try {
            while (queue.size() == this.size) {
                // Queue is full -> producer must wait for the consumer to "consume"
                cv.await();
            }
            queue.add(new Pair<>(product, isLast));
            cv.signalAll(); // Notify()
            System.out.println("Producer produces: " + product);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public Pair<Integer, Boolean> consume(){
        lock.lock();
        try {
            while (queue.size() == 0) {
                // Queue is empty -> consumer must wait for the producer to "produce"
                cv.await();
            }
            Pair<Integer, Boolean> received = queue.remove();
            cv.signalAll(); // Notify()
            System.out.println("Consumer consumes: " + received.getKey());
            Thread.sleep(1000);
            return received;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }
}
