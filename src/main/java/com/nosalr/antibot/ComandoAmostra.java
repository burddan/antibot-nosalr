package com.nosalr.antibot;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Comando /amostra.
 *
 * Tira uma amostra do estado observavel de quem digitou o comando
 * e mostra os campos da amostra no chat.
 */
public class ComandoAmostra implements CommandExecutor {

    /** Ultima amostra de cada jogador, pra calcular deslocamento e velocidade. */
    private final Map<UUID, Amostra> ultimaAmostra = new HashMap<>();

    /** Sessao de observacao de cada jogador. */
    private final Map<UUID, UUID> sessaoDoJogador = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender quemEnviou, Command comando, String nome, String[] argumentos) {
        if (!(quemEnviou instanceof Player)) {
            quemEnviou.sendMessage("Esse comando so funciona dentro do jogo.");
            return true;
        }

        Player jogador = (Player) quemEnviou;

        Amostra amostra = coletarAmostra(jogador);
        mostrarAmostra(jogador, amostra);

        // guarda pra proxima vez que o jogador usar o comando
        ultimaAmostra.put(jogador.getUniqueId(), amostra);

        return true;
    }

    /** Monta a amostra com os dados do jogador nesse instante. */
    private Amostra coletarAmostra(Player jogador) {
        Location local = jogador.getLocation();

        Amostra amostra = new Amostra();
        amostra.jogador = jogador.getUniqueId();
        amostra.momento = System.currentTimeMillis();
        amostra.mundo = local.getWorld().getName();
        amostra.x = local.getX();
        amostra.y = local.getY();
        amostra.z = local.getZ();
        amostra.yaw = local.getYaw();
        amostra.pitch = local.getPitch();
        amostra.estado = descreverEstado(jogador);
        amostra.sessao = pegarSessao(jogador);

        Amostra anterior = ultimaAmostra.get(jogador.getUniqueId());

        // na primeira amostra nao existe anterior, entao deslocamento e velocidade
        // ficam em zero. Se o jogador trocou de mundo tambem nao da pra comparar.
        if (anterior != null && anterior.mundo.equals(amostra.mundo)) {
            double difX = amostra.x - anterior.x;
            double difY = amostra.y - anterior.y;
            double difZ = amostra.z - anterior.z;

            amostra.deslocamento = Math.sqrt(difX * difX + difY * difY + difZ * difZ);

            double segundos = (amostra.momento - anterior.momento) / 1000.0;
            if (segundos > 0) {
                amostra.velocidadeHorizontal = Math.sqrt(difX * difX + difZ * difZ) / segundos;
                amostra.velocidadeVertical = difY / segundos;
            }
        }

        return amostra;
    }

    /** Junta num texto so os estados que o servidor consegue ver. */
    private String descreverEstado(Player jogador) {
        List<String> estados = new ArrayList<>();

        if (jogador.isOnGround()) {
            estados.add("no_chao");
        }
        if (jogador.isSneaking()) {
            estados.add("agachado");
        }
        if (jogador.isSprinting()) {
            estados.add("correndo");
        }
        if (jogador.isFlying()) {
            estados.add("voando");
        }
        if (jogador.isInWater()) {
            estados.add("na_agua");
        }

        if (estados.isEmpty()) {
            return "nenhum";
        }

        return String.join(",", estados);
    }

    /** Pega a sessao do jogador, criando uma nova se ele ainda nao tiver. */
    private UUID pegarSessao(Player jogador) {
        UUID sessao = sessaoDoJogador.get(jogador.getUniqueId());

        if (sessao == null) {
            sessao = UUID.randomUUID();
            sessaoDoJogador.put(jogador.getUniqueId(), sessao);
        }

        return sessao;
    }

    /** Escreve a amostra no chat do jogador. */
    private void mostrarAmostra(Player jogador, Amostra amostra) {
        SimpleDateFormat formatoDaData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        jogador.sendMessage("--- amostra ---");
        jogador.sendMessage("jogador: " + amostra.jogador);
        jogador.sendMessage("momento: " + formatoDaData.format(new Date(amostra.momento)));
        jogador.sendMessage("mundo: " + amostra.mundo);
        jogador.sendMessage(String.format("posicao: x=%.2f y=%.2f z=%.2f", amostra.x, amostra.y, amostra.z));
        jogador.sendMessage(String.format("direcao: yaw=%.1f pitch=%.1f", amostra.yaw, amostra.pitch));
        jogador.sendMessage(String.format("deslocamento: %.2f blocos", amostra.deslocamento));
        jogador.sendMessage(String.format("velocidade horizontal: %.2f blocos/s", amostra.velocidadeHorizontal));
        jogador.sendMessage(String.format("velocidade vertical: %.2f blocos/s", amostra.velocidadeVertical));
        jogador.sendMessage("estado: " + amostra.estado);
        jogador.sendMessage("sessao: " + amostra.sessao);
    }
}
