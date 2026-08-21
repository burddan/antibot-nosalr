package com.nosalr.antibot.eventos;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Representacao normalizada de uma acao que o servidor conseguiu observar.
 *
 * Todo evento, de qualquer tipo, tem os mesmos campos basicos: tipo,
 * momento, jogador e localizacao. O que muda de um tipo pro outro vai
 * dentro do contexto.
 *
 * Isso e de proposito. Quem for analisar os eventos depois le o tipo e o
 * contexto, e nao precisa conhecer cada tipo de evento que existe. Pra
 * passar a registrar um tipo novo basta criar o Evento com um tipo novo,
 * sem mexer em quem analisa.
 */
public class Evento {

    /** Tipo da acao. Ver TiposDeEvento. */
    public String tipo;

    /** Quando a acao aconteceu, em milissegundos. */
    public long momento;

    /** Quem fez a acao. */
    public UUID jogador;

    /** Onde a acao aconteceu. */
    public String mundo;
    public double x;
    public double y;
    public double z;

    /**
     * Informacoes que so fazem sentido pra esse tipo de evento.
     * Ex: num evento de quebra de bloco, qual bloco foi quebrado.
     */
    public final Map<String, String> contexto = new LinkedHashMap<>();

    /** Guarda uma informacao extra. Ex: anotar("bloco", "STONE"). */
    public void anotar(String chave, String valor) {
        if (valor != null) {
            contexto.put(chave, valor);
        }
    }

    @Override
    public String toString() {
        return tipo + " jogador=" + jogador + " mundo=" + mundo
                + String.format(" x=%.1f y=%.1f z=%.1f", x, y, z)
                + " contexto=" + contexto;
    }
}
