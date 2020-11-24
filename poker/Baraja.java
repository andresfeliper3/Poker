package poker;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Baraja {
	   public static final String RUTA_FILE = "/resources/cards.png";
	   public static final int CARD_WIDTH = 45;
	   public static final int CARD_HEIGHT = 60;
	   private static final int PALOS = 4;
	   private static final int VALORES = 13;
	  
	   //El ArrayList no es seguro para trabajar con hilos
	   
	   private ArrayList<Carta> mazo;
	 
	   private Random random;
	   
	   public Baraja() {
		   random = new Random();
		   mazo = new ArrayList<Carta>();
		   String valor;
		   for(int palo = 1; palo <= PALOS; palo++) {
			   for(int carta = 2; carta <= VALORES+1; carta++) {
				   //En ese orden debido a la imagen
				   switch(carta) {
				   case 11: 
					   valor = "J";
					   break;
				   case 12: 
					   valor = "Q";
					   break;
				   case 13: 
					   valor = "K";
					   break;
				   case 14: 
					   valor = "As";
					   break;
				   default: 
					   valor = String.valueOf(carta);
					   break;
				   } 
				   switch(palo) {
				   case 1: 
					   mazo.add(new Carta(valor,"Corazones"));
					   break;
				   case 2: 
					   mazo.add(new Carta(valor,"Diamantes"));
					   break;
				   case 3: 
					   mazo.add(new Carta(valor,"Picas"));
					   break;
				   case 4: 
					   mazo.add(new Carta(valor,"Treboles"));
					   break;
				   }
			   }
		   }
		   asignarImagen();
	   }
	   
	   //Asigna las porciones de la imagen a cada carta según su respectivo palo y valor
	   private void asignarImagen() {   	   
		   BufferedImage cardsImage = FileIO.readImageFile(this, RUTA_FILE);
		   //Índice del mazo
			int index = 0;
			//partición de la imagen
		    for(int palo = 0; palo < PALOS; palo++) {
		      for(int valor = 0; valor < VALORES; valor++) {
		          int x = valor * CARD_WIDTH;
		          int y = palo * CARD_HEIGHT;
		          BufferedImage subImagen = cardsImage.getSubimage(x, y, CARD_WIDTH, CARD_HEIGHT);
		          mazo.get(index).setImagen(subImagen);
		          index++;
		      } 
		     }		    
	  }
	   //Saca carta aleatoria del mazo
	   public Carta getCarta() {
		   int index = random.nextInt(mazoSize());
		   Carta carta = mazo.get(index);
		   mazo.remove(index); //elimina del mazo la carta usada
		   return carta;
	   }
	   //Retorna el tamaño del mazo
	   public int mazoSize() {
		   return mazo.size();
	   }
}
