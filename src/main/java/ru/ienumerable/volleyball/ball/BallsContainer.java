package ru.ienumerable.volleyball.ball;

import org.bukkit.Chunk;
import org.bukkit.World;
import ru.ienumerable.volleyball.Volleyball;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BallsContainer {

    private final Map<Chunk, List<Ball>> balls = new ConcurrentHashMap<>();

    public void addBall(Ball ball){
        Chunk chunk = ball.getChunk();
        if(!balls.containsKey(chunk)){
            balls.put(chunk, new ArrayList<>());
        }

        balls.get(chunk).add(ball);

    }

    public void moveBall(Ball ball, Chunk oldChunk){

        removeBall(ball, oldChunk);
        addBall(ball);
    }

    void removeBall(Ball ball){
        removeBall(ball, ball.getChunk());
    }

    public void dropAllBalls(){

        List<Ball> balls = Volleyball.getUpdater().getAll();

        if(balls == null) return;

        for(Ball ball : balls){
            ball.remove(true);
        }
    }

    public Ball[] getBalls(Chunk chunk){

        List<Ball> balls = new ArrayList<>();

        World world = chunk.getWorld();
        int px = chunk.getX();
        int pz = chunk.getZ();

        for(int x = px - 1; x <= px + 1; x++){
            for(int z = pz - 1; z <= pz + 1; z++){
                List<Ball> part = this.balls.get(world.getChunkAt(x, z));
                if(part == null) continue;
                balls.addAll(part);
            }
        }
        if(balls.size() == 0) return null;
        return balls.toArray(new Ball[balls.size()]);
    }

    private void removeBall(Ball ball, Chunk chunk){
        List<Ball> oldContainer = balls.get(chunk);
        if(oldContainer == null) return;
        oldContainer.remove(ball);
        if(oldContainer.size() == 0) balls.remove(chunk);
    }

}
