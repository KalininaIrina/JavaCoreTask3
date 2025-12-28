package com.task.port.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static final Logger logger = LogManager.getLogger();

    private Port() {
    }

    private static class PortHolder {
        private static final Port instance = new Port();
    }

    public static Port getInstance() {
        return PortHolder.instance;
    }

    private Warehouse warehouse;
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    private int freeDocks;
    private final Lock lock = new ReentrantLock(true);
    private final Condition dockAvailable = lock.newCondition();

    public void init(int dockCount, int warehouseCapacity, int initialContainers) {

        if (isInitialized.compareAndSet(false, true)) {

            this.freeDocks = dockCount;
            this.warehouse = new Warehouse(warehouseCapacity, initialContainers);

            logger.info("Port initialized with {} docks and warehouse {}/{}",
                    dockCount, initialContainers, warehouseCapacity);
        } else {
            logger.warn("Port is already initialized!");
        }
    }

    public void acquireDock() {
        lock.lock();
        try {
            while (freeDocks <= 0) {
                logger.debug("No docks available. Ship is waiting...");
                dockAvailable.await();
            }

            freeDocks--;
            logger.debug("Dock acquired. Docks left: {}", freeDocks);

        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for dock", e);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }


    public void releaseDock() {
        lock.lock();
        try {
            freeDocks++;
            logger.debug("Dock released. Docks available: {}", freeDocks);

            dockAvailable.signal();

        } finally {
            lock.unlock();
        }
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }
}