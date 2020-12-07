package Agents;

import JadePlatform.MainContainer;
import Util.Coordenadas;
import Util.Mapa;
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
        Mapa mapa = (Mapa)args[0];
        int tamanho_mapa = mapa.getTamanho();
        int numero_Estacoes = mapa.getEstacoes() * mapa.getEstacoes() ;

        //A posição inicial e o destino do utilizador vai ser gerado de forma aleatória
        Random rand = new Random();
        int estacao_inicio = 1 + rand.nextInt(numero_Estacoes);

        /*
        Vai ao mapa buscar a posição da estação onde começa
        */
        Coordenadas inicioEst = mapa.getCoordenadasDaEstacao(estacao_inicio);

        int fimX = rand.nextInt(tamanho_mapa);//Gerar uma posição de fim random
        int fimY = rand.nextInt(tamanho_mapa);//.........

        /*
        Vai ao mapa ver qual a estação que controla a posição que lhe calhou
         */
        Coordenadas fimEst = mapa.getEstacaoDaArea(new Coordenadas(fimX,fimY));
        while( fimEst.equals(inicioEst)){
            fimX = rand.nextInt(tamanho_mapa);
            fimY = rand.nextInt(tamanho_mapa);

            fimEst = mapa.getEstacaoDaArea(new Coordenadas(fimX,fimY));
        } // Este While serve para garantir que cada utilizador não escolhe como destino a estação onde começou


        posicaoInicial = new Coordenadas(inicioEst);
        posicaoDestino = new Coordenadas(fimX,fimY);
        posicaoAtual = new Coordenadas(posicaoInicial);
        estacaoProxima = new Coordenadas(posicaoInicial);
        estacaoDestino = new Coordenadas(fimEst);
        distanciaPercorrida = 0;
        aceitouIncentivo = 0;

        System.out.println("UTILIZADOR COMEÇA EM " + posicaoInicial.toString());
        System.out.println("UTILIZADOR TEM DE CHEGAR EM " + estacaoDestino.toString());
        System.out.println("UTILIZADOR QUERIA IR PARA " + posicaoDestino.toString());

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

            System.out.println("UTILIZADOR POSICAO ATUAL " + posicaoAtual.toString());
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
            Utilidade ut = new Utilidade(); // Calcular a distância entre 2 pontos para saber a % de caminho já percorrida
            double n1 = ut.distancia2pontos(posicaoInicial.getCoordX(),posicaoInicial.getCoordY(),posicaoDestino.getCoordX(),posicaoDestino.getCoordY());
            double n2 = ut.distancia2pontos(posicaoInicial.getCoordX(),posicaoInicial.getCoordY(),posicaoAtual.getCoordX(),posicaoAtual.getCoordY());
            distanciaPercorrida = n2/n1;
            //System.out.println(distanciaPercorrida);
        }
    }
}
