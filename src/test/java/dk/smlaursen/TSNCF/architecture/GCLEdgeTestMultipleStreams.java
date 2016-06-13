package dk.smlaursen.TSNCF.architecture;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class GCLEdgeTestMultipleStreams {
	private static GCLEdge edge;
	
	@BeforeClass
	public static void setup(){
		List<GCL> gcls = new LinkedList<GCL>();
		gcls.add(new GCL(0.0,10.4,8));
		gcls.add(new GCL(31.25,10.4,8));
		edge = new GCLEdge(100, 5.12);
		edge.addGCL(gcls);
		//[0.0-10.4], [31.25-41.65], [62.5-72.9], [93.75-104.15], [125.0-135.4], [156.25-166.65], [187.5-197.9], [218.75-229.15],...
	}
	
	@Test
	public void testNoInterference(){
		//Without GCL the interference = 0
		GCLEdge e = new GCLEdge(100, 5.12);
		assertEquals(125, e.calculateWorstCaseInterference(125.0), 0.05);
	}
	
	@Test
	public void test125() {
		// = 6 * 10.4
		assertEquals(125+(6*10.4), edge.calculateWorstCaseInterference(125.0), 0.05);
	}
	
	@Test
	public void test50(){
		// = 3 * 10.4
		assertEquals(55+(3*10.4), edge.calculateWorstCaseInterference(55), 0.05);
	}
	
	@Test
	public void test20(){
		// = 3 * 10.4
		assertEquals(50+(3*10.4), edge.calculateWorstCaseInterference(50), 0.05);
	}
}
