# antibot-nosalr

Sistema de observabilidade comportamental de jogadores para servidores Paper.

A ideia e registrar acoes observaveis dos jogadores ao longo do tempo e,
mais pra frente, analisar esses registros pra procurar padroes repetitivos
que possam indicar automacao (macro, farm automatizada e parecidos).

2 regras que valem pra todo o projeto:

- Repeticao **nao** e prova de bot. Jogador legitimo repete movimento em
  farm, mineracao e construcao.
- O plugin **nao** aplica punicao automatica. Ele junta evidencia pro
  administrador olhar.

## Requisitos exatos

| O que      | Versao                             |
| ---------- | ---------------------------------- |
| Paper      | 1.21.11 (build 132, canal STABLE)  |
| Java       | 21                                 |
| Gradle     | 9.8.0 (pelo wrapper)               |
| paper-api  | 1.21.11-R0.1-SNAPSHOT              |

Por que 1.21.11: e a ultima versao estavel da linha 1.21 e o `paper-api`
dela esta publicado no repositorio da Paper. As versoes 26.x pedem Java 25
e o `paper-api` delas hoje so existe como `-beta` / `-alpha`.

## Compilar

    ./gradlew build

O build copia o jar pronto pra `~/serverteste3s/plugins` sozinho.
Pra apontar pra outra pasta:

    ./gradlew build -PpastaServidor=/caminho/do/servidor

## Captura de acoes

O plugin escuta as acoes dos jogadores pela API de eventos do Paper e guarda
cada uma como um `Evento`.

### Representacao normalizada

Todo evento tem os mesmos campos basicos, nao importa o tipo:

- `tipo` — qual acao foi (ver `TiposDeEvento`)
- `momento` — timestamp em milissegundos
- `jogador` — UUID de quem fez
- `mundo`, `x`, `y`, `z` — onde aconteceu
- `contexto` — mapa com o que so faz sentido pra aquele tipo

### Tipos registrados hoje

| tipo                | evento da API              | contexto                            |
| ------------------- | -------------------------- | ----------------------------------- |
| `quebra_bloco`      | `BlockBreakEvent`          | bloco                               |
| `coloca_bloco`      | `BlockPlaceEvent`          | bloco                               |
| `interage_bloco`    | `PlayerInteractEvent`      | bloco, acao, mao                    |
| `usa_item`          | `PlayerInteractEvent`      | item, mao                           |
| `interage_entidade` | `PlayerInteractEntityEvent`| entidade, mao                       |
| `ataque`            | `EntityDamageByEntityEvent`| alvo, dano, causa                   |
| `consome_item`      | `PlayerItemConsumeEvent`   | item                                |
| `pescaria`          | `PlayerFishEvent`          | estado                              |
| `inventario`        | `InventoryClickEvent`      | tipo_inventario, slot, acao, item   |
| `entra_servidor`    | `PlayerJoinEvent`          | -                                   |
| `sai_servidor`      | `PlayerQuitEvent`          | -                                   |
| `troca_mundo`       | `PlayerChangedWorldEvent`  | mundo_anterior                      |

So entrou na lista acao que tem evento proprio na API do Paper 1.21.11 e que
foi conferida compilando contra o `paper-api` dessa versao.

### Acrescentar um tipo novo

1. Adicionar a constante em `TiposDeEvento`.
2. Escrever mais um metodo `@EventHandler` em `OuvinteDeAcoes`.

E so. Quem le os eventos trabalha em cima de `tipo` + `contexto`, entao os
algoritmos de analise nao precisam ser alterados pra dar conta de um tipo
novo.

### O que o plugin NAO observa

Isso aqui importa pra nao prometer o que nao existe:

- O plugin registra **o que a camada de servidor consegue ver**, e nao toda
  entrada fisica do jogador.
- **Nao** ve clique de mouse, tecla apertada ou pacote de rede cru. A API do
  Paper nao expoe isso, entao o plugin nao afirma observar.
- **Nao** guarda mensagem de chat, senha nem dado pessoal.
- **Nao** captura conteudo de trafego de rede.

Na pratica: se um jogador quebra um bloco, o servidor avisa que o bloco foi
quebrado. Quantas vezes ele clicou pra conseguir isso, o servidor nao conta.

Todos os ouvintes usam prioridade `MONITOR`, ou seja, o plugin so observa e
nunca cancela nem altera a acao do jogador.

### Onde os eventos ficam

Na memoria do servidor, em `RegistroDeEventos`, separados por jogador e com
limite de 500 eventos por jogador (os mais antigos saem). Nao tem banco de
dados ainda.

## O que o plugin faz hoje

So o comando `/amostra`: tira uma amostra do estado observavel de quem
digitou e mostra os campos no chat.

Campos da amostra:

- UUID do jogador
- timestamp
- mundo
- coordenadas X, Y e Z
- yaw e pitch
- deslocamento desde a amostra anterior
- velocidade horizontal e vertical estimada
- estado observavel do jogador
- identificador da sessao de observacao

O deslocamento e a velocidade precisam de uma amostra anterior pra serem
calculados, entao na primeira vez que o jogador usa o comando eles vem zero.

As amostras ficam so na memoria por enquanto.
