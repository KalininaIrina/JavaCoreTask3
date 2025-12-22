package com.task.port.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {
    private static final Logger logger = LogManager.getLogger();

    private int containerCount;
    private final int capacity;

    private final Lock lock = new ReentrantLock(true);

    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public Warehouse(int capacity, int initialCount){
        this.capacity = capacity;
        this.containerCount = initialCount;
    }

    public void addContainers(int amount){
        logger.debug("Ship tries to ADD {} containers. Warehouse state: {}/{}", amount, containerCount, capacity);

        lock.lock();

        try{
            while (containerCount + amount > capacity){
                logger.info("Warehouse FULL. Ship is waiting to ADD {} containers...", amount);
                notFull.await();
            }

            containerCount+=amount;
            logger.info("Ship ADDED {} containers. Warehouse: {}/{}", amount, containerCount, capacity);
            notEmpty.signalAll();

        } catch (InterruptedException e){
            logger.error("Thread interrupted while adding containers", e);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

    }

    public void getContainers(int amount){
        logger.debug("Ship tries to TAKE {} containers. Warehouse state: {}/{}", amount, containerCount, capacity);

        lock.lock();
        try {
            while (containerCount < amount) {
                logger.info("Warehouse EMPTY (not enough). Ship is waiting to TAKE {} containers...", amount);

                notEmpty.await();
            }

            containerCount -= amount;
            logger.info("Ship TOOK {} containers. Warehouse: {}/{}", amount, containerCount, capacity);

            notFull.signalAll();

        } catch (InterruptedException e) {
            logger.error("Thread interrupted while getting containers", e);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public int getContainerCount() {

        lock.lock();
        try{
            return containerCount;
        }finally {
            lock.unlock();
        }
    }
}
