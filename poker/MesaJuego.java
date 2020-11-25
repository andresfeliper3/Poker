/* Autores: Jose David Barona Hernández - 1727590
 *                  Andrés Felipe Rincón    - 1922840
 * Correos: jose.david.barona@correounivalle.edu.co 
 *             andres.rincon.lopez@correounivalle.edu.co
 * Mini proyecto 3: Poker
 * Fecha: 25/11/2020
 * 
 * */
package poker;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

// TODO: Auto-generated Javadoc
/**
 * The Class MesaJuego.
 */
public class MesaJuego extends JPanel {
	public static final String RUTA_CARTA_TAPADA = "/resources/cardBack.png";
	private PanelJugador jugador1, jugador2, jugador3, jugador4, jugador5;
	private PanelJugador[] panelJugadores = new PanelJugador[5];
	private JLabel cartaTapada;
	private ArrayList<Integer>apuestasJugadores;
	
	/**
	 * Instantiates a new mesa juego.
	 *
	 * @param nombreJugadorHumano the nombre jugador humano
	 * @param nombresJugadoresSimulados the nombres jugadores simulados
	 * @param manosJugadores the manos jugadores
	 * @param apuestasJugadores the apuestas jugadores
	 */
	public MesaJuego(String nombreJugadorHumano, String[] nombresJugadoresSimulados, List<List<Carta>>manosJugadores, List<Integer> apuestasJugadores) {
		initGUI(nombreJugadorHumano, nombresJugadoresSimulados, manosJugadores, apuestasJugadores);
		this.setBackground(Color.GREEN);
		this.setBorder(new TitledBorder("Mesa"));	
	}
	
	/**
	 * Inits the GUI.
	 *
	 * @param nombreJugadorHumano the nombre jugador humano
	 * @param nombresJugadoresSimulados the nombres jugadores simulados
	 * @param manosJugadores the manos jugadores
	 * @param apuestasJugadores the apuestas jugadores
	 */
	private void initGUI(String nombreJugadorHumano, String[] nombresJugadoresSimulados, List<List<Carta>>manosJugadores, List<Integer> apuestasJugadores) {
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		jugador1 = new PanelJugador(nombresJugadoresSimulados[0], manosJugadores.get(0), apuestasJugadores.get(0), false);
		jugador2 = new PanelJugador(nombresJugadoresSimulados[1], manosJugadores.get(1), apuestasJugadores.get(1), false);
		jugador3 = new PanelJugador(nombresJugadoresSimulados[2], manosJugadores.get(2), apuestasJugadores.get(2),false);
		jugador4 = new PanelJugador(nombresJugadoresSimulados[3], manosJugadores.get(3), apuestasJugadores.get(3), false);
		jugador5 = new PanelJugador(nombreJugadorHumano, manosJugadores.get(4), apuestasJugadores.get(4), true);
		panelJugadores[0] = jugador1;
		panelJugadores[1] = jugador2;
		panelJugadores[2] = jugador3;
		panelJugadores[3] = jugador4;
		panelJugadores[4] = jugador5;
		
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		add(jugador1, constraints);
		constraints.gridx = 1;
		constraints.gridy = 0;
		add(jugador2, constraints);
		constraints.gridx = 2;
		constraints.gridy = 0;
		add(jugador3, constraints);
		constraints.gridx = 3;
		constraints.gridy = 1;
		add(jugador4, constraints);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		add(jugador5, constraints);
		cartaTapada = new JLabel(new ImageIcon(FileIO.readImageFile(this, RUTA_CARTA_TAPADA)));
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		add(cartaTapada, constraints);
		
		
	}
	
	/**
	 * Mesa actualizar.
	 * 
	 * Actualiza la mesa con el estado actual del juego.
	 *
	 * @param manosJugadores the manos jugadores
	 * @param ganador the ganador
	 * @param ronda the ronda
	 */
		public void mesaActualizar(List<List<Carta>> manosJugadores, int ganador,int ronda) {
			//Actualizar la mesa de juego una vez ha ganado un jugador
			if(ronda==2) {
				
					jugador1.recibirCartas(manosJugadores.get(0),"");
					jugador2.recibirCartas(manosJugadores.get(1),"");
					jugador3.recibirCartas(manosJugadores.get(2),"");
					jugador4.recibirCartas(manosJugadores.get(3),"");
					jugador5.recibirCartas(manosJugadores.get(4),"");

			}else {
				if(ganador==0) {
					jugador1.recibirCartas(manosJugadores.get(0),"Ganaste");
					jugador2.recibirCartas(manosJugadores.get(1),"Perdiste");
					jugador3.recibirCartas(manosJugadores.get(2),"Perdiste");
					jugador4.recibirCartas(manosJugadores.get(3),"Perdiste");
					jugador5.recibirCartas(manosJugadores.get(4),"Perdiste");
				}else {
					if(ganador==1) {
						jugador1.recibirCartas(manosJugadores.get(0),"Perdiste");
						jugador2.recibirCartas(manosJugadores.get(1),"Ganaste");
						jugador3.recibirCartas(manosJugadores.get(2),"Perdiste");
						jugador4.recibirCartas(manosJugadores.get(3),"Perdiste");
						jugador5.recibirCartas(manosJugadores.get(4),"Perdiste");
					}else  if(ganador==2){
						jugador1.recibirCartas(manosJugadores.get(0),"Perdiste");
						jugador2.recibirCartas(manosJugadores.get(1),"Perdiste");
						jugador3.recibirCartas(manosJugadores.get(2),"Ganaste");
						jugador4.recibirCartas(manosJugadores.get(3),"Perdiste");
						jugador5.recibirCartas(manosJugadores.get(4),"Perdiste");
					}else if(ganador==3) {
						jugador1.recibirCartas(manosJugadores.get(0),"Perdiste");
						jugador2.recibirCartas(manosJugadores.get(1),"Perdiste");
						jugador3.recibirCartas(manosJugadores.get(2),"Perdiste");
						jugador4.recibirCartas(manosJugadores.get(3),"Ganaste");
						jugador5.recibirCartas(manosJugadores.get(4),"Perdiste");
						
					}else {
						jugador1.recibirCartas(manosJugadores.get(0),"Perdiste");
						jugador2.recibirCartas(manosJugadores.get(1),"Perdiste");
						jugador3.recibirCartas(manosJugadores.get(2),"Perdiste");
						jugador4.recibirCartas(manosJugadores.get(3),"Perdiste");
						jugador5.recibirCartas(manosJugadores.get(4),"Ganaste");
					}
				}
			}
		}
	
	/**
	 * Gets the jugador humano.
	 * 
	 * Retorna el panelJugador del jugador humano.
	 *
	 * @return the jugador humano
	 */
	public PanelJugador getJugadorHumano() {
		return jugador5;
	}
	
	/**
	 * Gets the mano humano.
	 *
	 * @return the mano humano
	 */
	public List<Carta> getManoHumano(){
		
		return jugador5.getMano();
	}
	
	/**
	 * Sets the panel jugador.
	 * 
	 * Se encarga de establecer en pantalla la apuesta del jugador.
	 *
	 * @param jugador the jugador
	 * @param apuesta the apuesta
	 */
	
	public void setPanelJugador(int jugador, int apuesta) {
		panelJugadores[jugador].setValorApuesta(apuesta);
	}
	
	/**
	 * Reiniciar panel jugadores.
	 *
	 * Se encarga de reiniciar todos los paneles de los jugadores
	 *
	 * @param manosJugadores the manos jugadores
	 * @param apuestasJugadores the apuestas jugadores
	 */
	public void reiniciarPanelJugadores(List<List<Carta>> manosJugadores, List<Integer> apuestasJugadores) {
		//Reiniciar todos los panelJugadores
		for(int i = 0; i < panelJugadores.length; i++) {
			//Reiniciar manos
			panelJugadores[i].recibirCartas(manosJugadores.get(i), "Nueva Ronda");
			//Reiniciar apuestas
			panelJugadores[i].setValorApuesta(apuestasJugadores.get(i));
			panelJugadores[i].actualizarPanelApuesta();
		}
		
		
		
	}
 }
