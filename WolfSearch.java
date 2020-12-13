
/* WolfSearch class
 *
 * binMeta project
 *
 * initial version coded by Remi Viotty, M1 Info 2019-20
 *
 * last update: Dec 13, 2020
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
         String msg = "Impossible to create WolfSearch object: ";
         if (obj == null) throw new Exception(msg + "the reference to the objective is null");
         this.obj = obj;
         Data ref = this.obj.solutionSample();
         int n = ref.numberOfBits();
         if (memorySize <= 0) throw new Exception(msg + "the specified size of the wolf memory is 0 or even negative");
         this.memorySize = memorySize;
         if (maxVision < 0 || maxVision > n) throw new Exception(msg + "meaningless maximum vision parameter");
         if (minVision > maxVision) throw new Exception(msg + "minimum vision parameter seems to be larger than specified maximum vision");
         if (minThreat < 0.0 || minThreat > 1.0) throw new Exception(msg + "the specified minimun threat is not a probability (must be in [0,1])");
         if (maxThreat < 0.0 || maxThreat > 1.0) throw new Exception(msg + "the specified maximun threat is not a probability (must be in [0,1])");
         if (minThreat > maxThreat) throw new Exception(msg + "minimum threat seems to be larger than maximum probability threat");
         if (np <= 0) throw new Exception(msg + "the specified population size is 0 or even negative");
         this.np = np;
         Random R = new Random();
         this.wolves = new Memory(this.np,3);  // 3 Memory parameters: vision, pbThrets, ephemeral memory
         for (int i = 0; i < this.np; i++)
         {
            Data D = new Data(n,0.5);
            int k = this.wolves.add(D,this.obj.value(D));
            int vision = minVision + R.nextInt(maxVision - minVision);
            double pbThreat = minThreat + R.nextDouble()*(maxThreat - minThreat);
            Memory ephemeral = new Memory(this.memorySize);
            this.wolves.setParameter(k,0,vision);
            this.wolves.setParameter(k,1,pbThreat);
            this.wolves.setParameter(k,2,ephemeral);
         }
         if (maxIt <= 0) throw new Exception(msg + "the specified maximum number of iterations is 0 or even negative");
         this.maxIt = maxIt;
         if (maxTime <= 0) throw new Exception(msg + "the maximum execution time is 0 or even negative");
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

   @Override
   public void optimize()  // by WolfSearch
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
             Data D = wolf.randomSelectInNeighbour(vision);
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
                   D = other.randomSelectInNeighbour(1);
                   value = this.obj.value(D);
                   this.wolves.set(i,D,value);
                }

                // any threats?
                if (R.nextDouble() < pbThreat)
                {
                   wolfMemory.add(wolf);
                   D = wolf.randomSelectInNeighbour(vision);
                   value = this.obj.value(D);
                   this.wolves.set(i,D,value);
                }
             }
         }

         // verifying best current solution
         int bestIndex = this.wolves.getBestIndex();
         Data newBest = this.wolves.getData(bestIndex);
         double newBestValue = this.wolves.getValue(bestIndex);
         if (this.objValue == null || this.objValue > newBestValue)
         {
            this.solution = new Data(newBest);
            this.objValue = newBestValue;
         }

         // preparing for next iteration
         this.monitor();
         it++;
      }
   }

   // main
   public static void main(String[] args)
   {
      int TIMEMAX = 2000;  // max time

      // BitCounter
      int n = 50;
      Objective obj = new BitCounter(n);
      WolfSearch ws = new WolfSearch(obj,100,10,2,44,0.1,0.3,TIMEMAX,TIMEMAX);
      System.out.println(ws);
      System.out.println("optimizing ...");
      ws.optimize();
      System.out.println();
      System.out.println(ws);
      System.out.println("solution : " + ws.getSolution());
      System.out.println();

      // Fermat
      int exp = 2;
      int ndigits = 10;
      obj = new Fermat(exp,ndigits);
      ws = new WolfSearch(obj,100,10,2,28,0.1,0.3,TIMEMAX,TIMEMAX);
      System.out.println(ws);
      System.out.println("optimizing ...");
      ws.optimize();
      System.out.println();
      System.out.println(ws);
      System.out.println("solution : " + ws.getSolution());
      Data x = new Data(ws.solution,0,ndigits-1);
      Data y = new Data(ws.solution,ndigits,2*ndigits-1);
      Data z = new Data(ws.solution,2*ndigits,3*ndigits-1);
      System.out.print("equivalent to the equation : " + x.posLongValue() + "^" + exp + " + " + y.posLongValue() + "^" + exp);
      if (ws.objValue == 0.0)
         System.out.print(" == ");
      else
         System.out.print(" ?= ");
      System.out.println(z.posLongValue() + "^" + exp);
      System.out.println();

      // ColorPartition
      n = 4;  int m = 14;
      ColorPartition cp = new ColorPartition(n,m);
      ws = new WolfSearch(cp,100,10,2,n*m-2,0.1,0.3,TIMEMAX,TIMEMAX);
      System.out.println(ws);
      System.out.println("optimizing ...");
      ws.optimize();
      System.out.println();
      System.out.println(ws);
      System.out.println("solution : " + ws.getSolution());
      cp.value(ws.solution);
      System.out.println("corresponding to the matrix :\n" + cp.show());
   }
}

