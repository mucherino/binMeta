
/* VariableNeighbourhoodSearch class
 *
 * binMeta project
 *
 * initial version coded by William Zounon (M2 Miage 2020-21)
 *
 * last update: June 25, 2021
 *
 * AM
 */

import java.util.List;
import java.util.ArrayList;

public class VariableNeighbourhoodSearch extends binMeta implements Objective 
{
   private int maxAttempts;  // predefined maximal number of attempts per neighborhood
   private boolean isObjective;  // true if the parameters are decision variables

   // VariableNeighbourhoodSearch constructor
   public VariableNeighbourhoodSearch(Data startPoint,Objective obj,int maxAttempts,long maxTime)
   {
      this.isObjective = false;
      try
      {
         if (startPoint == null) throw new Exception("VariableNeighbourhoodSearch: the reference to the starting point is null");
         this.solution = new Data(startPoint);
         if (obj == null) throw new Exception("VariableNeighbourhoodSearch: the reference to the objective is null");
         this.obj = obj;
         if (this.obj.solutionSample().numberOfBits() != this.solution.numberOfBits())
            throw new Exception("VariableNeighbourhoodSearch: starting point has a number of bits which does not match with objective function");
         this.objValue = this.obj.value(this.solution);
         if (maxAttempts <= 0) throw new Exception("VariableNeighbourhoodSearch: specified max number of attempts (per neighborhood) is nonpositive");
         this.maxAttempts = maxAttempts;
         if (maxTime <= 0) throw new Exception("VariableNeighbourhoodSearch: the maximum execution time (in ms) is nonpositive");
         this.maxTime = maxTime;
         this.metaName = "VariableNeighbourhoodSearch";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // VariableNeighbourhoodSearch constructor
   public VariableNeighbourhoodSearch(Objective obj,long maxTime)
   {
      this.isObjective = true;
      try
      {
         if (obj == null) throw new Exception("VariableNeighbourhoodSearch: the reference to the objective is null");
         this.obj = obj;
         if (maxTime <= 0) throw new Exception("VariableNeighbourhoodSearch: the maximum execution time (in ms) is nonpositive");
         this.maxTime = maxTime;
         this.maxAttempts = 0;
         this.solution = null;
         this.objValue = null;;
         this.metaName = "Objective VariableNeighbourhoodSearch";
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
      return new Data(12,0.5);  // 12 bits for maxAttempts (values ranging from 1 to 2^12)
   }

   // optimize (by VariableNeighbourhoodSearch)
   @Override
   public void optimize()
   {
      try
      {
         if (this.isObjective) 
            throw new Exception("VariableNeighbourhoodSearch: direct call to 'optimize' is not allowed when the object is initialized as an Objective");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // variables
      int l = 0;  // lower bound on neighbours (base 0)
      int u = 0;  // upper bound on neighbours
      int nbits = this.solution.numberOfBits();
      long startime = System.currentTimeMillis();

      // preliminary calculation on the number of Data objects per neighbourhood (with l==u)
      int [] mAttempts = new int [nbits];
      boolean limit = false;
      for (int k = 0; k < nbits; k++)
      {
         if (mAttempts[k] != 0)
         {
            if (!limit)
            {
               mAttempts[k] = this.solution.numberOfDataOnCircle(k);
               if (mAttempts[k] >= this.maxAttempts)
               {
                  mAttempts[k] = this.maxAttempts;
                  limit = true;
               }
               mAttempts[nbits-k-1] = mAttempts[k];
            }
            else
            {
               mAttempts[k] = this.maxAttempts;
            }
         }
      }

      // the center of all neighbours is locally optimized by LocalOpt
      LocalOpt opt = new LocalOpt(this.solution,this.obj,this.maxTime);
      opt.optimize();
      Data center = new Data(opt.getSolution());
      double refValue = opt.getObjVal();

      // main loop (trying all possible neighbourhoods out)
      while (System.currentTimeMillis() - startime < this.maxTime)
      {
         Data neighbour = null;
         boolean found = false;

         // choosing exploration method on the basis of the number of expected neighbours in the current neighbourhood
         boolean exhaustiveSearch = false;
         if (mAttempts[u] < this.maxAttempts)
         {
            int sum = mAttempts[l];
            for (int k = l + 1; k <= u; k++)  sum = sum + mAttempts[k];
            if (sum < this.maxAttempts)  exhaustiveSearch = true;
         }

         // exhaustive search?
         if (exhaustiveSearch)
         {
            for (int k = l; k <= u && !found; k++)
            {
               List<Data.bitIterator> list = new ArrayList<Data.bitIterator> ();
               for (int i = 0; i <= k; i++)  list.add(null);
               do {
                  neighbour = center.next(list);
                  if (neighbour != null)
                  {
                     double value = this.obj.value(neighbour);
                     if (value < refValue)
                     {
                        center = new Data(neighbour);
                        refValue = value;
                        found = true;
                     }
                  }
               }
               while (neighbour != null && !found && System.currentTimeMillis() - startime < this.maxTime);
            }
         }

         // random exploration of neighbourhood
         if (!exhaustiveSearch)
         {
            int nAttempts = 0;
            while (nAttempts < this.maxAttempts && !found && System.currentTimeMillis() - startime < this.maxTime)
            {
               neighbour = center.randomSelectInNeighbourhood(l+1,u+1);
               double value = this.obj.value(neighbour);
               if (value < refValue)
               {
                  center = new Data(neighbour);
                  refValue = value;
                  found = true;
               }
               nAttempts++;
            }
         }

         // if a better solution was not found
         if (!found)
         {
            // updating the bounds on the current neighbour
            if (exhaustiveSearch)
            {
               l = u + 1;  u = l;
               if (l > nbits - 1)
               {
                  l = 1;  u = u - 1;
               }
            }
            else
            {
               if (u < nbits - 1)
               {
                  u = u + 1;
               }
               else if (l < nbits - 1)
               {
                  l = l + 1;
               }
               else
               {
                  l = 1;
               }
            }
         }

         // if a better solution was found
         if (found)
         {
            opt = new LocalOpt(center,this.obj,this.maxTime);
            opt.optimize();
            center = new Data(opt.getSolution());
            refValue = opt.getObjVal();
            l = 1;  u = 1;
         }
      }

      // saving the best found solution
      this.solution = new Data(center);
      this.objValue = refValue;
   }

   // value
   @Override
   public double value(Data D)
   {
      try
      {
         String msg = "VariableNeighbourhoodSearch: specified Data object in method 'value' ";
         if (D == null) throw new Exception(msg + "is null");
         if (D.numberOfBits() != 12) throw new Exception(msg + "does not have 12 bits, as expected");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // loading the maxAttempts parameter from Data object
      this.maxAttempts = D.posIntValue();

      // initializing starting point
      this.solution = this.obj.solutionSample();
      this.objValue = this.obj.value(this.solution);

      // running VariableNeighbourhoodSearch with this value for the parameter
      VariableNeighbourhoodSearch VNS = new VariableNeighbourhoodSearch(this.solution,this.obj,this.maxAttempts,this.maxTime);

      // optimizing
      VNS.optimize();

      // VariableNeighbourhoodSearch objective value corresponds to value of found solution
      return VNS.objValue;
   }

   /* static methods defining some problem instances */

   // instance01 (SubsetSum, 2ms)
   public static VariableNeighbourhoodSearch instance01()
   {
      Objective obj = SubsetSum.instance01();
      return new VariableNeighbourhoodSearch(obj,2);
   }

   // instance02 (Fermat, 5ms)
   public static VariableNeighbourhoodSearch instance02()
   {
      Objective obj = new Fermat(2,5);
      return new VariableNeighbourhoodSearch(obj,5);
   }

   // instance03 (Pi, 9ms)
   public static VariableNeighbourhoodSearch instance03()
   {
      Objective obj = new Pi(20,4);
      return new VariableNeighbourhoodSearch(obj,9);
   }
}

