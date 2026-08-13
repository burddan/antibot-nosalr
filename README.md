# antibot-nosalr

Sistema de observabilidade comportamental de jogadores para servidores Paper.

A ideia e registrar acoes observaveis dos jogadores ao longo do tempo e,
mais pra frente, analisar esses registros pra procurar padroes repetitivos
que possam indicar automacao (macro, farm automatizada e parecidos).

Duas regras que valem pra todo o projeto:

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
