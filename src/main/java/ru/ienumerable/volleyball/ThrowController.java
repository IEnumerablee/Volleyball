package ru.ienumerable.volleyball;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.ienumerable.volleyball.ball.Ball;
import ru.ienumerable.volleyball.skin.SkullSkin;
import ru.ienumerable.volleyball.tools.math.Ray;
import ru.ienumerable.volleyball.tools.math.Vector;
import ru.ienumerable.volleyball.tools.update.Updatable;

public class ThrowController implements Updatable {

    private final SkullSkin skin;
    private final Player player;
    private boolean live = true;

    private int counter = 0;
    private double power = 0.0001;

    private final double halfRange = (Config.MAX_THROW_POWER - Config.MIN_THROW_POWER) / 2;
    private final double range = Config.MAX_THROW_POWER - Config.MIN_THROW_POWER;

    public ThrowController(SkullSkin skin, Player player) {
        this.skin = skin;
        this.player = player;
        Volleyball.getTcContainer().addController(this, player);
    }

    public static boolean throwBall(SkullSkin skin, Location loc){

        Ball ball = new Ball(loc, new Vector(), skin);

        Volleyball.getUpdater().put(ball);
        return true;
    }

    public static boolean throwBall(Player player, double power){
        return throwBall(player, player.getLocation(), power);
    }

    public static boolean throwBall(Player player, Location loc, double power){
        ItemStack item = player.getInventory().getItemInMainHand();

        SkullSkin skin = SkullSkin.getSkin(item);

        if(skin == null) return false;

        loc.setY(loc.getY() + 1);

        Ray pray = new Ray(loc, false);
        Ball ball = new Ball(loc, pray.getPoint(power), skin);

       Volleyball.getUpdater().put(ball);
        item.setAmount(item.getAmount() - 1);
        return true;
    }

    @Override
    public void update() {
        if (!live || !player.isSneaking() || SkullSkin.getSkin(player.getInventory().getItemInMainHand()) != skin) {
            live = false;
            Volleyball.getTcContainer().removeController(player);
            return;
        }

        counter++;
        power = Math.asin(Math.sin(counter * Config.THROW_CHECKING_SPEED - Math.PI/2)) * halfRange + halfRange;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(getBar(power)));
        power += Config.MIN_THROW_POWER;

    }

    @Override
    public boolean isLife() {
        return live;
    }

    public void throwBall(){
        throwBall(player, power);
        Volleyball.getTcContainer().removeController(player);
    }

    public void kill(){
        live = false;
    }

    private String getBar(double power){
        StringBuilder barBuilder = new StringBuilder();
        int cc = 4;

        double pointCost = cc / range;
        double fill = pointCost * power;

        for(int i = 0; i <= cc * 2; i++){
            if(i >= cc - fill && i <= cc + fill) barBuilder.append('█');
            else barBuilder.append(" ");
        }

        String bar = barBuilder.toString();

        ChatColor color;

        if(power >= range * 0.9) color = ChatColor.RED;
        else if(power >= range * 0.5) color = ChatColor.GOLD;
        else color = ChatColor.GREEN;

        return color + bar;

    }

    private double tri(double x){

        double pos = x % Math.PI;

        if(pos <= Math.PI / 2) return  - pos;
        else return pos - 0;
    }
}
