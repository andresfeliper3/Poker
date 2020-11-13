package poker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import java.util.List;

public class VistaGUIPoker extends JFrame {
	
	private JPanel zonaJuego, zonaDinero, zonaApuesta, panelBotones;
	private JTextArea panelRegistros;
	private MesaJuego mesaJuego;
	private Titulos titulo;
	private JButton descartar, aumentar, igualar, retirarse;
	private List<Integer> apuestasJugadores;
	private String nombreJugadorHumano;
	private String[] nombresJugadoresSimulados;
	
	private Escucha escucha;
	private ControlPoker controlPoker;
	
	public VistaGUIPoker(String[] nombresJugadoresSimulados, List<List<Carta>> manosJugadores, List<Integer> apuestasJugadores, ControlPoker controlPoker) {
		this.apuestasJugadores = apuestasJugadores;
		this.controlPoker = controlPoker;
		initGUI(nombresJugadoresSimulados, manosJugadores, apuestasJugadores);
		this.setTitle("Póker clásico");
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initGUI(String[] nombresJugadoresSimulados, List<List<Carta>> manosJugadores, List<Integer> apuestasJugadores) {
		//String nombreJugadorHumano = JOptionPane.showInputDialog(this, "Escribe tu nombre");
		nombreJugadorHumano = "yolas";
		this.nombresJugadoresSimulados = nombresJugadoresSimulados;
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		//Escucha
		escucha = new Escucha();
		
		//Titulos
		titulo = new Titulos("Póker Clásico", 40, Color.BLACK);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(titulo, constraints);
		
		//Zona de juego
		zonaJuego = new JPanel(new BorderLayout());
		//zonaJuego.setPreferredSize(new Dimension(800,500));
		zonaJuego.setBackground(Color.WHITE);
		zonaJuego.setBorder(new TitledBorder("Zona de juego"));
		constraints.gridx= 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		add(zonaJuego, constraints);
		//Zona dinero (dentro de zonaJuego)
		zonaDinero = new JPanel();
		//zonaDinero.setPreferredSize(new Dimension(100,100));
		zonaDinero.setBorder(new TitledBorder("Dinero"));
		zonaDinero.setBackground(Color.yellow);
		zonaJuego.add(zonaDinero, BorderLayout.NORTH);
		//Mesa de juego (dentro de zonaJuego)
		mesaJuego = new MesaJuego(nombreJugadorHumano, nombresJugadoresSimulados, manosJugadores, apuestasJugadores);
		zonaJuego.add(mesaJuego, BorderLayout.CENTER);
		//Zona apuesta (dentro de zonaJuego
		zonaApuesta = new JPanel();
		//zonaApuesta.setPreferredSize(new Dimension(100,100));
		zonaApuesta.setBorder(new TitledBorder("Apuestas"));
		zonaJuego.add(zonaApuesta, BorderLayout.SOUTH);
		
		//Panel de registros
		panelRegistros = new JTextArea();
		panelRegistros.setEditable(false);
		panelRegistros.setPreferredSize(new Dimension(300,450));
		panelRegistros.setBorder(new TitledBorder("Registros del juego"));
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.VERTICAL;
		//Primer mensaje
		editarRegistros(0, "", -1, -1);
		add(panelRegistros, constraints);
		
		//Panel de botones
		panelBotones = new JPanel();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(panelBotones, constraints);
		//botones
		descartar = new JButton("Descartar");
		descartar.addActionListener(escucha);
		panelBotones.add(descartar);
		aumentar = new JButton("Aumentar");
		aumentar.addActionListener(escucha);
		panelBotones.add(aumentar);
		igualar = new JButton("Igualar");
		igualar.addActionListener(escucha);
		panelBotones.add(igualar);
		retirarse = new JButton("Retirarse");
		retirarse.addActionListener(escucha);
		panelBotones.add(retirarse);
		
	}
	
	public void editarRegistros(int fase, String nombre, int apuesta, int operacion) {
		if(fase == 0) {
		//Apuesta inicial y se escoge el jugador mano 
	
			panelRegistros.append("Todos tienen una apuesta inicial de " + apuestasJugadores.get(0) + ".\n");
			String nombreJugadorMano;
			//Apuesta inicial
			if(controlPoker.getIdJugadorMano() == 5) {
				nombreJugadorMano = nombreJugadorHumano;
			}
			else {
				nombreJugadorMano = nombresJugadoresSimulados[controlPoker.getIdJugadorMano() - 1]; //-1 porque el id del jugador va de 1-4
			}
			panelRegistros.append("El jugador mano escogido al azar fue " + nombreJugadorMano + ".\n");
		
		}
		//Ronda de apuestas
		else if(fase == 1) {		
			/*Operacion
			 * 0: igualar
			 * 1: aumentar
			 * 2: retirarse
			 * */
			switch(operacion) {
				case 0:
					panelRegistros.append("El jugador " + nombre + " igualó a $" + apuesta + ".\n");
					break;
				case 1:
					panelRegistros.append("El jugador " + nombre + " aumentó a $" + apuesta + ".\n");
					break;
				case 2:
					panelRegistros.append("El jugador " + nombre + " se retiró.\n");
					break;
			}
		}
		//Avisa que es el turno del usuario en la ronda de apuestas
		else if(fase == 2) {
			panelRegistros.append("Es tu turno, " + nombreJugadorHumano + ". Puedes aumentar, igualar o retirarte.\n");
		} 
		//Avisa que se deben igualar las apuestas
		else if(fase == 3) {
			panelRegistros.append("Para descartar, las apuestas deben estar igualadas.\n");
		}
		//Comienza la ronda descarte
		else if(fase == 4) {
			panelRegistros.append("Comienza la ronda de descarte.\n");
		}
		
	}
	
	public void editarPanelJugador(int indexJugador, int apuesta) {
		mesaJuego.setPanelJugador(indexJugador, apuesta);
	}
	
	public void desactivarEscucha(JButton boton) {
		boton.removeActionListener(escucha);
	}
	public void activarEscucha(JButton boton) {
		boton.addActionListener(escucha);
	}
	
 	private class Escucha implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			//Si estamos en ronda de apuestas
			if(controlPoker.getRonda() == 0) {
				if(e.getSource() == aumentar) {
					int nuevaApuesta = controlPoker.getMaximaApuesta() + 500;
					//controlPoker.setApuestasJugadores(4, nuevaApuesta);
					//Pintar
					mesaJuego.getJugadorHumano().setValorApuesta(nuevaApuesta);
					//Despertar hilos
					controlPoker.turnos(5, nombreJugadorHumano, nuevaApuesta, 1, null);
				}
				else if(e.getSource() == igualar) {
					//controlPoker.setApuestasJugadores(4, controlPoker.getMaximaApuesta());
					//Pintar
					mesaJuego.getJugadorHumano().setValorApuesta(controlPoker.getMaximaApuesta());
					//Despertar hilos
					controlPoker.turnos(5, nombreJugadorHumano, controlPoker.getMaximaApuesta(), 0, null);
				}
				else if(e.getSource() == retirarse) {
					//El usuario pierde
					JOptionPane.showMessageDialog(panelBotones, "Perdiste");				}
				//Descartar
				else {
					JOptionPane.showMessageDialog(panelBotones, "Esta opción aún no está disponible.");
				}
				//Desactivar escuchas en ronda de apuestas 
				if(controlPoker.getRonda() == 0) {
					desactivarEscucha(aumentar);
					desactivarEscucha(igualar);
					desactivarEscucha(retirarse);
				}
				else if(controlPoker.getRonda() == 2) {
					//Desactivar escuchas en ronda de descarte
				}
			}
			//Ronda de descarte
			else if(controlPoker.getRonda() == 2) {
				if(e.getSource() == descartar) {
					
				}
				else {
					JOptionPane.showMessageDialog(panelBotones, "Esta opción ya no está dispnible");
				}
			}
		}
		
	}
}
