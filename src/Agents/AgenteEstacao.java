package Agents;

import JadePlatform.DFManager;
import Util.Coordenadas;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgenteEstacao extends Agent {

    private Coordenadas posicaoEstacao;
    private int capacidadeEstacao;
    private int bicicletas;
    private List<Coordenadas> areaDeControlo;
    private Map<String,Coordenadas> utilizadorNaArea;
    private ConcurrentHashMap<String,Double> ocupacaoEstacao;

    protected void setup(){
        super.setup();
        DFManager.registarAgente(this,"Estacao");
        System.out.println("Agente estacao entrou: " + getAID().getName());

        Object[] args = getArguments();
        posicaoEstacao = (Coordenadas) args[0];
        areaDeControlo = (List<Coordenadas>) args[1];
        ocupacaoEstacao = (ConcurrentHashMap<String, Double>) args[2];
        capacidadeEstacao = 20;
        bicicletas = 10; // Random starting values, depois talvez seja melhor alterado dependendo do numero
                        // de estações que se vai ter. devido ao mapa ser dinamico.

        utilizadorNaArea = new HashMap<>();

        this.addBehaviour(new ReceiveMessages());
    }

    protected void takeDown(){
        super.takeDown();
        System.out.println("Agente estacao terminou: " + getAID().getName());
        DFManager.deRegister(this);

    }

    private class ReceiveMessages extends CyclicBehaviour{

        public void action(){

            ACLMessage msg = receive();
            if(msg != null){
                if(msg.getPerformative() == ACLMessage.REQUEST){

                    if(msg.getContent().equals("Request para começar.")){ //Vai responder se é permitido começar o deslocamento aqui

                        ACLMessage resposta = new ACLMessage(ACLMessage.INFORM);

                        if(bicicletas > 0){
                            resposta.setContent("1");
                            bicicletas--;
                            String fullName = myAgent.getAID().getName();
                            int index = fullName.indexOf("@"); // Vai procurar o indice da primeira occurencia de "@"
                                                               //pois o Nome dos agentes é Estacao X@etc.etc.etc
                            String nome = fullName.substring(0,index);

                            synchronized (this){
                                ocupacaoEstacao.put(nome,(double)bicicletas/capacidadeEstacao);
                            }
                        }

                        else{ resposta.setContent("0"); }

                        resposta.addReceiver(msg.getSender());

                        myAgent.send(resposta);
                    }
                    else if(msg.getContent().equals("Ocupacao")){
                        ACLMessage resposta = new ACLMessage(ACLMessage.INFORM);
                        resposta.setContent(toStringOcupacao());
                        resposta.addReceiver(msg.getSender());
                        myAgent.send(resposta);

                    }
                    else if(msg.getContent().equals("Utilizadores")){

                        ACLMessage resposta = new ACLMessage(ACLMessage.INFORM);

                        StringBuilder str = new StringBuilder();

                        for (Map.Entry<String, Coordenadas> mapEntry : utilizadorNaArea.entrySet()) {
                            str.append(mapEntry.getValue().getCoordX() +" " + mapEntry.getValue().getCoordY()+"\n");
                        }

                        resposta.setContent(str.toString());
                        resposta.addReceiver(msg.getSender());
                        myAgent.send(resposta);
                    }
                }else if(msg.getPerformative() == ACLMessage.INFORM){

                    if(msg.getContent().equals("Cheguei ao destino.")){
                        String numUtilizador = msg.getSender().getName();
                        int ind = numUtilizador.indexOf("@");
                        String nomeUtilizador = numUtilizador.substring(0,ind);
                        utilizadorNaArea.remove(nomeUtilizador);

                        bicicletas++;
                        String fullName = myAgent.getAID().getName();
                        int index = fullName.indexOf("@"); // Vai procurar o indice da primeira occurencia de "@"
                                                           //pois o Nome dos agentes é Estacao X@etc.etc.etc
                        String nome = fullName.substring(0,index);
                        synchronized (this){
                            ocupacaoEstacao.put(nome,(double)bicicletas/capacidadeEstacao);
                        }
                    }

                    //Exemplo Mensagem::
                    //---- Nova Posicao: &7$ %1! ?0.12<
                    if(msg.getContent().contains("Nova Posicao:")){

                        String utilizador = msg.getSender().getName();
                        String nomeUtilizador = utilizador.substring(0,utilizador.indexOf("@")); //Utilizador que mandou a mensagem

                        String mensagem = msg.getContent();
                        int index1 = mensagem.indexOf("&")+1;
                        int index2 = mensagem.indexOf("$");
                        int index3 = mensagem.indexOf("%")+1;
                        int index4 = mensagem.indexOf("!");
                        int index5 = mensagem.indexOf("?")+1;
                        int index6 = mensagem.indexOf("<");

                        String endereco = myAgent.getAID().getName();
                        int numero = Integer.parseInt(endereco.substring(endereco.indexOf(" ")+1,endereco.indexOf("@")));
                        // ^^^^ Numero da estação

                        int novoX = Integer.parseInt(mensagem.substring(index1,index2));
                        int novoY = Integer.parseInt(mensagem.substring(index3,index4));

                        Coordenadas novaPosicao = new Coordenadas( novoX , novoY );

                        if(areaDeControlo.contains(novaPosicao)){//Para adicionar o utilizador no map depois de entrar na area
                            utilizadorNaArea.put(nomeUtilizador,novaPosicao);
                        }else if(utilizadorNaArea.containsKey(nomeUtilizador)){ //Para remover o utilizador do map depois de ele ter saido da area
                            utilizadorNaArea.remove(nomeUtilizador);
                        }
                    }
                }
            }
        }
    }


    public String toStringOcupacao() {

        StringBuilder str = new StringBuilder();

        str.append("<html>OCUPAÇÃO DAS ESTAÇÕES");
        str.append("<br/>");

        synchronized (this){

            for(int i = 1; i <26 ; i++){
                String nome = "Estacao "+i;
                if(this.ocupacaoEstacao.get(nome) != null){
                    str.append("<br/>" + nome+"  Ocupação: "+ this.ocupacaoEstacao.get(nome));
                }
                else{
                    break;
                }
            }
        }

        str.append("</html>");

        return str.toString();
    }


}
