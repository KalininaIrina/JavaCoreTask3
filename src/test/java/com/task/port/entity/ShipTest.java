package com.task.port.entity;

import com.task.port.exception.PortException;
import com.task.port.resource.Port;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @BeforeEach
    void setUp() {
        Port.getInstance().init(2, 100, 50);
    }

    @Test
    void call_LoadFromPort_Successful() throws Exception {
        Ship ship = new Ship(1, 100, 0, ShipAction.LOAD_FROM_PORT, 10);

        String result = ship.call();

        assertEquals("Ship 1 completed successfully", result);
        assertEquals(40, Port.getInstance().getWarehouse().getContainerCount());
    }

    @Test
    void call_LoadToPort_Successful() throws Exception {
        int initialWarehouseCount = Port.getInstance().getWarehouse().getContainerCount();

        Ship ship = new Ship(2, 100, 50, ShipAction.LOAD_TO_PORT, 20);

        String result = ship.call();

        assertEquals("Ship 2 completed successfully", result);
        assertEquals(initialWarehouseCount + 20, Port.getInstance().getWarehouse().getContainerCount());
    }

    @Test
    void call_LogicError_ThrowsPortException() {
        Ship ship = new Ship(3, 100, 10, ShipAction.LOAD_TO_PORT, 50);

        assertThrows(PortException.class, () -> ship.call());
    }
}