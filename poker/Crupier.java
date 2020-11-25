/* Autores: Jose David Barona Hernández - 1727590
 *                  Andrés Felipe Rincón    - 1922840
 * Correos: jose.david.barona@correounivalle.edu.co 
 *             andres.rincon.lopez@correounivalle.edu.co
 * Mini proyecto 3: Poker
 * Fecha: 25/11/2020
 * 
 * */
package poker;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


// TODO: Auto-generated Javadoc
/**
 * The Class Crupier.
 */
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

	private List<List<Carta>> manosJugadores = new ArrayList<List<Carta>>();
	private List<Integer> puntajes = new ArrayList<Integer>();
	private Carta estaCarta;
	private int valorJugada;

	/**
	 * Valores numericos.
	 * 
	 * Le entra una lista con el mazo y retorna una lista con los valores numéricos
	 * correspondientes a la cartas del mazo.
	 *
	 * @param mazo the mazo
	 * @return the list
	 */
	private List<Integer> valoresNumericos(List<Carta> mazo) {
		List<Integer> valores = new ArrayList<Integer>();
		for (Carta carta : mazo) {
			valores.add(carta.getValorNumerico());
		}
		return valores;
	}

	/**
	 * Mismo palo.
	 * 
	 * Retorna true si todas las cartas pertenencen al mismo palo, false en caso contrario.
	 *
	 * @param mazo the mazo
	 * @return true, if successful
	 */
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

	/**
	 * Checks if is escalera real.
	 *
	 *	Retorna true si el mazo es una escalera real, false en caso contrario.
	 * @param mazo the mazo
	 * @return true, if is escalera real
	 */
	private boolean isEscaleraReal(List<Carta> mazo) {
		// Revisar que todas sean del mismo palo
		if (!mismoPalo(mazo))
			return false;
		List<Integer> valores = valoresNumericos(mazo);
		if (Collections.max(valores) == 14 && Collections.min(valores) == 10) {
			return true;
		}
		return false;
	}

	
	/**
	 * Checks if is poker.
	 *
	 * Retorna true si el mazo es un poker, falso en caso contrario.
	 *
	 * @param mazo the mazo
	 * @return true, if is poker
	 */
	private boolean isPoker(List<Carta> mazo) {
		int cant_cartasIguales = 0;
		for (int i = 0; i < mazo.size(); i++) {
			estaCarta = mazo.get(i);
			cant_cartasIguales = 0;
			for (int j = 0; j < mazo.size(); j++) {
				if (estaCarta.getValorNumerico() == mazo.get(j).getValorNumerico()) {
					cant_cartasIguales++;
				}
				if (cant_cartasIguales == 4) {
					valorJugada = estaCarta.getValorNumerico();
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if is escalera.
	 * 
	 * Revisa si el jugador posee una escalera en sus cartas, donde se cumple que si la carta de mayor 
	 * valor menos la carta de menor valor es igual a 4, entonces posee una escalera
	 *
	 * @param mazo the mazo
	 * @return true, if is escalera
	 */

    private boolean isEscalera(List<Carta> mazo) {
        List<Integer> valores = new ArrayList<Integer>();
        List<Integer> valoresAlternos = new ArrayList<Integer>();
        for(Carta carta : mazo) {
            valores.add(carta.getValorNumerico());
        }
        for(Carta carta : mazo) {
            valoresAlternos.add(carta.getValorNumericoAlterno());
        }
        
        Collections.sort(valores);
        for(int i=0;i<valores.size()-1;i++) {
	        if(valores.get(i)+1 != valores.get(i+1)) {
	            return false;
	        }
        }
	     Collections.sort(valoresAlternos);
	        for(int i=0;i<valoresAlternos.size()-1;i++) {
		        if(valoresAlternos.get(i)+1 != valoresAlternos.get(i+1)) {
		            return false;
		        }
	        }
        valorJugada = Collections.max(valoresNumericos(mazo));
        return true;

    }
	
	/**
	 * Checks if is escalera color.
	 * 
	 * Retorna true si el mazo es una escalera color, false en caso contrario.
	 * La carta As puede forma escalera color con el rey o con el 2.
	 *
	 * @param mazo the mazo
	 * @return true, if is escalera color
	 */
	
	private boolean isEscaleraColor(List<Carta> mazo) {
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
			valorJugada = Collections.max(valoresNumericos(mazo));
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if is doble pareja.
	 * 
	 * Retorna true si el mazo es una doble pareja, false en caso contrario.
	 *
	 * @param mazo the mazo
	 * @return true, if is doble pareja
	 */
    private boolean isDoblePareja(List<Carta> mazo){
    	List<Carta> auxiliar = new ArrayList<Carta>();
    	if(isPareja(mazo)) {
    		for(Carta carta : mazo) {
	    			if(valorReferenciaPareja != carta.getValorNumerico()) {
	    				auxiliar.add(carta);
	    			}
    			}
			if(isPareja(auxiliar)) 
				return true;
    	}   
        return false;
    }
    int valorReferenciaPareja;
  
  /**
   * Checks if is full.
   *
   * Retirna true si el mazo es un full, false en caso contrario.
   *
   * @param mazo the mazo
   * @return true, if is full
   */
    public boolean isFull(List<Carta> mazo){
        ArrayList<Carta> auxiliar = new ArrayList<Carta>();
        if(isTrio(mazo)) {
            for(Carta carta : mazo) {
                if(valorReferenciaTrio != carta.getValorNumerico()) {
                    auxiliar.add(carta);
                }
            }
            if(isPareja(auxiliar)) {
                return true;
            }

        }
        return false;
    }
	
	/**
	 * Checks if is color.
	 *
	 *	Retorna true si es un color, false en caso contrario.
	 *
	 * @param mazo the mazo
	 * @return true, if is color
	 */
	private boolean isColor(List<Carta> mazo) {
		String palo = mazo.get(0).getPalo();
		
		for (Carta carta : mazo) {
			if (palo != carta.getPalo()) {
				return false;
			}
		}
		valorJugada = Collections.max(valoresNumericos(mazo));
		return true;
	}

	int valorReferenciaTrio;
	
	/**
	 * Checks if is trio.
	 * 
	 * Retorna true si el mazo es un trio, false en caso contrario.
	 *
	 * @param mazo the mazo
	 * @return true, if is trio
	 */
	private boolean isTrio(List<Carta> mazo) {
		int cartasIguales = 0;
		for (int carta = 0; carta < mazo.size(); carta++) {
			valorReferenciaTrio = mazo.get(carta).getValorNumerico();
			cartasIguales = 0;
			for (int cartaComparar = 0; cartaComparar < mazo.size(); cartaComparar++) {
				if (valorReferenciaTrio == mazo.get(cartaComparar).getValorNumerico()) {
					cartasIguales++;
				}
				if (cartasIguales == 3) {
					valorJugada = valorReferenciaTrio;
					return true;
				}
			}
		}
		return false;
	}

	
	/**
	 * Checks if is pareja.
	 * 
	 * Retorna true si el mazo es una pareja, false en caso contrario.
	 *
	 * @param mazo the mazo
	 * @return true, if is pareja
	 */
	private boolean isPareja(List<Carta> mazo) {
		int cant_cartasIguales = 0;
		for (int carta = 0; carta < mazo.size(); carta++) {
			valorReferenciaPareja = mazo.get(carta).getValorNumerico();
			cant_cartasIguales = 0;
			for (int cartaComparar = 0; cartaComparar < mazo.size(); cartaComparar++) {
				if (valorReferenciaPareja == mazo.get(cartaComparar).getValorNumerico()) {
					cant_cartasIguales++;
				}
				if (cant_cartasIguales == 2) {
					valorJugada = valorReferenciaPareja;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Carta mas alta.
	 * 
	 * Retorna el valor numérico de la carta mas alta del mazo.
	 *
	 * @param mazo the mazo
	 * @return the int
	 */
	private int cartaMasAlta(List<Carta> mazo) {
		int valor_cartaMasAlta = 0;
		List<Integer> valores = new ArrayList<Integer>();
		for (Carta carta : mazo) {
			valores.add(carta.getValorNumerico());
		}
		valor_cartaMasAlta = Collections.max(valores);
		return valor_cartaMasAlta;
	}
	
	/**
	 * Gets the valor max jugada.
	 *
	 * @return the valor max jugada
	 */
	public int getValorMaxJugada() {
		
		return valorJugada;
	}

	/**
	 * Ejecutar.
	 *
	 *	Recibe un mazo y retorna la constante de clase correspondiente a la jugada que tiene.
	 *
	 * @param mazoJugador the mazo jugador
	 * @return the int
	 */
	public int ejecutar(List<Carta> mazoJugador) {
		valorReferenciaTrio=0;
		valorReferenciaPareja=0;
		
		//Guardamos cada uno de los mazos en una lista de mazos
		this.manosJugadores.add(mazoJugador);
	
		if(isEscaleraReal(mazoJugador)) {
			puntajes.add(1);
			return ESCALERA_REAL;
		}else if(isPoker(mazoJugador)) {
			puntajes.add(2);
			return POKER;
		}else if(isEscaleraColor(mazoJugador)) {
			puntajes.add(3);
			return ESCALERA_COLOR;
		}else if(isFull(mazoJugador)) {
			puntajes.add(4);
			return FULL;
		}else if(isColor(mazoJugador)) {
			puntajes.add(5);
			return COLOR;
		}else if(isEscalera(mazoJugador)) {
			puntajes.add(6);
			return ESCALERA;
		}else if(isTrio(mazoJugador)) {
			puntajes.add(7);
			return TRIO;
		}else if(isDoblePareja(mazoJugador)) {
			puntajes.add(8);
			return DOBLE_PAREJA;
		}else if(isPareja(mazoJugador)) {
			puntajes.add(9);
			return PAREJA;
		}else {
			puntajes.add(10);
			return cartaMasAlta(mazoJugador);
		}
	}
	
}