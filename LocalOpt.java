
/* LocalOpt class
 *
 * binMeta project
 *
 * last update: April 21, 2021
 *
 * AM
 */

import java.util.Collections;
import java.util.ArrayList;
import java.util.TreeSet;

public class LocalOpt extends binMeta
{
   // LocalOpt constructor
   public LocalOpt(Data startPoint,Objective obj,long maxTime)
   {
      try
      {
         if (maxTime <= 0) throw new Exception("LocalOpt: the maximum execution time is 0 or even negative");
         this.maxTime = maxTime;
         if (startPoint == null) throw new Exception("LocalOpt: the reference to the starting point is null");
         this.solution = new Data(startPoint);
         if (obj == null) throw new Exception("LocalOpt: the reference to the objective is null");
         this.obj = obj;
         if (this.obj.solutionSample().numberOfBits() != this.solution.numberOfBits())
            throw new Exception("LocalOpt: starting point has a number of bits which does not match with objective function");
         this.objValue = this.obj.value(this.solution);
         this.metaName = "LocalOpt";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // optimize (by LocalOpt)
   @Override
   public void optimize()
   {
      long startime = System.currentTimeMillis();
      Data D = new Data(this.solution);
      int n = D.numberOfBits();
      double value = this.objValue;

      // main loop
      do
      {
         // preserving current solution
         this.solution = new Data(D);
         this.objValue = value;

         // gradient computation
         TreeSet<Integer> indices = new TreeSet<Integer> ();
         ArrayList<Double> g = new ArrayList<Double> (n);
         Data.bitIterator It = D.iterator();
         int i = 0;
         while (It.hasNext())
         {
            double gvalue = this.obj.value(It.withNextBitFlipped()) - value;
            if (gvalue < 0.0)
            {
               indices.add(i);
               g.add(gvalue);
            }
            else  g.add(-Double.MAX_VALUE);
            i++;
         }

         // performing full step along the opposite gradient direction
         if (!indices.isEmpty())
         {
            Data G = new Data(n,indices);
            Data E = Data.diff(D,G);
            value = this.obj.value(E);

            // if it is necessary to perform a partial step to get an improvement
            while (value >= this.objValue && !indices.isEmpty() && System.currentTimeMillis() - startime < this.maxTime)
            {
               // remove the current largest g value (the less important)
               int k = g.indexOf(Collections.max(g));
               g.set(k,-Double.MAX_VALUE);
               indices.remove(k);
               G = new Data(n,indices);
               E = Data.diff(D,G);
               value = this.obj.value(E);
            }

            // saving the best solution in D
            D = E;
         }
      }
      while (System.currentTimeMillis() - startime < this.maxTime && value < this.objValue);
   }
}

