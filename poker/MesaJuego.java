package poker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class MesaJuego extends JPanel {
	public static final String RUTA_CARTA_TAPADA = "/resources/cardBack.png";
	private PanelJugador jugador1, jugador2, jugador3, jugador4, jugador5;
	private PanelJugador[] panelJugadores = new PanelJugador[5];
	private JLabel cartaTapada;
	private ArrayList<Integer>apuestasJugadores;
	public MesaJuego(String nombreJugadorHumano, String[] nombresJugadoresSimulados, List<List<Carta>>manosJugadores, List<Integer> apuestasJugadores) {
		initGUI(nombreJugadorHumano, nombresJugadoresSimulados, manosJugadores, apuestasJugadores);
		//this.setPreferredSize(new Dimension(700, 600));
		this.setBackground(Color.GREEN);
		this.setBorder(new TitledBorder("Mesa"));	
	}
	
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
	//actualiza la mesa con el estado actual del juego
		public void mesaActualizar(List<List<Carta>> manosJugadores, int ganador) {
			//determinarResultado(manosJugadores);
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
	public PanelJugador getJugadorHumano() {
		return jugador5;
	}
	
	//Retorna la mano del jugador usuario
	public List<Carta> getManoHumano(){
		 System.out.println("Actualmente mi mazo es de tamaño: " + jugador5.getMano());
		return jugador5.getMano();
	}
	
	public void setPanelJugador(int jugador, int apuesta) {
		System.out.println("Mesa de juego, JUGADOR NUMERO " + jugador);
		//System.out.println("CON EL NOMBRE " + panelJugadores[jugador].getName());
		panelJugadores[jugador].setValorApuesta(apuesta);
	}
	
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
