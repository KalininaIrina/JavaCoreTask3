package com.task.port.entity;

import com.task.port.resource.Port;
import com.task.port.resource.Warehouse;
import com.task.port.state.ShipState;
import com.task.port.state.impl.CompletedState;
import com.task.port.state.impl.NewState;
import com.task.port.state.impl.ProcessingState;
import com.task.port.state.impl.WaitingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class Ship implements Callable<String> {
    private static final Logger logger = LogManager.getLogger();

    private final int id;
    private final int capacity;
    private int currentContainers;
    private final ShipAction action;
    private final int targetAmount;

    private ShipState state;

    public Ship(int id, int capacity, int currentContainers, ShipAction action, int targetAmount){

        this.id = id;
        this.capacity = capacity;
        this.currentContainers = currentContainers;
        this.action = action;
        this.targetAmount = targetAmount;
        this.state = new NewState();

    }

    @Override
    public String call(){
        logger.info("Ship {} arrived. State: {}. Goal: {} {} containers", id, state.getStateName(), action, targetAmount);

        try{
            this.state = new WaitingState();
            Port port = Port.getInstance();

            port.acquireDock();

            this.state = new ProcessingState();
            logger.info("Ship {} docked. State: {}", id, state.getStateName());

            Warehouse warehouse = port.getWarehouse();

            if (action == ShipAction.LOAD_TO_PORT) {
                // Разгрузка с Корабль на Склад
                // Проверяем, есть ли у нас столько контейнеров
                if (currentContainers >= targetAmount) {
                    warehouse.addContainers(targetAmount);
                    currentContainers -= targetAmount;
                } else {
                    logger.error("Ship {} error: Not enough containers on board!", id);
                }

            } else if (action == ShipAction.LOAD_FROM_PORT) {
                // Загрузка со Склад на Корабль
                // Проверяем, влезет ли
                if (currentContainers + targetAmount <= capacity) {
                    warehouse.getContainers(targetAmount);
                    currentContainers += targetAmount;
                } else {
                    logger.error("Ship {} error: Not enough space on ship!", id);
                }
            }


        } catch (Exception e) {
            logger.error("Ship {} crashed during operation", id, e);
            return "Ship " + id + " failed";
        } finally {
            Port.getInstance().releaseDock();
            this.state = new CompletedState();
            logger.info("Ship {} departed. State: {}. Containers on board: {}", id, state.getStateName(), currentContainers);
        }
        return "Ship " + id + " completed successfully";

    }

}
