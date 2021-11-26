package de.uni_muenster.hugo.cubie;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import de.uni_muenster.hugo.gui.ImButton;
import de.uni_muenster.hugo.viewer.Viewer;
import de.uni_muenster.hugo.viewer.Viewer3D;
import de.uni_muenster.hugo.viewer.ViewerBox;
import de.uni_muenster.hugo.viewer.ViewerDelegate;
import de.uni_muenster.hugo.viewer.ViewerDiag;
import de.uni_muenster.hugo.viewer.ViewerFlat;

/**
 * This class is not relevant for HuGO! and therefore no javadoc documentation was done.
 * The code is documented by a simple documentation inside the class. It is part of the applet
 * <a href="http://www.geocities.com/jaapsch/puzzles/cubie.htm">Cubie</a> by
 * Jaap Scherphuis © 2003-2004. The graphical visualization of the cube contained in Cubie is used for the GUI of the genetic solver.
 * This class is left in the project to keep the applet intact.
 * 
 * @author Jaap Scherphuis
 */
@SuppressWarnings("serial")
public final class Cubie
	extends java.applet.Applet implements ActionListener, ItemListener, FocusListener, Runnable, ViewerDelegate
{
	final int NUMGROUPS = 5;
	final int NUMVIEWS = 4;
	final int VIEWWIDTH = 300;
	final int VIEWHEIGHT = 350;

	Settings settings = new Settings();
    int symType = 0;
    int viewer = 0;
    Viewer cubeViewers[] = {
		new Viewer3D(VIEWWIDTH ,VIEWHEIGHT,settings,this, this),
		new ViewerDiag(VIEWWIDTH ,VIEWHEIGHT,settings,this, this),
		new ViewerBox(VIEWWIDTH ,VIEWHEIGHT,settings,this, this),
		new ViewerFlat(VIEWWIDTH ,VIEWHEIGHT,settings,this, this)
	};
    Panel rightPanel = new Panel();
    Button mixBut = new Button("Mix");
    Button resetBut = new Button("Reset");
    Button editBut = new Button("Edit");
    Button solveBut = new Button("Solve");

    Button viewBut = new Button("Change view");
    Button viewResetBut = new Button("Reset view");

    Panel boxPanel = new Panel();
    Button gensolBut = new Button("Solution");
    TextField textBox = new TextField("", 50);
    Button playBut = new ImButton(1);
    Button revBut = new ImButton(0);
    Button stepBut = new ImButton(5);
    Button backBut = new ImButton(4);
    Button endBut = new ImButton(7);
    Button beginBut = new ImButton(6);

    TabSet tabSet;
    Panel tabPanel[] = { new Panel(),new Panel() };

    Checkbox groupCheckBox[] = new Checkbox[NUMGROUPS];
    Checkbox superBox = new Checkbox("supergroup");
    CheckboxGroup cubeGroup = new CheckboxGroup();
    Checkbox groupRadioBox[] = {
			new Checkbox("Normal",cubeGroup,true),
			new Checkbox("Square",cubeGroup,false),
			new Checkbox("Slice",cubeGroup,false),
			new Checkbox("Anti-Slice",cubeGroup,false),
			new Checkbox("2-generator",cubeGroup,false)
		};

	Solver solvers[] = {
			new SolverKociemba(this),
			new SolverSquare(this),
			new SolverSlice(this),
			new SolverAntiSlice(this),
			new SolverTwoGen(this)
		};

    Checkbox symTwoColBox = new Checkbox("Two colors");
    SymButton symButton[] = new SymButton[29];
    SymButton symAllImage = new SymButton(null,0);
    SymButton symCurrentImage = new SymButton(null,0);
    Button symResetBut = new Button("Clear");

	Color colors[] = {
		new Color(255,0,0),	//unprepared
		new Color(192,192,192),	//prepared, ready, controls background color
		new Color(0,255,0),   //Running solver
        new Color(160,160,160), // viewer background color
	};
	boolean solution=true;
	boolean symTwoCol=false;
	MoveSequence generator;
	int seqPos=0;
	boolean playFw=true;
	boolean moveInProgress=false;

	// applet control
	boolean isPlaying=false;

	public void init(){
		int i,d,x,y;
		final int vw=VIEWWIDTH,vh=VIEWHEIGHT,gw=220,bw=gw/4,th=50,bh=20,bw2=75,bh2=16;

        // build main applet panel
        setLayout(null);
		setBackground(colors[3]);
        add(rightPanel);
		add(boxPanel);
		for( i=0; i<NUMVIEWS; i++){
			add(cubeViewers[i]);
			cubeViewers[i].setBounds(0,0,vw,vh);
			cubeViewers[i].setVisible(i==0);
            cubeViewers[i].setBackground(colors[3]);
		}
		rightPanel.setBounds(vw,0,gw,vh);
		boxPanel.setBounds(0,vh,vw+gw,th);

        //build right panel
		rightPanel.setLayout(null);
		rightPanel.setBackground(colors[3]);
		rightPanel.add(mixBut);   mixBut.setBounds(0,0,bw,bh);
		rightPanel.add(resetBut); resetBut.setBounds(bw,0,bw,bh);
		rightPanel.add(editBut);  editBut.setBounds(bw+bw,0,bw,bh);
		rightPanel.add(solveBut); solveBut.setBounds(bw+bw+bw,0,gw-3*bw,bh);
		rightPanel.add(viewBut);  viewBut.setBounds(0,vh-bh,bw+bw,bh);
		rightPanel.add(viewResetBut);  viewResetBut.setBounds(bw+bw,vh-bh,gw-bw-bw,bh);

        // add all right panel listeners
        mixBut.addActionListener(this);
        resetBut.addActionListener(this);
        editBut.addActionListener(this);
        solveBut.addActionListener(this);
        viewBut.addActionListener(this);
        viewResetBut.addActionListener(this);

		// Build set of tabpanels
        tabSet = new TabSet(this,new Color(128,128,128),colors[1]);
        tabSet.setBackground(colors[3]);
        rightPanel.add(tabSet);
        tabSet.setBounds(0,bh+1,gw,bh-1);
        tabSet.addTab("Groups",tabPanel[0]);
        tabSet.addTab("Symmetries",tabPanel[1]);
        for( i=0; i<2; i++){
            tabPanel[i].setLayout(null);
            tabPanel[i].setBounds(0,bh+bh,gw,vh-bh-bh-bh-2);
            tabPanel[i].setBackground(colors[1]);
            rightPanel.add(tabPanel[i]);
        }

		// build group tab panel
        d=tabPanel[0].getSize().height/(NUMGROUPS*2+3);
		for( i=0; i<NUMGROUPS; i++){
			groupCheckBox[i] = new Checkbox();
            groupCheckBox[i].setState(true);
			groupCheckBox[i].setEnabled(false);
			groupRadioBox[i].addItemListener(this);
            tabPanel[0].add(groupCheckBox[i]);
            tabPanel[0].add(groupRadioBox[i]);

			groupRadioBox[i].setBounds(    0,3*d+2*d*i,gw-25,2*d);
			groupCheckBox[i].setBounds(gw-25,3*d+2*d*i,   25,2*d);

			groupRadioBox[i].setBackground(colors[0]);
			groupCheckBox[i].setBackground(colors[0]);
		}
        tabPanel[0].add(superBox);
		superBox.setBounds( 25, d, gw-25,d+d);
		superBox.setBackground(colors[1]);
        // add all further group tab listeners
        superBox.addItemListener(this);


        // build symmetries tab panel
        d=tabPanel[1].getSize().height/9;
        for( i=0; i<29; i++){
            symButton[i] = new SymButton(this,1<<i);
            tabPanel[1].add(symButton[i]);
            symButton[i].setBackground(new Color(208,208,208));

            y=i;x=0;
            if     (y>=23){ y-=23; x=4; }
            else if(y>=19){ y-=17; x=2; }
            else if(y>=13){ y-=13; x=3; }
            else if(y>= 7){ y-= 7; x=1; }
            else if(y>  0){ y-= 1; x=0; }
            else          { x=2; }
            y=y*d+d; x*=gw/5;
            symButton[i].setBounds(x,y+d+d,gw/5-1,d-1);
        }

        Label l=new Label("Current:");
        l.setBounds(0,0,bw,d+d);
        tabPanel[1].add(l);
        symCurrentImage.setBounds(bw,0,bw,d+d);
        symCurrentImage.setEnabled(false);
        tabPanel[1].add(symCurrentImage);

        l=new Label("Selected:");
        l.setBounds(bw+bw,0,bw,d+d);
        tabPanel[1].add(l);
        symAllImage.setBounds(bw*3,0,bw,d+d);
        symAllImage.setEnabled(false);
        tabPanel[1].add(symAllImage);

        symTwoColBox.setBounds(5,d+d,bw*3-5,bh);
        tabPanel[1].add(symTwoColBox);
        symResetBut.setBounds(bw*3,d+d,bw,bh);
        tabPanel[1].add(symResetBut);
        symResetBut.setBackground(new Color(208,208,208));
        // add all symmetries tab listeners
        symTwoColBox.addItemListener(this);
        symResetBut.addActionListener(this);


        // Abspiel Kontrolle
		boxPanel.setLayout(null);
		boxPanel.add(gensolBut); gensolBut.setBounds(0,0,bw2,bh);
		boxPanel.add(textBox); textBox.setBounds(bw2,0,vw+gw-bw2,bh);

		boxPanel.add(beginBut); beginBut.setBounds(bw2+0   ,bh,bw,bh2);
		boxPanel.add(backBut);  backBut.setBounds( bw2+bw  ,bh,bw,bh2);
		boxPanel.add(revBut);   revBut.setBounds(  bw2+bw*2,bh,bw,bh2);
		boxPanel.add(playBut);  playBut.setBounds( bw2+bw*3,bh,bw,bh2);
		boxPanel.add(stepBut);  stepBut.setBounds( bw2+bw*4,bh,bw,bh2);
		boxPanel.add(endBut);   endBut.setBounds(  bw2+bw*5,bh,bw,bh2);

		boxPanel.setBackground(colors[3]);

        // add all solution box panel listeners
		gensolBut.addActionListener(this);
		textBox.addActionListener(this);
		textBox.addFocusListener(this);
		beginBut.addActionListener(this);
		backBut.addActionListener(this);
		revBut.addActionListener(this);
		playBut.addActionListener(this);
		stepBut.addActionListener(this);
		endBut.addActionListener(this);


        // initialise all solvers
		for( i=0; i<NUMGROUPS; i++) {
			solvers[i].settings(settings);
			new Thread(solvers[i]).start();
		}
		cubeViewers[viewer].repaint();
		updateStatus(false);
	}

    public void stop() {
		//tell thread to stop
		if( isPlaying ){
			isPlaying = false;
			while(moveInProgress){
				try { Thread.sleep( 50 ); } catch ( Exception ignored ) {}
			}
			settings.lockViewer=false;
		}
	}

	public void run()
	{
		int f,q;
		isPlaying=true;
		settings.lockViewer=true;
		do{
			if( playFw ){
				f=generator.getMoves()[seqPos];
				q=generator.getAmount()[seqPos];
				if( cubeViewers[viewer].showMove(f,q) ){
					moveInProgress=(viewer==0);
					seqPos++;
				}
				if( seqPos>=generator.getLength() ) isPlaying=false;
			}else{
				seqPos--;
				f=generator.getMoves()[seqPos];
				q=generator.getAmount()[seqPos];
				if(! cubeViewers[viewer].showMove(f,4-q) ) {
					seqPos++;
				}else{
					moveInProgress=(viewer==0);
				}
				if( seqPos<=0 ) isPlaying=false;
			}
			textBox.setText( generator.toString(solution,seqPos) );
			if( isPlaying ){
				try { Thread.sleep( 3000 ); } catch ( Exception ignored ) {}
				do{
					try { Thread.sleep( 3000 ); } catch ( Exception ignored ) {}
				}while(moveInProgress);
			}
		}while(isPlaying);
		settings.lockViewer=false;
	}
    private void updateStatus(boolean changed) {
		// Update status of current tab and solver buttons
        boolean t;
        if(tabSet.getTab()==0){
            boolean currentSolvable=false;
            for( int i=0;i<NUMGROUPS; i++){
                if(changed){
                    t=solvers[i].setPosition( settings.cubePos, true );
                    groupCheckBox[i].setState( t );
                }else{
                    t=groupCheckBox[i].getState();
                }
                if( i==settings.group ) currentSolvable=t;
                int c=1;
                if( !solvers[i].isPrepared()) c=0;
                else if( solvers[i].isRunning()) c=2;
                groupCheckBox[i].setBackground(colors[c]);
                this.groupRadioBox[i].setBackground(colors[c]);
            }
            t=solvers[settings.group].isPrepared() && currentSolvable ;
		}else{
            if(changed){
                t=solvers[0].setPosition( settings.cubePos, true );
            }else{
                t=groupCheckBox[0].getState();
            }
            t=solvers[0].isPrepared() && t;
            symCurrentImage.setType(settings.cubePos.getSym());
		}
        solveBut.setLabel( settings.solving ? "Stop": "Solve");
        solveBut.setEnabled( t || settings.solving );
	}

    public void destroy() {
		stop();
		if( settings.solving ){
			//tell solver to stop
			solvers[settings.group].stopSolving();
			//wait till it has indeed stopped
			while( settings.solving ){
				try { Thread.sleep( 100 ); } catch ( Exception ignored ) {}
			};
		}
        for(int i=0; i<NUMVIEWS; i++){
	        remove( cubeViewers[i] );
		}
        remove( rightPanel );
        remove( boxPanel );
    }

    public String getAppletInfo() {
		return "Title: Cubie \nAuthor: Jaap Scherphuis";
    }


//--- button action/listening routines ---
	public void solve()
	{
        int g=(tabSet.getTab()==0) ? settings.group : 0;
		if( settings.solving ){
			for(int i=0; i<NUMGROUPS; i++)
                solvers[i].stopSolving();
		}else if( solvers[g].setPosition( settings.cubePos, false ) ){
			startSolving();
			new Thread(solvers[g]).start();
		}
	}

    public void actionPerformed(ActionEvent e) {
		int i;
		Object src = e.getSource();
		if ( src == mixBut ){
			if( !settings.solving ){
                stop();// stop any animation
				if(tabSet.getTab()==0){
                    solvers[settings.group].mix(settings.cubePos);
				}else{
                    settings.cubePos.mix(symType, settings.superGroup,symTwoCol);
                }
				setSequencePosition(-1);
				updateStatus(true);
				cubeViewers[viewer].repaint();
			}
		}else if ( src == resetBut ){
			if( !settings.solving ){
                stop();// stop any animation
				settings.cubePos.reset();
				setSequencePosition(-1);
				updateStatus(true);
				cubeViewers[viewer].repaint();
			}
		}else if ( src == solveBut ){
			stop();// stop any animation
			solve();
		}else if ( src == gensolBut ){
			solution=!solution;
			gensolBut.setLabel( solution? "Solution":"Generator");
			if( generator==null ){
				textBox.setText( "" );
			}else{
				textBox.setText( generator.toString(solution,seqPos) );
			}
		}else if( src == viewBut ){
			viewer++; if( viewer>=NUMVIEWS ) viewer=0;
			for(i=0;i<NUMVIEWS;i++){
				cubeViewers[i].setVisible(i==viewer);
			}
		}else if( src == viewResetBut ){
			settings.cubePos.resetView();
			for(i=0;i<NUMVIEWS;i++){
				cubeViewers[i].reset();
			}
            updateStatus(false);
		}else if( src == editBut ){
			settings.edit=!settings.edit;
			editBut.setLabel( settings.edit? "Play":"Edit");
			seqPos=-1;
		}else if( src == textBox ){
			//change focus, forcing focus event to be processed
			groupRadioBox[settings.group].requestFocus();
		}else if( src == beginBut ){
			stop();
			setSequencePosition( (generator!=null && solution)?
										generator.getLength() : 0 );
			cubeViewers[viewer].repaint();
		}else if( src == backBut ){
			if( solution ) stepForward();
			else stepBackward();
		}else if( src == revBut ){
			if( solution ) playForward();
			else playBackward();
		}else if( src == playBut ){
			if( solution ) playBackward();
			else playForward();
		}else if( src == stepBut ){
			if( solution ) stepBackward();
			else stepForward();
		}else if( src == endBut ){
			stop();
			setSequencePosition( (generator==null || solution)?
									0 : generator.getLength() );
			cubeViewers[viewer].repaint();
		}else if( src == symResetBut ){
            //reset all buttons
            for(i=0;i<29;i++) symButton[i].setPressed(false);
            symType = 0;
			symAllImage.setType(symType);
        }else if( src == tabSet ){
            updateStatus(true);
		}else{
            // check for viewer actions
			for(i=0;i<NUMVIEWS;i++){
				if( src==cubeViewers[i] ){
					updateStatus(true);
					if( e.getActionCommand()=="user" ){
						seqPos=-1;
						if( generator!=null )
							textBox.setText( generator.toString(solution,seqPos) );
					}else{
						moveInProgress=false;
					}
					return;
				}
			}
            // check for solver actions
			for(i=0;i<NUMGROUPS;i++){
				if( src==solvers[i] ){
					if(e.getActionCommand()=="a"){	//init done
					}else if(e.getActionCommand()=="b"){	//solution found
                        stoppedSolving();
						generator = settings.generator;
						settings.generator=null;
						seqPos = generator.getLength();
						textBox.setText( generator.toString(solution,seqPos) );
						settings.cubePos.doSequence( generator );
					}else if(e.getActionCommand()=="c"){	//aborted solve
                        stoppedSolving();
                    }else if(e.getActionCommand()=="d"){    //ended solve
                        stoppedSolving();
                    }else if(e.getActionCommand()=="e"){    //started solve
					}
                    updateStatus(false);
					return;
				}
			}

            // check for symbutton actions
            for(i=0;i<29;i++){
                if( src==symButton[i] ){
                    if(e.getActionCommand()!=""){	//perform ref/rot
                    	if( !settings.lockViewer ){
	                    	if( seqPos >= 0 && generator!=null ){
	                    		generator.doSym(i);
                                textBox.setText( generator.toString(solution,seqPos) );
                            }
	                    	settings.cubePos.doSym(i,e.getActionCommand()=="c");
							updateStatus(true);
							cubeViewers[viewer].repaint();
						}
					}else{
	                    if(!symButton[i].isPressed()){
	                        symType |= 1<<i;
                            symButton[i].setPressed(true);
	                    }else{
	                        symType &= ~(1<<i);
                            symButton[i].setPressed(false);
	                    }
	                    symAllImage.setType(symType);
					}
                    return;
                }
            }
		}
	}

	// enable all buttons
	void stoppedSolving(){
		settings.lockViewer =false;
		textBox.setEnabled(true);
		mixBut.setEnabled(true);
		resetBut.setEnabled(true);
		superBox.setEnabled(true);

		playBut.setEnabled(true);
		revBut.setEnabled(true);
		stepBut.setEnabled(true);
		backBut.setEnabled(true);
		endBut.setEnabled(true);
		beginBut.setEnabled(true);
	}
	// disable all buttons
	void startSolving(){
		settings.lockViewer=true;
		textBox.setEnabled(false);
		mixBut.setEnabled(false);
		resetBut.setEnabled(false);
		superBox.setEnabled(false);

		playBut.setEnabled(false);
		revBut.setEnabled(false);
		stepBut.setEnabled(false);
		backBut.setEnabled(false);
		endBut.setEnabled(false);
		beginBut.setEnabled(false);
	}


	private void stepForward(){
		if( isPlaying ){
			stop();
		}else if( generator!=null && seqPos<generator.getLength() ){
			if( seqPos<0 ) setSequencePosition(0);
			int f=generator.getMoves()[seqPos];
			int q=generator.getAmount()[seqPos];
			if( cubeViewers[viewer].showMove(f,q) ) seqPos++;
			textBox.setText( generator.toString(solution,seqPos) );
		}
	}
	private void stepBackward(){
		if( isPlaying ){
			stop();
		}else if( generator!=null ){
			if( seqPos<0 ) setSequencePosition(generator.getLength());
			if( seqPos>0 ){
				seqPos--;
				int f=generator.getMoves()[seqPos];
				int q=generator.getAmount()[seqPos];
				if(! cubeViewers[viewer].showMove(f,4-q) ) seqPos++;
				textBox.setText( generator.toString(solution,seqPos) );
			}
		}
	}
	private void playForward(){
		if( isPlaying ){
			stop();
		}else if( generator!=null && seqPos<generator.getLength() ){
			if( seqPos<0 ) setSequencePosition(0);
			playFw=true;
			new Thread(this).start();
		}
	}
	private void playBackward(){
		if( isPlaying ){
			stop();
		}else if( generator!=null ){
			if( seqPos<0 ) setSequencePosition(generator.getLength());
			if( seqPos>0 ){
				playFw=false;
				new Thread(this).start();
			}
		}
	}

	private void setSequencePosition(int p){
		if( generator==null ){
			textBox.setText( "" );
			seqPos=-1;
		}else{
			if( p>generator.getLength() ) p=generator.getLength();
			seqPos = p;
			if( p>=0 ) settings.cubePos.doSequence( generator, p );
			textBox.setText( generator.toString(solution,seqPos) );
			updateStatus(true);
		}
	}

    public void itemStateChanged(ItemEvent e) {
		int i;
		Object src = e.getSource();
		if( src == superBox ){
			settings.superGroup=!settings.superGroup;
			//now set box to reflect actual choice of group
			superBox.setState( settings.superGroup );
			//update group solvability flags
			updateStatus(true);
			// update view
			cubeViewers[viewer].repaint();
            return;
		}else if( src == symTwoColBox ){
			symTwoCol=!symTwoCol;
			//set box to reflect actual choice
			symTwoColBox.setState( symTwoCol );
            return;
		}
		for( i=0;i<NUMGROUPS; i++){
			if( src == groupRadioBox[i] ){
				settings.group=i;
				//now set box to reflect actual choice of group
				cubeGroup.setSelectedCheckbox(groupRadioBox[settings.group]);
                //update (group solvability flags and) solve button
                updateStatus(false);
				return;
			}
		}
    }
    public void focusLost(FocusEvent e) {
		Object src = e.getSource();
		if( src==textBox ){
			textChanged();
		}
	}
	public void focusGained(FocusEvent e){}

	private void textChanged(){
		if( !settings.solving ) {
			if(generator==null) generator = new MoveSequence();
			generator.parse(textBox.getText(), solution);
			seqPos = generator.getLength();
			textBox.setText( generator.toString(solution,seqPos) );
			settings.cubePos.doSequence( generator );
			updateStatus(true);
			cubeViewers[viewer].repaint();
		}
	}

	public void scramblePerformed(Integer moveNumber) {
		// TODO Auto-generated method stub
	}

	public void enableControls3(boolean i) {
		// TODO Auto-generated method stub
	}
	
	public boolean isViewerLocked() {
		// TODO Auto-generated method stub
		return false;
	}
}
