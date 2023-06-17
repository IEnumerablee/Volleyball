package ru.ienumerable.volleyball.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.ienumerable.volleyball.Volleyball;

import java.lang.reflect.Field;
import java.util.UUID;

public class SkullSkin {

    private final String url;
    private final String name;
    private String permission;
    private final String id;

    private final String blockMsg;

    public SkullSkin(String id, String url, String name, String blockMsg, String permission) {
        this.url = "http://textures.minecraft.net/texture/" + url;
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.blockMsg = blockMsg;
    }

    public SkullSkin(String id, String url, String name) {
        this.url = "http://textures.minecraft.net/texture/" + url;
        this.id = id;
        this.name = name;
        blockMsg = "";
    }

    public ItemStack getItem() {
        ItemStack skull = getSkullByUrl(url);
        ItemMeta meta = skull.getItemMeta();

        meta.setDisplayName(name);

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Volleyball.getBallKey(), PersistentDataType.STRING, id);
        container.set(Volleyball.getBallRandomKey(), PersistentDataType.LONG, (long)(System.currentTimeMillis() * Math.random()));

        skull.setItemMeta(meta);

        return skull;
    }

    public String getBlockMsg() {
        return blockMsg;
    }

    public boolean hasPermission(Player player) {
        if(permission == null) return true;
        return player.hasPermission(permission);
    }

    public static SkullSkin getSkin(ItemStack item){
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        String id = dataContainer.get(Volleyball.getBallKey(), PersistentDataType.STRING);
        if(id == null) return null;
        return Volleyball.getSkullsContainer().getSkull(id);
    }

    public static boolean isContainSkin(ItemStack item){
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        return dataContainer.has(Volleyball.getBallKey(), PersistentDataType.STRING);
    }

    private static ItemStack getSkullByUrl(String url){
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        assert profileField != null;
        profileField.setAccessible(true);

        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

}