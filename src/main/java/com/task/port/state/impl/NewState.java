package com.task.port.state.impl;

import com.task.port.state.ShipState;

public class NewState implements ShipState {
    @Override
    public String getStateName() {
        return "NEW (Arriving to harbor)";
    }
}
