package poker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class MesaJuego extends JPanel {
	public static final String RUTA_CARTA_TAPADA = "/resources/cardBack.png";
	private PanelJugador jugador1, jugador2, jugador3, jugador4, jugador5;
	private JLabel cartaTapada;
	
	public MesaJuego(String nombreJugadorHumano, String[] nombresJugadoresSimulados, List<List<Carta>>manosJugadores, int apuestaInicial) {
		initGUI(nombreJugadorHumano, nombresJugadoresSimulados, manosJugadores, apuestaInicial);
		//this.setPreferredSize(new Dimension(700, 600));
		this.setBackground(Color.GREEN);
		this.setBorder(new TitledBorder("Mesa"));	
	}
	
	private void initGUI(String nombreJugadorHumano, String[] nombresJugadoresSimulados, List<List<Carta>>manosJugadores, int apuestaInicial) {
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		jugador1 = new PanelJugador(nombresJugadoresSimulados[0], manosJugadores.get(0), apuestaInicial, false);
		jugador2 = new PanelJugador(nombresJugadoresSimulados[1], manosJugadores.get(1), apuestaInicial, false);
		jugador3 = new PanelJugador(nombresJugadoresSimulados[2], manosJugadores.get(2), apuestaInicial,false);
		jugador4 = new PanelJugador(nombresJugadoresSimulados[3], manosJugadores.get(3), apuestaInicial, false);
		jugador5 = new PanelJugador(nombreJugadorHumano, manosJugadores.get(4), apuestaInicial, true);
		
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
 }
