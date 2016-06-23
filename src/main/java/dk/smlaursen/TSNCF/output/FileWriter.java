package dk.smlaursen.TSNCF.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import dk.smlaursen.TSNCF.application.TTApplication;
import dk.smlaursen.TSNCF.solver.Multicast;
import dk.smlaursen.TSNCF.solver.Solution;
import dk.smlaursen.TSNCF.solver.Unicast;

public class FileWriter {

	private FileWriter(){};

	/**@param sol the {@link Solution}
	 * @param f the {@link File}.*/
	public static void Output(Solution sol, File f){
		Writer writer = null;
		try{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			writer.write(sol.getCost().toDetailedString()+"\n\n");
			for(Multicast r : sol.getRouting()){
				//Only output AVB applications
				if(r.getApplication() instanceof TTApplication){
					continue;
				}
				writer.write(r+"\n");
				for(Unicast u : r.getUnicasts()){
					writer.write("  "+u.getRoute()+"\n");
				}
			}
		} catch (IOException e){

		} finally{
			try{writer.close();}catch(Exception ex){}
		}
	}
}
