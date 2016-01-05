package dk.smlaursen.TSNSolver.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxGraphModel.Filter;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;
import com.mxgraph.view.mxGraphView;

import dk.smlaursen.TSNSolver.architecture.EndSystem;
import dk.smlaursen.TSNSolver.architecture.Node;
import dk.smlaursen.TSNSolver.solver.VLAN;

public class Visualizer{
	private static final Dimension DEFAULT_SIZE = new Dimension(500, 320);
	
	/** Displays the topology in a JFrame.
	 * This call requires the libraries JGraphX and JGraphT-ext to be present on the classpath
	 * @param g the {@link Graph} to display*/
	public static void displayTopology(final Graph<Node, DefaultEdge> g){
		JGraphXAdapter<Node, DefaultEdge> adapter = new JGraphXAdapter<Node, DefaultEdge>(g);
		mxGraphComponent component = new mxGraphComponent(adapter);
		mxGraphModel graphModel = (mxGraphModel) component.getGraph().getModel();
		mxGraphView view = component.getGraph().getView();
		view.setScale(2);
		Collection<Object> cells = graphModel.getCells().values();
		
		//Filter to get endSystems
		Object[] endSystems = mxGraphModel.filterCells(cells.toArray(), new Filter() {
			@Override
			public boolean filter(Object cell) {
				if(cell instanceof mxCell){
					mxCell mxc = (mxCell) cell;
					if(mxc.getValue() instanceof EndSystem){
						return true;
					}
				}
				return false;
			}
		});
		
		//Filter to get edges
		Object[] edges = mxGraphModel.filterCells(cells.toArray(), new Filter() {
			@Override
			public boolean filter(Object cell) {
				if(cell instanceof mxCell){
					mxCell mxc = (mxCell) cell;
					if(mxc.getValue() instanceof DefaultEdge){
						return true;
					}
				}
				return false;
			}
		});
		
		mxStyleUtils.setCellStyles(graphModel, edges, mxConstants.STYLE_ENDARROW, mxConstants.NONE);
		mxStyleUtils.setCellStyles(graphModel, edges, mxConstants.STYLE_NOLABEL ,"1");
		mxStyleUtils.setCellStyles(graphModel, endSystems, mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		mxStyleUtils.setCellStyles(graphModel, endSystems, mxConstants.STYLE_FILLCOLOR, "FAFAD2");
		
		//Disable editing of figure
		component.setEnabled(false);
		
		mxGraphLayout layout = new mxHierarchicalLayout(adapter);
		layout.execute(adapter.getDefaultParent());
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setSize(DEFAULT_SIZE);
				frame.setLayout(new BorderLayout(50,50));
				frame.add(component, BorderLayout.CENTER);
				frame.setTitle("Topology Visualization");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);			
			}
		});
	}
	
	public static void displaySolution(Set<VLAN> solution){
		
	}
}
