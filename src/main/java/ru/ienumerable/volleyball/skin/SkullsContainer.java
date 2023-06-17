package ru.ienumerable.volleyball.skin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.ienumerable.volleyball.Config;
import ru.ienumerable.volleyball.Volleyball;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SkullsContainer {

    private final Map<String, SkullSkin> nameUrlPair = new HashMap<>();

    public void parseSkulls(){

        nameUrlPair.clear();

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(Volleyball.getInstance().getDataFolder(), "skins.yml"));
        Set<String> idSet = yamlConfiguration.getKeys(false);
        for(String id: idSet) {
            MemorySection skinSection = (MemorySection) yamlConfiguration.get(id);

            String name = skinSection.getString("name");
            String url = skinSection.getString("url");
            String perm = skinSection.getString("perm");
            String bm = skinSection.getString("lock_msg");

            nameUrlPair.put(id, new SkullSkin(id, url, name, bm, perm));
        }

    }

    public Set<String> getALlSkinId(){
        return nameUrlPair.keySet();
    }

    public boolean containsId(String id){
        return nameUrlPair.containsKey(id);
    }

    public SkullSkin getSkull(String id){
        SkullSkin skull = nameUrlPair.get(id);
        if(skull == null){
            Bukkit.getLogger().warning("Attempt to get unannounced ball. Returning a default ball...");
            skull = getSkull(Config.DEFAULT_SKULLSKIN);
        }
        return skull;
    }
}
