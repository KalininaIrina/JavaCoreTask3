package com.task.port.state.impl;

import com.task.port.state.ShipState;

public class ProcessingState implements ShipState {
    @Override
    public String getStateName() {
        return "PROCESSING (Loading/Unloading)";
    }
}
