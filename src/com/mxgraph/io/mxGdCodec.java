/**
 * Copyright (c) 2010-2012, JGraph Ltd
 */
package com.mxgraph.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mxgraph.view.mxGraph;

/**
 * Parses a GD .txt file and imports it in the given graph.<br/>
 * This class depends from the classes contained in
 * com.mxgraph.io.gd.
 */
public class mxGdCodec
{
	private static final Logger log = Logger.getLogger(mxGdCodec.class.getName());

	/**
	 * Represents the different states in the parse of a file.
	 */
	public enum mxGDParseState
	{
		START, NUM_NODES, PARSING_NODES, PARSING_EDGES
	}

	/**
	 * Map with the vertex cells added in the addNode method.
	 */
	protected static HashMap<String, Object> cellsMap = new HashMap<String, Object>();

	/**
	 * Parses simple GD format and populate the specified graph
	 * @param input GD file to be parsed
	 * @param graph Graph where the parsed graph is included.
	 */
	public static void decode(String input, mxGraph graph)
	{
		BufferedReader br = new BufferedReader(new StringReader(input));
		mxGDParseState state = mxGDParseState.START;
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		
		try
		{
			String line = br.readLine().trim();
			while (line != null)
			{
				switch (state)
				{
					case START:
					{
						if (!line.startsWith("#"))
						{
							state = mxGDParseState.NUM_NODES;
						}
						else
						{
							break;
						}
					}
					case NUM_NODES:
					{
						if (!line.startsWith("#"))
						{
							int numVertices = Integer.valueOf(line);
							
							for (int i = 0; i < numVertices; i++)
							{
								String label = String.valueOf(i);
								Object vertex = graph.insertVertex(parent, label, label,
										0, 0, 10, 10);
								
								cellsMap.put(label, vertex);
							}
						}
						else
						{
							state = mxGDParseState.PARSING_EDGES;
						}
						
						break;
					}
					case PARSING_NODES:
					{
						if (line.startsWith("# Edges"))
						{
							state = mxGDParseState.PARSING_EDGES;
						}
						else if (!line.equals(""))
						{
							String[] items = line.split(",");
							if (items.length != 5)
							{
								throw new Exception("Error in parsing");
							}
							else
							{
								double x = Double.valueOf(items[1]);
								double y = Double.valueOf(items[2]);
								double width = Double.valueOf(items[3]);
								double height = Double.valueOf(items[4]);
								
								
								//Set the node name as label.
								String label = items[0];

								//Insert a new vertex in the graph
								Object vertex = graph.insertVertex(parent, label, label,
										x - width / 2.0, y - height / 2.0, width,
										height);
								
								cellsMap.put(label, vertex);
							}
						}
						break;
					}
					case PARSING_EDGES:
					{
						if (!line.equals(""))
						{
							String[] items = line.split(" ");
							if (items.length != 2)
							{
								throw new Exception("Error in parsing");
							}
							else
							{
								Object source = cellsMap.get(items[0]);
								Object target = cellsMap.get(items[1]);

								graph.insertEdge(parent, null, "", source, target);
							}
						}
						break;
					}
				}

				line = br.readLine();
			}
		}
		
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Failed to decode", e);
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
	/**
	 * Generates a GD text output with the cells in the graph.
	 * The implementation only uses the cells located in the default parent.
	 * @param xml Graph with the cells.
	 * @return The GD document generated.
	 */
	public static String encode(String xml)
	{
		StringBuilder builder = new StringBuilder();
		
		try {
			String nodeString = "Node";
			String connectionString = "Connection";

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xml)));

			
			builder.append("# Topology file is converted using Edge Topology Designer\n");
			
			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			final NodeList nList = doc.getElementsByTagName("mxCell");
			log.info("Topology is converted...");
			final HashMap<String, String> serverId = new HashMap<>();

			for (int temp = 0; temp < nList.getLength(); temp++) {

				final Node nNode = nList.item(temp);

				final Element eElement = (Element) nNode;
				// If it is only a node
				if ((nNode.getNodeType() == Node.ELEMENT_NODE) && !eElement.hasAttribute("edge")
						&& eElement.hasAttribute("value")) {

					try {
						builder.append(nodeString + "," + eElement.getAttribute("serverid") + ","
								+ eElement.getAttribute("value") + "," + eElement.getAttribute("warningmessage") + "\n");
						serverId.put(eElement.getAttribute("id"), eElement.getAttribute("serverid"));
					} catch (final Exception e) {
						continue;
					}

				} // If it is a connection
				else if ((nNode.getNodeType() == Node.ELEMENT_NODE) && eElement.hasAttribute("edge")) {
					builder.append(connectionString + "," + serverId.get(eElement.getAttribute("source")) + ","
							+ serverId.get(eElement.getAttribute("target")) + "," + eElement.getAttribute("value") + "\n");
				}
			}
			log.info("Saved");

			return builder.toString();

		} catch (Exception e) {
			e.printStackTrace();
			log.severe("An error occurred during parsing model!");
			return "An error occurred during parsing model!";
		}
	}
}
