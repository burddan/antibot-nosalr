package com.nosalr.antibot.eventos;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Escuta as acoes dos jogadores e guarda cada uma como um Evento.
 *
 * Tudo aqui usa prioridade MONITOR: o plugin so observa, nunca cancela nem
 * altera nada do que o jogador fez. Onde o evento pode ser cancelado por
 * outro plugin, usamos ignoreCancelled pra registrar so o que realmente
 * aconteceu.
 *
 * O que da pra observar aqui e o que a API do servidor entrega. O plugin
 * NAO ve clique de mouse, tecla apertada nem pacote de rede cru, porque a
 * API do Paper nao expoe isso. Tambem nao guardamos mensagem de chat nem
 * dado pessoal.
 *
 * Pra registrar um tipo de acao novo: adiciona a constante no TiposDeEvento
 * e escreve mais um metodo aqui. Nada mais precisa mudar.
 */
public class OuvinteDeAcoes implements Listener {

    private final RegistroDeEventos registro;

    public OuvinteDeAcoes(RegistroDeEventos registro) {
        this.registro = registro;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void aoQuebrarBloco(BlockBreakEvent evento) {
        Block bloco = evento.getBlock();

        Evento registrado = criar(TiposDeEvento.QUEBRA_BLOCO, evento.getPlayer(), bloco.getLocation());
        registrado.anotar("bloco", bloco.getType().name());

        registro.guardar(registrado);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void aoColocarBloco(BlockPlaceEvent evento) {
        Block bloco = evento.getBlockPlaced();

        Evento registrado = criar(TiposDeEvento.COLOCA_BLOCO, evento.getPlayer(), bloco.getLocation());
        registrado.anotar("bloco", bloco.getType().name());

        registro.guardar(registrado);
    }

    /**
     * Esse evento serve pra duas coisas: interacao com bloco e uso de item
     * no ar. Separamos olhando a acao que o jogador fez.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void aoInteragir(PlayerInteractEvent evento) {
        Player jogador = evento.getPlayer();
        Action acao = evento.getAction();

        if (acao == Action.LEFT_CLICK_BLOCK || acao == Action.RIGHT_CLICK_BLOCK) {
            Block bloco = evento.getClickedBlock();

            if (bloco == null) {
                return;
            }

            Evento registrado = criar(TiposDeEvento.INTERAGE_BLOCO, jogador, bloco.getLocation());
            registrado.anotar("bloco", bloco.getType().name());
            registrado.anotar("acao", acao.name());
            registrado.anotar("mao", nomeDaMao(evento.getHand()));

            // se tinha item na mao, guarda qual era: usar item olhando pra um
            // bloco cai aqui, e nao em usa_item
            if (evento.getItem() != null) {
                registrado.anotar("item", evento.getItem().getType().name());
            }

            registro.guardar(registrado);
            return;
        }

        if (acao == Action.RIGHT_CLICK_AIR && evento.getItem() != null) {
            Evento registrado = criar(TiposDeEvento.USA_ITEM, jogador, jogador.getLocation());
            registrado.anotar("item", evento.getItem().getType().name());
            registrado.anotar("mao", nomeDaMao(evento.getHand()));

            registro.guardar(registrado);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void aoInteragirComEntidade(PlayerInteractEntityEvent evento) {
        Evento registrado = criar(TiposDeEvento.INTERAGE_ENTIDADE, evento.getPlayer(),
                evento.getRightClicked().getLocation());
        registrado.anotar("entidade", evento.getRightClicked().getType().name());
        registrado.anotar("mao", nomeDaMao(evento.getHand()));

        registro.guardar(registrado);
    }

    /** So registra quando quem causou o dano foi um jogador. */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void aoCausarDano(EntityDamageByEntityEvent evento) {
        if (!(evento.getDamager() instanceof Player)) {
            return;
        }

        Player jogador = (Player) evento.getDamager();

        Evento registrado = criar(TiposDeEvento.ATAQUE, jogador, evento.getEntity().getLocation());
        registrado.anotar("alvo", evento.getEntity().getType().name());
        registrado.anotar("dano", String.format("%.2f", evento.getDamage()));
        registrado.anotar("causa", evento.getCause().name());

        registro.guardar(registrado);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void aoConsumirItem(PlayerItemConsumeEvent evento) {
        Evento registrado = criar(TiposDeEvento.CONSOME_ITEM, evento.getPlayer(),
                evento.getPlayer().getLocation());
        registrado.anotar("item", evento.getItem().getType().name());

        registro.guardar(registrado);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void aoPescar(PlayerFishEvent evento) {
        Evento registrado = criar(TiposDeEvento.PESCARIA, evento.getPlayer(),
                evento.getPlayer().getLocation());
        registrado.anotar("estado", evento.getState().name());

        registro.guardar(registrado);
    }

    /**
     * Guarda so o que da pra observar com seguranca: em que inventario foi,
     * qual slot, que tipo de acao e o item do slot. Nao copiamos o conteudo
     * inteiro do inventario do jogador.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void aoMexerNoInventario(InventoryClickEvent evento) {
        if (!(evento.getWhoClicked() instanceof Player)) {
            return;
        }

        Player jogador = (Player) evento.getWhoClicked();

        Evento registrado = criar(TiposDeEvento.INVENTARIO, jogador, jogador.getLocation());
        registrado.anotar("tipo_inventario", evento.getInventory().getType().name());
        registrado.anotar("slot", String.valueOf(evento.getSlot()));
        registrado.anotar("acao", evento.getAction().name());

        ItemStack item = evento.getCurrentItem();
        if (item != null) {
            registrado.anotar("item", item.getType().name());
        }

        registro.guardar(registrado);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void aoEntrarNoServidor(PlayerJoinEvent evento) {
        registro.guardar(criar(TiposDeEvento.ENTRA_SERVIDOR, evento.getPlayer(),
                evento.getPlayer().getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void aoSairDoServidor(PlayerQuitEvent evento) {
        registro.guardar(criar(TiposDeEvento.SAI_SERVIDOR, evento.getPlayer(),
                evento.getPlayer().getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void aoTrocarDeMundo(PlayerChangedWorldEvent evento) {
        Evento registrado = criar(TiposDeEvento.TROCA_MUNDO, evento.getPlayer(),
                evento.getPlayer().getLocation());
        registrado.anotar("mundo_anterior", evento.getFrom().getName());

        registro.guardar(registrado);
    }

    /** Monta um evento com os campos basicos ja preenchidos. */
    private Evento criar(String tipo, Player jogador, Location onde) {
        Evento evento = new Evento();

        evento.tipo = tipo;
        evento.momento = System.currentTimeMillis();
        evento.jogador = jogador.getUniqueId();
        evento.mundo = onde.getWorld().getName();
        evento.x = onde.getX();
        evento.y = onde.getY();
        evento.z = onde.getZ();

        return evento;
    }

    /** Nome da mao usada, em texto. */
    private String nomeDaMao(EquipmentSlot mao) {
        if (mao == null) {
            return null;
        }

        if (mao == EquipmentSlot.OFF_HAND) {
            return "secundaria";
        }

        return "principal";
    }
}
