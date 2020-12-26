package Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Classe que contem as informações egerais do mapa
public class Mapa implements Serializable {

    private Map<Integer,Coordenadas> posicaoEstacoes;
    private int tamanho; // Tamanho do mapa
    private int estacoes; //estacoes * estacoes = total estacoes (nr de estações)
    private int bicicletas; //Numero maximo de bicicletas que uma estação pode ter
    private boolean escolhido; // Boolean que verifica se o tamanho do mapa e o numero de estações ja foi escolhido
    private int[][] zonas; // Array com as posicoes que cada estação controla

    // Construtor parametrizado que recebe as posicoes das estações, o tamanho do mapa, o numero de estações e um boolean que verifica se o tamanho do mapa e o numero de estações ja foi escolhido
    public Mapa(Map<Integer, Coordenadas> pE, int tm, int estacoes, int bc, boolean esc) {
        if(pE != null){
            int tamanho = pE.size();

            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(tamanho);

            for (Map.Entry<Integer, Coordenadas> mapEntry : pE.entrySet()) {
                this.posicaoEstacoes.put(mapEntry.getKey(), mapEntry.getValue().clone());
            }
            this.tamanho = tm;
            this.estacoes = estacoes;
            this.escolhido = esc;
            this.bicicletas = bc;
        }
        else{
            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(30);
            this.tamanho = 20;
            this.estacoes = 2;
            this.bicicletas = 10;
            this.escolhido = false;

        }
    }
    // Construtor de copia de Mapa
    public Mapa(Mapa m){
        if(m != null){
            int tamanho = m.getPosicaoEstacoes().size();

            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(tamanho);

            for (Map.Entry<Integer, Coordenadas> mapEntry : m.getPosicaoEstacoes().entrySet()) {
                this.posicaoEstacoes.put(mapEntry.getKey(), mapEntry.getValue().clone());
            }
            this.tamanho = m.getTamanho();
            this.estacoes = m.getEstacoes();
            this.escolhido = m.isEscolhido();
            this.bicicletas = m.getBicicletas();

        }
        else{
            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(30);
            this.tamanho = 20;
            this.estacoes = 2;
            this.bicicletas = 10;
            this.escolhido = false;

        }
    }
    // Construtor por omissão de Mapa
    public Mapa() {
        this.posicaoEstacoes = new HashMap<>(30);
        this.tamanho = 20;
        this.estacoes = 2;
        this.bicicletas = 10;
        this.escolhido = false;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getEstacoes() {
        return estacoes;
    }

    public int getBicicletas() { return bicicletas; }

    public boolean isEscolhido() {
        return escolhido;
    }

    public int[][] getZonas() {
        return zonas;
    }

    public Map<Integer, Coordenadas> getPosicaoEstacoes() {
        return posicaoEstacoes;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public void setEstacoes(int estacoes) {
        this.estacoes = estacoes;
    }

    public void setBicicletas(int bicicletas) { this.bicicletas = bicicletas; }

    public void setEscolhido(boolean escolhido) {
        this.escolhido = escolhido;
    }

    public void setZonas(int[][] zonas) {
        this.zonas = zonas;
    }

    public void setPosicaoEstacoes(Map<Integer, Coordenadas> posicaoEstacoes) {
        this.posicaoEstacoes = posicaoEstacoes;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (Map.Entry<Integer, Coordenadas> mapEntry : this.posicaoEstacoes.entrySet()) {
            str.append(mapEntry.getValue().toString() +" \n");
        }
        str.append("Tamanho: "+ this.tamanho + " Estações: " + this.estacoes +" Escolhido: "+ this.escolhido +" \n");
        return str.toString();
    }

    //ERRADO
    @Override
    public boolean equals(Object o){
        if(this == o) return true;

        if((o == null) || (this.getClass() != o.getClass())) return false;

        Mapa m = (Mapa) o;
        return (this.posicaoEstacoes.equals(m));
    }

    @Override
    public Mapa clone(){
        return new Mapa(this);
    }

    //Metodo que Aloca com 0's o mapa inicial para nao existiram valores nulos
    public void mapaMatrix(){

        this.zonas = new int[this.tamanho][this.tamanho];

        for(int i = 0; i < this.tamanho; i++) {
            for(int j = 0; j < this.tamanho; j++) {
                this.zonas[i][j] = 0;
            }
        }

    }

    //Metodo que gera as posições que cada estação cobre
    public void generateMapa(int y,int iteracao,int valor){

        int espaco = this.tamanho / this.estacoes;

        if(this.estacoes == 3 && (this.tamanho != 120)) espaco++;

        if(iteracao >= this.estacoes) return;

        for(int e = 0; e < this.estacoes ; e++){
            for(int i = e * espaco; i < espaco * (e +1) && i < this.tamanho; i ++){
                for(int j = y; j < espaco * (iteracao +1) && j < this.tamanho; j++){

                    this.zonas[i][j] = valor;
                }
            }
            Coordenadas c = new Coordenadas(((espaco/2) + (espaco*e)),((espaco*iteracao) + (espaco/2)));
            this.posicaoEstacoes.put(valor,c);
            valor++;
        }

        generateMapa((iteracao+1)*espaco,iteracao+1,valor);
    }

    //Metodo que imprime o mapa via texto
    public void mapaMatrixPrint(){

        for (int[] row : this.zonas) {
            for (int x : row) {
                if (x <= 9) System.out.print(x + "   ");
                else System.out.print(x + "  ");
            }
            System.out.print(" \n");
        }
    }
    // Metodo que imprime as coodernadas de cada estação
    public void printEstacoes(){
        for (Map.Entry<Integer, Coordenadas> mapEntry : this.posicaoEstacoes.entrySet()) {
            System.out.print("Estacao: "+mapEntry.getKey() + " Coordenadas: " + mapEntry.getValue().toString());
        }
    }

    public Coordenadas getCoordenadasDaEstacao(int numeroEstacao){
        return posicaoEstacoes.get(numeroEstacao).clone();
    }


    public Coordenadas getEstacaoDaArea(Coordenadas posicao) { return posicaoEstacoes.get(zonas[posicao.getCoordX()][posicao.getCoordY()]); }

    public String getNomeEstacao(Coordenadas posicao){
        int numero = zonas[posicao.getCoordX()][posicao.getCoordY()];
        return ("Estacao "+numero);
    }

    public List<Coordenadas> getZonasDaEstacao(int numeroEstacao){
        List<Coordenadas> toReturn = new ArrayList<>();
        for(int x=0 ; x<tamanho; x++)
            for(int y=0; y<tamanho; y++)
                if(zonas[x][y] == numeroEstacao)
                    toReturn.add(new Coordenadas(x,y));

        return  toReturn;
    }

}
