package Interface;

import Util.Coordenadas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Cell {

    private Coordenadas coord; // Tem a posicao x e y de cada celula do mapa
    private JLabel label;
    private int tamanho;// Tamanho do mapa
    private int tipo; // tipo de celula

    // Construtor parametrizado
    public Cell(int tamanho){
        this.label = new JLabel();
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        this.label.setVerticalAlignment(SwingConstants.CENTER);
        this.label.setOpaque(true);

        this.tamanho = tamanho;
        this.tipo = 0; //chao
        this.image();
    }
    // metodo para alterar tipo de imagem
    public void setTipo(int tipo){

        this.tipo = tipo;

    }

    public void setCoordenadas(Coordenadas c){
        this.coord = c;
    }

    public JLabel getLabel(){
        return this.label;
    }
    // metodo que verifica qual o tipo de imagem a pintar
    public void image(){
        String s = "Images/floor.png";
        if(this.tipo == 0) s = "Images/floor.png";
        if(this.tipo == 1) s = "Images/station.png";
        if(this.tipo == 2) s = "Images/bycicle.png";

        BufferedImage i = null;
        try {

            i = ImageIO.read(new File(s));

        } catch (IOException e) {
            e.printStackTrace();
        }

        Image dimg = i.getScaledInstance(960/this.tamanho, 960/this.tamanho, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(dimg);

        this.label.setIcon(imageIcon);
    }



}
