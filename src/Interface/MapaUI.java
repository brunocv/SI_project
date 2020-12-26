package Interface;

import Util.Coordenadas;
import Util.Mapa;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapaUI {

    private Mapa mapa; // Mapa com posicoes e nr de estações, numero maximo de users, tamanho
    private JPanel jpanel;
    private Cell cell[][]; // Celulas do mapa (cada posicao)
    private List<Coordenadas> posicoesAntigas;

    // Construtor parametrizado
    public MapaUI(Mapa mapa){
        this.mapa = mapa;
        this.jpanel = new JPanel();
        this.jpanel.setLayout(new GridLayout(this.mapa.getTamanho(), this.mapa.getTamanho()));

        this.jpanel.setBounds(20, 20, 1060, 960);
        this.cell = new Cell[this.mapa.getTamanho()][this.mapa.getTamanho()];
        this.posicoesAntigas = new ArrayList<>(200);
        initCell();
    }
    // inicia todas as celulas com
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
    // Desenha para cada posicao a imagem definida
    public void draw(int objeto, Map<Integer,Coordenadas> estacoes){
        for (Map.Entry<Integer, Coordenadas> mapEntry : estacoes.entrySet()) {
            Cell c = this.cell[mapEntry.getValue().getCoordX()][mapEntry.getValue().getCoordY()];
            c.setTipo(objeto);
            c.image();
        }

    }
    // Desenha nas posicoes dos utilizadores a imagem correspondente aos utilizadores
    public void drawUtilizadores(List<Coordenadas> utilizadores){

        for(Coordenadas c : this.posicoesAntigas){
            Cell ce = this.cell[c.getCoordX()][c.getCoordY()];
            ce.setTipo(0);
            ce.image();
        }

        clean();

        for(Coordenadas c : utilizadores){
            Cell ce = this.cell[c.getCoordX()][c.getCoordY()];
            ce.setTipo(2);
            ce.image();

        }

        setPosicoesAntigas(utilizadores);
    }

    public JPanel getPanel(){
        return this.jpanel;
    }

    public void clean(){
        this.posicoesAntigas.clear();
    }

    public void setPosicoesAntigas(List<Coordenadas> utilizadores){

        for(Coordenadas c : utilizadores){
            this.posicoesAntigas.add(c.clone());
        }
    }
}
