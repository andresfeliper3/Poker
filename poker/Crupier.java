package poker;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class Crupier {

	public static final int ESCALERA_REAL = 1;
	public static final int POKER = 2;
	public static final int ESCALERA_COLOR = 3;
	public static final int FULL = 4;
	public static final int COLOR = 5;
	public static final int ESCALERA = 6;
	public static final int TRIO = 7;
	public static final int DOBLE_PAREJA = 8;
	public static final int PAREJA = 9;
	public static final int CARTA_MAS_ALTA = 10;

	private Baraja baraja = new Baraja();

	private List<Carta> mazo1 = new ArrayList<Carta>();
	private int valorParaGanar = 0; 
	
	private Carta estaCarta;

	public Crupier() {
		// Escalera real

	}

	private List<Integer> valoresNumericos(List<Carta> mazo) {
		List<Integer> valores = new ArrayList<Integer>();
		for (Carta carta : mazo) {
			valores.add(carta.getValorNumerico());
		}
		return valores;
	}

	private boolean mismoPalo(List<Carta> mazo) {
		String palo = mazo.get(0).getPalo();
		for (Carta carta : mazo) {
			// Si alguna carta tiene un palo diferente, no es escalera real
			if (carta.getPalo() != palo) {
				return false;
			}
		}
		return true;
	}

	// Retorna true si el mazo es una escalera real
	private boolean isEscaleraReal(List<Carta> mazo) {
		// Revisar que todas sean del mismo palo
		System.out.println("Revisar si tiene ESCALERA REAL");
		if (!mismoPalo(mazo))
			return false;
		List<Integer> valores = valoresNumericos(mazo);
		if (Collections.max(valores) == 14 && Collections.min(valores) == 10) {
			return true;
		}
		return false;
	}

	// Revisa si el jugador tiene un Poker, recorriendo cada carta y revisando que
	// tenga cuatro veces el mismo valor de una carta
	private boolean isPoker(List<Carta> mazo) {
		System.out.println("Revisar si tiene POKER");
		int cant_cartasIguales = 0;
		for (int i = 0; i < mazo.size(); i++) {
			estaCarta = mazo.get(i);
			cant_cartasIguales = 0;
			for (int j = 0; j < mazo.size(); j++) {

				if (estaCarta.getValorNumerico() == mazo.get(j).getValorNumerico()) {
					cant_cartasIguales++;
				}
				if (cant_cartasIguales == 4) {
					valorParaGanar = 0;
					return true;
				}
			}
		}
		return false;
	}
	//Revisa si el jugador posee una escalera en sus cartas, donde se cumple que si la carta de mayor valor menos la carta de menor valor es igual a 4, entonces posee una escalera
    private boolean isEscalera(List<Carta> mazo) {
    	System.out.println("Revisar si tiene ESCALERA");
        List<Integer> valores = new ArrayList<Integer>();
        List<Integer> valoresAlternos = new ArrayList<Integer>();
        for(Carta carta : mazo) {
            valores.add(carta.getValorNumerico());
            System.out.println("Normal: " + carta.getValorNumerico());
        }
        for(Carta carta : mazo) {
            valoresAlternos.add(carta.getValorNumericoAlterno());
            System.out.println("Alterno: " + carta.getValorNumericoAlterno());
        }
        if(Collections.max(valores)-Collections.min(valores) == 4) {
            return true;
        }
        else if(Collections.max(valoresAlternos)-Collections.min(valoresAlternos) == 4) {
            return true;
        }

        return false;
    }
	// Retorna true si el mazo es una escalera color
	private boolean isEscaleraColor(List<Carta> mazo) {
		System.out.println("Revisar si tiene ESCALERA COLOR");
		if (!mismoPalo(mazo))
			return false;
		List<Integer> valores = valoresNumericos(mazo);
		List<Integer> valoresAlternos = new ArrayList<Integer>();
		for (Carta carta : mazo) {
			valoresAlternos.add(carta.getValorNumericoAlterno());
		}
		int diferencia = 4;
		// As formando escaleras con rey y con 2
		if (Collections.max(valores) - Collections.min(valores) == diferencia
				|| Collections.max(valoresAlternos) - Collections.min(valoresAlternos) == diferencia) {
			return true;
		}
		return false;
	}

	//Doble pareja
    private boolean isDoblePareja(List<Carta> mazo){
    	System.out.println("Revisar si tiene DOBLE PAREJA");
    	if(isPareja(mazo)) {
    		
    		for(Carta carta : mazo) {
    			if(valorReferenciaPareja == carta.getValorNumerico()) {
    				mazo.remove(carta);
    			}
    			if(isPareja(mazo)) {
    				return true;
    			}
    		}
    	}   
        return false;
    }
    int valorReferenciaPareja;
	//Retorna true si la mano es un full
    private boolean isFull(List<Carta> mazo){
    	System.out.println("Revisar si tiene FULL");
    	if(isTrio(mazo)) {
    		for(Carta carta : mazo) {
    			if(valorReferenciaPareja == carta.getValorNumerico()) {
    				mazo.remove(carta);
    			}
    			if(isPareja(mazo)) {
    				return true;
    			}
    		}
    	}   
        return false;
    }
	// Retorna true si el mazo es un color
	private boolean isColor(List<Carta> mazo) {
		System.out.println("Revisar si tiene COLOR");
		String palo = mazo.get(0).getPalo();
		for (Carta carta : mazo) {
			if (palo != carta.getPalo()) {
				return false;
			}
		}
		return true;
	}

	int valorReferenciaTrio;
	// Retorna true si el mazo es un trío
	private boolean isTrio(List<Carta> mazo) {
		System.out.println("Revisar si tiene TRIO");
		int cartasIguales = 0;
		for (int carta = 0; carta < mazo.size(); carta++) {
			valorReferenciaTrio = mazo.get(carta).getValorNumerico();
			cartasIguales = 0;
			for (int cartaComparar = 0; cartaComparar < mazo.size(); cartaComparar++) {
				if (valorReferenciaTrio == mazo.get(cartaComparar).getValorNumerico()) {
					cartasIguales++;
				}
				if (cartasIguales == 3) {

					return true;
				}
			}
		}
		return false;
	}

	// Revisa si el mazo del jugador tiene una pareja, es decir; dos cartas con
	// igual valor numérico

	private boolean isPareja(List<Carta> mazo) {
		System.out.println("Revisar si tiene PAREJA: " + mazo.size());
		int cant_cartasIguales = 0;
		for (int i = 0; i < mazo.size(); i++) {
			estaCarta = mazo.get(i);
			cant_cartasIguales = 0;
			for (int j = 0; j < mazo.size(); j++) {
				if (estaCarta.getValorNumerico() == mazo.get(j).getValorNumerico()) {
					cant_cartasIguales++;
				}
				if (cant_cartasIguales == 2) {
					return true;
				}
			}
		}
		return false;
	}

	private int cartaMasAlta(List<Carta> mazo) {

		int valor_cartaMasAlta = 0;

		List<Integer> valores = new ArrayList<Integer>();
		for (Carta carta : mazo) {
			valores.add(carta.getValorNumerico());
		}
		valor_cartaMasAlta = Collections.max(valores);
		return valor_cartaMasAlta;
	}

	public int ejecutar(List<Carta> mazoJugador) {
		
		this.mazo1 = mazoJugador;
	
		if(isEscaleraReal(mazoJugador)) {
			
			return ESCALERA_REAL;
		}else if(isPoker(mazoJugador)) {
			
			return POKER;
		}else if(isEscaleraColor(mazoJugador)) {
			
			return ESCALERA_COLOR;
		}else if(isFull(mazoJugador)) {
			
			return FULL;
		}else if(isColor(mazoJugador)) {
			
			return COLOR;
		}else if(isEscalera(mazoJugador)) {
			
			return ESCALERA;
		}else if(isTrio(mazoJugador)) {
			
			return TRIO;
		}else if(isDoblePareja(mazoJugador)) {
			
			return DOBLE_PAREJA;
		}else if(isPareja(mazoJugador)) {

			return PAREJA;
		}else {
			System.out.println("NO TIENE JUEGO, SU VALOR DE JUEGO ES POR SU CARTA MÁS ALTA: "+cartaMasAlta(mazoJugador) );
			return CARTA_MAS_ALTA;
		}
	}
	
}