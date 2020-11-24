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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.util.List;

public class VistaGUIPoker extends JFrame {
	
	private JPanel zonaJuego, zonaDinero, zonaApuesta, panelBotones;
	private JTextArea panelRegistros;
	private MesaJuego mesaJuego;
	private Titulos titulo;
	private JButton descartar, aumentar, igualar, retirarse;
	private List<Integer> apuestasJugadores;
	private List<List<Carta>> manosJugadores;
	private String nombreJugadorHumano;
	private String[] nombresJugadoresSimulados;
	private JScrollPane scroll;
	
	private Escucha escucha;
	private ControlPoker controlPoker;

	
	public VistaGUIPoker(String[] nombresJugadoresSimulados, List<List<Carta>> manosJugadores, List<Integer> apuestasJugadores, ControlPoker controlPoker) {
		this.apuestasJugadores = apuestasJugadores;
		this.manosJugadores = manosJugadores;
		this.controlPoker = controlPoker;
		initGUI(nombresJugadoresSimulados, manosJugadores, apuestasJugadores);
		this.setTitle("P�ker cl�sico");
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
		titulo = new Titulos("P�ker Cl�sico", 40, Color.BLACK);
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
		zonaApuesta.setBorder(new TitledBorder("Apuestas"));
		zonaJuego.add(zonaApuesta, BorderLayout.SOUTH);
		
		
		
		panelRegistros = new JTextArea();
		panelRegistros.setBounds(20,20,600,400);
		panelRegistros.setEditable(false);
		panelRegistros.setBorder(new LineBorder(Color.BLACK));
		panelRegistros.setPreferredSize(new Dimension(300,450));
		//Panel de registros
		scroll = new JScrollPane(panelRegistros);
		//scroll.setBounds(20,20,600,400);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBorder(new TitledBorder("Registros del juego"));
		//scroll.getViewport().setBackground(Color.WHITE);
		//scroll.getViewport().add(panelRegistros);

		
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.VERTICAL;
		
		add(scroll, constraints);
		
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
	
			panelRegistros.append("Todos tienen una apuesta inicial de $" + apuestasJugadores.get(0) + ".\n");
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
					
					panelRegistros.append("El jugador " + nombre + " igual� a $" + apuesta + ".\n");
					break;
				case 1:
					
					panelRegistros.append("El jugador " + nombre + " aument� a $" + apuesta + ".\n");
					break;
				case 2:
					
					panelRegistros.append("El jugador " + nombre + " se retir�.\n");
					break;
			}
		}
		//Avisa que es el turno del usuario en la ronda de Descartes
		else if(fase == 2) {
			panelRegistros.append("Es tu turno, " + nombreJugadorHumano + ". Puedes aumentar, igualar o retirarte.\n");
		} 
		//Avisa que se deben igualar las apuestas
		else if(fase == 3) {
			panelRegistros.append("\nPara descartar, las apuestas deben estar igualadas.\n");
			panelRegistros.append("Comienza la ronda de igualaci�n.\n");
		}
		//Ronda de igualaci�n de apuestas
		else if(fase == 4) {
			//El jugador iguala
			if(operacion == 0) {
				panelRegistros.append("El jugador " + nombre + " igual� a $" + apuesta + ".\n");
			}
			//El jugador se retira
			else if(operacion == 2) {
				panelRegistros.append("El jugador " + nombre + " se retir�.\n");
			} 
			//El jugador aumenta. NO DEBER�A HACER ESTO
			else if(operacion == 1) {
				JOptionPane.showMessageDialog(panelBotones, "El jugador " + nombre + " no deber�a poder aumentar.\n");
			}
		}
		//Comienza la ronda descarte
		else if(fase == 5) {
			mesaJuego.getJugadorHumano().activarEscuchas();
			panelRegistros.append("\nComienza la ronda de descarte.\n");
		}
		//Mensaje a jugador humano en ronda de igualaci�n
		else if(fase == 6) {
			panelRegistros.append("Es tu turno " + nombreJugadorHumano + ". Puedes igualar o retirarte.\n");
		}
		//Mensajes en ronda de descarte
		else if(fase == 7) {
			panelRegistros.append("El jugador " + nombre + " descart� " + operacion + " cartas.\n");
		}
		//Mensaje a usuairo en ronda de descarte
		else if(fase == 8) {
			panelRegistros.append("Es tu turno de descartar.\nHaz click en las cartas que quieres descartar y \nluego haz click en el bot�n Descartar.\n");
			
		}
		//Mensaje para iniciar la segunda ronda de apuestas
		else if(fase==9) {
			mesaJuego.getJugadorHumano().desactivarEscuchas();
			panelRegistros.append("\nInicia la segunda ronda de apuestas.\n");
		}
		//Mensaje para iniciar la revisi�n y determinar un ganador
		else if(fase==10) {
			panelRegistros.append("\nEl crupier revisar� los mazos y determinar� un ganador.\n");
		}
		//Mensaje para determinar un ganador
		else if(fase==11) {
			panelRegistros.append("\nEL GANADOR ES: "+apuesta+", con: "+operacion +" puntos");
		}
	}
	
	public void actualizarVistaPoker(List<List<Carta>> manosJugadores,int ganador) {
		//debe llamarse cuanto el control tenga las nuevas manos y el resultado
		mesaJuego.mesaActualizar(manosJugadores,ganador);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//otraRonda.setVisible(true);
			}
			
		});
		
		
	}
	//Reinicia las condiciones iniciales de la vista GUI con los nuevos datos generados por el controlPoker
	private void reiniciarVistaGUIPoker() { 
		//Cambiar apuestas y manos
		apuestasJugadores = controlPoker.getApuestasJugadores();
		manosJugadores = controlPoker.getManosJugadores();
		//Pasar apuestasJugadores y manosJugadores a mesaJuego
		mesaJuego.reiniciarPanelJugadores(manosJugadores, apuestasJugadores);
		//Vaciar panel de registros
		panelRegistros.setText("");
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
					//controlPoker.setApuestasJugadores(4, nuevaApuesta);			
					//Despertar hilos
					controlPoker.turnos(5, nombreJugadorHumano, 1, null);
					//Pintar
					mesaJuego.getJugadorHumano().setValorApuesta(controlPoker.getApuestasJugadores().get(4));
				}
				else if(e.getSource() == igualar) {
					//controlPoker.setApuestasJugadores(4, controlPoker.getMaximaApuesta());
					//Despertar hilos
					controlPoker.turnos(5, nombreJugadorHumano, 0, null);
					//Pintar
					mesaJuego.getJugadorHumano().setValorApuesta(controlPoker.getApuestasJugadores().get(4));
				}
				else if(e.getSource() == retirarse) {
					//El usuario se retira y pierde
					controlPoker.setHumanoRetirado(true);
					
					controlPoker.setJugadoresEnjuego();
					int option = JOptionPane.showConfirmDialog(panelBotones, "�Deseas jugar otra vez?", "Te retiraste", JOptionPane.YES_NO_CANCEL_OPTION);	
					//Pregunta si el usuario quiere seguir jugando
					if(option == JOptionPane.YES_OPTION) {
						//reiniciar juego
						JOptionPane.showMessageDialog(panelBotones, "Reiniciando juego");
						reiniciarVistaGUIPoker();
						controlPoker.reiniciarJuego();		
						//controlPoker.turnos(5, nombreJugadorHumano, 2, null);
					}
					else if(option == JOptionPane.NO_OPTION) {
						System.exit(0);
					}
				}
				//Descartar
				else {
					JOptionPane.showMessageDialog(panelBotones, "Esta opci�n a�n no est� disponible.");
				}
				//Desactivar escuchas en ronda de apuestas 
				if(controlPoker.getRonda() == 0) {
					/*
					desactivarEscucha(aumentar);
					desactivarEscucha(igualar);
					desactivarEscucha(retirarse);
					*/
				}
				else if(controlPoker.getRonda() == 2) {
					//Desactivar escuchas en ronda de descarte
				}
			}
			//Ronda de igualaci�n
			else if(controlPoker.getRonda() == 1) {
				if(e.getSource() == igualar) {
					//Despertar hilos
					controlPoker.turnos(5, nombreJugadorHumano, 0, null);
					//Pintar
					mesaJuego.getJugadorHumano().setValorApuesta(controlPoker.getApuestasJugadores().get(4));
				}
				else if(e.getSource() == retirarse) {
					//El usuario se retira y pierde
					controlPoker.setHumanoRetirado(true);
					controlPoker.setJugadoresEnjuego();
					int option = JOptionPane.showConfirmDialog(panelBotones, "�Deseas jugar otra vez?", "Te retiraste", JOptionPane.YES_NO_CANCEL_OPTION);	
					//Pregunta si el usuario quiere seguir jugando
					if(option == JOptionPane.YES_OPTION) {
						//reiniciar juego
						JOptionPane.showMessageDialog(panelBotones, "Reiniciando juego");
						controlPoker.reiniciarJuego();
						reiniciarVistaGUIPoker();
						//controlPoker.turnos(5, nombreJugadorHumano, 2, null);
					}
					else if(option == JOptionPane.NO_OPTION) {
						System.exit(0);
					}
				}
				else if(e.getSource() == aumentar) {
					JOptionPane.showMessageDialog(panelBotones, "Esta opci�n ya no est� disponible");
				}
				//descartar
				else {
					JOptionPane.showMessageDialog(panelBotones, "Todav�a no puede descartar cartar");
				}
			}
			//Ronda de descarte. Se revisa que el jugador humano no se haya retirado para realiza esta acci�n
			else if(controlPoker.getRonda() == 2 && !controlPoker.isHumanoRetirado()) {
				if(e.getSource() == descartar) {
					controlPoker.descarteJugadorHumano(mesaJuego.getManoHumano());
					System.out.println(nombreJugadorHumano + " llama a turnos desde bot�n descarte y quiere descartar" + ControlPoker.NUMERO_CARTAS_MANO + " " +mesaJuego.getManoHumano().size() );
					controlPoker.turnos(5, nombreJugadorHumano, ControlPoker.NUMERO_CARTAS_MANO - mesaJuego.getManoHumano().size(), null);
				}
				else {
					JOptionPane.showMessageDialog(panelBotones, "Ronda " + controlPoker.getRonda() + ", Esta opci�n ya no est� disponible");
				}
			}
		}
		
	}
}
