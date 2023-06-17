package ru.ienumerable.volleyball.skin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.ClickInformation;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.type.ChestMenu;
import ru.ienumerable.volleyball.Volleyball;

import java.util.ArrayList;
import java.util.List;

public class SkinChanger {

    private final Player player;
    private final SkullSkin skin;

    private List<Menu> menuPages;

    public SkinChanger(Player player, SkullSkin skin) {
        this.player = player;
        this.skin = skin;
        createMenu();
    }


    public void openMenu(){
        menuPages.get(0).open(player);
    }

    private void createMenu(){

        ItemStack arrowForward = getNamedItem(Material.ARROW, ChatColor.GOLD + ">>");
        ItemStack arrowBackward = getNamedItem(Material.ARROW, ChatColor.GOLD + "<<");

        Menu.Builder<ChestMenu.Builder> menuTemplate = ChestMenu.builder(3).
                title(">> Выбор скина <<");

        Mask itemSlots = BinaryMask.builder(menuTemplate.getDimensions())
                .pattern("111111111")
                .pattern("111111111")
                .pattern("000000000")
                .build();

        PaginatedMenuBuilder menuBuilder = PaginatedMenuBuilder.builder(menuTemplate)
                .slots(itemSlots)
                .nextButton(arrowForward)
                .nextButtonEmpty(new ItemStack(Material.AIR))
                .nextButtonSlot(26)
                .previousButton(arrowBackward)
                .previousButtonEmpty(new ItemStack(Material.AIR))
                .previousButtonSlot(18);


        SkullsContainer container = Volleyball.getSkullsContainer();

        int slot = 1;

        for(String id : container.getALlSkinId()){
            SkullSkin skin = container.getSkull(id);
            ItemStack item = skin.getItem();

            SlotSettings.Builder slotBuilder = SlotSettings.builder();

            if(skin.equals(this.skin)){
                stripColors(item);
                addPrefix(item, ChatColor.WHITE + "> ");
                addSuffix(item, " <");
                addDescription(item, ChatColor.GRAY + "Скин уже выбран");
                slotBuilder.item(item);
            }else if(!skin.hasPermission(player)){
                stripColors(item);
                addPrefix(item, ChatColor.RED + "☒ ");
                addDescription(item, ChatColor.GRAY + skin.getBlockMsg());
                slotBuilder.item(item);
            }else{
                addDescription(item, ChatColor.GRAY + "Нажмите, чтобы выбрать этот скин");
                slotBuilder.clickHandler(SkinChanger::selectHandler);
                slotBuilder.item(item);
            }
            menuBuilder.addItem(slotBuilder.build());
            slot++;
        }

        menuPages = menuBuilder.build();

    }

    private static void selectHandler(Player player, ClickInformation info){

        SkullSkin newSkin = SkullSkin.getSkin(info.getClickedSlot().getItem(player));
        player.getInventory().setItemInMainHand(newSkin.getItem());
        info.getClickedMenu().close(player);
    }

    private ItemStack addDescription(ItemStack item, String description){

        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        for(String line : description.split("@n")){
            lore.add(line);
        }

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;

    }

    private void stripColors(ItemStack item){

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.stripColor(meta.getDisplayName()));

        item.setItemMeta(meta);

    }

    private void addSuffix(ItemStack item, String suffix){

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(meta.getDisplayName() + suffix);

        item.setItemMeta(meta);

    }

    private void addPrefix(ItemStack item, String prefix) {

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(prefix + meta.getDisplayName());

        item.setItemMeta(meta);
    }

    private ItemStack getNamedItem(Material material, String name){

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        item.setItemMeta(meta);

        return item;
    }
}
