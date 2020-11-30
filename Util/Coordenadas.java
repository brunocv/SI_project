package Util;

import java.io.Serializable;

public class Coordenadas implements Serializable {

    private int coordX;
    private int coordY;

    public Coordenadas(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public Coordenadas() {
        this.coordX = 0;
        this.coordY = 0;
    }

    public Coordenadas(Coordenadas c){
        this.coordX = c.getCoordX();
        this.coordY = c.getCoordY();
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;

        if((o == null) || (this.getClass() != o.getClass())) return false;

        Coordenadas c = (Coordenadas) o;
        return ((this.getCoordX() == c.getCoordX()) && (this.getCoordY() == c.getCoordY()));
    }

    @Override
    public String toString() {
      StringBuilder str = new StringBuilder();

      str.append("Coordenada X: " + this.coordX +" || Coordenada Y: "+ this.coordY +"\n");

      return str.toString();
    }

    @Override
    public Coordenadas clone(){
        return new Coordenadas(this);
    }
}