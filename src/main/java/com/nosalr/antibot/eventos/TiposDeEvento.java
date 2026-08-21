package com.nosalr.antibot.eventos;

/**
 * Nomes dos tipos de evento que o plugin registra hoje.
 *
 * Sao textos de proposito: pra acrescentar um tipo novo basta adicionar uma
 * constante aqui e registrar o evento no OuvinteDeAcoes. Nada que le os
 * eventos precisa ser alterado por causa disso.
 */
public class TiposDeEvento {

    public static final String QUEBRA_BLOCO = "quebra_bloco";
    public static final String COLOCA_BLOCO = "coloca_bloco";
    public static final String INTERAGE_BLOCO = "interage_bloco";
    public static final String INTERAGE_ENTIDADE = "interage_entidade";
    public static final String ATAQUE = "ataque";
    public static final String CONSOME_ITEM = "consome_item";
    public static final String USA_ITEM = "usa_item";
    public static final String PESCARIA = "pescaria";
    public static final String INVENTARIO = "inventario";
    public static final String ENTRA_SERVIDOR = "entra_servidor";
    public static final String SAI_SERVIDOR = "sai_servidor";
    public static final String TROCA_MUNDO = "troca_mundo";
}
