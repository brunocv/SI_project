package Agents;

import Behaviours.TratamentoIncentivo;
import Util.Coordenadas;
import Util.Mapa;
import Util.Utilidade;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Random;

// Responsavel por fazer  uma  viagem  entre  duas  estaçoes
public class AgenteUtilizador extends Agent {

    private Coordenadas posicaoInicial;
    private Coordenadas posicaoAtual;
    private Coordenadas posicaoDestino;
    private Coordenadas estacaoProxima;
    private Coordenadas estacaoDestino;
    private String nomeEstacaoDestino;
    private double distanciaPercorrida;
    private int aceitouIncentivo;
    private int concluido;
    private int greediness; //Para agentes comportarem-se de maneira diferente, varia entre -3 e 3

    protected void setup(){
        super.setup();
      //  System.out.println("Agente utilizador entrou: " + getAID().getName());

        Object[] args = getArguments();
        Mapa mapa = (Mapa)args[0];
        int tamanho_mapa = mapa.getTamanho();
        int numero_Estacoes = mapa.getEstacoes() * mapa.getEstacoes() ;

        //A posição inicial e o destino do utilizador vai ser gerado de forma aleatória
        Random rand = new Random();
        int estacao_inicio = 1 + rand.nextInt(numero_Estacoes);

        /*
            Vai perguntar à estação se ela possui bicicletas suficientes para poder começar lá
        */
        int resposta = perguntaBicicleta(estacao_inicio);
        while(resposta == 0){ // Este ciclo while vai garantir que começa numa estação com bicicletas
            estacao_inicio = 1 + rand.nextInt(numero_Estacoes);
            resposta = perguntaBicicleta(estacao_inicio);
        }

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
        nomeEstacaoDestino = mapa.getNomeEstacao(estacaoDestino);
        distanciaPercorrida = 0;
        aceitouIncentivo = 0;
        concluido = 0;
        greediness = rand.nextInt(7) - 3;


        //Vai avisar a estação que ela é o destino dele.
        AID receiver = new AID();
        receiver.setLocalName(nomeEstacaoDestino);
        ACLMessage subscribe = new ACLMessage(ACLMessage.SUBSCRIBE);
        subscribe.addReceiver(receiver);
        this.send(subscribe);
        //--------------------------

        this.addBehaviour(new Movimento(this,1000));
        this.addBehaviour(new TratamentoIncentivo(this));
    }

    protected void takeDown(){
        super.takeDown();
      //  System.out.println("Agente utilizador terminou: " + getAID().getName());

    }

    public Coordenadas getPosicao(){return posicaoAtual.clone();}

    public Coordenadas getDestino() { return estacaoDestino.clone(); }

    public Coordenadas getOrigem() { return posicaoInicial.clone(); }

    public void setNewDestino(String key, Coordenadas coordenadas) {

        String estacaoAntiga = nomeEstacaoDestino;
        nomeEstacaoDestino = key;
        estacaoDestino = coordenadas.clone();
        aceitouIncentivo = 1;

        AID receiverOld = new AID();
        AID receiverNew = new AID();

        receiverOld.setLocalName(estacaoAntiga);
        receiverNew.setLocalName(key);

        ACLMessage sendOld = new ACLMessage(ACLMessage.CANCEL);
        ACLMessage sendNew = new ACLMessage(ACLMessage.SUBSCRIBE);

        sendOld.addReceiver(receiverOld);
        sendNew.addReceiver(receiverNew);

        this.send(sendOld);
        this.send(sendNew);
    }

    private int perguntaBicicleta(int estacao_inicio) {

        String estacao = "Estacao "+estacao_inicio;

        AID receiver = new AID();
        receiver.setLocalName(estacao);
        ACLMessage aEnviar = new ACLMessage(ACLMessage.REQUEST);
        aEnviar.addReceiver(receiver);
        aEnviar.setContent("Request para começar.");
        this.send(aEnviar);

        ACLMessage confirmation = receive();
        while(confirmation == null){confirmation = receive();}

        return Integer.parseInt(confirmation.getContent());
    }

    public void reduceValue() { aceitouIncentivo--; }

    public int getIncentivo() { return aceitouIncentivo; }

    public int getGreed() { return greediness; }

    private class Movimento extends TickerBehaviour{
        // // Behaviour responsavel pela movimentação do utilizador
        public Movimento(Agent a,long timeout){super(a,timeout);}

        protected void onTick(){
            if(concluido == 0) {
                int atualX = posicaoAtual.getCoordX();
                int atualY = posicaoAtual.getCoordY();
                int destinoX = estacaoDestino.getCoordX();
                int destinoY = estacaoDestino.getCoordY();

                //System.out.println("UTILIZADOR POSICAO ATUAL " + posicaoAtual.toString());
                Random rand = new Random(); // O random vai ser usado para poder haver um movimento mais variado
                // isto é, para não estar sempre a se mover da mesma maneira.

                if (atualX < destinoX && atualY < destinoY) {
                    Coordenadas novaPosicao = new Coordenadas(0, 0);
                    int bool = rand.nextInt(2);

                    if (bool == 1) {
                        novaPosicao = new Coordenadas(atualX + 1, atualY);
                    } else {
                        novaPosicao = new Coordenadas(atualX, atualY + 1);
                    }

                    posicaoAtual = new Coordenadas(novaPosicao);
                }
                if (atualX < destinoX && atualY > destinoY) {
                    Coordenadas novaPosicao = new Coordenadas(0, 0);
                    int bool = rand.nextInt(2);

                    if (bool == 1) {
                        novaPosicao = new Coordenadas(atualX + 1, atualY);
                    } else {
                        novaPosicao = new Coordenadas(atualX, atualY - 1);
                    }

                    posicaoAtual = new Coordenadas(novaPosicao);
                }
                if (atualX < destinoX && atualY == destinoY) {
                    Coordenadas novaPosicao = new Coordenadas(atualX + 1, atualY);
                    posicaoAtual = new Coordenadas(novaPosicao);
                }
                if (atualX > destinoX && atualY < destinoY) {
                    Coordenadas novaPosicao = new Coordenadas(0, 0);
                    int bool = rand.nextInt(2);

                    if (bool == 1) {
                        novaPosicao = new Coordenadas(atualX - 1, atualY);
                    } else {
                        novaPosicao = new Coordenadas(atualX, atualY + 1);
                    }

                    posicaoAtual = new Coordenadas(novaPosicao);
                }
                if (atualX > destinoX && atualY > destinoY) {
                    Coordenadas novaPosicao = new Coordenadas(0, 0);
                    int bool = rand.nextInt(2);

                    if (bool == 1) {
                        novaPosicao = new Coordenadas(atualX - 1, atualY);
                    } else {
                        novaPosicao = new Coordenadas(atualX, atualY - 1);
                    }
                    posicaoAtual = new Coordenadas(novaPosicao);
                }
                if (atualX > destinoX && atualY == destinoY) {
                    Coordenadas novaPosicao = new Coordenadas(atualX - 1, atualY);
                    posicaoAtual = new Coordenadas(novaPosicao);
                }
                if (atualX == destinoX && atualY < destinoY) {
                    Coordenadas novaPosicao = new Coordenadas(atualX, atualY + 1);
                    posicaoAtual = new Coordenadas(novaPosicao);
                }
                if (atualX == destinoX && atualY > destinoY) {
                    Coordenadas novaPosicao = new Coordenadas(atualX, atualY - 1);
                    posicaoAtual = new Coordenadas(novaPosicao);
                }
                if (atualX == destinoX && atualY == destinoY) {
                   // System.out.println("Agente utilizador: " + getAID().getName() + " chegou ao destino: X=" + posicaoAtual.getCoordX() + "Y=" + posicaoAtual.getCoordY() + ".");
                    concluido = 1;
                    myAgent.addBehaviour(new InformarFim(nomeEstacaoDestino));
                }

                if(concluido == 0) {
                    Utilidade ut = new Utilidade(); // Calcular a distância entre 2 pontos para saber a % de caminho já percorrida
                    double n1 = ut.distancia2pontos(posicaoInicial.getCoordX(), posicaoInicial.getCoordY(), posicaoDestino.getCoordX(), posicaoDestino.getCoordY());
                    double n2 = ut.distancia2pontos(posicaoInicial.getCoordX(), posicaoInicial.getCoordY(), posicaoAtual.getCoordX(), posicaoAtual.getCoordY());
                    distanciaPercorrida = n2 / n1;

                    myAgent.addBehaviour(new InformarMovimento(distanciaPercorrida, posicaoAtual));
                }
            }
        }
    }

    private class InformarMovimento extends OneShotBehaviour{

        double dtPercorrida;
        Coordenadas posAtual;

        // // Behaviour responsavel por informar um movimento do utilizador á estação
        public InformarMovimento(double distanciaPercorrida, Coordenadas posicaoAtual){
            dtPercorrida = distanciaPercorrida;
            posAtual = posicaoAtual.clone();
        }

        public void action(){
            try {
                //Construir a descrição de agente Estação
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Estacao");
                dfd.addServices(sd);


                //Array de Resultados da procura por estações
                DFAgentDescription[] resultados = DFService.search(myAgent, dfd);

                //Mandar mensagem a todas as Estações
                for (int i = 0; i < resultados.length; i++) {
                    DFAgentDescription dfd1 = resultados[i];
                    AID estacao = dfd1.getName();

                    String mensagem = "Nova Posicao: &" + posAtual.getCoordX() + "$ %" + posAtual.getCoordY() + "! ?" + dtPercorrida + "< -" + aceitouIncentivo + "@" + "€" + nomeEstacaoDestino + "£";

                    ACLMessage aEnviar = new ACLMessage(ACLMessage.INFORM);
                    aEnviar.addReceiver(estacao);
                    aEnviar.setContent(mensagem);
                    myAgent.send(aEnviar);
                }
            } catch (FIPAException fe){ fe.printStackTrace(); }
        }
    }
// // // Behaviour responsavel por informar que chegou á estcao final
    private class InformarFim extends OneShotBehaviour{

        private String nomeEstacao;

        public InformarFim(String nome){
            nomeEstacao = nome;
        }

        public void action(){
            AID receiver = new AID();
            receiver.setLocalName(nomeEstacao);
            ACLMessage aEnviar = new ACLMessage(ACLMessage.INFORM);
            aEnviar.addReceiver(receiver);
            aEnviar.setContent("Cheguei ao destino.");
            myAgent.send(aEnviar);
        }
    }

}
