package de.uni_muenster.hugo.viewer;

/**
 * The Interface ViewerDelegate.
 * 
 * @author Christian Grelle
 */
public interface ViewerDelegate {

	/**
	 * Scramble performed.
	 * 
	 * @param moveNumber the move number
	 */
	void scramblePerformed(Integer moveNumber);
	
	/**
	 * Checks if is viewer locked.
	 * 
	 * @return true, if is viewer locked
	 */
	public boolean isViewerLocked();
	
	/**
	 * 
	 * Enables and disables the controls of the cassete recorder buttons.
	 * 
	 * @param bool the determinant
	 */
	public void enableControls3(boolean bool);
	
}
