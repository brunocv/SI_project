package Interface;

import Util.Coordenadas;
import Util.Mapa;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapaUI {

    private Mapa mapa;
    private JPanel jpanel;
    private Cell cell[][];

    public MapaUI(Mapa mapa){
        this.mapa = mapa;
        this.jpanel = new JPanel();
        this.jpanel.setLayout(new GridLayout(this.mapa.getTamanho(), this.mapa.getTamanho()));

        this.jpanel.setBounds(20, 20, 1060, 960);
        this.cell = new Cell[this.mapa.getTamanho()][this.mapa.getTamanho()];

        initCell();
    }

    private void initCell(){
        for (int i = 0; i < this.mapa.getTamanho(); i++){
            for (int j = 0; j < this.mapa.getTamanho(); j++){

                cell[i][j] = new Cell(this.mapa.getTamanho());
                cell[i][j].setCoordenadas(new Coordenadas(i,j));
                jpanel.add(cell[i][j].getLabel());
            }
        }

       draw(1,mapa.getPosicaoEstacoes());
    }

    public void draw(int objeto, Map<Integer,Coordenadas> estacoes){
        for (Map.Entry<Integer, Coordenadas> mapEntry : estacoes.entrySet()) {
            Cell c = this.cell[mapEntry.getValue().getCoordX()][mapEntry.getValue().getCoordY()];
            c.setTipo(objeto);
            c.image();
        }

    }

    public void drawUtilizadores(List<Coordenadas> utilizadores){

        for(Coordenadas c : utilizadores){
            Cell ce = this.cell[c.getCoordY()][c.getCoordY()];
            ce.setTipo(2);
            ce.image();

        }
    }

    public JPanel getPanel(){
        return this.jpanel;
    }
}
