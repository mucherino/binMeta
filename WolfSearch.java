
/* WolfSearch class
 *
 * binMeta project
 *
 * initial version coded by Remi Viotty, M1 Info 2019-20
 *
 * last update: May 20, 2021
 *
 * AM
 */

import java.util.ArrayList;
import java.util.Random;

public class WolfSearch extends binMeta implements Objective
{
   private boolean isObjective;  // true if the parameters are decision variables
   private int np;  // population size
   private int minVision;  // minimal vision of wolves
   private int maxVision;  // maximal vision of wolves
   private double minThreat;  // minimal threat probability
   private double maxThreat;  // maximal threat probability
   private int memorySize;  // the size of wolf memory
   private Memory wolves;  // Wolf population

   // WolfSearch constructor
   public WolfSearch(Objective obj,int np,int memorySize,int minVision,int maxVision,double minThreat,double maxThreat,long maxTime)
   {
      this.isObjective = false;
      try
      {
         if (obj == null) throw new Exception("WolfSearch: the reference to the objective is null");
         this.obj = obj;
         Data ref = this.obj.solutionSample();
         int n = ref.numberOfBits();
         if (memorySize <= 0) throw new Exception("WolfSearch: specified size of the wolf memory is nonpositive");
         this.memorySize = memorySize;
         if (maxVision < 0 || maxVision > n) throw new Exception("WolfSearch: meaningless maximum vision parameter");
         if (minVision > maxVision) throw new Exception("WolfSearch: minimum vision parameter seems to be larger than specified maximum vision");
         this.minVision = minVision;
         this.maxVision = maxVision;
         if (minThreat < 0.0 || minThreat > 1.0) throw new Exception("WolfSearch: specified minimun threat is not a probability (must be in [0,1])");
         if (maxThreat < 0.0 || maxThreat > 1.0) throw new Exception("WolfSearch: specified maximun threat is not a probability (must be in [0,1])");
         if (minThreat > maxThreat) throw new Exception("WolfSearch: minimum threat seems to be larger than maximum probability threat");
         this.minThreat = minThreat;
         this.maxThreat = maxThreat;
         if (np <= 0) throw new Exception("WolfSearch: specified population size is nonpositive");
         this.np = np;
         Random R = new Random();
         this.wolves = new Memory(this.np,"fifo",3);  // 3 Memory parameters: vision, pbThrets, ephemeral memory
         while (!this.wolves.isFull())
         {
            Data D = new Data(n,0.2 + 0.6*R.nextDouble());
            int k = this.wolves.add(D,this.obj.value(D));
            int vision = minVision;
            if (maxVision - minVision > 0)  vision = vision + R.nextInt(maxVision - minVision);
            double pbThreat = minThreat + R.nextDouble()*(maxThreat - minThreat);
            Memory ephemeral = new Memory(this.memorySize);
            this.wolves.setParameter(k,0,vision);
            this.wolves.setParameter(k,1,pbThreat);
            this.wolves.setParameter(k,2,ephemeral);
         }
         if (maxTime <= 0) throw new Exception("WolfSearch: specified maximum execution time is nonpositive");
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

   // WolfSearch constructor
   public WolfSearch(Objective obj,long maxTime)
   {
      this.isObjective = true;
      try
      {
         if (obj == null) throw new Exception("WolfSearch: the reference to the objective is null");
         this.obj = obj;
         if (maxTime <= 0) throw new Exception("WolfSearch: specified maximum execution time is nonpositive");
         this.maxTime = maxTime;
         this.np = 0;
         this.memorySize = 0;
         this.wolves = null;
         this.objValue = null;
         this.metaName = "Objective WolfSearch";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // getName
   @Override
   public String getName()
   {
      return new String(this.metaName);
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      int size = 29; // 9(np) + 8(memorySize) + 2(minVision) + 2(maxVision) + 4(minThreat) + 4(maxThreat)
      return new Data(size,0.5);
   }

   // optimize (by WolfSearch)
   @Override
   public void optimize()
   {
      try
      {
         if (this.isObjective) throw new Exception("WolfSearch: direct call to 'optimize' is not allowed when the object is initialized as an Objective");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // getting started
      Random R = new Random();
      int it = 0;
      long startime = System.currentTimeMillis();

      // main loop
      while (System.currentTimeMillis() - startime < this.maxTime)
      {
         // for every wolf
         for (int i = 0; i < this.np && System.currentTimeMillis() - startime < this.maxTime; i++)
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
                LocalOpt lopt = new LocalOpt(D,this.obj,2+R.nextInt(18));
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

   // loadParameters (private method)
   private void loadParameters(Data D)
   {
      try
      {
         if (!this.isObjective) 
            throw new Exception("WolfSearch: call to 'loadParameters' is not allowed when the object is not initialized as an Objective");
         if (D == null) throw new Exception("WolfSearch: Data object is null");
         if (D.numberOfBits() != 29) throw new Exception("WolfSearch: Unexpected bit string length in Data object");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // size in terms of bits of internal objective
      int n = this.obj.solutionSample().numberOfBits();

      // extracting decision variables from Data object, and copying in attributes
      this.np = new Data(D,0,9).posIntValue();  // 10 bits (np)
      this.memorySize = new Data(D,9,17).posIntValue();  // 8 bits (memorySize)
      int[] visions = {n/8,2*n/8,3*n/8,4*n/8,5*n/8,6*n/8,7*n/8,n};
      int min = new Data(D,17,19).intValue();  // 2 bits (minVision)
      this.minVision = visions[min];
      int max = new Data(D,19,21).intValue();  // 2 bits (maxVision)
      this.maxVision = visions[min + max];
      this.minThreat = 0.5*(new Data(D,21,25).doubleValueNormalized());  // 4 bits (minThreat)
      this.maxThreat = minThreat + 0.5*(new Data(D,25,29).doubleValueNormalized());  // 4 bits (maxThreat)
   }

   // parametersToString
   public String parametersToString(Data D)
   {
      String print = "[";
      this.loadParameters(D);
      print = print + "np = " + this.np + " | ";
      print = print + "memorySize = " + this.memorySize + " | ";
      print = print + "minVision = " + this.minVision + " | ";
      print = print + "maxVision = " + this.maxVision + " | ";
      print = print + "minThreat = " + this.minThreat + " | ";
      print = print + "maxThreat = " + this.maxThreat + "]";
      return print;
   }

   // value
   @Override
   public double value(Data D)
   {
      // loading parameters from Data object
      this.loadParameters(D);

      // running WolfSearch with these parameters
      WolfSearch WS = new WolfSearch(this.obj,np,memorySize,minVision,maxVision,minThreat,maxThreat,this.maxTime);

      // optimizing
      WS.optimize();

      // WolfSearch objective value corresponds to value of found solution
      return WS.objValue;
   }

   /* static methods defining some problem instances */

   // instance01 (SubsetSum, 2ms)
   public static WolfSearch instance01()
   {
      Objective obj = SubsetSum.instance01();
      return new WolfSearch(obj,2);
   }

   // instance02 (Fermat, 5ms)
   public static WolfSearch instance02()
   {
      Objective obj = new Fermat(2,5);
      return new WolfSearch(obj,5);
   }

   // instance03 (Pi, 9ms)
   public static WolfSearch instance03()
   {
      Objective obj = new Pi(20,4);
      return new WolfSearch(obj,9);
   }
}

