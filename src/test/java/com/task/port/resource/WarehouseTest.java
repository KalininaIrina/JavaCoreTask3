package com.task.port.resource;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    @Test
    void testAddAndGetContainers_BasicLogic() {

        Warehouse warehouse = new Warehouse(100, 50);

        warehouse.addContainers(20);
        assertEquals(70, warehouse.getContainerCount());

        warehouse.getContainers(30);
        assertEquals(40, warehouse.getContainerCount());
    }

    @Test
    void testWarehouseBlocking_WhenFull() throws InterruptedException {

        Warehouse warehouse = new Warehouse(100, 90);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch latch = new CountDownLatch(2);

        executor.submit(() -> {
            try {
                System.out.println("Thread-1: Trying to add 20...");
                warehouse.addContainers(20);
                System.out.println("Thread-1: Success adding 20!");
            } finally {
                latch.countDown();
            }
        });

        Thread.sleep(1000);

        executor.submit(() -> {
            try {
                System.out.println("Thread-2: Taking 50...");
                warehouse.getContainers(50);
                System.out.println("Thread-2: Took 50!");
            } finally {
                latch.countDown();
            }
        });


        boolean finished = latch.await(5, TimeUnit.SECONDS);
        assertTrue(finished, "Threads did not finish in time (Deadlock?)");

        assertEquals(60, warehouse.getContainerCount());

        executor.shutdown();
    }
}