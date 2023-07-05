package ru.ienumerable.volleyball;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;
import ru.ienumerable.volleyball.ball.BallsContainer;
import ru.ienumerable.volleyball.skin.SkinsRegistry;
import ru.ienumerable.volleyball.tools.update.Updater;

public final class Volleyball extends JavaPlugin {

    private static Volleyball instance;

    private static NamespacedKey ballKey;
    private static NamespacedKey ballRandomKey;

    private static final SkinsRegistry SKIN_REGISTRY = new SkinsRegistry();

    private static final TCManager TC_MANAGER = new TCManager();

    private static final BallsContainer ballsContainer = new BallsContainer();

    private static final Updater updater = new Updater();

    @Override
    public void onEnable() {


        instance = this;
        ballKey = new NamespacedKey(instance, "BBNB_Ball");
        ballRandomKey = new NamespacedKey(instance, "BBNB_Ball_random");

        saveResource("skins.yml", false);
        saveResource("config.yml", false);

        loadConfig();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);

        getCommand("ballskin").setExecutor(new CommandListener());
        getCommand("ballsreload").setExecutor(new CommandListener());

        updater.startScheduler();

    }

    @Override
    public void onDisable() {

        ballsContainer.dropAllBalls();

    }

    public static void loadConfig(){

        SKIN_REGISTRY.parseSkulls();
        Config.init();

    }

    public static Volleyball getInstance() {
        return instance;
    }

    public static NamespacedKey getBallKey() {
        return ballKey;
    }
    public static NamespacedKey getBallRandomKey() {
        return ballRandomKey;
    }

    public static TCManager getTcContainer() {
        return TC_MANAGER;
    }

    public static BallsContainer getBallsContainer(){
        return ballsContainer;
    }

    public static SkinsRegistry getSkullsContainer(){
        return SKIN_REGISTRY;
    }

    public static Updater getUpdater() {
        return updater;
    }

}
