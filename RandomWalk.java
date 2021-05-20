
/* RandomWalk class
 *
 * binMeta project
 *
 * last update: April 21, 2021
 *
 * AM
 */

import java.util.Random;

public class RandomWalk extends binMeta
{
   // RandomWalk constructor
   public RandomWalk(Data startPoint,Objective obj,long maxTime)
   {
      try
      {
         if (maxTime <= 0) throw new Exception("RandomWalk: the maximum execution time (in ms) is nonpositive");
         this.maxTime = maxTime;
         if (startPoint == null) throw new Exception("RandomWalk: the reference to the starting point is null");
         if (startPoint.numberOfBits() < 2) throw new Exception("RandomWalk: too few bits to justify use of meta-heuristics");
         this.solution = new Data(startPoint);
         if (obj == null) throw new Exception("RandomWalk: the reference to the objective is null");
         this.obj = obj;
         if (this.obj.solutionSample().numberOfBits() != this.solution.numberOfBits())
            throw new Exception("RandomWalk: starting point has a number of bits which does not match with objective function");
         this.objValue = this.obj.value(this.solution);
         this.metaName = "RandomWalk";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // optimize (by RandomWalk)
   @Override
   public void optimize()
   {
      Random R = new Random();
      Data D = new Data(this.solution);
      int maxstep = Math.max(3,D.numberOfBits());  // Hamming distance will be randomly selected in [1,maxstep]
      long startime = System.currentTimeMillis();

      // main loop
      while (System.currentTimeMillis() - startime < this.maxTime)
      {
         // the random walker can walk in a neighbourhood of D
         int h = 1 + R.nextInt(maxstep - 1);

         // generating a new solution in the neighbour of D with Hamming distance h
         Data newD = D.randomSelectInNeighbourhood(h);

         // evaluating the quality of the generated solution
         double value = obj.value(newD);
         if (this.objValue > value)
         {
            this.objValue = value;
            this.solution = new Data(newD);
         }

         // the walk continues from the new generated solution
         D = newD;
      }
   }
}

