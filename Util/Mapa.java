package Util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Mapa implements Serializable {

    private Map<Integer,Coordenadas> posicaoEstacoes;

    public Mapa(Map<Integer, Coordenadas> pE) {
        if(pE != null){
            int tamanho = pE.size();

            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(tamanho);

            for (Map.Entry<Integer, Coordenadas> mapEntry : pE.entrySet()) {
                this.posicaoEstacoes.put(mapEntry.getKey(), mapEntry.getValue().clone());
            }
        }
        else{
            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(10);
        }
    }

    public Mapa(Mapa m){
        if(m != null){
            int tamanho = m.getPosicaoEstacoes().size();

            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(tamanho);

            for (Map.Entry<Integer, Coordenadas> mapEntry : m.getPosicaoEstacoes().entrySet()) {
                this.posicaoEstacoes.put(mapEntry.getKey(), mapEntry.getValue().clone());
            }
        }
        else{
            this.posicaoEstacoes = new HashMap<Integer, Coordenadas>(10);
        }

    }

    public Mapa() {
        this.posicaoEstacoes = new HashMap<>(10);
    }

    public Map<Integer, Coordenadas> getPosicaoEstacoes() {
        return posicaoEstacoes;
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
}
