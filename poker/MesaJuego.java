package poker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MesaJuego extends JPanel {
	
	public static final String CARTA_TAPADA_FILE="/resources/cardBack.png";
	
	private PanelJugador jugador1, jugador2, jugador3, jugador4, jugador5;
	private JLabel labelMazo;
	private Carta cartaComun;
	private JPanel comunes;
	
	public MesaJuego(String jugadorHumano, String[] jugadoresSimulados, List<List<Carta>> manosJugadores,Carta cartaComun) {
		
		jugador1 = new PanelJugador(jugadoresSimulados[0],manosJugadores.get(0),false);
		jugador2 = new PanelJugador(jugadoresSimulados[1],manosJugadores.get(1),false);
		jugador3 = new PanelJugador(jugadoresSimulados[2],manosJugadores.get(2),false);
		jugador4 = new PanelJugador(jugadoresSimulados[3],manosJugadores.get(3),false);
		
		
		//Jugador humano
		jugador5 = new PanelJugador(jugadorHumano,manosJugadores.get(4),true);
		
		initGUI(cartaComun);
		
		
		
	}

	private void initGUI(Carta cartaComun) {
		// TODO Auto-generated method stub
		this.setBackground(Color.GREEN);
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.gridwidth=1;
		constraints.fill=GridBagConstraints.CENTER;
		add(jugador1,constraints);
		
		constraints.gridx=1;
		constraints.gridy=0;
		constraints.gridwidth=1;
		constraints.fill=GridBagConstraints.CENTER;
		add(jugador2,constraints);
		
		constraints.gridx=2;
		constraints.gridy=0;
		constraints.gridwidth=1;
		constraints.fill=GridBagConstraints.CENTER;
		add(jugador3,constraints);
		
		constraints.gridx=3;
		constraints.gridy=1;
		constraints.gridwidth=1;
		constraints.fill=GridBagConstraints.CENTER;
		add(jugador4,constraints);
		
		constraints.gridx=1;
		constraints.gridy=2;
		constraints.gridwidth=2;
		constraints.fill=GridBagConstraints.CENTER;
		add(jugador5,constraints);
		
		labelMazo = new JLabel(new ImageIcon(FileIO.readImageFile(this, CARTA_TAPADA_FILE)));
		this.cartaComun = cartaComun;
		
		constraints.gridx=1;
		constraints.gridy=1;
		constraints.gridwidth=2;
		constraints.fill=GridBagConstraints.CENTER;
		add(labelMazo,constraints);
		
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
	//retorna la mano del jugador usuario
	public List<Carta> getManoHumano(){
		
		return jugador5.getMano();
	}

}
