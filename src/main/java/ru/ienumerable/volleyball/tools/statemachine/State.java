package ru.ienumerable.volleyball.tools.statemachine;

public interface State {

    boolean attemptToSwitch(StateSpace space);

    void update(StateSpace space);
}
