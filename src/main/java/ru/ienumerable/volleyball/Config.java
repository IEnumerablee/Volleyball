package ru.ienumerable.volleyball;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public static String DEFAULT_SKULLSKIN = "ball";

    public static double RADIAN = 180 / Math.PI;

    public static int DESPAWN_TIME = 10000;

    public static int IPT = 20;

    public static double GRAVITY = -0.0001;

    public static double DEFAULT_BOUNCE_ENERGY_LOSS = 2;


    public static double MAX_SPEED = 0.1;
    public static double MAX_ROT_SPEED = 0.3;
    public static double ROT_SPEED_PROPORTION = 10;
    public static double ROT_BREAKING_SPEED = 1.1;

    public static double THROW_CHECKING_SPEED = 0.06 * Math.PI;
    public static double MAX_THROW_POWER = 0.05;
    public static double MIN_THROW_POWER = 0.005;

    public static final List<Material> TRANSPARENT_MATERIALS = new ArrayList<>();

    public static final Map<Material, Double> BOUNCE_ENERGY_LOSSES = new HashMap<>();


    public static void init(){

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(Volleyball.getInstance().getDataFolder(), "config.yml"));

        IPT = yamlConfiguration.getInt("IPT");

        MAX_SPEED = yamlConfiguration.getDouble("MAX_SPEED") / 20 / IPT;
        MAX_ROT_SPEED = yamlConfiguration.getDouble("MAX_ROT_SPEED") / 20 / IPT;
        ROT_SPEED_PROPORTION = yamlConfiguration.getDouble("ROT_SPEED_PROPORTION");
        ROT_BREAKING_SPEED = yamlConfiguration.getDouble("ROT_BREAKING_SPEED") / 20 / IPT;

        GRAVITY = yamlConfiguration.getDouble("GRAVITY") / 20 / IPT;

        MAX_THROW_POWER = yamlConfiguration.getDouble("MAX_THROW_POWER") / 20 / IPT;
        MIN_THROW_POWER = yamlConfiguration.getDouble("MIN_THROW_POWER") / 20 / IPT;
        THROW_CHECKING_SPEED = yamlConfiguration.getDouble("THROW_CHECKING_SPEED") * Math.PI;

        DEFAULT_BOUNCE_ENERGY_LOSS = yamlConfiguration.getDouble("DEFAULT_BOUNCE_ENERGY_LOSS");

        DEFAULT_SKULLSKIN = yamlConfiguration.getString("DEFAULT_SKULLSKIN");

        if(!Volleyball.getSkullsContainer().containsId(DEFAULT_SKULLSKIN)) throw new IllegalArgumentException("Default ball skin is not defined");

        DESPAWN_TIME = yamlConfiguration.getInt("DESPAWN_TIME");


        for(Object block : yamlConfiguration.getList("TRANSPARENT_BLOCKS")){
            TRANSPARENT_MATERIALS.add(Material.getMaterial((String) block));
        }


        MemorySection elBlockSection = (MemorySection) yamlConfiguration.get("BLOCKS_ENERGY_LOSSES");

        for(String block: elBlockSection.getKeys(false)) {
            BOUNCE_ENERGY_LOSSES.put(Material.getMaterial(block), elBlockSection.getDouble(block));
        }
    }

    public static void inittmp(){
        TRANSPARENT_MATERIALS.add(Material.AIR);
        TRANSPARENT_MATERIALS.add(Material.CAVE_AIR);
        TRANSPARENT_MATERIALS.add(Material.VOID_AIR);
        TRANSPARENT_MATERIALS.add(Material.GRASS);

        BOUNCE_ENERGY_LOSSES.put(Material.WATER, 5D);
        BOUNCE_ENERGY_LOSSES.put(Material.SLIME_BLOCK, 1D);
        BOUNCE_ENERGY_LOSSES.put(Material.MOSS_BLOCK, 3D);
        BOUNCE_ENERGY_LOSSES.put(Material.HAY_BLOCK, 5D);
        BOUNCE_ENERGY_LOSSES.put(Material.HONEY_BLOCK, 100D);
        BOUNCE_ENERGY_LOSSES.put(Material.ICE, 0.8D);
        BOUNCE_ENERGY_LOSSES.put(Material.PACKED_ICE, 0.6D);
        BOUNCE_ENERGY_LOSSES.put(Material.BLUE_ICE, 0.4D);

    }

}
