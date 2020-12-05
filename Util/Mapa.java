package Util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Mapa implements Serializable {

    private Map<Integer,Coordenadas> posicaoEstacoes;
    private int tamanho; //tamanho * 40 + 40 = total tamanho
    private int estacoes; //estacoes * estacoes = total estacoes
    private boolean escolhido;
    private int[][] zonas;

    public Mapa(Map<Integer, Coordenadas> pE, int tm, int estacoes, boolean esc) {
        if(pE != null){
            int tamanho = pE.size();

            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(tamanho);

            for (Map.Entry<Integer, Coordenadas> mapEntry : pE.entrySet()) {
                this.posicaoEstacoes.put(mapEntry.getKey(), mapEntry.getValue().clone());
            }
            this.tamanho = tm;
            this.estacoes = estacoes;
            this.escolhido = esc;

        }
        else{
            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(30);
            this.tamanho = 0;
            this.estacoes = 1;
            this.escolhido = false;

        }
    }

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

        }
        else{
            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(30);
            this.tamanho = 0;
            this.estacoes = 1;
            this.escolhido = false;

        }
    }

    public Mapa() {
        this.posicaoEstacoes = new HashMap<>(30);
        this.tamanho = 0;
        this.estacoes = 1;
        this.escolhido = false;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getEstacoes() {
        return estacoes;
    }

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

    public void mapaMatrix(){

        int alocar = (this.tamanho * 40) + 40;

        this.zonas = new int[alocar][alocar];

        for(int i = 0; i < alocar; i++) {
            for(int j = 0; j < alocar; j++) {
                this.zonas[i][j] = 0;
            }
        }

    }

    public void generateMapa(int y,int iteracao,int valor){

        int totalTamanho = (this.tamanho * 40) + 40;
        int espaco = totalTamanho / this.estacoes;

        if(this.estacoes == 3) espaco++;

        if(iteracao >= this.estacoes) return;
        System.out.println("Está a dar");

        for(int e = 0; e < this.estacoes ; e++){
            for(int i = e * espaco; i < espaco * (e +1) && i<totalTamanho; i ++){
                for(int j = y; j < espaco * (iteracao +1) && j<totalTamanho; j++){

                    this.zonas[i][j] = valor;
                }
            }
            valor++;
        }

        generateMapa((iteracao+1)*espaco,iteracao+1,valor);
    }

    public void mapaMatrixPrint(){

        for (int[] row : this.zonas) {
            for (int x : row) {
                if (x <= 9) System.out.print(x + "   ");
                else System.out.print(x + "  ");
            }
            System.out.print(" \n");
        }
    }
}
