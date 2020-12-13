package Agents;

import Behaviours.PedirOcupacao;
import Behaviours.PedirUtilizadores;
import Interface.UI;
import Util.Coordenadas;
import Util.Mapa;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.*;

public class AgenteInterface extends Agent {

    private Mapa mapa;
    private UI ui;
    private String ocupacaoEstacao;
    private List<Coordenadas> posicaoUtilizadores;
    private int estacoes;
    private int contagem;

    protected void setup(){
        super.setup();
        System.out.println("Agente interface entrou: " + getAID().getName());

        Object[] args = this.getArguments();
        this.mapa = (Mapa) args[0];
        this.posicaoUtilizadores = new ArrayList<>();
        this.estacoes = mapa.getEstacoes() * mapa.getEstacoes();
        this.contagem = 0;

        try{
            Thread.sleep(2000);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        this.addBehaviour(new PedirOcupacao(this,4000));
        this.addBehaviour(new ReceiveInfo());
        this.addBehaviour(new drawOcupacao(this,4500));
        this.addBehaviour(new PedirUtilizadores(this,3000));
        startUI();
    }

    protected void takeDown(){
        super.takeDown();
        System.out.println("Agente interface terminou: " + getAID().getName());

    }

    public void startUI(){
        this.ui = new UI(mapa,this);
        this.ui.getJFrame().setVisible(true);
    }

    private class ReceiveInfo extends CyclicBehaviour {

        public void action() {

            if(contagem >= estacoes){
                ui.drawUtilizadores(posicaoUtilizadores);
                posicaoUtilizadores.clear();
                contagem = 0;
            }

            ACLMessage msg = receive();
            if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
                if(msg.getContent().contains("Estacao")){
                    ocupacaoEstacao = msg.getContent();
                }
                else{
                    if(msg.getContent()!=null){
                        contagem++;
                        String str = msg.getContent();
                        str = str.replaceAll("[\n]+", " ");
                        String posicoes[] = str.split(" ");

                        for(int i = 0; i< posicoes.length && posicoes.length > 1; i+=2){
                            if(posicoes == null) break;
                            if(posicoes[i] != null && posicoes[i]!="" && posicoes[i]!=" " && posicoes[i+1] != null && posicoes[i+1] != "" && posicoes[i+1]!=" "){
                                Coordenadas c = new Coordenadas(Integer.parseInt(posicoes[i]),Integer.parseInt(posicoes[i+1]));
                                posicaoUtilizadores.add(c);
                            }
                        }
                    }
                }
            }
        }
    }

    private class drawOcupacao extends TickerBehaviour {

        public drawOcupacao(Agent a, long timeout){
            super(a,timeout);
        }

        protected void onTick(){
            ui.drawOcupacaoEstacao(ocupacaoEstacao);
        }
    }

}
