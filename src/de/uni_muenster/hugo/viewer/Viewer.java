package de.uni_muenster.hugo.viewer;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import de.uni_muenster.hugo.cubie.CubePosition;
import de.uni_muenster.hugo.cubie.Settings;
/**
 * The Class Viewer is an abstract class implemented by the subclasses containing the different views of the cube.
 * 
 * @author Jaap Scherphuis
 * @author Christian Grelle
 */
@SuppressWarnings("serial")
public abstract class Viewer extends Canvas
	implements MouseListener, MouseMotionListener
{
	/** The off image. */
	Image offImage;
	
	/** The off graphics. */
	Graphics offGraphics;
	
	/** The height. */
	int width, height;
	
	/** The base color. */
	Color baseColor = new Color(0,0,0);		 //black cube
	
	/** The scramble sequence. */
	@SuppressWarnings("unchecked")
	public
	static Vector scrambleSequence = new Vector();
	
	/** The delegate. */
	public ViewerDelegate delegate;
	
	/** The colors. */
	Color colors[] = {
		new Color( 255,   0, 0   ), //red
		new Color( 0,     0, 255 ), //blue
		new Color( 255, 255, 0   ), //yellow
		new Color( 255, 160, 64   ), //orange
		new Color( 0,   192, 0   ), //green
		new Color( 255, 255, 255 )  //white
	};
	// cube group setting
	/** The settings. */
	Settings settings;
	
	/** The main actionlistener. */
	ActionListener main;

	/**
	 * Instantiates a new viewer.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param s the settings
	 * @param m the actionlistener
	 * @param v the viewerdelegate
	 */
	public Viewer(int x, int y, Settings s, ActionListener m, ViewerDelegate v){
		delegate = v;
		width=x; height=y;
		settings = s;
		addMouseListener(this);
		main=m;
	}
	
	/**
	 * Initialise.
	 */
	public void initialise(){
		offImage = createImage(width, height ); // Double buffer
		offGraphics = offImage.getGraphics();
	}
	
	/**
	 * Reset.
	 */
	public void reset(){
		repaint();
	}

	//--- facelet routines ---
	// conversion data from internal cube representation to external facelet representation
	/**
 	* Edits the move.
 	* 
 	* @param f1 the first face
 	* @param f2 the second face
 	*/
	private void editMove( int f1, int f2){
		// edit mode, move facelet f1 to f2
		//find cubelet for f1
		int c1=0,o1=0,c2=0,o2=0;
		boolean f=false;
		if( settings.lockViewer ) return;
		if( f1<0 || f2<0 || f1>=54 || f2>=54 ) return;
		for( o1=0; o1<3; o1++){
			for( c1=0; c1<26; c1++){
				if( CubePosition.cubelet2facelet[c1][o1]==f1 ){ f=true; break; }
			}
			if(f) break;
		}
		if(f==false) return;
		f=false;
		for( o2=0; o2<3; o2++){
			for( c2=0; c2<26; c2++){
				if( CubePosition.cubelet2facelet[c2][o2]==f2 ){ f=true; break; }
			}
			if(f) break;
		}
		if(f==false) return;
		settings.cubePos.editMove(c1,o1,c2,o2);
		doEvent(true);
		repaint();
		delegate.enableControls3(false);
	}

	/**
	 * Show move.
	 * 
	 * @param face the face
	 * @param qu the direction
	 * 
	 * @return true, if successful
	 */
	public boolean showMove(int face, int qu)
	{
		settings.cubePos.doMove( face, qu, true );
		repaint();
		doEvent(false);
		return(true);
		
	}

//--- mouse routines ---
	/** The last coordinates. */
int lastX, lastY, lastF=-1;
	
	/** The keys. */
	int keys=0;
	
	/** moved. */
	boolean moved = false;
	
	/** The sensitivity drag. */
	final int sensitivityDrag = 40;
	
	/** The sensitivity move. */
	final int sensitivityMove = 12;


    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
    	if(!delegate.isViewerLocked()) {
	       	addMouseMotionListener(this);
			lastX = e.getX();
			lastY = e.getY();
			lastF = getFacelet( lastX, lastY );
			keys = e.isShiftDown()?1:0;
			keys+= e.isControlDown()?2:0;
			keys+= e.isAltDown()?4:0;
			moved=false;
			e.consume();
    	}
	}
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
    	if(!delegate.isViewerLocked()) {
	        removeMouseMotionListener(this);
			if( settings.edit && lastF>=0 ){
				editMove( lastF, getFacelet(e.getX(), e.getY()));
			}else if(!moved){
				checkMouseMove( e.getX(), e.getY(), sensitivityMove );
			}
			e.consume();
    	}
	}
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged( MouseEvent e ) {
    	if(!delegate.isViewerLocked()) {
			if( (!settings.edit || lastF<0 ) && !moved ) checkMouseMove( e.getX(), e.getY(), sensitivityDrag );
			e.consume();
    	}
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {}
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {}
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {}
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {}

	/**
	 * Check mouse move.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param d the direction
	 */
	abstract protected void checkMouseMove(int x, int y, int d);
	
	/**
	 * Gets the facelet.
	 * 
	 * @param x the x
	 * @param y the y
	 * 
	 * @return the facelet
	 */
	abstract protected int getFacelet( int x, int y );


    /**
	 * Try move.<BR>
	 * m=0-5, normal turn <BR>
	 * m=6-8, middle layer turn <BR>
	 * m=9-11, cube turn <BR>
	 * m=12-14, slice move (output only) <BR>
	 * m=15-17, anti-slice move (output only) <BR>
     * check group restrictions, parse keys, and call domove <BR>
     * q=+/-1 or +-2.
	 * 
	 * @param m the move
	 * @param q the quarter turn direction
	 */
	@SuppressWarnings("unchecked")
	public void tryMove( int m, int q )
	{
		if( m<9 ){
			if( settings.group==1 ) keys|=1;
			if( settings.group==2 ) { keys|=2; keys&=3; }
			if( settings.group==3 ) { keys|=4; keys&=5; }
			if( settings.group==3 && m>5 ) keys|=1;
		}
		if( settings.group==4 ){
			if( m!=1 && m!=3 && m<6 ) return;
		}

		if( (keys&1)!=0 ){
			//shift pressed: half turn
			if(q>0) q=2;  //positive
			else q=-2;
		}
		
		//capture moves for the genetic solver
		if(q==1){
			if(m==0)
				scrambleSequence.add(new Integer(5));
			if(m==1)
				scrambleSequence.add(new Integer(1));
			if(m==2)
				scrambleSequence.add(new Integer(0));
			if(m==3)
				scrambleSequence.add(new Integer(2));
			if(m==4)
				scrambleSequence.add(new Integer(4));
			if(m==5)
				scrambleSequence.add(new Integer(3));	
		}
		if(q==-1){
			if(m==0)
				scrambleSequence.add(new Integer(5+12));
			if(m==1)
				scrambleSequence.add(new Integer(1+12));
			if(m==2)
				scrambleSequence.add(new Integer(0+12));
			if(m==3)
				scrambleSequence.add(new Integer(2+12));
			if(m==4)
				scrambleSequence.add(new Integer(4+12));
			if(m==5)
				scrambleSequence.add(new Integer(3+12));
		}
		if(q==2){
			if(m==0)
				scrambleSequence.add(new Integer(5+6));
			if(m==1)
				scrambleSequence.add(new Integer(1+6));
			if(m==2)
				scrambleSequence.add(new Integer(0+6));
			if(m==3)
				scrambleSequence.add(new Integer(2+6));
			if(m==4)
				scrambleSequence.add(new Integer(4+6));
			if(m==5)
				scrambleSequence.add(new Integer(3+6));	
		}
		if(q==-2){
			if(m==0)
				scrambleSequence.add(new Integer(5+6));
			if(m==1)
				scrambleSequence.add(new Integer(1+6));
			if(m==2)
				scrambleSequence.add(new Integer(0+6));
			if(m==3)
				scrambleSequence.add(new Integer(2+6));
			if(m==4)
				scrambleSequence.add(new Integer(4+6));
			if(m==5)
				scrambleSequence.add(new Integer(3+6));
		}
		
		//middle sclices are not turnable
		if(!((m==6)||(m==7)||(m==8))){
			//whole cube moves do not provide a turn
			if(!((m==9)||(m==10)||(m==11))){
		//delegate last (just created) element	
		delegate.scramblePerformed((Integer) scrambleSequence.lastElement());
		delegate.enableControls3(false);	
			}
		doMove(m,q);
			}
	}

	/**
	 * Do move.
	 * 
	 * @param m the move
	 * @param q the quarter turn direction
	 */
	void doMove(int m, int q){
		if( settings.lockViewer ) return;
		settings.cubePos.doMove(m,q,true);
		repaint();
		doEvent(true);
	}

	/**
	 * Do event.<BR>
	 * Dispatch action event that move performed.<BR>
	 * Two event types: usermove and automove. First is a move done
     * by user acting on viewer via mouse. Second is move initiated from
	 * a function call from Cubie.<BR>
	 * 
	 * @param user the user
	 */
	void doEvent(boolean user){

		ActionEvent e=new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
									user ? "user": "auto");
		main.actionPerformed( e );
	}

	//-- display routines ---
	/* (non-Javadoc)
	 * @see java.awt.Canvas#update(java.awt.Graphics)
	 */
	public void update(Graphics g) { paint(g); }
	
	/**
	 * Gets the scramble sequence.
	 * 
	 * @return the scramble sequence
	 */
	@SuppressWarnings("unchecked")
	public Vector getScrambleSequence() {
		return scrambleSequence;
	}
	
	/**
	 * Reset scramble sequence.
	 */
	public void resetScrambleSequence() {
		scrambleSequence.removeAllElements();
	}
	
}
