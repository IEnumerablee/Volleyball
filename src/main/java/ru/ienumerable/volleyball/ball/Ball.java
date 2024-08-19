package ru.ienumerable.volleyball.ball;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import ru.ienumerable.volleyball.Config;
import ru.ienumerable.volleyball.Volleyball;
import ru.ienumerable.volleyball.skin.SkullSkin;
import ru.ienumerable.volleyball.tools.math.Angle;
import ru.ienumerable.volleyball.tools.math.Vector;
import ru.ienumerable.volleyball.tools.update.Updatable;

public class Ball implements Updatable {

    private boolean isLife = true;
    private int timer = 0;
    private boolean isTimerStarted;

    private final Vector velocity;
    private EulerAngle rotation;
    private final Angle rotationSpeed;
    private final Location position;
    private final Angle angle;

    private final Location standPos;
    ArmorStand stand;
    SkullSkin skin;

    private final LivingEntity target;

    private Chunk chunk;

    public Ball(Location position, Vector velocity, SkullSkin skin) {
        this.velocity = velocity;
        this.position = position;
        this.skin = skin;
        standPos = position.clone();
        angle = new Angle(position);
        rotationSpeed = new Angle();
        rotFloorAccelerate();
        rotation = new EulerAngle(0, 0, 0);
        stand = spawnStand();
        chunk = position.getChunk();
        target = spawnTarget();
        Volleyball.getBallsContainer().addBall(this);
    }

    @Override
    public boolean isLife() {
        return isLife;
    }

    @Override
    public void update() {

        if (!this.isLife) {
            return;
        }
        for (int i = 0; i < Config.IPT; ++i) {
            this.processGravity();
            this.angle.vectorToAngle(this.velocity);
            this.stabilize();
            this.applyVelocity();
        }
        this.rotation = this.rotation.add(this.rotationSpeed.pitch, this.rotationSpeed.yaw, 0.0);
        this.processTail();
        this.despawnProcess();
        this.chunkCheck();

    }

    public SkullSkin getSkin() {
        return skin;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public double getDistance(Location loc){
        if(loc.getWorld() != position.getWorld()) return 999999999;

        return position.distance(loc);

    }

    public void remove(boolean isDrop){

        this.isLife = false;
        Volleyball.getBallsContainer().removeBall(this);
        this.stand.remove();
        this.target.remove();

        if(isDrop) position.getWorld().dropItem(position, skin.getItem());

    }

    public void punch(Vector power){

        velocity.mul(0);
        velocity.add(power);

        rotWallAccelerate();

        position.getWorld().spawnParticle(Particle.SWEEP_ATTACK, position, 1, 0.1, 0.1, 0.1, 1);
        playSound();

    }

    public boolean isPigOwning(LivingEntity foundTarget){
        return foundTarget.equals(target);
    }

    private void stabilize(){
        if(velocity.length() > Config.MAX_SPEED){
            velocity.normalize(Config.MAX_SPEED);
        }
        if(rotationSpeed.length() > Config.MAX_ROT_SPEED) rotationSpeed.normalize(Config.MAX_ROT_SPEED);
    }

    private void applyVelocity() {

        Vector newPos = new Vector(
                position.getX() + velocity.x,
                position.getY() + velocity.y,
                position.getZ() + velocity.z
        );

        int xfbd = (int) newPos.x;
        int yfbd = (int) newPos.y;
        int zfbd = (int) newPos.z;

        if(position.getX() < 0) xfbd -= 1;
        if(position.getY() < 0) yfbd -= 1;
        if(position.getZ() < 0) zfbd -= 1;

        Material block = position.getWorld().getBlockAt(xfbd, yfbd, zfbd).getType();

        if (!Config.TRANSPARENT_MATERIALS.contains(block)){
            bounce(newPos, block);
        }else {
            position.setX(newPos.x);
            position.setY(newPos.y);
            position.setZ(newPos.z);
        }

        standPos.setX(newPos.x);
        standPos.setY(newPos.y - 0.49 + (Math.sin((rotation.getX() * Config.RADIAN - 90) * (Math.PI / 180)) * 0.2 + 0.2));
        standPos.setZ(newPos.z);

    }

    private void processGravity(){
        velocity.y += Config.GRAVITY;
    }

    private void processTail(){
        stand.teleport(standPos);
        stand.setHeadPose(rotation);
        target.teleport(new Location(position.getWorld(), position.getX(), position.getY() - 1, position.getZ()));
    }

    private void despawnProcess(){

        double speed = velocity.length();

        if(speed <= Config.MAX_SPEED * 0.01){

            if(!isTimerStarted){
                isTimerStarted = true;
            }else{
                timer++;
                if(timer >= Config.DESPAWN_TIME){
                    remove(true);
                }
            }
        }else{
            isTimerStarted = false;
            timer = 0;
        }
    }

    private void bounce(Vector collidePos, Material block){

        Double energyLoss = Config.BOUNCE_ENERGY_LOSSES.get(block);
        if(energyLoss == null) energyLoss = Config.DEFAULT_BOUNCE_ENERGY_LOSS;

        velocity.div(energyLoss);

        boolean isRot = true;
        if(velocity.length() <= Config.MAX_SPEED * 0.1){
            decreaseRotSpeed();
            isRot = false;
        }else{
            playSound();
        }

        Vector normal = collidePos.cloneV();
        Vector pos = new Vector(position);

        normal.floor();

        normal.x += 0.5;
        normal.y += 0.5;
        normal.z += 0.5;

        normal.sub(pos);
        normal.abs();

        if(normal.x > normal.z){
            if (normal.y > normal.x){
                if(isRot) rotFloorAccelerate();
                velocity.bounce(2);
            }else{
                if(isRot) rotWallAccelerate();
                velocity.bounce(1);
            }
        }else{
            if (normal.y > normal.z){
                if(isRot) rotFloorAccelerate();
                velocity.bounce(2);
            }else{
                if(isRot) rotWallAccelerate();
                velocity.bounce(3);
            }
        }

    }

    private void decreaseRotSpeed(){
        rotationSpeed.yaw /= Config.ROT_BREAKING_SPEED;
        rotationSpeed.pitch /= Config.ROT_BREAKING_SPEED;
    }

    private void rotWallAccelerate(){
        rotationSpeed.pitch += velocity.y * Config.ROT_SPEED_PROPORTION;
        rotationSpeed.yaw += velocity.z * Config.ROT_SPEED_PROPORTION;
    }

    private void rotFloorAccelerate(){
        rotationSpeed.pitch += (Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z)) * Config.ROT_SPEED_PROPORTION;
    }

    private void playSound(){
        position.getWorld().playSound(position, Sound.BLOCK_BAMBOO_BREAK, 10, 1);
    }

    private void chunkCheck(){
        Chunk nowChunk = position.getChunk();
        if(!nowChunk.equals(chunk)){
            Chunk old = chunk;
            chunk = nowChunk;
            Volleyball.getBallsContainer().moveBall(this, old);
        }
    }

    private ArmorStand spawnStand(){

        ArmorStand stand = (ArmorStand) position.getWorld().spawnEntity(position, EntityType.ARMOR_STAND);

        stand.getEquipment().setHelmet(skin.getItem());

        stand.setGravity(false);
        stand.setBasePlate(false);
        stand.setVisible(false);
        stand.setInvulnerable(true);
        stand.setCanPickupItems(false);
        stand.setCollidable(false);

        stand.setSmall(true);

        return stand;
    }

    private LivingEntity spawnTarget() {
        ArmorStand stand = (ArmorStand) position.getWorld().spawnEntity(position, EntityType.ARMOR_STAND);

        stand.getEquipment().setHelmet(new ItemStack(Material.AIR));

        stand.setGravity(false);
        stand.setBasePlate(false);
        stand.setVisible(false);
        stand.setCanPickupItems(false);
        stand.setCollidable(false);

        return stand;
    }

}