package com.task.port.main;

import com.task.port.entity.Ship;
import com.task.port.entity.ShipAction;
import com.task.port.reader.PortReader;
import com.task.port.resource.Port;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        PortReader reader = new PortReader();
        List<String> lines = reader.readLines("data/port_data.txt");

        if (lines.isEmpty()) {
            logger.error("File is empty or not found!");
            return;
        }

        try {
            String[] portConfig = lines.get(0).split(" ");
            int dockCount = Integer.parseInt(portConfig[0]);
            int warehouseCapacity = Integer.parseInt(portConfig[1]);
            int initialContainers = Integer.parseInt(portConfig[2]);

            Port.getInstance().init(dockCount, warehouseCapacity, initialContainers);

        } catch (Exception e) {
            logger.fatal("Invalid port config in file!", e);
            return;
        }

        List<Ship> ships = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            try {
                String[] parts = lines.get(i).split(" ");
                int id = Integer.parseInt(parts[0]);
                int capacity = Integer.parseInt(parts[1]);
                int current = Integer.parseInt(parts[2]);
                ShipAction action = ShipAction.valueOf(parts[3]); // Превращаем строку в Enum
                int target = Integer.parseInt(parts[4]);

                ships.add(new Ship(id, capacity, current, action, target));

            } catch (Exception e) {
                logger.error("Error parsing ship line: " + lines.get(i), e);
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        logger.info("STARTING SIMULATION with {} ships...", ships.size());

        try {
            List<Future<String>> results = executorService.invokeAll(ships);

            for (Future<String> result : results) {
                logger.info("Result: {}", result.get());
            }

        } catch (Exception e) {
            logger.error("Simulation failed", e);
        } finally {
            executorService.shutdown();
        }

        logger.info("SIMULATION FINISHED. Final Warehouse State: {}/{}",
                Port.getInstance().getWarehouse().getContainerCount(), 100);
    }
}