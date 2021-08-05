
/* MultiStart class
 *
 * binMeta project
 *
 * initial version coded by Fatma Hamdi (M2 Miage 2020-21)
 *
 * last update: August 5, 2021
 *
 * AM
 */

import java.util.Random;

public class MultiStart extends binMeta implements Objective 
{
   private Memory M;  // internal memory
   private boolean isObjective;  // true if the parameters are decision variables

   // MultiStart constructor
   public MultiStart(Data startPoint,Objective obj,int memorySize,long maxTime)
   {
      this.isObjective = false;
      try
      {
         if (startPoint == null) throw new Exception("MultiStart: the reference to the starting point is null");
         this.solution = new Data(startPoint);
         if (obj == null) throw new Exception("MultiStart: the reference to the objective is null");
         this.obj = obj;
         if (this.obj.solutionSample().numberOfBits() != this.solution.numberOfBits())
            throw new Exception("MultiStart: starting point has a number of bits which does not match with objective function");
         this.objValue = this.obj.value(this.solution);
         this.M = null;
         if (memorySize <= 1) throw new Exception("MultiStart: specified memory size is too small, must be at least equal to 2");
         this.M = new Memory(memorySize,"WO");
         if (this.M == null) throw new Exception("MultiStart: error while initializing internal memory (capacity " + memorySize + ")");
         if (maxTime <= 0) throw new Exception("MultiStart: the maximum execution time (in ms) is nonpositive");
         this.maxTime = maxTime;
         this.metaName = "MultiStart";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // MultiStart constructor
   public MultiStart(Objective obj,long maxTime)
   {
      this.isObjective = true;
      try
      {
         if (obj == null) throw new Exception("MultiStart: the reference to the objective is null");
         this.obj = obj;
         if (maxTime <= 0) throw new Exception("MultiStart: the maximum execution time (in ms) is nonpositive");
         this.maxTime = maxTime;
         this.M = null;
         this.solution = null;
         this.objValue = null;;
         this.metaName = "Objective MultiStart";
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
      return new Data(10,0.5);  // 10 bits for the memory size (values ranging from 10 to 10 + 2^10)
   }

   // optimize (by MultiStart)
   @Override
   public void optimize()
   {
      try
      {
         if (this.isObjective) 
            throw new Exception("MultiStart: direct call to 'optimize' is not allowed when the object is initialized as an Objective");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // main variables
      int h = 1;
      Data D = null;
      double value = 0.0;
      long startime = System.currentTimeMillis();
      Random R = new Random();

      // include initial solution in the Memory (after local optimization)
      LocalOpt refine = new LocalOpt(this.solution,this.obj,this.maxTime);
      refine.optimize();
      D = refine.getSolution();
      value = this.obj.value(D);
      M.add(D,value);

      // main loop
      while (System.currentTimeMillis() - startime < this.maxTime)
      {
         // select a random Data object from the memory
         int k = R.nextInt(M.numberOfEntries());  // (the memory is always 'compact')
         D = M.getData(k);

         // looking for a neighbour and applying local optimization
         Data newD = D.randomSelectInNeighbourhood(h,h);
         refine = new LocalOpt(newD,this.obj,this.maxTime);
         refine.optimize();
         newD = refine.getSolution();
         value = this.obj.value(newD);

         // inclusion in memory (under conditions)
         if (!M.contains(newD))
         {
            value = obj.value(newD);
            M.add(newD,value);
            continue;
         }

         // if newD was actually not "new" for the memory
         Data[] Datarray = M.toDataArray();
         newD = Data.randomSelectAtDistanceFrom(h,Datarray);
         refine = new LocalOpt(newD,this.obj,this.maxTime);
         refine.optimize();
         newD = refine.getSolution();

         // inclusion in memory (under conditions)
         if (!M.contains(newD))
         {
            value = obj.value(newD);
            M.add(newD,value);
            continue;
         }

         // updating neighbourhood radius
         if (h < D.numberOfBits())  h = h + 1;
      }

      // extract the best solution from the memory
      int i = M.indexOfBest();
      this.solution = M.getData(i);
      this.objValue = M.getValue(i);
   }

   // value
   @Override
   public double value(Data D)
   {
      try
      {
         String msg = "MultiStart: specified Data object in method 'value' ";
         if (D == null) throw new Exception(msg + "is null");
         if (D.numberOfBits() != 10) throw new Exception(msg + "does not have 10 bits, as expected");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // loading the parameter from the Data object, and initializing Memory object
      int memorySize = 10 + D.intValue();

      // initializing starting point
      this.solution = this.obj.solutionSample();
      this.objValue = this.obj.value(this.solution);

      // running MultiStart with the specified Memory capacity
      MultiStart MS = new MultiStart(this.solution,this.obj,memorySize,this.maxTime);

      // optimizing
      MS.optimize();

      // MultiStart objective value corresponds to value of found solution
      return MS.objValue;
   }

   /* static methods defining some problem instances */

   // instance01 (SubsetSum, 2ms)
   public static MultiStart instance01()
   {
      Objective obj = SubsetSum.instance01();
      return new MultiStart(obj,2);
   }

   // instance02 (Fermat, 5ms)
   public static MultiStart instance02()
   {
      Objective obj = new Fermat(2,5);
      return new MultiStart(obj,5);
   }

   // instance03 (Pi, 9ms)
   public static MultiStart instance03()
   {
      Objective obj = new Pi(20,4);
      return new MultiStart(obj,9);
   }
}

