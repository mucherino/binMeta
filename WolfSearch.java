
/* WolfSearch class
 *
 * binMeta project
 *
 * initial version coded by Remi Viotty, M1 Info 2019-20
 *
 * last update: April 21, 2021
 *
 * AM
 */

import java.util.ArrayList;
import java.util.Random;

public class WolfSearch extends binMeta
{
   private int np;  // population size
   private int maxIt;  // maximum number of iterations
   private int memorySize;  // the size of wolf memory
   private Memory wolves;  // Wolf population

   // WolfSearch constructor
   public WolfSearch(Objective obj,int np,int memorySize,int minVision,int maxVision,double minThreat,double maxThreat,int maxIt,long maxTime)
   {
      try
      {
         if (obj == null) throw new Exception("WolfSearch: the reference to the objective is null");
         this.obj = obj;
         Data ref = this.obj.solutionSample();
         int n = ref.numberOfBits();
         if (memorySize <= 0) throw new Exception("WolfSearch: the specified size of the wolf memory is nonpositive");
         this.memorySize = memorySize;
         if (maxVision < 0 || maxVision > n) throw new Exception("WolfSearch: meaningless maximum vision parameter");
         if (minVision > maxVision) throw new Exception("WolfSearch: minimum vision parameter seems to be larger than specified maximum vision");
         if (minThreat < 0.0 || minThreat > 1.0) throw new Exception("WolfSearch: the specified minimun threat is not a probability (must be in [0,1])");
         if (maxThreat < 0.0 || maxThreat > 1.0) throw new Exception("WolfSearch: the specified maximun threat is not a probability (must be in [0,1])");
         if (minThreat > maxThreat) throw new Exception("WolfSearch: minimum threat seems to be larger than maximum probability threat");
         if (np <= 0) throw new Exception("WolfSearch: the specified population size is nonpositive");
         this.np = np;
         Random R = new Random();
         this.wolves = new Memory(this.np,"fifo",3);  // 3 Memory parameters: vision, pbThrets, ephemeral memory
         while (!this.wolves.isFull())
         {
            Data D = new Data(n,0.2 + 0.6*R.nextDouble());
            int k = this.wolves.add(D,this.obj.value(D));
            int vision = minVision + R.nextInt(maxVision - minVision);
            double pbThreat = minThreat + R.nextDouble()*(maxThreat - minThreat);
            Memory ephemeral = new Memory(this.memorySize);
            this.wolves.setParameter(k,0,vision);
            this.wolves.setParameter(k,1,pbThreat);
            this.wolves.setParameter(k,2,ephemeral);
         }
         if (maxIt <= 0) throw new Exception("WolfSearch: the specified maximum number of iterations is nonpositive");
         this.maxIt = maxIt;
         if (maxTime <= 0) throw new Exception("WolfSearch: the maximum execution time is nonpositive");
         this.maxTime = maxTime;
         this.solution = null;
         this.objValue = null;
         this.metaName = "WolfSearch";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // optimize (by WolfSearch)
   @Override
   public void optimize()
   {
      Random R = new Random();
      int it = 0;
      long startime = System.currentTimeMillis();

      // main loop
      while (it < this.maxIt && System.currentTimeMillis() - startime < this.maxTime)
      {
         // for every wolf
         for (int i = 0; i < this.np; i++)
         {
             // current wolf
             Data wolf = this.wolves.getData(i);
             double wolfValue = this.wolves.getValue(i);
             int vision = (int) this.wolves.getParameter(i,0);
             double pbThreat = (double) this.wolves.getParameter(i,1);
             Memory wolfMemory = (Memory) this.wolves.getParameter(i,2);

             // trying to prey for new food initiatively
             Data D = wolf.randomSelectInNeighbourhood(vision);
             if (R.nextInt(2) == 0)
             {
                LocalOpt lopt = new LocalOpt(D,this.obj,2+R.nextInt(98));
                lopt.optimize();
                D = lopt.getSolution();
             }
             double value = this.obj.value(D);
             if (!wolfMemory.contains(D) && value < wolfValue)
             {
                // preying initiatively worked out
                wolfMemory.add(wolf);
                this.wolves.set(i,D,value);
             }
             else
             {
                // preying new food passively (interacting with other wolves)
                int toapproach = -1;
                int repulsion = wolf.numberOfBits();
                for (int j = 0; j < this.np; j++)
                {
                   // is there any other wolf doing better than wolf i ?
                   if (i != j)
                   {
                      Data other = this.wolves.getData(j);
                      int h = wolf.hammingDistanceTo(other);
                      if (h < vision)  // wolf i can see the other
                      {
                         value = this.wolves.getValue(j);
                         if (value < wolfValue && h < repulsion)
                         {
                            toapproach = j;
                            repulsion = h;
                         }
                      }
                   }
                }
                if (toapproach != -1)
                {
                   // wolf i joins the selected wolf
                   Data other = this.wolves.getData(toapproach);
                   wolfMemory.add(wolf);
                   D = other.randomSelectInNeighbourhood(1);
                   value = this.obj.value(D);
                   this.wolves.set(i,D,value);
                }

                // any threats?
                if (R.nextDouble() < pbThreat)
                {
                   D = wolf.randomSelectInNeighbourhood(vision,vision);
                   value = this.obj.value(D);
                   this.wolves.set(i,D,value);
                }
             }
         }

         // verifying best current solution
         int bestIndex = this.wolves.indexOfBest();
         Data newBest = this.wolves.getData(bestIndex);
         double newBestValue = this.wolves.getValue(bestIndex);
         if (this.objValue == null || this.objValue > newBestValue)
         {
            this.solution = new Data(newBest);
            this.objValue = newBestValue;
         }

         // preparing for next iteration
         it++;
      }
   }
}

