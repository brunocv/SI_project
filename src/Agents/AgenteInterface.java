package Agents;

import Behaviours.PedirOcupacao;
import Interface.UI;
import Util.Mapa;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
        this.addBehaviour(new ReceiveOcupacao());
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

    private class ReceiveOcupacao extends CyclicBehaviour {

        public void action() {

            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    ocupacaoEstacao = msg.getContent();
                    System.out.println(ocupacaoEstacao);
                }
            }
        }
    }
}
