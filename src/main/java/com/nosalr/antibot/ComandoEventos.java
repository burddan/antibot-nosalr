package com.nosalr.antibot;

import com.nosalr.antibot.eventos.Evento;
import com.nosalr.antibot.eventos.RegistroDeEventos;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Comando /eventos. Mostra os ultimos eventos observados de um jogador.
 *
 * Como usar:
 *   /eventos                       os seus proprios eventos (dentro do jogo)
 *   /eventos <jogador>             os eventos daquele jogador
 *   /eventos <jogador> <quantos>   muda quantos eventos aparecem
 *
 * Funciona no chat e no console do servidor.
 */
public class ComandoEventos implements CommandExecutor {

    /** Quantos eventos aparecem quando nao pedem uma quantidade. */
    private static final int QUANTIDADE_PADRAO = 10;

    /** Teto pra nao inundar o chat. */
    private static final int QUANTIDADE_MAXIMA = 50;

    private final RegistroDeEventos registro;

    public ComandoEventos(RegistroDeEventos registro) {
        this.registro = registro;
    }

    @Override
    public boolean onCommand(CommandSender quemEnviou, Command comando, String nome, String[] argumentos) {
        UUID jogador;
        String nomeDoJogador;

        // de quem sao os eventos que vamos mostrar
        if (argumentos.length >= 1) {
            nomeDoJogador = argumentos[0];
            jogador = acharJogador(nomeDoJogador);
        } else if (quemEnviou instanceof Player) {
            Player quem = (Player) quemEnviou;
            nomeDoJogador = quem.getName();
            jogador = quem.getUniqueId();
        } else {
            // o console nao tem eventos proprios, precisa dizer o jogador
            quemEnviou.sendMessage("Use: /eventos <jogador> [quantos]");
            return true;
        }

        // quantos eventos mostrar
        int quantos = QUANTIDADE_PADRAO;

        if (argumentos.length >= 2) {
            try {
                quantos = Integer.parseInt(argumentos[1]);
            } catch (NumberFormatException erro) {
                quemEnviou.sendMessage("'" + argumentos[1] + "' nao e um numero.");
                return true;
            }
        }

        if (quantos < 1) {
            quantos = 1;
        }

        if (quantos > QUANTIDADE_MAXIMA) {
            quantos = QUANTIDADE_MAXIMA;
        }

        List<Evento> eventos = registro.eventosDo(jogador);

        if (eventos.isEmpty()) {
            quemEnviou.sendMessage("Nenhum evento guardado para " + nomeDoJogador + ".");
            return true;
        }

        // os mais novos estao no fim da lista, entao mostramos o final dela
        int comecoDaLista = eventos.size() - quantos;

        if (comecoDaLista < 0) {
            comecoDaLista = 0;
        }

        quemEnviou.sendMessage("--- eventos de " + nomeDoJogador + " (mostrando "
                + (eventos.size() - comecoDaLista) + " de " + eventos.size() + ") ---");

        for (int i = comecoDaLista; i < eventos.size(); i++) {
            quemEnviou.sendMessage(montarLinha(eventos.get(i)));
        }

        return true;
    }

    /**
     * Acha o UUID pelo nome. Se o jogador tiver saido do servidor os eventos
     * dele continuam guardados, entao tambem procuramos fora dos online.
     */
    private UUID acharJogador(String nome) {
        Player online = Bukkit.getPlayerExact(nome);

        if (online != null) {
            return online.getUniqueId();
        }

        return Bukkit.getOfflinePlayer(nome).getUniqueId();
    }

    /** Monta uma linha curta, pra caber no chat. */
    private String montarLinha(Evento evento) {
        SimpleDateFormat formatoDaHora = new SimpleDateFormat("HH:mm:ss");

        String linha = "[" + formatoDaHora.format(new Date(evento.momento)) + "] "
                + evento.tipo
                + String.format(" (%.0f, %.0f, %.0f)", evento.x, evento.y, evento.z);

        for (Map.Entry<String, String> informacao : evento.contexto.entrySet()) {
            linha = linha + " " + informacao.getKey() + "=" + informacao.getValue();
        }

        return linha;
    }
}
