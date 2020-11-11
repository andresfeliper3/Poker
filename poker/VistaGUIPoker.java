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
	private int apuestaInicial;
	
	private Escucha escucha;
	private ControlPoker controlPoker;
	
	public VistaGUIPoker(String[] nombresJugadoresSimulados, List<List<Carta>> manosJugadores, int apuestaInicial, ControlPoker controlPoker) {
		this.apuestaInicial = apuestaInicial;
		initGUI(nombresJugadoresSimulados, manosJugadores);
		this.setTitle("Póker clásico");
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initGUI(String[] nombresJugadoresSimulados, List<List<Carta>> manosJugadores) {
		//String nombreJugadorHumano = JOptionPane.showInputDialog(this, "Escribe tu nombre");
		String nombreJugadorHumano = "yolas";
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
		mesaJuego = new MesaJuego(nombreJugadorHumano, nombresJugadoresSimulados, manosJugadores, apuestaInicial);
		zonaJuego.add(mesaJuego, BorderLayout.CENTER);
		//Zona apuesta (dentro de zonaJuego
		zonaApuesta = new JPanel();
		//zonaApuesta.setPreferredSize(new Dimension(100,100));
		zonaApuesta.setBorder(new TitledBorder("Apuestas"));
		zonaJuego.add(zonaApuesta, BorderLayout.SOUTH);
		
		//Panel de registros
		panelRegistros = new JTextArea();
		panelRegistros.setEditable(true);
		panelRegistros.setPreferredSize(new Dimension(300,450));
		panelRegistros.setBorder(new TitledBorder("Registros del juego"));
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.VERTICAL;
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
	private class Escucha implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
