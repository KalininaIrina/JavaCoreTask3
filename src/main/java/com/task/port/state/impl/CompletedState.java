package com.task.port.state.impl;
import com.task.port.state.ShipState;

public class CompletedState implements ShipState {
    @Override
    public String getStateName() {
        return "COMPLETED (Departed)";
    }
}