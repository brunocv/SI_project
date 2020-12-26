package Util;


public class Utilidade {
    // Metodo que calcula a distancia entre dois pontos
    public double distancia2pontos(int x1,int y1,int x2,int y2){
        return Math.sqrt( ( Math.pow((x1-x2),2) + Math.pow((y1-y2),2) ) );
    }
}
