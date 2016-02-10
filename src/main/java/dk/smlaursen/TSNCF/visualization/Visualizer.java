package dk.smlaursen.TSNCF.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxGraphModel.Filter;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;
import com.mxgraph.view.mxGraphView;

import dk.smlaursen.TSNCF.architecture.EndSystem;
import dk.smlaursen.TSNCF.architecture.GCLEdge;
import dk.smlaursen.TSNCF.architecture.Node;
import dk.smlaursen.TSNCF.solver.VLAN;

public class Visualizer{
	private Object[] endSystems, edges, highlightedVls;
	private mxGraphModel graphModel;
	private mxGraphComponent canvasComponent;
	private Collection<Object> cells;
	private JComboBox<VLAN> comboBox;
	private JPanel zoomPanel = new JPanel(new BorderLayout());

	/**Setups the topology in a JFrame.
	 * This call requires the libraries JGraphX and JGraphT-ext to be present on the classpath
	 * @param g the {@link Graph} to display*/
	public Visualizer(final Graph<Node, GCLEdge> g){
		JGraphXAdapter<Node, GCLEdge> adapter = new JGraphXAdapter<Node, GCLEdge>(g);
		canvasComponent = new mxGraphComponent(adapter);
		graphModel = (mxGraphModel) canvasComponent.getGraph().getModel();
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

		mxStyleUtils.setCellStyles(graphModel, edges, mxConstants.STYLE_NOLABEL ,"1");
		mxStyleUtils.setCellStyles(graphModel, edges, mxConstants.STYLE_STROKECOLOR, "black");
		mxStyleUtils.setCellStyles(graphModel, endSystems, mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		mxStyleUtils.setCellStyles(graphModel, endSystems, mxConstants.STYLE_FILLCOLOR, "FAFAD2");

		//Disable editing of figure
		canvasComponent.setEnabled(false);
		new mxFastOrganicLayout(adapter).execute(adapter.getDefaultParent());
		new mxParallelEdgeLayout(canvasComponent.getGraph()).execute(adapter.getDefaultParent());
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
		JSlider slider = new JSlider(SwingConstants.VERTICAL, 5, 50, 10);
		zoomPanel.add(new JLabel("Zoom "), BorderLayout.NORTH);
		zoomPanel.add(slider, BorderLayout.CENTER);
		
		mxGraphView view = canvasComponent.getGraph().getView();
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        double scale = ((int) source.getValue())/10.0;
			        SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							view.setScale(scale);
						}
					});
			    }
			}
		});
	}

	public void topologyPanel(){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setLayout(new BorderLayout(50,50));
				frame.add(canvasComponent, BorderLayout.CENTER);				
				frame.add(comboBox, BorderLayout.SOUTH);
				frame.add(zoomPanel, BorderLayout.EAST);
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
