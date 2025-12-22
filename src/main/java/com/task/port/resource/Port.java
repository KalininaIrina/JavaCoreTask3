package com.task.port.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Port {
    private static final Logger logger = LogManager.getLogger();

     private Warehouse warehouse;
     private Semaphore docks;

     private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    private Port() {
    }

    private static class PortHolder {
        private static final Port INSTANCE = new Port();
    }

    public static Port getInstance() {
        return PortHolder.INSTANCE;
    }

    public void init(int dockCount, int warehouseCapacity, int initialContainers){

        if(isInitialized.compareAndSet(false, true)){
            this.docks = new Semaphore(dockCount, true);
            this.warehouse = new Warehouse(warehouseCapacity, initialContainers);
            logger.info("Port initialized with {} docks and warehouse {}/{}", dockCount, initialContainers, warehouseCapacity);
        } else{
            logger.warn("Port is already initialized!");
        }

    }

    // занять причал
    public void acquireDock(){
        try{
            docks.acquire();
            logger.debug("Dock acquired. Remaining docks: {}", docks.availablePermits());
        } catch (InterruptedException e){
            logger.error("Interrupted while waiting for dock", e);
            Thread.currentThread().interrupt();
        }
    }

    //освободить причал
    public void releaseDock(){
        docks.release();
        logger.debug("Dock released. Available docks: {}", docks.availablePermits());
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }
}
