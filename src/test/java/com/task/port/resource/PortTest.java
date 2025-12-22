package com.task.port.resource;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PortTest {

    @Test
    void getInstance_ShouldReturnSameObject() {
        Port port1 = Port.getInstance();
        Port port2 = Port.getInstance();

        assertSame(port1, port2, "Port must be a Singleton");
    }

    @Test
    void init_ShouldInitializeOnlyOnce() {
        Port port = Port.getInstance();
        port.init(2, 100, 50);

        assertNotNull(port.getWarehouse());
        Warehouse oldWarehouse = port.getWarehouse();
        port.init(5, 500, 0);

        assertSame(oldWarehouse, port.getWarehouse(), "Port should not re-initialize");
    }
}