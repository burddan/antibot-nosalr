package com.nosalr.antibot;

import java.util.UUID;

/**
 * Uma amostra e uma "foto" do estado observavel de um jogador em um instante.
 *
 * Importante: a amostra so guarda o que da pra ver acontecendo no jogo.
 * Ela nao diz se o jogador usa bot ou nao, isso e analise de outra parte
 * do sistema que ainda nao existe.
 */
public class Amostra {

    /** Quem foi observado. */
    public UUID jogador;

    /** Quando a amostra foi tirada, em milissegundos. */
    public long momento;

    /** Nome do mundo onde o jogador estava. */
    public String mundo;

    public double x;
    public double y;
    public double z;

    /** Para onde o jogador estava olhando. */
    public float yaw;
    public float pitch;

    /** Quantos blocos o jogador andou desde a amostra anterior. */
    public double deslocamento;

    /** Velocidade estimada em blocos por segundo. */
    public double velocidadeHorizontal;
    public double velocidadeVertical;

    /** Estado observavel, ex: "no_chao,correndo". */
    public String estado;

    /** Identificador da sessao de observacao do jogador. */
    public UUID sessao;
}
