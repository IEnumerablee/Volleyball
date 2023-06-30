package ru.ienumerable.volleyball.tools.statemachine;

import java.util.HashMap;
import java.util.Map;

public class StateSpace {

    private final Map<String, StateAxis> axisSet = new HashMap<>();

    public StateAxis getAxis(String id){
        return axisSet.get(id);
    }

    public void addAxis(StateAxis axis, String id){
        axisSet.put(id, axis);
    }

    public void update(){
        for(String id : axisSet.keySet()) axisSet.get(id).update();
    }

    public static final class StateAxis{

        private final StateSpace space;

        private String nowId;
        private State now;
        private final Map<String, State> statesSet = new HashMap<>();

        public StateAxis(StateSpace space, String firstState) {
            this.space = space;
            nowId = firstState;
        }

        private void update(){
            if(now == null) now = statesSet.get(nowId);
            now.update(space);
        }

        public void addStateVariant(State state, String id){
            statesSet.put(id, state);
        }

        public String getNowStateId(){
            return nowId;
        }

        public State getNowState() {
            return now;
        }

        public boolean stateSwitchAttempt(String id){
            boolean isAccept = statesSet.get(id).attemptToSwitch(space);

            if(isAccept){
                nowId = id;
                now = statesSet.get(id);
            }

            return isAccept;
        }

    }

}
