package poker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class VistaGUIPoker extends JFrame {

	private MesaJuego mesaJuego;
	private JPanel panelBotones,zonaJuego,zonaDinero,zonaApuesta;
	private JButton descartar,retirarse,aumentarApuesta,igualar;
	private Escucha escucha;
	private Titulos titulo;
	private JTextArea registros;
	
	private ControlPoker controlPoker;
	
	public VistaGUIPoker(String[] jugadoresSimulados,List<List<Carta>> manosJugadores,Carta cartaComun, ControlPoker controlPoker) {
		
		this.controlPoker = controlPoker;
		
		initGUI(jugadoresSimulados,manosJugadores,cartaComun);
		
		this.setTitle("Poker");
		this.setResizable(true);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initGUI(String[] jugadoresSimulados,List<List<Carta>> manosJugadores,Carta cartaComun) {
		// TODO Auto-generated method stub
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		String jugadorHumano = "Mi mano";
		escucha = new Escucha();
		
		//Titulo
		titulo = new Titulos("Poker Clásico",50,Color.BLACK);
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.gridwidth=2;
		constraints.fill=GridBagConstraints.HORIZONTAL;
		add(titulo,constraints);
		
		//Zona de juego
		zonaJuego = new JPanel();
		zonaJuego.setBorder(new TitledBorder("Zona de Juego"));
		//zonaJuego.setPreferredSize(new Dimension(1000,1000));
		zonaJuego.setLayout(new BorderLayout());
		
		zonaDinero = new JPanel();
		zonaDinero.setBorder(new TitledBorder("Zona de Dinero"));
		zonaJuego.add(zonaDinero,BorderLayout.NORTH);
		
		//Mesa de juego
		mesaJuego = new MesaJuego(jugadorHumano,jugadoresSimulados,manosJugadores,cartaComun)  ;
		
		mesaJuego.setBorder(new TitledBorder("Mesa de Juego"));
		zonaJuego.add(mesaJuego,BorderLayout.CENTER);
		
		//Zona de apuesta
		zonaApuesta = new JPanel();
		zonaApuesta.setBorder(new TitledBorder("Zona de Apuestas"));
		zonaJuego.add(zonaApuesta,BorderLayout.SOUTH);
	
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.gridwidth=1;
		constraints.fill=GridBagConstraints.NONE;
		add(zonaJuego,constraints);
		
		//Zona de Botones
		panelBotones = new JPanel();
		panelBotones.setBorder(new TitledBorder("Botones"));
		descartar = new JButton("Descartar");
		retirarse = new JButton("Retirarse");
		aumentarApuesta = new JButton("Aumentar Apuesta");
		igualar = new JButton("Igualar");
		descartar.addActionListener(escucha);
		retirarse.addActionListener(escucha);
		aumentarApuesta.addActionListener(escucha);
		igualar.addActionListener(escucha);
		
		panelBotones.add(descartar);
		panelBotones.add(retirarse);
		panelBotones.add(aumentarApuesta);
		panelBotones.add(igualar);
		
		constraints.gridx=0;
		constraints.gridy=2;
		constraints.gridwidth=2;
		constraints.fill=GridBagConstraints.NONE;
		add(panelBotones,constraints);
		
		//Zona de Registros
		registros = new JTextArea();
		registros.setBorder(new TitledBorder("REGISTROS"));
		registros.setPreferredSize(new Dimension(300,600));
		
		constraints.gridx=1;
		constraints.gridy=1;
		constraints.gridwidth=1;
		constraints.fill=GridBagConstraints.NONE;
		add(registros,constraints);
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
	
	private class Escucha extends MouseAdapter implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent eventAction) {
			// TODO Auto-generated method stub
			if(eventAction.getSource()==descartar) {
	
				controlPoker.descarteJugadorHumano(mesaJuego.getManoHumano());
			}else if(eventAction.getSource()==aumentarApuesta) {
				

			}
		}	
		
	}
}
