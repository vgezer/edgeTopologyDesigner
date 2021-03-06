package com.mxgraph.examples.swing.editor;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;

public class EditorPopupMenu extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3132749140550242191L;

	public EditorPopupMenu(BasicGraphEditor editor)
	{
		boolean selected = !editor.getGraphComponent().getGraph()
				.isSelectionEmpty();
		boolean onlyOneSelected = (editor.getGraphComponent().getGraph()
				.getSelectionCount() == 1) ? true: false;

		add(editor.bind(mxResources.get("undo"), new HistoryAction(true),
				"/com/mxgraph/examples/swing/images/undo.gif"));

		addSeparator();

		add(
				editor.bind(mxResources.get("cut"), TransferHandler
						.getCutAction(),
						"/com/mxgraph/examples/swing/images/cut.gif"))
				.setEnabled(selected);
		add(
				editor.bind(mxResources.get("copy"), TransferHandler
						.getCopyAction(),
						"/com/mxgraph/examples/swing/images/copy.gif"))
				.setEnabled(selected);
		add(editor.bind(mxResources.get("paste"), TransferHandler
				.getPasteAction(),
				"/com/mxgraph/examples/swing/images/paste.gif"));

		addSeparator();

		add(
				editor.bind(mxResources.get("delete"), mxGraphActions
						.getDeleteAction(),
						"/com/mxgraph/examples/swing/images/delete.gif"))
				.setEnabled(selected);

		addSeparator();

		// Creates the format menu
		JMenu formatMenu = new JMenu(mxResources.get("format"));
		formatMenu.setEnabled(selected);
		JMenu menu = (JMenu) add(formatMenu);

		EditorMenuBar.populateFormatMenu(menu, editor);

		// Creates the shape menu
		JMenu shapeMenu = new JMenu(mxResources.get("shape"));
		shapeMenu.setEnabled(selected);
		menu = (JMenu) add(shapeMenu);

		EditorMenuBar.populateShapeMenu(menu, editor);

		addSeparator();
		

		add(editor.bind(mxResources.get("edit"), mxGraphActions
						.getEditAction())).setEnabled(onlyOneSelected);
		add(editor.bind(mxResources.get("setServerId"), mxGraphActions.getServerIdChangeAction(),
						"/com/mxgraph/examples/swing/images/wrench.png")).setEnabled(onlyOneSelected);
		add(editor.bind(mxResources.get("warning"), mxGraphActions.getWarningMessageChangeAction(),
				"/com/mxgraph/examples/swing/images/rule.gif")).setEnabled(onlyOneSelected);
		addSeparator();

		add(editor.bind(mxResources.get("selectEdges"), mxGraphActions
				.getSelectVerticesAction()));
		add(editor.bind(mxResources.get("selectVertices"), mxGraphActions
				.getSelectEdgesAction()));

		addSeparator();

		add(editor.bind(mxResources.get("selectAll"), mxGraphActions
				.getSelectAllAction()));
	}

}
