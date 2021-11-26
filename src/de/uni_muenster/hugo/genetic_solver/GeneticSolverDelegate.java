package de.uni_muenster.hugo.genetic_solver;

/**
 * The Interface GeneticSolverDelegate.
 * 
 * @author Christian Grelle
 */
public interface GeneticSolverDelegate {
	
	/**
	 * Prints the text and goes to new line.
	 * 
	 * @param text the text
	 */
	public void printTextLn(String text);
	
	/**
	 * Prints the text.
	 * 
	 * @param text the text
	 */
	public void printText(String text);
	
	/**
	 * Sets the solution.
	 */
	public void setSolution();
	
	/**
	 * Enables all controls.
	 * 
	 * @param bool the determinant
	 */
	public void enableControls(boolean bool);	
	
	/**
	 * Enables edit state of the cube.
	 * 
	 * @param bool the determinant
	 */
	public void enableEdit(boolean bool);
	
	/**
	 * Sets the solution text field.
	 * 
	 * @param text the new solution text field
	 */
	public void setSolutionTextField(String text);
	
	/**
	 * Changes Pgrogressbar2 state from determined to indetermined and the other way round.
	 */
	public void pb2ChangeState();
	
	/**
	 * Updates progressbar1. 
	 * Updates the progress indicator and label.
	 * 
	 * @param task the task
	 */
	public void updateProgressBar1(int task);
	
	/**
	 * Updates progressbar2.
	 * Updates the progress label.
	 * 
	 * @param phase the phase
	 */
	public void updateProgressBar2(int phase);
}
