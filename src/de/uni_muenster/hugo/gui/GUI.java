package de.uni_muenster.hugo.gui;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;

import de.uni_muenster.hugo.genetic_solver.GeneticSolver;
import de.uni_muenster.hugo.genetic_solver.GeneticSolverDelegate;
import de.uni_muenster.hugo.viewer.Viewer;
import de.uni_muenster.hugo.viewer.Viewer3D;
import de.uni_muenster.hugo.viewer.ViewerBox;
import de.uni_muenster.hugo.viewer.ViewerDelegate;
import de.uni_muenster.hugo.viewer.ViewerDiag;
import de.uni_muenster.hugo.viewer.ViewerFlat;
import de.uni_muenster.hugo.cubie.*;

/**
 * The Class Graphical User Interface including all visible elements and related methods.
 * The GUI contains the buttons, labels, textarea, textfields and more interactive elements
 * to enable communication to the user. An actionPerformed method performs the actions
 * that are invoked by the interaction. Some helper methods support the actionPerformed
 * method. In the end, all generated settings are sent to the genetic solver for calculation.
 * 
 * @author Christian Grelle & contributors
 */
public class GUI extends JFrame implements ActionListener, ViewerDelegate, GeneticSolverDelegate, Runnable {
	
	private static final long serialVersionUID = 1L;

	// Solver
	/** The solver. */
	private GeneticSolver solver;
	
	// Process controls
	/** Is playing. */
	boolean isPlaying = false;
	
	/** Viewer locked. */
	boolean viewerLocked = false;
	
	/** Reseted. */
	boolean reseted = true;
	
	/** Play forward. */
	boolean playFwd = true;
	
	/** The face. */
	int[] f;
	
	/** The face temporary. */
	int[] f_tmp;
	
	/** The quarter turn. */
	int[] q;
	
	/** The quarter turn temporary. */
	int[] q_tmp;
	
	/** The sequence position. */
	int seqPos=0;
	
	/** The solution string. */
	private String solutionString = "";
	
	/** The scramble field text. */
	String scrambleFieldText = "";
	
	/** solution. */
	boolean solution = false;
    
    /** The viewer count. */
    int viewer = 0;
    
    /** The repetitions. */
    int repetitions = 1;
    
    /** The comma seperated value file. */
    File csv = new File("hugo_statistics.csv");
    
    // FileChooser
    /** The chooser status. */
    JFileChooser chooserStat = new JFileChooser(new File("."));
    
	//Buttons
	/** The f button. */
	private JButton fBut = new JButton("F");
	
	/** The u button. */
	private JButton uBut = new JButton("U");
	
	/** The r button. */
	private JButton rBut = new JButton("R");
	
	/** The b button. */
	private JButton bBut = new JButton("B");
	
	/** The d button. */
	private JButton dBut = new JButton("D");
	
	/** The l button. */
	private JButton lBut = new JButton("L");
	
	/** The f2 button. */
	private JButton f2But = new JButton("F2");
	
	/** The u2 button. */
	private JButton u2But = new JButton("U2");
	
	/** The r2 button. */
	private JButton r2But = new JButton("R2");
	
	/** The b2 button. */
	private JButton b2But = new JButton("B2");
	
	/** The d2 button. */
	private JButton d2But = new JButton("D2");
	
	/** The l2 button. */
	private JButton l2But = new JButton("L2");
	
	/** The fa button. */
	private JButton faBut = new JButton("F'");
	
	/** The ua button. */
	private JButton uaBut = new JButton("U'");
	
	/** The ra button. */
	private JButton raBut = new JButton("R'");
	
	/** The ba button. */
	private JButton baBut = new JButton("B'");
	
	/** The da button. */
	private JButton daBut = new JButton("D'");
	
	/** The la button. */
	private JButton laBut = new JButton("L'");
	
	/** The edit button. */
	private JButton edit = new JButton("Edit");
    
    /** The reset button. */
    private JButton reset = new JButton("Reset");
    
    /** The solve button. */
    private JButton solve = new JButton("Solve");
    
    /** The change view button. */
    private JButton changeView = new JButton("Change view");
    
    /** The reset view button. */
    private JButton resetView = new JButton("Reset view");
    
    /** The save monitor button. */
    private JButton saveMonitor = new JButton("Save monitor");
    
    /** The change path button. */
    private JButton changePath = new JButton("Change path");
    
    /** The cancel button. */
    private JButton cancel = new JButton("Cancel");
    
    /** The play button. */
    private Button playBut = new ImButton(1);
    
    /** The reverse button. */
    private Button revBut = new ImButton(0);
    
    /** The step button. */
    private Button stepBut = new ImButton(5);
    
    /** The step back button. */
    private Button backBut = new ImButton(4);
	
    // Gridbag Constraints
	/** The insets. */
    Insets insets = new Insets(2,2,2,2);
	
	// Spinner
	/** The model1. */
	SpinnerNumberModel model1 = new SpinnerNumberModel(1, 1, 99999999, 1);
    
    /** The repetitions spinner. */
    JSpinner repSpinner = new JSpinner(model1);
	
	// Text
	/** The text area. */
	JTextArea textArea = new JTextArea();
	
	/** The scroll pane. */
	JScrollPane scrollPane = new JScrollPane(textArea);
	
	/** The scramble text field. */
	JTextField scrambleTextField = new JTextField("");
	
	/** The solution text field. */
	JTextField solutionTextField = new JTextField("");
	
	/** The generations1 text field. */
	JTextField generations1TextField = new JTextField("20");
	
	/** The generations2 text field. */
	JTextField generations2TextField = new JTextField("300");
	
	/** The generations3 text field. */
	JTextField generations3TextField = new JTextField("50");
	
	/** The population size1 text field. */
	JTextField populationSize1TextField = new JTextField("50");
	
	/** The population size2 text field. */
	JTextField populationSize2TextField = new JTextField("50");
	
	/** The population size3 text field. */
	JTextField populationSize3TextField = new JTextField("50");
	
	/** The individual size1 text field. */
	JTextField individualSize1TextField = new JTextField("30");
	
	/** The individual size2 text field. */
	JTextField individualSize2TextField = new JTextField("30");
	
	/** The individual size3 text field. */
	JTextField individualSize3TextField = new JTextField("30");
	
	/** The crossover prob1 text field. */
	JTextField crossoverProb1TextField = new JTextField("0.6");
	
	/** The crossover prob2 text field. */
	JTextField crossoverProb2TextField = new JTextField("0.6");
	
	/** The crossover prob3 text field. */
	JTextField crossoverProb3TextField = new JTextField("0.6");
	
	/** The mutation prob1 text field. */
	JTextField mutationProb1TextField = new JTextField("0.03");
	
	/** The mutation prob2 text field. */
	JTextField mutationProb2TextField = new JTextField("0.03");
	
	/** The mutation prob3 text field. */
	JTextField mutationProb3TextField = new JTextField("0.03");
	
	/** The save path text field. */
	JTextField savePathTextField = new JTextField(csv.getAbsolutePath());
	
	// Tabbed pane
	/** The tabbed pane. */
	JTabbedPane tabbedPane = new JTabbedPane();
	
	/** The phase1 panel. */
	JPanel phase1Panel = new JPanel();
	
	/** The phase2 panel. */
	JPanel phase2Panel = new JPanel();
	
	/** The phase3 panel. */
	JPanel phase3Panel = new JPanel();
	
	/** The statistics panel. */
	JPanel statisticsPanel = new JPanel();
	
	// Checkboxes save statistics
	  /** The checkbox. */
	JCheckBox cb = new JCheckBox( "", true );
	  
	// Progressbars
	  /** The progress bar1. */
	JProgressBar progressBar1 = new JProgressBar(0,100);
	  
  	/** The progress bar2. */
  	JProgressBar progressBar2 = new JProgressBar(0,100);

	// Labels
	/** The scrambel label. */
	JLabel scrambelLabel = new JLabel("Scramble:");
	
	/** The solution label. */
	JLabel solutionLabel = new JLabel("Solution:");
	
	/** The generations1 label. */
	JLabel generations1Label = new JLabel("Generations:");
	
	/** The generations2 label. */
	JLabel generations2Label = new JLabel("Generations:");
	
	/** The generations3 label. */
	JLabel generations3Label = new JLabel("Generations:");
	
	/** The population size1 label. */
	JLabel populationSize1Label = new JLabel("Population size:");
	
	/** The population size2 label. */
	JLabel populationSize2Label = new JLabel("Population size:");
	
	/** The population size3 label. */
	JLabel populationSize3Label = new JLabel("Population size:");
	
	/** The individual size1 label. */
	JLabel individualSize1Label = new JLabel("Individual size:");
	
	/** The individual size2 label. */
	JLabel individualSize2Label = new JLabel("Individual size:");
	
	/** The individual size3 label. */
	JLabel individualSize3Label = new JLabel("Individual size:");
	
	/** The crossover prob1 label. */
	JLabel crossoverProb1Label = new JLabel("Crossover probability:");
	
	/** The crossover prob2 label. */
	JLabel crossoverProb2Label = new JLabel("Crossover probability:");
	
	/** The crossover prob3 label. */
	JLabel crossoverProb3Label = new JLabel("Crossover probability:");
	
	/** The mutation prob1 label. */
	JLabel mutationProb1Label = new JLabel("Mutation probability:");
	
	/** The mutation prob2 label. */
	JLabel mutationProb2Label = new JLabel("Mutation probability:");
	
	/** The mutation prob3 label. */
	JLabel mutationProb3Label = new JLabel("Mutation probability:");
	
	/** The create statistics label. */
	JLabel createStatisticsLabel = new JLabel("Create CSV statistics:");
	
	/** The repetitions label. */
	JLabel repetitionsLabel = new JLabel("Calculation repetitions:");
	
	/** The pb1 label. */
	JLabel pb1Label = new JLabel("");
	
	/** The pb2 label. */
	JLabel pb2Label = new JLabel("");
	
	// Algorithm Settings
	/** The settings. */
	Settings settings = new Settings();
	
	// Writes scramble in scrambleTextField
	/* (non-Javadoc)
	 * @see ViewerDelegate#scramblePerformed(java.lang.Integer)
	 */
	public void scramblePerformed(Integer moveNumber) {
		scrambleFieldText += numberToLetterConversion(moveNumber) + " ";
		scrambleTextField.setText(scrambleFieldText);
	}
	
	/**
	 * Number to letter conversion.
	 * Converts scrambles numbers into letter notation.
	 * 
	 * @param number the number
	 * 
	 * @return the string
	 */
	public String numberToLetterConversion(int number){
		switch (number){   
	     case  0: return "F";
	     case  1: return "U";
	     case  2: return "R";
	     case  3: return "B";
	     case  4: return "D";
	     case  5: return "L";
	     case  6: return "F2";
	     case  7: return "U2";
	     case  8: return "R2";
	     case  9: return "B2";
	     case  10: return "D2";
	     case  11: return "L2";
	     case  12: return "F'";
	     case  13: return "U'";
	     case  14: return "R'";
	     case  15: return "B'";
	     case  16: return "D'";
	     case  17: return "L'";   
	     default: return "";
		}
	}
	

	// Cube Viewers
	/** The viewer id. */
	private int viewerId = 0;
	
	/** The cube viewers. */
	Viewer cubeViewers[];
	
	/** An instantiation of the kociemba solver */
	public Solver solverKoc = new SolverKociemba(this);
	
    /**
     * Creates the GUI and shows it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI() {
    	
    	solver = new GeneticSolver(this);
        
    	// Mainpanel (works as Container with Gridbaglayout)
        JFrame frame = new JFrame("HuGO!");
        GridBagLayout g = new GridBagLayout();
        frame.setLayout(g);
        frame.setBounds(170, 180, 800, 600);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = insets;
        c.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = insets;
        c2.fill = GridBagConstraints.BOTH;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        scrollPane.setAutoscrolls(true);
        
        // Viewer for cube
        Viewer tempViewersArray[] = {
    			new Viewer3D(300 ,350,settings,this, this),
    			new ViewerDiag(300 ,350,settings,this, this),
    			new ViewerBox(300 ,350,settings,this, this),
    			new ViewerFlat(300 ,350,settings,this, this)
    	};
        cubeViewers = tempViewersArray;
        
        // Add all Viewers to the frame
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 6;
        c.gridheight = 1;
        for(int i=0; i < cubeViewers.length; i++) {
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 6;
            c.gridheight = 1;
            
        	cubeViewers[i].setSize(300, 350);
        	g.setConstraints(cubeViewers[i], c);
        	Insets insets = new Insets(2,7,2,2);
        	c.insets=insets;
            frame.add(cubeViewers[i], c);
            cubeViewers[i].setVisible(true);
        	if(i == viewerId)
        		cubeViewers[i].setVisible(true);
        }
        Insets insets = new Insets(2,2,2,2);
    	c.insets=insets;
        
        // Progressbars
        c2.gridx = 0;
        c2.gridy = 0;
        c2.gridwidth = 6;
        c2.gridheight = 1;
        g.setConstraints(progressBar1, c2);
        progressBar1.setString(pb1Label.getText());
		progressBar1.setStringPainted(false);
        frame.add(progressBar1);
        
        c2.gridx = 11;
        c2.gridy = 5;
        c2.gridwidth = 3;
        c2.gridheight = 1;
        g.setConstraints(progressBar2, c2);
        progressBar2.setString(pb2Label.getText());
        progressBar2.setStringPainted(false);
        progressBar2.setIndeterminate(false);
        frame.add(progressBar2);
        
        // Top Buttons
        c.gridx = 8;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        edit.setPreferredSize(new Dimension(122,26));
        g.setConstraints(edit, c);
        frame.add(edit);
        c.gridx = 6;
        c.gridy = 0;
        c.gridwidth=2;
        reset.setPreferredSize(new Dimension(122,26));
        g.setConstraints(reset, c);
        frame.add(reset);
        c.gridx = 10;
        c.gridy = 0;
        c.gridwidth=2;
        changeView.setPreferredSize(new Dimension(122,26));
        g.setConstraints(changeView, c);
        frame.add(changeView);
        c.gridx = 12;
        c.gridy = 0;
        c.gridwidth=2;
        resetView.setPreferredSize(new Dimension(122,26));
        g.setConstraints(resetView, c);
        frame.add(resetView);

        c.gridx = 14;
        c.gridy = 0;
        c.gridwidth=2;
        saveMonitor.setPreferredSize(new Dimension(122,26));
        g.setConstraints(saveMonitor, c);
        frame.add(saveMonitor);
        
        c.gridx = 14;
        c.gridy = 5;
        c.gridwidth=2;
        solve.setPreferredSize(new Dimension(122,26));
        g.setConstraints(solve, c);
        frame.add(solve);

        // Textfield for output
        c2.gridx = 6;
        c2.gridy = 1;
        c2.gridwidth = 12;
        c2.gridheight = 2;
        g.setConstraints(scrollPane, c2);
        frame.add(scrollPane);
        textArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        textArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        textArea.setEditable(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension (580, 50));
        
        // Scramble and Solution
        c.gridx = 6;
        c.gridy = 3;
        c.gridheight = 1;
        c.gridwidth = 1;
        g.setConstraints(scrambelLabel, c);
        frame.add(scrambelLabel);
        
        c.gridx = 7;
        c.gridy = 3;
        c.gridheight = 1;
        c.gridwidth = 11;
        g.setConstraints(scrambleTextField, c);
        scrambleTextField.setPreferredSize(new Dimension(1,26)); 
        scrambleTextField.setEditable(false);
        frame.add(scrambleTextField);
        
        c.gridx = 6;
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 1;
        g.setConstraints(solutionLabel, c);
        frame.add(solutionLabel);
        
        c.gridx = 7;
        c.gridy = 4;
        c.gridheight = 1;
        c.gridwidth = 11;
        g.setConstraints(solutionTextField, c);
        solutionTextField.setPreferredSize(new Dimension(1,26));
        solutionTextField.setEditable(false);
        frame.add(solutionTextField);
        
        // Solution Buttons
        c.gridx = 7;
        c.gridy = 5;
        c.gridheight = 1;
        c.gridwidth = 1;
        g.setConstraints(revBut, c);
        revBut.setPreferredSize(new Dimension(59,26));
        frame.add(revBut);
        
        c.gridx = 8;
        c.gridy = 5;
        g.setConstraints(backBut, c);
        backBut.setPreferredSize(new Dimension(59,26));
        frame.add(backBut);
 
        c.gridx = 9;
        c.gridy = 5;
        g.setConstraints(stepBut, c);
        stepBut.setPreferredSize(new Dimension(59,26));
        frame.add(stepBut);
       
        c.gridx = 10;
        c.gridy = 5;
        g.setConstraints(playBut, c);
        playBut.setPreferredSize(new Dimension(59,26));
        frame.add(playBut);
        enableControls3(false);
        
        // Cube Control Keyboard
        c.gridx = 0;
        c.gridy = 3;
        g.setConstraints(fBut, c);
        frame.add(fBut);
        
        c.gridx = 1;
        c.gridy = 3;
        g.setConstraints(uBut, c);
        frame.add(uBut);
        
        c.gridx = 2;
        c.gridy = 3;
        g.setConstraints(rBut, c);
        frame.add(rBut);
        
        c.gridx = 3;
        c.gridy = 3;
        g.setConstraints(bBut, c);
        frame.add(bBut);
        
        c.gridx = 4;
        c.gridy = 3;
        g.setConstraints(dBut, c);
        frame.add(dBut);
        
        c.gridx = 5;
        c.gridy = 3;
        g.setConstraints(lBut, c);
        frame.add(lBut);
        
        c.gridx = 0;
        c.gridy = 4;
        g.setConstraints(f2But, c);
        frame.add(f2But);
        
        c.gridx = 1;
        c.gridy = 4;
        g.setConstraints(u2But, c);
        frame.add(u2But);
        
        c.gridx = 2;
        c.gridy = 4;
        g.setConstraints(r2But, c);
        frame.add(r2But);
        
        c.gridx = 3;
        c.gridy = 4;
        g.setConstraints(b2But, c);
        frame.add(b2But);
        
        c.gridx = 4;
        c.gridy = 4;
        g.setConstraints(d2But, c);
        frame.add(d2But);
        
        c.gridx = 5;
        c.gridy = 4;
        g.setConstraints(l2But, c);
        frame.add(l2But);
        
        c.gridx = 0;
        c.gridy = 5;
        g.setConstraints(faBut, c);
        frame.add(faBut);
        
        c.gridx = 1;
        c.gridy = 5;
        g.setConstraints(uaBut, c);
        frame.add(uaBut);
        
        c.gridx = 2;
        c.gridy = 5;
        g.setConstraints(raBut, c);
        frame.add(raBut);
        
        c.gridx = 3;
        c.gridy = 5;
        g.setConstraints(baBut, c);
        frame.add(baBut);
        
        c.gridx = 4;
        c.gridy = 5;
        g.setConstraints(daBut, c);
        frame.add(daBut);
        
        c.gridx = 5;
        c.gridy = 5;
        g.setConstraints(laBut, c);
        frame.add(laBut);
        
        // panel
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 6;
        c.gridheight = 1;
        g.setConstraints(tabbedPane, c);
        frame.add(tabbedPane);
		tabbedPane.addTab( "Phase 1", phase1Panel );
    	tabbedPane.addTab( "Phase 2", phase2Panel );
    	tabbedPane.addTab( "Phase 3", phase3Panel );
    	tabbedPane.addTab( "Statistics", statisticsPanel );
    	
    	phase1Panel.setLayout( new GridLayout( 5, 2 ) );
    	phase1Panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    	phase2Panel.setLayout( new GridLayout( 5, 2 ) );
    	phase2Panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    	phase3Panel.setLayout( new GridLayout( 5, 2 ) );
    	phase3Panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    	statisticsPanel.setLayout( new GridLayout( 5, 2 ) );
    	statisticsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    	
    	phase1Panel.add( generations1Label );
    	phase1Panel.add( generations1TextField );
    	phase1Panel.add( populationSize1Label );
    	phase1Panel.add( populationSize1TextField );
    	phase1Panel.add( individualSize1Label );
    	phase1Panel.add( individualSize1TextField );
    	phase1Panel.add( crossoverProb1Label );
    	phase1Panel.add( crossoverProb1TextField );
    	phase1Panel.add( mutationProb1Label );
    	phase1Panel.add( mutationProb1TextField );
    	
    	phase2Panel.add( generations2Label );
    	phase2Panel.add( generations2TextField );
     	phase2Panel.add( populationSize2Label );
    	phase2Panel.add( populationSize2TextField );
    	phase2Panel.add( individualSize2Label );
    	phase2Panel.add( individualSize2TextField );
    	phase2Panel.add( crossoverProb2Label );
    	phase2Panel.add( crossoverProb2TextField );
    	phase2Panel.add( mutationProb2Label );
    	phase2Panel.add( mutationProb2TextField );
    	
    	phase3Panel.add( generations3Label );
    	phase3Panel.add( generations3TextField );
     	phase3Panel.add( populationSize3Label );
    	phase3Panel.add( populationSize3TextField );
    	phase3Panel.add( individualSize3Label );
    	phase3Panel.add( individualSize3TextField );
    	phase3Panel.add( crossoverProb3Label );
    	phase3Panel.add( crossoverProb3TextField );
    	phase3Panel.add( mutationProb3Label );
    	phase3Panel.add( mutationProb3TextField );
    	
   
    	statisticsPanel.add(repetitionsLabel);
    	statisticsPanel.add(repSpinner);
    	statisticsPanel.add(new JLabel(""));
    	statisticsPanel.add(new JLabel(""));
     	statisticsPanel.add( createStatisticsLabel );
    	statisticsPanel.add( cb );
    	statisticsPanel.add(new JLabel("Save path:"));
    	savePathTextField.setBackground(statisticsPanel.getBackground());
    	savePathTextField.setPreferredSize(new Dimension(1,1));
    	savePathTextField.setEditable(false);
    	statisticsPanel.add(savePathTextField);
    	statisticsPanel.add(new JLabel(""));
    	statisticsPanel.add(changePath);
    	
        // ActionListener for all buttons
        fBut.addActionListener(this);
        uBut.addActionListener(this);
    	rBut.addActionListener(this);
    	bBut.addActionListener(this);
    	dBut.addActionListener(this);
    	lBut.addActionListener(this);
    	f2But.addActionListener(this);
    	u2But.addActionListener(this);
    	r2But.addActionListener(this);
    	b2But.addActionListener(this);
    	d2But.addActionListener(this);
    	l2But.addActionListener(this);
    	faBut.addActionListener(this);
    	uaBut.addActionListener(this);
    	raBut.addActionListener(this);
    	baBut.addActionListener(this);
    	daBut.addActionListener(this);
    	laBut.addActionListener(this);
    	
        reset.addActionListener(this);
        solve.addActionListener(this);
        changeView.addActionListener(this);
        resetView.addActionListener(this);
        edit.addActionListener(this);
        saveMonitor.addActionListener(this);
        changePath.addActionListener(this);
        cancel.addActionListener(this);
        
        playBut.addActionListener(this);
        revBut.addActionListener(this);
        stepBut.addActionListener(this);
        backBut.addActionListener(this);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        printLogo();
    }

    /**
     * The main method.
     * Starts the splash screen. Can be configured to use system dependent look and feel by deleting the comment tags
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {
    	   try {
			   GUI gui = new GUI();
			   gui.createAndShowGUI();
    	        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	      } catch (Exception e) {
    	        e.printStackTrace();
    	      }
    	//SplashScreen splashScreen = new SplashScreen ("images/hugo_logo.png");
    	//splashScreen.open (4500);
    }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings({ "deprecation", "unchecked", "static-access" })
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		//top buttons
		if(src == changeView) {
			viewerId++;
			if(viewerId == cubeViewers.length)
				viewerId = 0;
			for(int i=0; i < cubeViewers.length; i++) {
	        	if(i == viewerId)
	        		cubeViewers[i].setVisible(true);
	        	else
	        		cubeViewers[i].setVisible(false);
	        }
		}
		
		if(src == resetView) {
			settings.cubePos.resetView();
			for(int i=0;i<cubeViewers.length;i++){
				cubeViewers[i].reset();
			}
		}
		
		if(src == reset) {
			reset();
			settings.cubePos.resetView();
			for(int i=0;i<cubeViewers.length;i++){
				cubeViewers[i].reset();
			}
		}
		
		if(src == edit) {
			settings.edit=!settings.edit;			
			// edit.setLabel( settings.edit? "Play":"Edit");
			// seqPos=-1;
			// if(!settings.edit) {
			//	 reset();
			// }
			edit.setEnabled(false);
		}
		
		if(src == saveMonitor) {
		saveMonitor();   
		}
		
		//solve button
		if(src == solve) {
			if(solve.getText()=="Cancel"){
				cancel();
			}else{
			boolean parametersValid;
			textArea.setText("");
			solver = new GeneticSolver(this);
			parametersValid = sendValidParam();
			if(parametersValid){
				solve.setLabel("Cancel");
				repetitions=Integer.parseInt(repSpinner.getValue().toString());
				progressBar1.setMaximum(repetitions);
				progressBar1.setStringPainted(true);
				progressBar2.setStringPainted(true);
				updateProgressBar1(0);
				seqPos = 0;
				if(settings.edit || cubeViewers[viewerId].getScrambleSequence().isEmpty()) {
					solver.initialize(null);
					solver.setCubeScrambled(cubeScrambledConversion(settings.cubePos.faceletColor));
				} else {
					solver.initialize(convertScrambleSequenceToIntArray(cubeViewers[viewerId].getScrambleSequence()));
				}
				solver.start();
			}
			}
		}
		
		if(src == cancel) {
			cancel();
		}
		
		// Cassette recorder buttons
		if(!isPlaying && src == playBut) {
			if(solutionTextField.getText()!="no moves"){
				f = solver.getF();
				q = solver.getQ();
				if(!isPlaying && seqPos != f.length) {				
					isPlaying = true;
					playFwd = true;
					Thread t = new Thread(this);
					t.start();
				}
			}
		}
	
		if(!isPlaying && src == stepBut) {
			if(solutionTextField.getText()!="no moves"){
				isPlaying = true;
				f = solver.getF();
				q = solver.getQ();
				stepForward();
				isPlaying = false;
			}
		}
		
		if(!isPlaying && src == backBut) {
			if(solutionTextField.getText()!="no moves"){
				isPlaying = true;
				f = solver.getF();
				q = solver.getQ();
				stepBackward();
				isPlaying = false;
			}
		}
		
		if(src == revBut) {
			if(solutionTextField.getText()!="no moves"){
				if(!isPlaying && seqPos != 0) {			
					f = solver.getF();
					q = solver.getQ();
					playFwd = false;
					isPlaying = true;
					Thread t = new Thread(this);
					t.start();				
				}
			}
		}
		
		// Set savepath button
		if(src == changePath){
		try{
			chooserStat.setAcceptAllFileFilterUsed(false);
			chooserStat.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
		    		}
		 
				@Override
				public String getDescription() {
					return "*.csv";
		    		}
			});
			if(chooserStat.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
				String path = chooserStat.getSelectedFile().getPath();
				if (!path.toLowerCase().endsWith(".csv"))
					path = path + ".csv";
				if (new File(path).getParentFile().exists()){
					csv = new File(path);
					savePathTextField.setText(csv.getAbsolutePath());
		    		}else{
		    			JOptionPane.showMessageDialog(null,"Destination file path does not exist at\n"+new File(path).getPath()+"\nFile path not set","Unknown destination",JOptionPane.ERROR_MESSAGE);	
		    		}
		  		}  		
			}
		catch (Exception e2){
			JOptionPane.showMessageDialog(this,e2.getMessage(),"File Error",JOptionPane.ERROR_MESSAGE);
			}
		}
		
		//keyboard
		if(src == fBut) {
			if(cubeViewers[viewerId].showMove(2,1)){
			cubeViewers[viewerId].scrambleSequence.add(0);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == uBut) {
			if(cubeViewers[viewerId].showMove(1,1)){
			cubeViewers[viewerId].scrambleSequence.add(1);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == rBut) {
			if(cubeViewers[viewerId].showMove(3,1)){
			cubeViewers[viewerId].scrambleSequence.add(2);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == bBut) {
			if(cubeViewers[viewerId].showMove(5,1)){
			cubeViewers[viewerId].scrambleSequence.add(3);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == dBut) {
			if(cubeViewers[viewerId].showMove(4,1)){
			cubeViewers[viewerId].scrambleSequence.add(4);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == lBut) {
			if(cubeViewers[viewerId].showMove(0,1)){
			cubeViewers[viewerId].scrambleSequence.add(5);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == f2But) {
			if(cubeViewers[viewerId].showMove(2,2)){
			cubeViewers[viewerId].scrambleSequence.add(6);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == u2But) {
			if(cubeViewers[viewerId].showMove(1,2)){
			cubeViewers[viewerId].scrambleSequence.add(7);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}	
		if(src == r2But) {
			if(cubeViewers[viewerId].showMove(3,2)){
			cubeViewers[viewerId].scrambleSequence.add(8);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == b2But) {
			if(cubeViewers[viewerId].showMove(5,2)){
			cubeViewers[viewerId].scrambleSequence.add(9);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}	
		if(src == d2But) {
			if(cubeViewers[viewerId].showMove(4,2)){
			cubeViewers[viewerId].scrambleSequence.add(10);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == l2But) {
			if(cubeViewers[viewerId].showMove(0,2)){
			cubeViewers[viewerId].scrambleSequence.add(11);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == faBut) {
			if(cubeViewers[viewerId].showMove(2,-1)){
			cubeViewers[viewerId].scrambleSequence.add(12);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == uaBut) {
			if(cubeViewers[viewerId].showMove(1,-1)){
			cubeViewers[viewerId].scrambleSequence.add(13);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == raBut) {
			if(cubeViewers[viewerId].showMove(3,-1)){
			cubeViewers[viewerId].scrambleSequence.add(14);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == baBut) {
			if(cubeViewers[viewerId].showMove(5,-1)){
			cubeViewers[viewerId].scrambleSequence.add(15);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == daBut) {
			if(cubeViewers[viewerId].showMove(4,-1)){
			cubeViewers[viewerId].scrambleSequence.add(16);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(src == laBut) {
			if(cubeViewers[viewerId].showMove(0,-1)){
			cubeViewers[viewerId].scrambleSequence.add(17);
			cubeViewers[viewerId].delegate.scramblePerformed((Integer) cubeViewers[viewerId].scrambleSequence.lastElement());
			enableControls3(false);
			}
		}
		if(settings.edit && seqPos==0){
			checkSolvable();
		}
	}

	/**
	 * Converts vector scramble sequence to integer array.
	 * 
	 * @param scs the vector sequence
	 * 
	 * @return the integer array
	 */
	@SuppressWarnings("unchecked")
	private int[] convertScrambleSequenceToIntArray(Vector scs) {
		int[] array = new int[scs.size()];
		for(int i=0; i<scs.size(); i++) {
			array[i] = (Integer) scs.get(i);
		}
		return array;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * 
	 * Checks for forward or backward play, then performs the move
	 * on the cube and sets the solutionsequence.
	 */
	public void run() {
		enableControls3(false);
		enableControls2(false);
		solve.setEnabled(false);
		if(playFwd) {	
			for(int i=seqPos; i < f.length; i++) {
				seqPos = i;
				seqPos++;
				setSolutionFieldPosition();
				seqPos--;
				cubeViewers[viewerId].showMove(f[seqPos], q[seqPos]);
				try {
					Thread.sleep(800);
				} catch(Exception ignored) {}
			}
			seqPos = f.length;			
		} else {
			for(int i=seqPos-1; i >= 0; i--) {				
				seqPos = i;				
				setSolutionFieldPosition();				
				cubeViewers[viewerId].showMove(f[seqPos], 4-q[seqPos]);
				try {
					Thread.sleep(800);
				} catch(Exception ignored) {}
			}
			seqPos = 0;
		}
		isPlaying = false;
		enableControls3(true);
		if(seqPos==0){
			enableControls2(true);
			solve.setEnabled(true);
		}
	}
	
	/**
	 * Step forward. Performs the move
	 * on the cube and sets the solution sequence.
	 */
	private void stepForward() {
		if(seqPos <= f.length-1) {
			enableControls2(false);
			solve.setEnabled(false);
			if(cubeViewers[viewerId].showMove(f[seqPos], q[seqPos])){
				seqPos++;
				setSolutionFieldPosition();
			}
		}
	}
	
	/**
	 * Step backward. Performs the move
	 * on the cube and sets the solution sequence.
	 */
	private void stepBackward() {
		if(seqPos > 0) {
			if(cubeViewers[viewerId].showMove(f[seqPos-1], 4-q[seqPos-1])){
			seqPos--;
			setSolutionFieldPosition();
			if(seqPos==0){
				enableControls2(true);
				solve.setEnabled(true);
			}
			}
		}
	}
	
	/**
	 * Sets the solution field position.
	 */
	private void setSolutionFieldPosition() {
		// Each move as literal gets 3 spaces.
		int idxForBlank = seqPos*3;
		String before = solutionString.substring(0, idxForBlank);
		String sign = "*";
		String after = solutionString.substring(idxForBlank, solutionString.length());
		solutionTextField.setText(before + sign + after);
	}

	/**
	 * Cube scrambled conversion.
	 * The cubie representation in Cubie gets converted to met the representation of the genetic solver.
	 * Therefore the faces are renamed and the cube is rotated. 
	 * 
	 * @param cube the cube
	 * 
	 * @return the int[]
	 */
	private int[] cubeScrambledConversion(int[] cube){
		int[] convertedCube1 = new int[54];
		int[] convertedCube2 = new int[54];
		
		// Rename faces
		for(int i=0; i<cube.length; i++){
			switch(cube[i]){
			case 2: convertedCube1[i]=0; break;
			case 1: convertedCube1[i]=1; break;
			case 3: convertedCube1[i]=2; break;
			case 5: convertedCube1[i]=3; break;
			case 4: convertedCube1[i]=4; break;
			case 0: convertedCube1[i]=5; break;
			}
		}
		
		// Rotate cube
		System.arraycopy(convertedCube1, 18, convertedCube2, 0, 9); //new F
		System.arraycopy(convertedCube1, 9, convertedCube2, 9, 9); //new U
		System.arraycopy(convertedCube1, 27, convertedCube2, 18, 9); //new R
		System.arraycopy(convertedCube1, 45, convertedCube2, 27, 9); //new B
		System.arraycopy(convertedCube1, 36, convertedCube2, 36, 9); //new D
		System.arraycopy(convertedCube1, 0, convertedCube2, 45, 9); //new L
		
		return convertedCube2;
	}
	
	
	/* (non-Javadoc)
	 * @see ViewerDelegate#isViewerLocked()
	 */
	public boolean isViewerLocked() {
		return viewerLocked;
	}
	
	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#enableControls(boolean)
	 */
	public void enableControls(boolean bool) {
		  fBut.setEnabled(bool);
		  uBut.setEnabled(bool);
		  rBut.setEnabled(bool);
		  bBut.setEnabled(bool);
		  dBut.setEnabled(bool);
		  lBut.setEnabled(bool);
		  f2But.setEnabled(bool);
		  u2But.setEnabled(bool);
		  r2But.setEnabled(bool);
		  b2But.setEnabled(bool);
		  d2But.setEnabled(bool);
		  l2But.setEnabled(bool);
		  faBut.setEnabled(bool);
		  uaBut.setEnabled(bool);
		  raBut.setEnabled(bool);
		  baBut.setEnabled(bool);
		  daBut.setEnabled(bool);
		  laBut.setEnabled(bool);
		
	      playBut.setEnabled(bool);
	      revBut.setEnabled(bool);
	      stepBut.setEnabled(bool);
	      backBut.setEnabled(bool);
	      
	      viewerLocked = !bool;
	}
	
	/**
	 * Enables the controls of the keyboard and the viewer (cube).
	 * 
	 * @param bool the determinant
	 */
	public void enableControls2(boolean bool) {
		  fBut.setEnabled(bool);
		  uBut.setEnabled(bool);
		  rBut.setEnabled(bool);
		  bBut.setEnabled(bool);
		  dBut.setEnabled(bool);
		  lBut.setEnabled(bool);
		  f2But.setEnabled(bool);
		  u2But.setEnabled(bool);
		  r2But.setEnabled(bool);
		  b2But.setEnabled(bool);
		  d2But.setEnabled(bool);
		  l2But.setEnabled(bool);
		  faBut.setEnabled(bool);
		  uaBut.setEnabled(bool);
		  raBut.setEnabled(bool);
		  baBut.setEnabled(bool);
		  daBut.setEnabled(bool);
		  laBut.setEnabled(bool);
	      
	      viewerLocked = !bool;
	}
	
	/* (non-Javadoc)
	 * @see ViewerDelegate#enableControls3(boolean)
	 */
	public void enableControls3(boolean bool) {
	      playBut.setEnabled(bool);
	      revBut.setEnabled(bool);
	      stepBut.setEnabled(bool);
	      backBut.setEnabled(bool);
	}

	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#printTextLn(java.lang.String)
	 */
	public void printTextLn(String text) {
		textArea.append(text+"\n");
		//textArea.setText(textArea.getText() + text + "\n" );
		//textArea.repaint();
		//scrollPane.getVerticalScrollBar().setValue(textArea.getText().length());
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#printText(java.lang.String)
	 */
	public void printText(String text) {
		textArea.append(text);
		//textArea.setText(textArea.getText() + text);
		//textArea.repaint();
		//scrollPane.getVerticalScrollBar().setValue(textArea.getText().length());
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#setSolutionTextField(java.lang.String)
	 */
	public void setSolutionTextField(String text) {
		this.solutionTextField.setText(text);
		this.solutionString = text;
	}
	
	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#setSolution()
	 */
	public void setSolution() {
		solution = true;
	}
	
	/**
	 * Reset the program. Stops a calculation and resets all fields.
	 */
	@SuppressWarnings("deprecation")
	private void reset() {
		scrambleFieldText = "";
		scrambleTextField.setText("");
		solutionTextField.setText("");			
		solutionString = "";
		textArea.setText("");
		f = new int[0];
		q = new int[0];
		cubeViewers[viewerId].resetScrambleSequence();
		seqPos = 0;
		if( !settings.solving ){
            //stop();// stop any animation
			settings.cubePos.reset();
			//setSequencePosition(-1);
			//updateStatus(true);
			cubeViewers[viewer].repaint();
		printLogo();	
		}
		solver.stop();
		solver = new GeneticSolver(this);
		reseted = true;	
		enableControls2(true);
		enableControls3(false);
		solve.setEnabled(true);
		edit.setEnabled(true);
		settings.edit=false;
		updateProgressBar1(-1);
		updateProgressBar2(0);
		progressBar2.setIndeterminate(false);
		solve.setLabel("Solve");
	}
	
	/**
	 * Cancels the program. Only stops a calculation. The field values keep unchanged.
	 */
	@SuppressWarnings("deprecation")
	private void cancel() {
		solver.stop();
		enableControls2(true);
		if(!settings.edit){
			edit.setEnabled(true);
		}
		updateProgressBar2(0);
		progressBar2.setIndeterminate(false);
		solve.setLabel("Solve");
	}
	
	
	/**
	 * Saves monitor. Opens a menu to stores the monitor content.
	 */
	private void saveMonitor( ) {
	      JFileChooser jfc = new JFileChooser(new File("."));
	      int result = jfc.showSaveDialog(this);
	      if(result == JFileChooser.CANCEL_OPTION) return;
	      File file = jfc.getSelectedFile();
	      try {
	         BufferedWriter bw = new BufferedWriter(new FileWriter(file));
	         bw.write(textArea.getText());
	         bw.close();
	      }
	      catch (Exception e) {
	         JOptionPane.showMessageDialog(
	            this,
	            e.getMessage(),
	            "File Error",
	            JOptionPane.ERROR_MESSAGE
	         );
	      }
	   }
	
	/**
	 * Prints the logo of the genetic solver given name to the monitor.
	 */
	public void printLogo(){
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("");
		printTextLn("                                                                                           HuGO!");
		printTextLn("");
		printTextLn("                                                              Human strategy based Genetic Optimizer");
	}
	
	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#pb2ChangeState()
	 */
	public void pb2ChangeState(){
		boolean indeterminate = progressBar2.isIndeterminate();
        progressBar2.setIndeterminate(!indeterminate);
	}
	
	/**
	 * Checks parameters for validity and sends them  to the genetic solver.
	 * If some illegal values are entered, a specific message is sent to the monitor.  
	 * 
	 * @return true, if successful
	 */
	public boolean sendValidParam(){
		int gen1, gen2, gen3;
		int pop1, pop2, pop3;
		int ind1, ind2, ind3;
		double cro1, cro2, cro3;
		double mut1, mut2, mut3;
		boolean csvEnabled;
		
		// Parameter checking
		try {
			gen1 = Integer.parseInt(generations1TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(generations1TextField.getText()+" in [Phase 1: Generations] must be a positive Integer");
			return false;
		}
		if(gen1<=0){
			textArea.setText(generations1TextField.getText()+" in [Phase 1: Generations] must be a positive Integer");
			return false;
		}
		
		try {
			gen2 = Integer.parseInt(generations2TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(generations2TextField.getText()+" in [Phase 2: Generations] must be a positive Integer");
			return false;
		}
		if(gen2<=0){
			textArea.setText(generations2TextField.getText()+" in [Phase 2: Generations] must be a positive Integer");
			return false;
		}
		
		try {
			gen3 = Integer.parseInt(generations3TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(generations3TextField.getText()+" in [Phase 3: Generations] must be a positive Integer");
			return false;
		}
		if(gen3<=0){
			textArea.setText(generations3TextField.getText()+" in [Phase 3: Generations] must be a positive Integer");
			return false;
		}
		
		try {
			pop1 = Integer.parseInt(populationSize1TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(populationSize1TextField.getText()+" in [Phase 1: Population size] must be a positive Integer");
			return false;
		}
		if(pop1<=0){
			textArea.setText(populationSize1TextField.getText()+" in [Phase 1: Population size] must be a positive Integer");
			return false;
		}
		
		try {
			pop2 = Integer.parseInt(populationSize2TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(populationSize2TextField.getText()+" in [Phase 2: Population size] must be a positive Integer");
			return false;
		}
		if(pop2<=0){
			textArea.setText(populationSize2TextField.getText()+" in [Phase 2: Population size] must be a positive Integer");
			return false;
		}
		
		try {
			pop3 = Integer.parseInt(populationSize3TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(populationSize3TextField.getText()+" in [Phase 3: Population size] must be a positive Integer");
			return false;
		}
		if(pop3<=0){
			textArea.setText(populationSize3TextField.getText()+" in [Phase 3: Population size] must be a positive Integer");
			return false;
		}
		
		try {
			ind1 = Integer.parseInt(individualSize1TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(individualSize1TextField.getText()+" in [Phase 1: Individual size] must be a positive Integer");
			return false;
		}
		if(ind1<=0){
			textArea.setText(individualSize1TextField.getText()+" in [Phase 1: Individual size] must be a positive Integer");
			return false;
		}
		
		try {
			ind2 = Integer.parseInt(individualSize2TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(individualSize2TextField.getText()+" in [Phase 2: Individual size] must be a positive Integer");
			return false;
		}
		if(ind2<=0){
			textArea.setText(individualSize2TextField.getText()+" in [Phase 2: Individual size] must be a positive Integer");
			return false;
		}
		
		try {
			ind3 = Integer.parseInt(individualSize3TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(individualSize3TextField.getText()+" in [Phase 3: Individual size] must be a positive Integer");
			return false;
		}
		if(ind3<=0){
			textArea.setText(individualSize3TextField.getText()+" in [Phase 3: Individual size] must be a positive Integer");
			return false;
		}
		
		try {
			cro1 = Double.parseDouble(crossoverProb1TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(crossoverProb1TextField.getText()+" in [Phase 1: Crossover probability] must be a Double in ]0;1]");
			return false;
		}
		if((cro1<=0)||(cro1>1)){
			textArea.setText(crossoverProb1TextField.getText()+" in [Phase 1: Crossover probability] must be a Double in ]0;1]");
			return false;
		}
		
		try {
			cro2 = Double.parseDouble(crossoverProb2TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(crossoverProb2TextField.getText()+" in [Phase 2: Crossover probability] must be a Double in ]0;1]");
			return false;
		}
		if((cro2<=0)||(cro2>1)){
			textArea.setText(crossoverProb2TextField.getText()+" in [Phase 2: Crossover probability] must be a Double in ]0;1]");
			return false;
		}
		
		try {
			cro3 = Double.parseDouble(crossoverProb3TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(crossoverProb3TextField.getText()+" in [Phase 3: Crossover probability] must be a Double in ]0;1]");
			return false;
		}
		if((cro3<=0)||(cro3>1)){
			textArea.setText(crossoverProb3TextField.getText()+" in [Phase 3: Crossover probability] must be a Double in ]0;1]");
			return false;
		}
		
		try {
			mut1 = Double.parseDouble(mutationProb1TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(mutationProb1TextField.getText()+" in [Phase 1: Mutation probability] must be a Double in ]0;1]");
			return false;
		}
		if((mut1<=0)||(mut1>1)){
			textArea.setText(mutationProb1TextField.getText()+" in [Phase 1: Mutation probability] must be a Double in ]0;1]");
			return false;
		}
		
		try {
			mut2 = Double.parseDouble(mutationProb2TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(mutationProb2TextField.getText()+" in [Phase 2: Mutation probability] must be a Double in ]0;1]");
			return false;
		}
		if((mut2<=0)||(mut2>1)){
			textArea.setText(mutationProb2TextField.getText()+" in [Phase 2: Mutation probability] must be a Double in ]0;1]");
			return false;
		}
		
		try {
			mut3 = Double.parseDouble(mutationProb3TextField.getText());
		}
		catch (NumberFormatException e){
			textArea.setText(mutationProb3TextField.getText()+" in [Phase 3: Mutation probability] must be a Double in ]0;1]");
			return false;
		}
		if((mut3<=0)||(mut3>1)){
			textArea.setText(mutationProb3TextField.getText()+" in [Phase 3: Mutation probability] must be a Double in ]0;1]");
			return false;
		}
		
		// Checkbox checking
		if (cb.isSelected()) {
		    csvEnabled = true;
		} else {
			csvEnabled = false;
		}
		
		// Repetitions checking
		try {
			repetitions = Integer.parseInt(repSpinner.getValue().toString());
		}
		catch (NumberFormatException e){
			textArea.setText(repSpinner.getValue().toString()+" in [Statistics: repetitions] must be an Integer in [1;99,999,999]");
			return false;
		}
		if((repetitions<1)||(repetitions>99999999)){
			textArea.setText(repSpinner.getValue().toString()+" in [Statistics: repetitions] must be an Integer in [1;99,999,999]");
			return false;
		}
		
		// Send parameter values to the genetic solver
		solver.setParam(gen1,gen2,gen3,convertEvenToOdd(pop1),convertEvenToOdd(pop2),convertEvenToOdd(pop3),ind1,ind2,ind3,cro1,cro2,cro3,mut1,mut2,mut3,csvEnabled,csv,repetitions);
		return true;
	}
	
	// Update the progress indicator and label
	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#updateProgressBar1(int)
	 */
	public void updateProgressBar1(int task){
		if(task==-1){
			pb1Label.setText("");
			progressBar1.setString(pb1Label.getText());
			progressBar1.setValue(0);
		}else{
			pb1Label.setText("Cubes solved: "+task+" of "+repetitions);
			progressBar1.setString(pb1Label.getText());
			progressBar1.setValue(task);
		}
	}
	
	// Update the progress label
	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#updateProgressBar2(int)
	 */
	@SuppressWarnings("deprecation")
	public void updateProgressBar2(int phase){
		if(phase==0){
			pb2Label.setText("");
			progressBar2.setString(pb2Label.getText());
		}else if (phase==4){
			pb2Label.setText("Cube solved");
			progressBar2.setString(pb2Label.getText());
			solve.setLabel("Solve");
		}else{
			pb2Label.setText("Performing phase "+phase);
			progressBar2.setString(pb2Label.getText());
		}
	}
	
	/**
	 * Checks the solvability of the current cube configuration.
	 */
	public void checkSolvable(){
		solve.setEnabled(solverKoc.setPosition(settings.cubePos, true));
	}
	
	/**
	 * Checks whether a number is even oder odd and adds 1, if it is even to make it odd.
	 * This is used becaues the genetic algorithm needs an odd population size to realize
	 * elitist strategy.
	 * 
	 * @param number an even oder odd number
	 * 
	 * @return the odd number
	 */
	public int convertEvenToOdd(int number){
		if((number%2)==0){
			number+=1;
			return number;
		}else{
			return number;
		}
	}
	
	/* (non-Javadoc)
	 * @see GeneticSolverDelegate#enableEdit(boolean)
	 */
	public void enableEdit(boolean bool){
		if(settings.edit==false){
		edit.setEnabled(bool);
		}
	}
}

