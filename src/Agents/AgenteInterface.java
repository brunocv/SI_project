package Agents;

import Behaviours.PedirBicicletas;
import Behaviours.PedirFalhas;
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
    private int contagem_graph_1;
    private int contagem_graph_2;
    private int bicicletas[];
    private int falhas[];

    protected void setup(){
        super.setup();
        System.out.println("Agente interface entrou: " + getAID().getName());

        Object[] args = this.getArguments();
        this.mapa = (Mapa) args[0];
        this.posicaoUtilizadores = new ArrayList<>();
        this.estacoes = mapa.getEstacoes() * mapa.getEstacoes();
        this.contagem = 0;
        this.contagem_graph_1 = 0;
        this.contagem_graph_2 = 0;
        this.bicicletas = new int[this.estacoes];
        this.falhas = new int[this.estacoes];
        Arrays.fill(this.bicicletas, 0);
        Arrays.fill(this.falhas,0);

        try{
            Thread.sleep(2000);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        this.addBehaviour(new PedirOcupacao(this,1500));
        this.addBehaviour(new ReceiveInfo());
        this.addBehaviour(new drawOcupacao(this,2000));
        this.addBehaviour(new PedirUtilizadores(this,1000));
        this.addBehaviour(new PedirBicicletas(this,3000));
        this.addBehaviour(new PedirFalhas(this,6000));
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

            if (contagem >= estacoes) {
                ui.drawUtilizadores(posicaoUtilizadores);
                posicaoUtilizadores.clear();
                contagem = 0;
            }
            if (contagem_graph_1 >= estacoes){
                ui.drawBicicletas(bicicletas);
                contagem_graph_1 = 0;
            }
            if(contagem_graph_2 >= estacoes){
                ui.drawFalhas(falhas);
                contagem_graph_2 = 0;
            }

            ACLMessage msg = receive();
            if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
                if (msg.getContent().contains("Estacao")) {
                    ocupacaoEstacao = msg.getContent();
                }
                else if(msg.getContent().contains("Bic")){
                    contagem_graph_1++;
                    String est = msg.getSender().getName();
                    int index = est.indexOf("@");
                    int index2 = est.indexOf(" ") +1;

                    int estacao = Integer.parseInt(est.substring(index2,index));
                    bicicletas[estacao-1] = Integer.parseInt(msg.getContent().substring(4));

                }
                else if(msg.getContent().contains("Fail")){
                    contagem_graph_2++;
                    String est = msg.getSender().getName();
                    int index = est.indexOf("@");
                    int index2 = est.indexOf(" ") +1;

                    int estacao = Integer.parseInt(est.substring(index2,index));
                    falhas[estacao-1] = Integer.parseInt(msg.getContent().substring(5));

                }
                else if(msg.getContent() != null){
                    contagem++;
                    String str = msg.getContent();
                    str = str.replaceAll("[\n]+", " ");
                    String posicoes[] = str.split(" ");

                    for (int i = 0; i < posicoes.length && posicoes.length > 1; i += 2) {
                        if (posicoes == null) break;
                        if (posicoes[i] != null && posicoes[i] != "" && posicoes[i] != " " && posicoes[i + 1] != null && posicoes[i + 1] != "" && posicoes[i + 1] != " ") {
                            Coordenadas c = new Coordenadas(Integer.parseInt(posicoes[i]), Integer.parseInt(posicoes[i + 1]));
                            posicaoUtilizadores.add(c);
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
