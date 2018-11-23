/**
 * Copyright (c) 2012, JGraph Ltd
 */

package com.mxgraph.examples.swing.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.mxgraph.analysis.StructuralException;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphGenerator;
import com.mxgraph.analysis.mxTraversal;
import com.mxgraph.analysis.mxGraphProperties;
import com.mxgraph.analysis.mxGraphProperties.GraphType;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraph.mxICellVisitor;

public class GraphConfigDialog extends JDialog {
	/**
	 * Number of nodes
	 */
	protected int numNodes = 6;

	/**
	 * Number of edges
	 */
	protected int numEdges = 6;

	/**
	 * Valence
	 */
	protected int valence = 2;

	/**
	 * Number of rows for a grid graph
	 */
	protected int numRows = 8;

	protected int numVertexesInBranch = 3;

	public int getNumVertexesInBranch() {
		return numVertexesInBranch;
	}

	public void setNumVertexesInBranch(int numVertexesInBranch) {
		this.numVertexesInBranch = numVertexesInBranch;
	}

	/**
	 * Number of columns for a grid graph
	 */
	protected int numColumns = 8;

	protected int minWeight = 1;

	public int getMinWeight() {
		return minWeight;
	}

	public void setMinWeight(int minWeight) {
		this.minWeight = minWeight;
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}

	protected int maxWeight = 10;

	/**
	 * Number of vertexes for the left group in a bipartite graph
	 */
	protected int numVertexesLeft = 5;

	/**
	 * Number of vertexes for the right group in a bipartite graph
	 */
	protected int numVertexesRight = 5;

	/**
	 * The start vertex (by value) for various algorithms
	 */
	protected String startVertexValue = "";

	/**
	 * The end vertex (by value) for various algorithms (mostly pathfinding)
	 */
	protected String endVertexValue = "";

	protected int numBranches = 4;

	public int getNumBranches() {
		return numBranches;
	}

	public void setNumBranches(int numBranches) {
		this.numBranches = numBranches;
	}

	/**
	 * If set, arrowheads are drawn
	 */
	protected boolean arrows = false;

	protected boolean weighted = false;

	/**
	 * If set, self-loops are allowed during graph generation
	 */
	protected boolean allowSelfLoops = false;

	/**
	 * If set, parallel edges are allowed during graph generation
	 */
	protected boolean allowMultipleEdges = false;

	/**
	 * If set, the generated graph will be always connected
	 */
	protected boolean forceConnected = true;

	/**
	 * Spacing for groups in a bipartite graph
	 */
	protected float groupSpacing = 200;

	/**
	 * Grid spacing for a grid graph
	 */
	protected float gridSpacing = 80;

	private static final long serialVersionUID = 1535851135077957959L;

	protected boolean insertGraph = false;

	protected mxGraph graph;

	protected mxAnalysisGraph aGraph;

	protected GraphType graphType;

	protected JTextField numNodesField = new JTextField();

	protected JTextField numEdgesField = new JTextField();

	protected JRadioButton serverType = new JRadioButton(mxResources.get("serverId"));

	protected JRadioButton nameType = new JRadioButton(mxResources.get("serverName"));

	protected JTextField startVertexValueField = new JTextField();

	protected JTextField endVertexValueField = new JTextField();


	public GraphConfigDialog(final GraphType graphType2, String dialogText) {
		super((Frame) null, dialogText, true);

		if (graphType2 == GraphType.DIJKSTRA) {
			JPanel panel = new JPanel(new GridLayout(3, 3, 4, 4));
			ButtonGroup group = new ButtonGroup();
			group.add(serverType);
			group.add(nameType);
			serverType.setSelected(true);
			panel.add(new JLabel(mxResources.get("type")));
			panel.add(serverType);
			panel.add(nameType);
			panel.add(new JLabel(mxResources.get("startingNode")));
			panel.add(new JLabel(":"));
			panel.add(startVertexValueField);
			panel.add(new JLabel(mxResources.get("finalNode")));
			panel.add(new JLabel(":"));
			panel.add(endVertexValueField);
			JPanel panelBorder = new JPanel();
			panelBorder.setBorder(new EmptyBorder(10, 10, 10, 10));
			panelBorder.add(panel);

			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

			panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
					BorderFactory.createEmptyBorder(16, 8, 8, 8)));

			JButton applyButton = new JButton(mxResources.get("calculate"));
			JButton closeButton = new JButton(mxResources.get("cancel"));
			buttonPanel.add(closeButton);
			buttonPanel.add(applyButton);
			getRootPane().setDefaultButton(applyButton);
			applyButton.addActionListener(new ActionListener() {
				double distance = 0;

				public void actionPerformed(ActionEvent e) {

					applyValues();
					String startValue = startVertexValueField.getText();
					String endValue = endVertexValueField.getText();

					Object startVertex = null;
					Object endVertex = null;

					// Fetch All Vertices
					Object[] vertex = aGraph.getChildVertices(graph.getDefaultParent());

					// Loop through all Vertices
					for (int i = 0; i < vertex.length; i++) {

						mxCell v = (mxCell) vertex[i];
						Object typeValue = null;
						if(nameType.isSelected()) {
							typeValue = v.getValue(); 
						}
						else {
							typeValue = v.getServerid();
						}
						if (typeValue.equals(startValue)) {
							startVertex = (mxCell) vertex[i];
						} else if (typeValue.equals(endValue)) {
							endVertex = (mxCell) vertex[i];
						}
					}
					
					if (startVertex == null) {
						JOptionPane.showMessageDialog(null, mxResources.get("emptyValue", new String[] {mxResources.get("startingNode")}), 
								mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
					} else if (endVertex == null) {
						JOptionPane.showMessageDialog(null, mxResources.get("emptyValue", new String[] {mxResources.get("finalNode")}), 
								mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
					}
					else {
						if (graphType2 == GraphType.DIJKSTRA) {
							// showMessageDialog()
							JFrame messageDialog = new JFrame();
							String message = "";

							try {
								mxTraversal.dijkstra(aGraph, startVertex, endVertex, new mxICellVisitor() {
									@Override
									// simple visitor that prints current vertex
									public boolean visit(Object vertex, Object edge) {
										mxCell v = (mxCell) vertex;
										mxCell e = (mxCell) edge;
										String eVal = "N/A";

										if (e != null) {
											if (e.getValue() == null) {
												eVal = "1.0";
											} else {
												eVal = e.getValue().toString();
											}
										}

										if (!eVal.equals("N/A")) {
											distance = distance + Double.parseDouble(eVal);
										}
										message.concat("(v: " + v.getValue() + " e: " + eVal + "). \n" );

										return false;
									}
								});

								message.concat(mxResources.get("totalDistance") + distance);
								JOptionPane.showMessageDialog(messageDialog, mxResources.get("totalDistance") + distance);
								setVisible(false);
							} catch (StructuralException e1) {
								System.out.println(e1);
							}
						}
					}

				}
			});
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					insertGraph = false;
					setVisible(false);
				}
			});

			getContentPane().add(panelBorder, BorderLayout.CENTER);
			getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			pack();
			setResizable(false);
			// setLocationRelativeTo(parent);
		}
		
	}

	public void configAnalysisGraph(mxGraph graph, mxGraphGenerator generator, Map<String, Object> props) {
		this.aGraph.setGraph(graph);

		if (generator == null) {
			this.aGraph.setGenerator(new mxGraphGenerator(null, null));
		} else {
			this.aGraph.setGenerator(generator);
		}

		if (props == null) {
			Map<String, Object> properties = new HashMap<String, Object>();
			mxGraphProperties.setDirected(properties, false);
			this.aGraph.setProperties(properties);
		} else {
			this.aGraph.setProperties(props);
		}
	}

	/**
	 * 
	 */
	protected void applyValues() {
		setNumNodes(Integer.parseInt(this.numNodesField.getText()));
		setNumEdges(Integer.parseInt(this.numEdgesField.getText()));
		setStartVertexValue(this.startVertexValueField.getText());
		setEndVertexValue(this.endVertexValueField.getText());
	}

	public void configureLayout(mxGraph graph, GraphType graphType, mxAnalysisGraph aGraph) {
		this.graph = graph;
		this.graphType = graphType;
		this.aGraph = aGraph;

		this.numNodesField.setText(String.valueOf(getNumNodes()));
		this.numEdgesField.setText(String.valueOf(getNumEdges()));
		this.startVertexValueField.setText(String.valueOf(getStartVertexValue()));
		this.endVertexValueField.setText(String.valueOf(getEndVertexValue()));
	}

	public void setAllowMultipleEdges(boolean allowMultipleEdges) {
		this.allowMultipleEdges = allowMultipleEdges;
	}

	public void setAllowSelfLoops(boolean allowSelfLoops) {
		this.allowSelfLoops = allowSelfLoops;
	}

	public void setArrows(boolean arrows) {
		this.arrows = arrows;
	}

	public void setEndVertexValue(String endVertexValue) {
		this.endVertexValue = endVertexValue;
	}

	public void setForceConnected(boolean forceConnected) {
		this.forceConnected = forceConnected;
	}

	public void setGridSpacing(float gridSpacing) {
		if (gridSpacing < 1) {
			gridSpacing = 1;
		}
		this.gridSpacing = gridSpacing;
	}

	public void setGroupSpacing(float groupSpacing) {
		this.groupSpacing = groupSpacing;
	}

	/**
	 * @param insertIntoModel
	 *            The insertIntoModel to set.
	 */

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	/**
	 * @param numEdges
	 *            The numEdges to set.
	 */
	public void setNumEdges(int numEdges) {
		if (numEdges < 1) {
			numEdges = 1;
		} else if (numEdges > 2000000) {
			numEdges = 2000000;
		}
		this.numEdges = numEdges;
	}

	/**
	 * @param numNodes
	 *            The numNodes to set.
	 */
	public void setNumNodes(int numNodes) {
		if (numNodes < 1) {
			numNodes = 1;
		} else if (numNodes > 2000000) {
			numNodes = 2000000;
		}
		this.numNodes = numNodes;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public void setNumVertexesLeft(int numVertexesLeft) {
		if (numVertexesLeft < 1) {
			numVertexesLeft = 1;
		} else if (numVertexesLeft > 300) {
			numVertexesLeft = 300;
		}
		this.numVertexesLeft = numVertexesLeft;
	}

	public void setNumVertexesRight(int numVertexesRight) {
		if (numVertexesRight < 1) {
			numVertexesRight = 1;
		} else if (numVertexesRight > 300) {
			numVertexesRight = 300;
		}
		this.numVertexesRight = numVertexesRight;
	}

	public void setStartVertexValue(String startVertexValue) {
		this.startVertexValue = startVertexValue;
	}

	public void setValence(int valence) {
		if (valence < 0) {
			valence = 0;
		} else if (valence > 100) {
			valence = 100;
		}
		this.valence = valence;
	}

	public String getEndVertexValue() {
		return endVertexValue;
	}

	public float getGridSpacing() {
		return gridSpacing;
	}

	public float getGroupSpacing() {
		return groupSpacing;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public int getNumEdges() {
		return numEdges;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumVertexesLeft() {
		return numVertexesLeft;
	}

	public int getNumVertexesRight() {
		return numVertexesRight;
	}

	public String getStartVertexValue() {
		return startVertexValue;
	}

	public int getValence() {
		return valence;
	}

	public boolean isAllowMultipleEdges() {
		return allowMultipleEdges;
	}

	public boolean isAllowSelfLoops() {
		return allowSelfLoops;
	}

	public boolean isArrows() {
		return arrows;
	}

	public boolean isForceConnected() {
		return forceConnected;
	}

	public boolean isWeighted() {
		return weighted;
	}

	public void setWeighted(boolean weighted) {
		this.weighted = weighted;
	}
}
