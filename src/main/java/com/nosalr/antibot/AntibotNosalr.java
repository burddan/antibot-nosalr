package com.nosalr.antibot;

import com.nosalr.antibot.eventos.OuvinteDeAcoes;
import com.nosalr.antibot.eventos.RegistroDeEventos;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principal do plugin.
 */
public class AntibotNosalr extends JavaPlugin {

    /** Onde os eventos observados ficam guardados. */
    private final RegistroDeEventos registroDeEventos = new RegistroDeEventos();

    @Override
    public void onEnable() {
        getCommand("amostra").setExecutor(new ComandoAmostra());
        getCommand("eventos").setExecutor(new ComandoEventos(registroDeEventos));

        getServer().getPluginManager().registerEvents(new OuvinteDeAcoes(registroDeEventos), this);

        getLogger().info("AntibotNosalr ligado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AntibotNosalr desligado, " + registroDeEventos.quantidadeGuardada()
                + " eventos estavam guardados.");
    }
}
