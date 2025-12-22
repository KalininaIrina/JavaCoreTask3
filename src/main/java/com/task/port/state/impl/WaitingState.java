package com.task.port.state.impl;

import com.task.port.state.ShipState;

public class WaitingState implements ShipState {
    @Override
    public String getStateName() {
        return "WAITING (Queue for dock)";
    }
}
