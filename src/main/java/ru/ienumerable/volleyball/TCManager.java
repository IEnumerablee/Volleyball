package ru.ienumerable.volleyball;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TCManager {

    private final Map<Player, ThrowController> controllers = new HashMap<>();

    public void addController(ThrowController controller, Player player){
        controllers.put(player, controller);
    }

    public ThrowController getController(Player player){
        return controllers.get(player);
    }

    public void removeController(Player player){
        ThrowController controller = controllers.get(player);
        if(controller == null) return;
        controller.kill();
        controllers.remove(player);
    }

}
