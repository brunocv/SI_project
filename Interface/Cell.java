package Interface;

import Util.Coordenadas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Cell {

    private Coordenadas coord;
    private JLabel label;
    private int tamanho;
    private int tipo;

    public Cell(int tamanho){
        this.label = new JLabel();
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        this.label.setVerticalAlignment(SwingConstants.CENTER);
        this.label.setOpaque(true);

        this.tamanho = tamanho;
        this.tipo = 0; //chao
        this.image();
    }

    public void setCoordenadas(Coordenadas c){
        this.coord = c;
    }

    public JLabel getLabel(){
        return this.label;
    }

    public void image(){

        BufferedImage i = null;
        try {
            String s = "/home/dreamerz/IdeaProjects/Trabalho_SI/src/Images/floor.png";
            System.out.println("Deu");
            i = ImageIO.read(new File(s));
            System.out.println("fasfsaf");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image dimg = i.getScaledInstance(800/this.tamanho, 800/this.tamanho, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(dimg);

        //ImageIcon imageIcon = new ImageIcon(new ImageIcon("Images/floor.png").getImage().getScaledInstance(800/this.tamanho, 800/this.tamanho, Image.SCALE_DEFAULT));
        this.label.setIcon(imageIcon);
    }



}
