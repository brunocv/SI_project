package Agents;

import Behaviours.PedirOcupacao;
import Behaviours.PedirUtilizadores;
import Interface.UI;
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

import java.util.HashMap;
import java.util.Map;

public class AgenteInterface extends Agent {

    private Mapa mapa;
    private UI ui;
    private String ocupacaoEstacao;

    protected void setup(){
        super.setup();
        System.out.println("Agente interface entrou: " + getAID().getName());

        Object[] args = this.getArguments();
        this.mapa = (Mapa) args[0];

        try{
            Thread.sleep(2000);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        this.addBehaviour(new PedirOcupacao(this,5000));
        this.addBehaviour(new ReceiveInfo());
        this.addBehaviour(new drawOcupacao(this,6000));
        this.addBehaviour(new PedirUtilizadores(this,4000));
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

            ACLMessage msg = receive();
            if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
                if(msg.getContent().contains("Estacao")){
                    ocupacaoEstacao = msg.getContent();
                }
                else{
                    System.out.println("NOVO");
                    System.out.print(msg.getContent());
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
