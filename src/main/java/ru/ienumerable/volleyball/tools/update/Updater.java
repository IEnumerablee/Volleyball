package ru.ienumerable.volleyball.tools.update;

import ru.ienumerable.volleyball.Volleyball;

import java.util.ArrayList;
import java.util.List;

public class Updater {

    private final List<Updatable> objects;

    private final UpdateProcessor updateProcessor;

    private boolean isSchedulerInitialized = false;

    public Updater(){
        objects = new ArrayList<>();
        updateProcessor = new UpdateProcessor(objects);
    }

    public void remove(Updatable updatable){
        objects.remove(updatable);
    }

    public void put(Updatable updatable){
        objects.add(updatable);
    }

    public <T extends Updatable> List<T> getAll(){

        List<T> result = new ArrayList<>();

        for(Updatable object : objects){

            try {

                T t = (T) object;
                result.add(t);

            }catch (ClassCastException ignored){}

        }
        return result;
    }

    public void startScheduler(){

        if(isSchedulerInitialized) throw new IllegalStateException("Scheduler is already running");

        Volleyball instance = Volleyball.getInstance();
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, this::update, 0, 1);

        isSchedulerInitialized = true;
    }

    private void update(){
        updateProcessor.update();
        updateProcessor.clear();


    }

    private final static class UpdateProcessor{

        private final List<Updatable> objects;
        private final List<Updatable> toRemove = new ArrayList<>();

        public UpdateProcessor(List<Updatable> objects) {
            this.objects = objects;
        }

        public void update(){
            for(Updatable updatable : objects){
                updatable.update();
                if(!updatable.isLife()) toRemove.add(updatable);
            }
        }

        public void clear(){
            toRemove.forEach(objects::remove);
            toRemove.clear();
        }

    }

}
