package me.perch.manager;

import me.perch.Eggs;
import me.perch.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ConfigManager {

    private final Eggs plugin;
    private Material eggMaterial;
    private String eggNameRaw;
    private List<String> eggLoreTemplate;

    private String filledEggNameFormat;
    private String filledEggHeaderFormat;
    private String filledEggStatFormat;
    private String filledEggDefaultColor;

    public ConfigManager(Eggs plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.reloadConfig();
        this.eggMaterial = Material.valueOf(plugin.getConfig().getString("items.empty-egg.material", "EGG"));
        this.eggNameRaw = plugin.getConfig().getString("items.empty-egg.name", "<yellow>Mob Egg");
        this.eggLoreTemplate = plugin.getConfig().getStringList("items.empty-egg.lore");

        this.filledEggNameFormat = plugin.getConfig().getString("items.filled-egg.name-format", "<yellow>Mob Egg: <reset><name>");
        this.filledEggHeaderFormat = plugin.getConfig().getString("items.filled-egg.header-format", "<gray>Contains: <name>");
        this.filledEggStatFormat = plugin.getConfig().getString("items.filled-egg.stat-format", "<white>%label%: %value%");
        this.filledEggDefaultColor = plugin.getConfig().getString("items.filled-egg.default-stat-color", "<#FFFF3A>");
    }

    public String getEggNameRaw() { return eggNameRaw; }
    public Component getEggName() { return parse(eggNameRaw); }

    public String getFilledEggNameFormat() { return filledEggNameFormat; }
    public String getFilledEggHeaderFormat() { return filledEggHeaderFormat; }
    public String getFilledEggStatFormat() { return filledEggStatFormat; }
    public String getFilledEggDefaultColor() { return filledEggDefaultColor; }

    public Component parse(String text) {
        return ColorUtil.parse(text);
    }

    public Material getEggMaterial() { return eggMaterial; }
    public List<String> getEggLoreTemplate() { return eggLoreTemplate; }

    public Component getMessage(String path) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String msg = plugin.getConfig().getString(path, "Message not found");
        return parse(prefix + msg);
    }

    public List<String> getBlacklist() { return plugin.getConfig().getStringList("blacklist"); }

    public SoundData getSound(String type) {
        String path = "sounds." + type;

        if (plugin.getConfig().isConfigurationSection(path)) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
            String soundName = section.getString("sound");
            double volume = section.getDouble("volume", 1.0);
            double pitch = section.getDouble("pitch", 1.0);

            try {
                return new SoundData(Sound.valueOf(soundName), (float) volume, (float) pitch);
            } catch (Exception e) {
                return null;
            }
        }
        else {
            String name = plugin.getConfig().getString(path);
            if (name == null) return null;
            try {
                return new SoundData(Sound.valueOf(name), 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public Particle getParticle(String type) {
        String name = plugin.getConfig().getString("particles." + type);
        if (name == null) return null;
        try { return Particle.valueOf(name); } catch (IllegalArgumentException e) { return null; }
    }

    public record SoundData(Sound sound, float volume, float pitch) {}
}