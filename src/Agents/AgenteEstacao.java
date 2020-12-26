package Agents;

import JadePlatform.DFManager;
import Util.Coordenadas;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// responsavel por saber quais os Agentes Utilizador na sua ́area, e dependendo das taxas de ocupacao das estacoes incentivar utilizadores apartirem para um novo destino
public class AgenteEstacao extends Agent {

    private Coordenadas posicaoEstacao;
    private int capacidadeEstacao;
    private int bicicletas;
    private List<Coordenadas> areaDeControlo;
    private Map<String,Coordenadas> utilizadorNaArea;
    private ConcurrentHashMap<String,Double> ocupacaoEstacao;
    private ConcurrentHashMap<String,Double> ocupacaoFutura;
    private int falhas;
    private List<String> listaDeEspera;
    private List<String> aCaminho; // Lista de utilizadores que têm esta estação como destino

    protected void setup(){
        super.setup();
        DFManager.registarAgente(this,"Estacao");
        System.out.println("Agente estacao entrou: " + getAID().getName());

        Object[] args = getArguments();
        posicaoEstacao = (Coordenadas) args[0];
        areaDeControlo = (List<Coordenadas>) args[1];
        ocupacaoEstacao = (ConcurrentHashMap<String, Double>) args[2];
        ocupacaoFutura = (ConcurrentHashMap<String, Double>) args[3];
        listaDeEspera = new ArrayList<>();
        aCaminho = new ArrayList<>();
        capacidadeEstacao = (int) args[4];
        bicicletas = capacidadeEstacao/2; // Random starting values, depois talvez seja melhor alterado dependendo do numero
                        // de estações que se vai ter. devido ao mapa ser dinamico.
        falhas = 0;
        utilizadorNaArea = new HashMap<>();

        this.addBehaviour(new ReceiveMessages());
        this.addBehaviour(new EntregaBicicletas(this,1000));
    }

    protected void takeDown(){
        super.takeDown();
        System.out.println("Agente estacao terminou: " + getAID().getName());
        DFManager.deRegister(this);

    }
        // Verifica tipo de mensagens recebidas
    private class ReceiveMessages extends CyclicBehaviour{

        public void action(){

            ACLMessage msg = receive();
            if(msg != null){

              //  System.out.println("ME:" + myAgent.getAID().getName()+ "-" + aCaminho.toString());
                if(msg.getPerformative() == ACLMessage.REQUEST){

                    parseRequests(msg);

                }else if(msg.getPerformative() == ACLMessage.INFORM){

                    parseInforms(msg);

                }else if(msg.getPerformative() == ACLMessage.SUBSCRIBE) {

                    parseSubscribe(msg);

                }else if(msg.getPerformative() == ACLMessage.CANCEL){

                    parseCancel(msg);

                }else if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){

                //    parseAcceptProposal(msg);

                } else if(msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                    //System.out.println("He did not accepted");
                }
            }
        }

        private void parseInforms(ACLMessage msg){

            if(msg.getContent().contains("Cheguei ao destino.")){
                String numUtilizador = msg.getSender().getName();
                int ind = numUtilizador.indexOf("@");
                String nomeUtilizador = numUtilizador.substring(0,ind);
                utilizadorNaArea.remove(nomeUtilizador);

                listaDeEspera.add(nomeUtilizador);
                aCaminho.remove(nomeUtilizador);

                String fullName = myAgent.getAID().getName();
                int index = fullName.indexOf("@"); // Vai procurar o indice da primeira occurencia de "@"
                //pois o Nome dos agentes é Estacao X@etc.etc.etc
                String nome = fullName.substring(0,index);

                double ocup = (double) ( bicicletas + listaDeEspera.size() )/capacidadeEstacao;
                ocupacaoEstacao.put(nome,ocup);
            }

            //Exemplo Mensagem::
            // Nova Posicao: &7$ %1! ?0.12< -0@ €7£
            // &PosicaoX$ %PosicaoY! ?PercentagemPercorrida< -IncentivoRecebido@ €EstacaoDestino£
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
                int index9 = mensagem.indexOf("€") + 1;
                int index10 = mensagem.indexOf("£");

                //String endereco = myAgent.getAID().getName();
                //int numero = Integer.parseInt(endereco.substring(endereco.indexOf(" ") + 1, endereco.indexOf("@")));
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

                        HashMap<String, Double> rec = new HashMap<>();

                        double val = ocupacaoEstacao.get(mensagem.substring(index9,index10));
                        if(val < 0.85 ) {
                            rec = getRecomendacoes(1);
                        }else{
                            rec = getRecomendacoes(2);
                        }
                        //^^Função q vai buscar as estações com menos de 20% de bicicletas restantes

                        if (rec.size() > 0) {
                            try {
                                ACLMessage resposta = new ACLMessage(ACLMessage.PROPOSE);
                                resposta.addReceiver(msg.getSender());
                                if(val < 0.85){
                                    resposta.setConversationId("Incentivo-1");
                                }else{
                                    resposta.setConversationId("Incentivo-2");
                                }
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
        }


        private void parseRequests(ACLMessage msg){

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

                else{ falhas++; resposta.setContent("0"); }

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
        }


        private void parseSubscribe(ACLMessage msg){
            String endereco = msg.getSender().getName();
            int index = endereco.indexOf("@");
            String nome = endereco.substring(0,index);

            aCaminho.add(nome);

            String fullName = myAgent.getAID().getName();
            int index2 = fullName.indexOf("@"); // Vai procurar o indice da primeira occurencia de "@"
            //pois o Nome dos agentes é Estacao X@etc.etc.etc
            String nome2 = fullName.substring(0,index2);

            double ocupFut = (double) ( bicicletas + listaDeEspera.size() + aCaminho.size() )/capacidadeEstacao;
            ocupacaoFutura.put(nome2,ocupFut);
        }


        private void parseCancel(ACLMessage msg){
            String endereco = msg.getSender().getName();
            int index = endereco.indexOf("@");
            String nome = endereco.substring(0,index);
            System.out.println("YO YO");
            aCaminho.remove(nome);

            String fullName = myAgent.getAID().getName();
            int index2 = fullName.indexOf("@"); // Vai procurar o indice da primeira occurencia de "@"
            //pois o Nome dos agentes é Estacao X@etc.etc.etc
            String nome2 = fullName.substring(0,index2);

            double ocupFut = (double) ( bicicletas + listaDeEspera.size() + aCaminho.size() )/capacidadeEstacao;
            ocupacaoFutura.put(nome2,ocupFut);
        }


        private void parseAcceptProposal(ACLMessage msg){

            double ocupacao = ocupacaoEstacao.get(msg.getContent());
            int biclas = (int)Math.floor(ocupacao*capacidadeEstacao);

            ocupacaoEstacao.put( msg.getContent() , (double) ((biclas + 1) / capacidadeEstacao) );

        }
    }

    private class EntregaBicicletas extends TickerBehaviour {
        public EntregaBicicletas(Agent a,long timeout) {super(a,timeout);}

        protected void onTick(){
            int diff = capacidadeEstacao - bicicletas;
            if(listaDeEspera.size() > 0 && diff > 0){

                String nomeUtilizador = listaDeEspera.get(0);
                listaDeEspera.remove(0);

                AID receiver = new AID();
                receiver.setLocalName(nomeUtilizador);
                ACLMessage aEnviar = new ACLMessage(ACLMessage.CONFIRM);
                aEnviar.addReceiver(receiver);
                aEnviar.setContent("Entrega Completa!");
                myAgent.send(aEnviar);

                bicicletas++;

                String fullName = myAgent.getAID().getName();
                int index = fullName.indexOf("@"); // Vai procurar o indice da primeira occurencia de "@"
                //pois o Nome dos agentes é Estacao X@etc.etc.etc
                String nome = fullName.substring(0,index);

                double ocup = (double) ( bicicletas + listaDeEspera.size() )/capacidadeEstacao;
                ocupacaoEstacao.put(nome,ocup);

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

    public HashMap<String,Double> getRecomendacoes(int tipo) {
        HashMap<String,Double> toReturn = new HashMap<>();

        Iterator it = ocupacaoEstacao.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
            String estacao = (String) pair.getKey();

            Double ocup = (Double) pair.getValue(); // Ocupação da Estação

            Double ocupFutura = ocupacaoFutura.get(estacao); // Ocupação da estação tendo em conta o numero de bicicletas que a têm como destino

            //  System.out.println("ME:"+ this.getLocalName() +"PASS AND A GO ::: " + ocupFutura);

            if ((ocup <= 0.25 * tipo) && (ocupFutura <= 0.75))
                toReturn.put((String) pair.getKey(),ocup);
        }
        return toReturn;
    }
}
