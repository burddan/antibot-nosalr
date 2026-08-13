package com.nosalr.antibot;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principal do plugin.
 *
 * Por enquanto o plugin so tem o comando /amostra.
 */
public class AntibotNosalr extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("amostra").setExecutor(new ComandoAmostra());
        getLogger().info("AntibotNosalr ligado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AntibotNosalr desligado.");
    }
}
