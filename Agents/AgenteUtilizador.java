package Agents;

import JadePlatform.MainContainer;
import Util.Coordenadas;
import Util.Utilidade;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.Random;

public class AgenteUtilizador extends Agent {

    private Coordenadas posicaoInicial;
    private Coordenadas posicaoAtual;
    private Coordenadas posicaoDestino;
    private Coordenadas estacaoDestino;
    private Coordenadas estacaoProxima;
    private double distanciaPercorrida;
    private int aceitouIncentivo;

    protected void setup(){
        super.setup();
        System.out.println("Agente utilizador entrou: " + getAID().getName());

        Object[] args = getArguments();
        int tamanho_mapa = (int)args[0];
        int numero_Estacoes = (int)args[1] * (int)args[1];

        //A posição inicial e o destino do utilizador vai ser gerado de forma aleatória
        Random rand = new Random();
        int estacao_inicio = 1 + rand.nextInt(numero_Estacoes);
        /*
        ... Perguntar qual a posição da estação
         */

        int fimX = rand.nextInt(tamanho_mapa);
        int fimY = rand.nextInt(tamanho_mapa);
        /*
        ... Perguntar qual a estação que controla a area onde ele acaba
         */

        //Para testar o movimento vou gerar duas random de inicio
            // depois remover
        int inicioX = rand.nextInt(tamanho_mapa);
        int inicioY = rand.nextInt(tamanho_mapa);

        //Temporario enquanto não existem estações
        posicaoInicial = new Coordenadas(inicioX,inicioY);
        posicaoDestino = new Coordenadas(fimX,fimY);
        posicaoAtual = new Coordenadas(posicaoInicial);
        estacaoProxima = new Coordenadas(posicaoInicial);
        estacaoDestino = new Coordenadas(posicaoDestino);
        distanciaPercorrida = 0;
        aceitouIncentivo = 0;

        //System.out.println("My Destino: X="+posicaoDestino.getCoordX()+"Y="+posicaoDestino.getCoordY()+".");

        this.addBehaviour(new Movimento(this,500));

        //doDelete();
    }

    protected void takeDown(){
        super.takeDown();
        System.out.println("Agente utilizador terminou: " + getAID().getName());

    }

    private class Movimento extends TickerBehaviour{
        public Movimento(Agent a,long timeout){super(a,timeout);}

        protected void onTick(){
            int atualX = posicaoAtual.getCoordX();
            int atualY = posicaoAtual.getCoordY();
            int destinoX = estacaoDestino.getCoordX();
            int destinoY = estacaoDestino.getCoordY();

            Random rand = new Random(); // O random vai ser usado para poder haver um movimento mais variado
                                        // isto é, para não estar sempre a se mover da mesma maneira.

            if( atualX < destinoX && atualY < destinoY){
                Coordenadas novaPosicao = new Coordenadas(0,0);
                int bool = rand.nextInt(2);

                if(bool == 1){novaPosicao = new Coordenadas(atualX+1,atualY);}
                else {novaPosicao = new Coordenadas(atualX,atualY+1);}

                posicaoAtual = new Coordenadas(novaPosicao);
            }
            if( atualX < destinoX && atualY > destinoY){
                Coordenadas novaPosicao = new Coordenadas(0,0);
                int bool = rand.nextInt(2);

                if(bool == 1){novaPosicao = new Coordenadas(atualX+1,atualY);}
                else {novaPosicao = new Coordenadas(atualX,atualY-1);}

                posicaoAtual = new Coordenadas(novaPosicao);
            }
            if( atualX < destinoX && atualY == destinoY){ Coordenadas novaPosicao = new Coordenadas(atualX+1,atualY);
                                                          posicaoAtual = new Coordenadas(novaPosicao);
                                                        }
            if( atualX > destinoX && atualY < destinoY){
                Coordenadas novaPosicao = new Coordenadas(0,0);
                int bool = rand.nextInt(2);

                if(bool == 1){novaPosicao = new Coordenadas(atualX-1,atualY);}
                else {novaPosicao = new Coordenadas(atualX,atualY+1);}

                posicaoAtual = new Coordenadas(novaPosicao);
            }
            if( atualX > destinoX && atualY > destinoY){
                Coordenadas novaPosicao = new Coordenadas(0,0);
                int bool = rand.nextInt(2);

                if(bool == 1){novaPosicao = new Coordenadas(atualX-1,atualY);}
                else {novaPosicao = new Coordenadas(atualX,atualY-1);}
                posicaoAtual = new Coordenadas(novaPosicao);
            }
            if( atualX > destinoX && atualY == destinoY){ Coordenadas novaPosicao = new Coordenadas(atualX-1,atualY);
                                                          posicaoAtual = new Coordenadas(novaPosicao);
                                                        }
            if( atualX == destinoX && atualY < destinoY){ Coordenadas novaPosicao = new Coordenadas(atualX,atualY+1);
                                                          posicaoAtual = new Coordenadas(novaPosicao);
                                                        }
            if( atualX == destinoX && atualY > destinoY){ Coordenadas novaPosicao = new Coordenadas(atualX,atualY-1);
                                                          posicaoAtual = new Coordenadas(novaPosicao);
                                                        }
            if( atualX == destinoX && atualY == destinoY){
                System.out.println("Agente utilizador: " + getAID().getName()+" chegou ao destino: X="+posicaoAtual.getCoordX()+"Y="+posicaoAtual.getCoordY()+".");
                doDelete();
            }
            //System.out.println("Agente utilizador: " + getAID().getName()+" moveu para: X="+posicaoAtual.getCoordX()+"Y="+posicaoAtual.getCoordY()+".");
            Utilidade ut = new Utilidade();
            double n1 = ut.distancia2pontos(posicaoInicial.getCoordX(),posicaoInicial.getCoordY(),posicaoDestino.getCoordX(),posicaoDestino.getCoordY());
            double n2 = ut.distancia2pontos(posicaoInicial.getCoordX(),posicaoInicial.getCoordY(),posicaoAtual.getCoordX(),posicaoAtual.getCoordY());
            distanciaPercorrida = n2/n1;
            System.out.println(distanciaPercorrida);
        }
    }
}
