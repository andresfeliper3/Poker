package poker;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Carta extends JLabel{
	 private String valor;
	 private String palo;
	 private BufferedImage imagen;
	 private int valorNumerico; //para facilitar el cálculo

	public Carta(String valor, String palo) {
		 this.valor = valor;
		 this.palo = palo;

		 switch(valor) {
		   case "J": 
			   valorNumerico = 11;
			   break;
		   case "Q":
			   valorNumerico = 12;
			   break;
		   case "K": 
			   valorNumerico = 13;
			   break;
		   case "As": 
			   valorNumerico = 14;
			   break;
		   default: 
			   valorNumerico = Integer.parseInt(valor);
			   break;
		   } 
	 }
	//retorna el valor numérico de la carta
	 public int getValorNumerico() {
		 return valorNumerico;
	 }
	 //retorna el valor de la carta en tipo String
	 public String getValor() {
		 return valor;
	 }
	 //Retorna el valor del Palo de la carta
	 public String getPalo() {
		 return palo;
	 }
	 //retorna el valor numérico alterno de la carta As
	 public int getValorNumericoAlterno() {
		 //Valor alterno de la carta As (14 0 1)
		 if(valorNumerico==14) {
			 return 1;
		 }
		 else {
			 return valorNumerico;
		 }
	 }

	 //Para visualizar la carta a manera de String para hacer un seguimiento
	 public String toString() {
		 return valor+palo;
	 }
	 //Asigna la imagen de la carta
	 public void setImagen(BufferedImage imagen) {
		 this.imagen = imagen;
		 setIcon(new ImageIcon(imagen));
	 }
	 //Retorna el bufferedImage conrrespondiente a la carta
	 public BufferedImage getImagen() {
		return imagen;
	}
}
