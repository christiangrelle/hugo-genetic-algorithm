package de.uni_muenster.hugo.cubie;

/**
 * The Class Settings. Initializes the Cubie settings.
 * 
 * @author Jaap Scherphuis
 * @author Christian Grelle
 * 
 */
public final class Settings
{
	/** The current cube group. */
	public int group = 0;	// 
	
	/** Set if centre orientation visible.*/
	public boolean superGroup = false; 
	
	/** Set while some solver is busy*/
	public boolean solving = false;
	
	/** The generator. */
	public MoveSequence generator = null;	
	
	/** Set when edit mode, else play mode.. */
	public boolean edit = false;	

	/** Set by cubie to disable user interaction on viewer. */
	public boolean lockViewer = false;	
	
	/** The current cube position. */
	public CubePosition cubePos = new CubePosition();
}