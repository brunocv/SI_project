package Agents;

import JadePlatform.DFManager;
import Util.Coordenadas;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
    private int falhas;

    protected void setup(){
        super.setup();
        DFManager.registarAgente(this,"Estacao");
        System.out.println("Agente estacao entrou: " + getAID().getName());

        Object[] args = getArguments();
        posicaoEstacao = (Coordenadas) args[0];
        areaDeControlo = (List<Coordenadas>) args[1];
        ocupacaoEstacao = (ConcurrentHashMap<String, Double>) args[2];
        capacidadeEstacao = 14;
        bicicletas = 7; // Random starting values, depois talvez seja melhor alterado dependendo do numero
                        // de estações que se vai ter. devido ao mapa ser dinamico.
        falhas = 0;
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
                        if(bicicletas==0) falhas++;
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
                    else if(msg.getContent().equals("Bicicletas")){
                        ACLMessage resposta = new ACLMessage(ACLMessage.INFORM);
                        resposta.setContent("Bic "+ bicicletas);
                        resposta.addReceiver(msg.getSender());
                        myAgent.send(resposta);
                    }
                    else if(msg.getContent().equals("Falhas")){
                        ACLMessage resposta = new ACLMessage(ACLMessage.INFORM);
                        resposta.setContent("Fail "+ falhas);
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

                    if(msg.getContent().contains("Cheguei ao destino.")){
                        String numUtilizador = msg.getSender().getName();
                        int ind = numUtilizador.indexOf("@");
                        String nomeUtilizador = numUtilizador.substring(0,ind);
                        utilizadorNaArea.remove(nomeUtilizador);

                        bicicletas++;

                        String fullName = myAgent.getAID().getName();
                        int index = fullName.indexOf("@"); // Vai procurar o indice da primeira occurencia de "@"
                                                           //pois o Nome dos agentes é Estacao X@etc.etc.etc
                        String nome = fullName.substring(0,index);

                        ocupacaoEstacao.put(nome,(double)bicicletas/capacidadeEstacao);
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
                        int index7 = mensagem.indexOf("-") + 1;
                        int index8 = mensagem.indexOf("@");

                        String endereco = myAgent.getAID().getName();
                        int numero = Integer.parseInt(endereco.substring(endereco.indexOf(" ") + 1, endereco.indexOf("@")));
                        // ^^^^ Numero da estação

                        int novoX = Integer.parseInt(mensagem.substring(index1, index2));
                        int novoY = Integer.parseInt(mensagem.substring(index3, index4));

                        Coordenadas novaPosicao = new Coordenadas(novoX, novoY);

                        if (areaDeControlo.contains(novaPosicao)) {//Para adicionar o utilizador no map depois de entrar na area
                            utilizadorNaArea.put(nomeUtilizador, novaPosicao);

                            double percorrido = Double.parseDouble(mensagem.substring(index5, index6));
                            int incentivo = Integer.parseInt(mensagem.substring(index7, index8));
                            //System.out.println("User :" + msg.getSender().getName() + " Percorrido:" + percorrido );
                            if (percorrido >= 0.75 && incentivo <= 0) {

                                HashMap<String,Double> rec = getRecomendacoes();
                                //^^Função q vai buscar as estações com menos de 20% de bicicletas restantes

                                if(rec.size() > 0) {
                                    try {
                                        ACLMessage resposta = new ACLMessage(ACLMessage.PROPOSE);
                                        resposta.addReceiver(msg.getSender());
                                        resposta.setContent("Incentivo");
                                        resposta.setContentObject(rec);
                                        myAgent.send(resposta);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        } else if (utilizadorNaArea.containsKey(nomeUtilizador)) { //Para remover o utilizador do map depois de ele ter saido da area
                            utilizadorNaArea.remove(nomeUtilizador);
                        }
                    }
                } else if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                    double ocupacao = ocupacaoEstacao.get(msg.getContent());
                    int biclas = (int)Math.floor(ocupacao*capacidadeEstacao);

                    synchronized (this) {
                        ocupacaoEstacao.put( msg.getContent() , (double) ((biclas + 1) / capacidadeEstacao) );
                    }
                   // System.out.println("He accepted");
                } else if(msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                    //System.out.println("He did not accepted");
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

    public HashMap<String,Double> getRecomendacoes() {
        HashMap<String,Double> toReturn = new HashMap<>();

        Iterator it = ocupacaoEstacao.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
            Double ocup = (Double) pair.getValue();
            if (ocup <= 0.20)
                toReturn.put((String) pair.getKey(),ocup);
        }
        return toReturn;
    }
}
