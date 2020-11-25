/* Autores: Jose David Barona Hernández - 1727590
 *                  Andrés Felipe Rincón    - 1922840
 * Correos: jose.david.barona@correounivalle.edu.co 
 *             andres.rincon.lopez@correounivalle.edu.co
 * Mini proyecto 3: Poker
 * Fecha: 25/11/2020
 * 
 * */
package poker;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

// TODO: Auto-generated Javadoc
/**
 * The Class Carta.
 */
public class Carta extends JLabel{
	 private String valor;
	 private String palo;
	 private BufferedImage imagen;
	 private int valorNumerico;

	/**
	 * Instantiates a new carta.
	 *
	 * @param valor the valor
	 * @param palo the palo
	 */
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
	
	/**
	 * Gets the valor numerico.
	 *	retorna el valor numérico de la carta
	 * @return the valor numerico
	 */
	 public int getValorNumerico() {
		 return valorNumerico;
	 }
	 
 	/**
 	 * Gets the valor.
 	 * retorna el valor de la carta en tipo String
 	 * @return the valor
 	 */
	 public String getValor() {
		 return valor;
	 }
	 
 	/**
 	 * Gets the palo.
 	 * Retorna el valor del Palo de la carta
 	 * @return the palo
 	 */
	 public String getPalo() {
		 return palo;
	 }
	 
 	/**
 	 * Gets the valor numerico alterno.
 	 * retorna el valor numérico alterno de la carta As
 	 * @return the valor numerico alterno
 	 */
	 public int getValorNumericoAlterno() {
		 //Valor alterno de la carta As (14 0 1)
		 if(valorNumerico==14) {
			 return 1;
		 }
		 else {
			 return valorNumerico;
		 }
	 }

	 /**
 	 * To string.
 	 * Para visualizar la carta a manera de String para hacer un seguimiento
 	 * @return the string
 	 */
	 public String toString() {
		 return valor+palo;
	 }
	 
 	/**
 	 * Sets the imagen.
 	 * Asigna la imagen de la carta
 	 * @param imagen the new imagen
 	 */
	 public void setImagen(BufferedImage imagen) {
		 this.imagen = imagen;
		 setIcon(new ImageIcon(imagen));
	 }
	 
 	/**
 	 * Gets the imagen.
 	 * Retorna el bufferedImage conrrespondiente a la carta
 	 * @return the imagen
 	 */
	 public BufferedImage getImagen() {
		return imagen;
	}
}
