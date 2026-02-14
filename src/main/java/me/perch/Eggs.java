package me.perch;

import me.perch.listeners.EggListener;
import me.perch.manager.ConfigManager;
import me.perch.manager.EggManager;
import me.perch.util.EggCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Eggs extends JavaPlugin {

    private ConfigManager configManager;
    private EggManager eggManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.eggManager = new EggManager(this);

        getServer().getPluginManager().registerEvents(new EggListener(this), this);
        getCommand("percheggs").setExecutor(new EggCommand(this));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EggManager getEggManager() {
        return eggManager;
    }
}