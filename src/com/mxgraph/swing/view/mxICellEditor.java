/**
 * Copyright (c) 2008, Gaudenz Alder
 */
package com.mxgraph.swing.view;

import java.util.EventObject;

/**
 *
 */
public interface mxICellEditor
{

	/**
	 * Returns the cell that is currently being edited.
	 */
	public Object getEditingCell();

	/**
	 * Starts editing the given cell.
	 */
	public void startEditing(Object cell, EventObject trigger);
	
	
	public void startServerIdChange(Object cell, EventObject trigger);
	
	public void stopServerIdChange(boolean cancel);

	/**
	 * Stops the current editing.
	 */
	public void stopEditing(boolean cancel);

}
