package Agents;

import Interface.UI;
import Util.Mapa;
import jade.core.Agent;

public class AgenteInterface extends Agent {

    private Mapa mapa;
    private UI ui;

    protected void setup(){
        super.setup();
        System.out.println("Agente interface entrou: " + getAID().getName());

        Object[] args = this.getArguments();
        this.mapa = (Mapa) args[0];
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
}
