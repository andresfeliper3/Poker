package poker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;



public class PanelJugador extends JPanel{
	
	public static final int FONT_SIZE = 18;
	public static final int FONT_SIZE_2 = 14;
	public static final String FONT_TYPE = Font.DIALOG;
	public static final int FONT_STYLE = Font.BOLD;
	private static final int WIDTH = Baraja.CARTA_WIDTH*4;
	private static final int HEIGHT = Baraja.CARTA_HEIGHT + FONT_SIZE*7;
	
	
	private List<Carta> mano = new ArrayList<Carta>();
	String nombreJugador;
	private JLabel mensaje,dineroActual,nombre;
	private JPanel panelMano;
	private boolean isHuman;
	private Escucha escucha;
	private int dinero = 5000;
	
	
	public PanelJugador(String nombreJugador, List<Carta> cartas, boolean isHuman) {
		setLayout(new BorderLayout());
		this.setBackground(Color.GREEN);
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.nombreJugador = nombreJugador;

		
		nombre = new JLabel(nombreJugador + "        "+dinero);
		nombre.setFont(new Font(FONT_TYPE,FONT_STYLE,FONT_SIZE));
		add(nombre,BorderLayout.NORTH);
		mensaje = new JLabel();
		mensaje.setFont(new Font(FONT_TYPE,FONT_STYLE,FONT_SIZE_2));
		add(mensaje,BorderLayout.SOUTH);
		
		this.isHuman = isHuman;
		
		panelMano = new JPanel();
		panelMano.setBackground(Color.GREEN);
		
		for(Carta carta : cartas) {
			mano.add(carta);
		}
				
		if(isHuman) {
			escucha = new Escucha();
			for(Carta carta : mano) {
				carta.addMouseListener(escucha);
			}
			mensaje.setText("Inicias ... ");
		}
		
	    refrescarPanelMano();
		add(panelMano,BorderLayout.CENTER);
		
	}
	
	private void refrescarPanelMano() {
		panelMano.removeAll();
		if(mano!=null) {
			for(Carta carta : mano) {
				panelMano.add(carta);
			}
		}
	}
	
	//Muestra el dinero total que ha apostado el jugador en la ronda
	public void refrescarDineroApostado() {
		nombre.removeAll();
		dinero -= 500;
		nombre.setText(nombreJugador + "        "+dinero);
	}
	
   public void recibirCartas(List<Carta> nuevasCartas, String texto) {
	   SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mano=nuevasCartas;
			refrescarPanelMano();
			mensaje.setText(texto);
			panelMano.revalidate();	
		    panelMano.repaint();
		}
		   
	   });
   }
	
   public void eliminarCartaNoHuman(ArrayList<Integer> indexes) {
	   mano = Collections.synchronizedList(mano);
	   synchronized (mano){
		   for(Integer index : indexes) {
			   mano.remove(index);
		   }
	
	   }   
	   
	   SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			refrescarPanelMano();
		    panelMano.revalidate();	
		    panelMano.repaint();
		    mensaje.setText("Descartó ...");
		}	   
	   });
   }
   
   private void eliminarCarta(Carta cartaEliminar) { 
	   mano = Collections.synchronizedList(mano);
	   synchronized (mano){
		   for(int i=0;i<mano.size();i++) {
			 if(cartaEliminar.getValor()==mano.get(i).getValor() && cartaEliminar.getPalo()==mano.get(i).getPalo() ) {
				 mano.remove(i);
			 }   
		   }
		   
	   }
   }
   
   public List<Carta> getMano() {
	  return mano; 
   }
   
   public void desactivarEscuchas() {
	   SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			for(Carta carta : mano) {
				carta.removeMouseListener(escucha);
			}
		}		   
	   });
   }
   
   public void activarEscuchas() {
	   SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			for(Carta carta : mano) {
				carta.addMouseListener(escucha);
			}
		}		   
	   });   
   }
   
   
   private class Escucha extends MouseAdapter{

	@Override
	public void mouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		Carta cartaEliminar = (Carta) event.getSource();
		//eliminar carta del mazo y actualizar el panelMano 
	    eliminarCarta(cartaEliminar);
	    refrescarPanelMano();
	    mensaje.setText("Descartó ...");
	    panelMano.revalidate();	
	    panelMano.repaint();
	 } 
   }
}