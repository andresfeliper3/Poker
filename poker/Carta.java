package poker;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Carta extends JLabel{
	 private String valor;
	 private String palo;
	 private BufferedImage imagen;
	 private int valorNumerico;

	public Carta(String valor, String palo) {
		 this.valor = valor;
		 this.palo = palo;

		 switch(valor) {
		   case "J": valorNumerico=11;break;
		   case "Q": valorNumerico=12;break;
		   case "K": valorNumerico=13;break;
		   case "As": valorNumerico=14;break;
		   default: valorNumerico = Integer.parseInt(valor);break;
		   } 
	 }

	 public int getValorNumerico() {
		 return valorNumerico;
	 }

	 public String getValor() {
		 return valor;
	 }

	public void setValor(String valor) {
		 this.valor = valor;
	 }

	 public String getPalo() {
		 return palo;
	 }

	 public void setPalo(String palo) {
		 this.palo = palo;
	 }

	 public String toString() {
		 return valor+palo;
	 }

	 public void setImagen(BufferedImage imagen) {
		 this.imagen=imagen;
		 setIcon(new ImageIcon(imagen));
	 }

	 public BufferedImage getImagen() {
		return imagen;
	}
}
