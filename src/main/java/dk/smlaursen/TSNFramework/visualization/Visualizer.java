package dk.smlaursen.TSNFramework.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
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

import dk.smlaursen.TSNFramework.architecture.EndSystem;
import dk.smlaursen.TSNFramework.architecture.GCLEdge;
import dk.smlaursen.TSNFramework.architecture.Node;
import dk.smlaursen.TSNFramework.solver.VLAN;

public class Visualizer{
	private static final Dimension DEFAULT_SIZE = new Dimension(500, 320);
	private Object[] endSystems, edges, highlightedVls;
	private mxGraphModel graphModel;
	private mxGraphComponent canvasComponent;
	private Collection<Object> cells;
	private JComboBox<VLAN> comboBox;

	/**Setups the topology in a JFrame.
	 * This call requires the libraries JGraphX and JGraphT-ext to be present on the classpath
	 * @param g the {@link Graph} to display*/
	public Visualizer(final Graph<Node, GCLEdge> g){
		JGraphXAdapter<Node, GCLEdge> adapter = new JGraphXAdapter<Node, GCLEdge>(g);
		canvasComponent = new mxGraphComponent(adapter);
		graphModel = (mxGraphModel) canvasComponent.getGraph().getModel();
		mxGraphView view = canvasComponent.getGraph().getView();
		view.setScale(2);
		cells = graphModel.getCells().values();

		//Filter to get endSystems
		endSystems = mxGraphModel.filterCells(cells.toArray(), new Filter() {
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
		edges = mxGraphModel.filterCells(cells.toArray(), new Filter() {
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
		mxStyleUtils.setCellStyles(graphModel, edges, mxConstants.STYLE_STROKECOLOR, "black");
		mxStyleUtils.setCellStyles(graphModel, endSystems, mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		mxStyleUtils.setCellStyles(graphModel, endSystems, mxConstants.STYLE_FILLCOLOR, "FAFAD2");

		//Disable editing of figure
		canvasComponent.setEnabled(false);
		mxGraphLayout layout = new mxHierarchicalLayout(adapter);
		layout.execute(adapter.getDefaultParent());

		//Setup combobox
		comboBox = new JComboBox<VLAN>();
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VLAN vlan = (VLAN) comboBox.getSelectedItem();
				if(vlan != null)
					displayVLAN(vlan);
			}
		});
	}


	public void topologyPanel(){

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setSize(DEFAULT_SIZE);
				frame.setLayout(new BorderLayout(50,50));
				frame.add(canvasComponent, BorderLayout.CENTER);
				frame.add(comboBox, BorderLayout.SOUTH);
				frame.setTitle("Topology Visualization");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);			
			}
		});
	}

	/**Adds the VLAN to the comboBox beneath the figure for display*/
	public void addSolutions(Set<VLAN> sol) {
		for(VLAN vl : sol){
			comboBox.addItem(vl);
		}
	}

	private void displayVLAN(final VLAN route){
		Set<DefaultEdge> edgeSet = new HashSet<DefaultEdge>();
		for(int i = 0; i < route.getRoutings().size(); i++){
			edgeSet.addAll(route.getRoutings().get(i).getEdgeList());
		}

		highlightedVls = mxGraphModel.filterCells(cells.toArray(), new Filter() {
			@Override
			public boolean filter(Object cell) {
				if(cell instanceof mxCell){
					mxCell mxc = (mxCell) cell;
					if(mxc.getValue() instanceof DefaultEdge && edgeSet.contains(mxc.getValue())){
						return true;
					}
				}
				return false;
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mxStyleUtils.setCellStyles(graphModel, edges, mxConstants.STYLE_STROKECOLOR, "black");
				mxStyleUtils.setCellStyles(graphModel, highlightedVls, mxConstants.STYLE_STROKECOLOR, "red");
			}
		});

	}
}
