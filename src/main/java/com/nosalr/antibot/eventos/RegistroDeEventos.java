package com.nosalr.antibot.eventos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Guarda os eventos observados, separados por jogador.
 *
 * Por enquanto fica tudo na memoria do servidor. Cada jogador tem um limite
 * de eventos guardados pra memoria nao crescer sem parar; quando passa do
 * limite, os mais antigos saem.
 */
public class RegistroDeEventos {

    /** Quantos eventos no maximo ficam guardados por jogador. */
    private static final int LIMITE_POR_JOGADOR = 500;

    private final Map<UUID, List<Evento>> eventosPorJogador = new HashMap<>();

    /** Guarda um evento novo. */
    public void guardar(Evento evento) {
        List<Evento> lista = eventosPorJogador.get(evento.jogador);

        if (lista == null) {
            lista = new ArrayList<>();
            eventosPorJogador.put(evento.jogador, lista);
        }

        lista.add(evento);

        while (lista.size() > LIMITE_POR_JOGADOR) {
            lista.remove(0);
        }
    }

    /** Devolve uma copia dos eventos de um jogador, do mais antigo pro mais novo. */
    public List<Evento> eventosDo(UUID jogador) {
        List<Evento> lista = eventosPorJogador.get(jogador);

        if (lista == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(lista);
    }

    /** Quantos eventos estao guardados no total. */
    public int quantidadeGuardada() {
        int total = 0;

        for (List<Evento> lista : eventosPorJogador.values()) {
            total = total + lista.size();
        }

        return total;
    }
}
