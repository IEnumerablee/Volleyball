package ru.ienumerable.volleyball;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.ienumerable.volleyball.skin.SkinChanger;
import ru.ienumerable.volleyball.skin.SkullSkin;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        switch (command.getLabel()) {
            case "ballskin" -> {
                if (sender instanceof Player player) {

                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (!item.hasItemMeta() || !SkullSkin.isContainSkin(item)) {
                        player.sendMessage(ChatColor.RED + "Возьмите мяч в руки, чтобы сменить скин!");
                        return true;
                    }

                    SkinChanger skinChanger = new SkinChanger(player, SkullSkin.getSkin(item));
                    skinChanger.openMenu();
                }
            }
            case "ballsreload" -> {
                Volleyball.getBallsContainer().dropAllBalls();
                Volleyball.loadConfig();
                sendMsgToOps(ChatColor.GREEN + "Презагружено!");
            }
        }

        return true;
    }

    private void sendMsgToOps(String msg){

        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.isOp()) player.sendMessage(msg);
        }
    }
}
