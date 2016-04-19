package dk.smlaursen.TSNCF.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomDistributions {
	
	public static int RouletteWheelDistribution(int size){
	   double r = ThreadLocalRandom.current().nextDouble();
	   int index = 0;
	   double val = 0.5;
	   while(true){
		   if(r > val || index >= size-1){
			   break;
		   }
		   index++;
		   val = val / 2.0;
	   }
	   return index;
	}
}
