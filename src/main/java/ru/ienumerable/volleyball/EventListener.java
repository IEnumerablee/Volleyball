package ru.ienumerable.volleyball;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import ru.ienumerable.volleyball.ball.Ball;
import ru.ienumerable.volleyball.skin.SkullSkin;
import ru.ienumerable.volleyball.tools.math.Ray;
import ru.ienumerable.volleyball.tools.math.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListener implements Listener {

    private Map<Player, Long> timer = new HashMap<>();

    @EventHandler
    public void standInteract(PlayerArmorStandManipulateEvent event){

        Ball ball = getBall(event.getRightClicked());
        if(ball == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().getType() != Material.AIR) return;

        player.getInventory().setItemInMainHand(ball.getSkin().getItem());
        ball.remove(false);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){

        if(!(event.getEntity() instanceof ArmorStand)) return;

        Ball ball = getBall((ArmorStand) event.getEntity());

        if(ball == null) return;
        event.setCancelled(true);

        if(!(event instanceof EntityDamageByEntityEvent)) return;
        if(!(((EntityDamageByEntityEvent) event).getDamager() instanceof Player)) return;

        Player player = (Player) ((EntityDamageByEntityEvent) event).getDamager();
        Location loc = ((EntityDamageByEntityEvent) event).getDamager().getLocation();
        ball.punch(new Ray(loc, false).getPoint(getPunchPower((player))));

    }

    @EventHandler
    public void craftBall(PlayerInteractEvent event){

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();
        Action action = event.getAction();

        if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        if(item.getType() != Material.LEATHER && item.getAmount() < 8) return;

        for(Entity entity : detectEntity(player.getLocation())){

            if(entity.getType() == EntityType.PUFFERFISH){


                Location loc = entity.getLocation();

                ThrowController.throwBall(Volleyball.getSkullsContainer().getSkull(Config.DEFAULT_SKULLSKIN), loc);

                player.getWorld().playSound(loc, Sound.ENTITY_PUFFER_FISH_BLOW_UP, 10, 1);

                player.getWorld().spawnParticle(Particle.CLOUD, loc, 10, 0.5, 0.5, 0.5, 0.2);

                entity.remove();
                item.setAmount(item.getAmount() - 8);
                return;
            }


        }

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){

        Player player = event.getPlayer();

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if(SkullSkin.getSkin(player.getInventory().getItemInMainHand()) != null){

                if(!player.isSneaking()) {
                    Long nowTime = System.currentTimeMillis();
                    Long oldTime = timer.get(player);
                    if (oldTime != null && nowTime - oldTime < 5) return;
                    timer.put(player, nowTime);

                    Location loc = event.getClickedBlock().getLocation();

                    if (loc.getX() <= 0) loc.setX(loc.getX() + 0.5);
                    else loc.setX(loc.getX() + 0.5);

                    if (loc.getZ() <= 0) loc.setZ(loc.getZ() + 0.5);
                    else loc.setZ(loc.getZ() + 0.5);

                    ThrowController.throwBall(player, loc, 0);
                }

                event.setCancelled(true);
            }

        }else if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {

            ThrowController controller = Volleyball.getTcContainer().getController(player);
            if(controller != null){
                controller.throwBall();
                return;
            }

            boolean isCanceled = ThrowController.throwBall(player, getPunchPower(player));

            event.setCancelled(isCanceled);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event){

        if(!event.isSneaking()) return;

        Player player = event.getPlayer();

        if(Volleyball.getTcContainer().getController(player) != null);

        ItemStack item = player.getInventory().getItemInMainHand();

        SkullSkin skin = SkullSkin.getSkin(item);

        if(skin == null){
            catchBall(player);
            return;
        }

        ThrowController controller = new ThrowController(skin, player);
        Volleyball.getUpdater().put(controller);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        timer.remove(player);
        Volleyball.getTcContainer().removeController(player);
    }

    private Ball getBall(LivingEntity pig){
        Ball[] balls = Volleyball.getBallsContainer().getBalls(pig.getLocation().getChunk());
        if(balls == null) return null;

        for(Ball ball : balls){
            if(ball.isPigOwning(pig)){
                return ball;
            }
        }
        return null;
    }

    private void catchBall(Player player){

        if(player.getInventory().getItemInMainHand().getType() != Material.AIR) return;

        Location loc = player.getLocation();

        Ball[] balls = Volleyball.getBallsContainer().getBalls(loc.getChunk());
        if (balls == null) return;

        for (Ball ball : balls) {
            if (ball.getDistance(loc) <= 2.5) {
                ball.remove(false);
                player.getInventory().setItemInMainHand(ball.getSkin().getItem());
                return;
            }
        }
    }

    private List<Entity> detectEntity(Location loc){

        Location dloc = loc.clone();

        dloc.setY(dloc.getY() + 1.5);

        Ray ray = new Ray(dloc, true);

        List<Entity> entities = new ArrayList<>();

        for(Vector detectPos : ray.cast(5, 0.2)) {
            Location detectLoc = new Location(loc.getWorld(), detectPos.x, detectPos.y, detectPos.z);

            entities.addAll(detectLoc.getWorld().getNearbyEntities(detectLoc, 0.25, 0.25, 0.25, (entity) -> !entities.contains(entity)));


        }
        return entities;
    }

    private double getPunchPower(Player player){

        Location loc = player.getLocation();

        double power = (Config.MAX_THROW_POWER - Config.MIN_THROW_POWER) / 2 + Config.MIN_THROW_POWER;
        double fragment = (Config.MAX_THROW_POWER - Config.MIN_THROW_POWER) / 4 + Config.MIN_THROW_POWER;

        if(loc.getWorld().getBlockAt((int) loc.getX(), (int) (loc.getBlockY() - 0.5), (int) loc.getZ()).getType() == Material.AIR) power += fragment;
        if(player.isSprinting()) power += fragment;

        return power;

    }

}
