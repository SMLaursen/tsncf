package dk.smlaursen.TSNCF.architecture;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class GCLEdgeTest {
	private static GCLEdge edge;
	
	@BeforeClass
	public static void setup(){
		List<GCL> gcls = new LinkedList<GCL>();
		gcls.add(new GCL(0.0,10.4,8));
		edge = new GCLEdge(100, 5.12);
		edge.addGCL(gcls);
	}
	
	@Test
	public void testNoInterference(){
		//Without GCL the interference = 0
		GCLEdge e = new GCLEdge(100, 5.12);
		assertEquals(125, e.calculateWorstCaseInterference(125.0), 0.05);
	}
	
	@Test
	public void test125() {
		// = 3 * 10.4
		assertEquals(156.2, edge.calculateWorstCaseInterference(125.0), 0.05);
	}
	
	@Test
	public void test50(){
		// = 2 * 10.4
		assertEquals(75.8, edge.calculateWorstCaseInterference(55), 0.05);
	}
	
	@Test
	public void test20(){
		// = 1 * 10.4
		assertEquals(60.4, edge.calculateWorstCaseInterference(50), 0.05);
	}
	
}
