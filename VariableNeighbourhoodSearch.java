
/* VariableNeighbourhoodSearch class
 *
 * binMeta project
 *
 * History:
 * - initial version coded by William Zounon (M2 Miage 2020-21)
 * - the VNS variants have been coded by Simon B. Hengeveld (PhD candidate Matisse Doctoral School 2021-24)
 *
 * In Hamming space, the distance between two solutions is at least 1. As a consequence, the neighbourhoods cannot
 * slighly grow as in continuous spaces. For this reason, we implement here, for all VNS variants, the Fleszar-Hindi
 * extention, where more than one random solution is selected in each neighbourhood. The maxAttempts parameter indicates
 * the number of solutions that are to be randomly extracted from each neighbourhood. When possible, we rather perform
 * an exhaustive search on the current neighbourhood.
 *
 * last update: April 16, 2023
 *
 * AM
 */

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class VariableNeighbourhoodSearch extends binMeta implements Objective 
{
   private int maxAttempts;  // predefined maximal number of attempts per neighborhood
   private final boolean isObjective;  // true if the parameters are decision variables
   private int procedure;  // for the definition of the neighbourhoods
   private int l;  // current lower bound on Hamming distance (for the definition of the neighbourhoods)
   private int u;  // current upper bound on Hamming distance (idem)
   private double alpha;  // alpha coefficient for skewed procedure for neighhourhood update
   private Data mask;  // used for exhaustively exploring some (relatively) small neighbourhoods
   private final String[] variant = {"basic","cyclic","pipe","jumping","skewed","nested"};  // VNS variant names

   // VariableNeighbourhoodSearch constructor
   public VariableNeighbourhoodSearch(Data startPoint,Objective obj,String vnsType,int maxAttempts,long maxTime)
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
         if (vnsType == null) throw new Exception("VariableNeighbourhoodSearch: the String supposed to contain the VNS type is null");
         if (vnsType.length() == 0) throw new Exception("VariableNeighbourhoodSearch: the String supposed to contain the VNS type is empty");
         this.setType(vnsType);
         if (maxAttempts <= 0) throw new Exception("VariableNeighbourhoodSearch: specified max number of attempts (per neighborhood) is nonpositive");
         this.maxAttempts = maxAttempts;
         if (maxTime <= 0) throw new Exception("VariableNeighbourhoodSearch: the maximum execution time (in ms) is nonpositive");
         this.maxTime = maxTime;
         this.metaName = "VariableNeighbourhoodSearch (" + vnsType + ")";
         this.l = 2;
         this.u = 2;
         this.alpha = 1.0;
         this.mask = null;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // VariableNeighbourhoodSearch constructor (for the basic implementation)
   public VariableNeighbourhoodSearch(Data startPoint,Objective obj,long maxTime)
   {
      this(startPoint,obj,"basic",100,maxTime);
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
         this.objValue = null;
         this.metaName = "Objective VariableNeighbourhoodSearch";
         this.procedure = -1;
         this.l = 0;
         this.u = 0;
         this.alpha = 0.0;
         this.mask = null;
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
      return this.metaName;
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      return new Data(15,0.5);  // 3 bits for the VNS type + 12 bits for maxAttempts (values ranging from 1 to 2^12)
   }

   // upperBound
   @Override
   public Double upperBound()
   {
      return null;
   }

   // set VNS type
   public void setType(String vnsType)
   {
      try
      {
         this.procedure = -1;
         for (int i = 0; i < 6; i++)
         {
            if (vnsType.equalsIgnoreCase(variant[i]))
            {
               this.procedure = i;
               break;
            }
         }
         if (this.procedure == -1) throw new Exception("VariableNeighbourhoodSearch: '" + vnsType + "' is an unknown VNS type");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // set the alpha coefficient (only for 'skewed' version)
   public void setAlpha(double alpha)
   {
      try
      {
         if (this.procedure != 4) throw new Exception("The alpha coefficient only needs to be set up for the skewed VNS variant");
         if (alpha <= 0.0) throw new Exception("The alpha coefficient value needs to be strictly positive");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      this.alpha = alpha;
   }

   // get the current alpha coefficient (only for 'skewed' version)
   public double getAlpha()
   {
      try
      {
         if (this.procedure != 4) throw new Exception("The alpha coefficient only needs to be set up for the skewed VNS variant");
         if (this.alpha == 0.0) throw new Exception("The alpha coefficient was not defined yet");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return this.alpha;
   }

   // verifying whether we can perform an exhaustive search on some neighbourhoods (because too small)
   private boolean[] neighbourhoodAnalysis()
   {
      // preparing data
      int nbits = this.solution.numberOfBits();
      boolean [] exhaustive = new boolean [nbits];
      for (int k = 0; k < nbits; k++)  exhaustive[k] = false;

      // verifying whether the number of solutions in the neighbourhoods is larger than maxAttempts
      for (int k = 0; k < nbits/2; k++)
      {
         if (this.solution.numberOfDataOnCircle(k+1) <= this.maxAttempts)
         {
            exhaustive[k] = true;
            exhaustive[nbits - k - 2] = true;
         }
         else break;
      }
      exhaustive[nbits - 1] = true;

      return exhaustive;
   }

   // iterating exhaustively over the current neighbourhood
   private Data exhaustivelyExploreNeighbourhood()
   {
      int nbits = this.solution.numberOfBits();

      if (this.mask == null)  // first iteration of the exhaustive search
      {
         this.mask = new Data(new Data(nbits - this.l,false),new Data(this.l,true));
         return new Data(this.solution,this.mask,"xor");
      }

      // for all other iterations of the exhaustive search
      Data.bitIterator bit = this.mask.iterator();
      int k = 0;
      while (bit.hasNext() && bit.next() == 1)  k++;
      if (k < this.mask.numberOfOnes())
      {
         // current mask indicates that the exploration of the current circle is not over yet
         while (bit.hasNext() && bit.next() == 0)  k++;
         Data three = new Data(new Data(nbits - 2,false),Data.valueOf(3,2));
         Data mm = Data.shift(three,k - nbits + 2);
         this.mask = new Data(this.mask,mm,"xor");
         return new Data(this.solution,this.mask,"xor");
      }
      else
      {
         // exploration of current circle is over; are there any others?
         if (k < this.u)
         {
            k = k + 1;
            this.mask = new Data(new Data(nbits - k,false),new Data(k,true));
            return new Data(this.solution,this.mask,"xor");
         }
     }

     // if we reach this point, the exhaustive search is over
     this.mask = null;
     return null;
   }

   // shaking procedure
   private Data shake()
   {
      return this.solution.randomSelectInNeighbourhood(this.l,this.u);
   }

   // reset neighbourhood
   private void resetNeighbourhood()
   {
      this.l = 2;
      this.u = 2;
   }

   // next neighbourhood
   private void nextNeighbourhood()
   {
      int nbits = this.solution.numberOfBits();
      if (this.l < this.u)
      {
         this.l = this.l + 1;
      }
      else if (this.u < nbits - 1)
      {
         this.u = this.u + 1;
         this.l = 2;
      }
      else this.resetNeighbourhood();
   }

   // keep best solution
   private Data keepBest(Data sol1,Data sol2)
   {
      try
      {
         Exception E = new Exception("Internal error: something wrong with Data objects");
         if (sol1 == null) throw E;
         if (sol2 == null) throw E;
         if (sol1.numberOfBits() != sol2.numberOfBits()) throw E;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      double value1 = this.obj.value(sol1);
      double value2 = this.obj.value(sol2);
      if (value1 < value2)  return sol1;
      return sol2;
   }

   // update solution
   private boolean updateSolution(Data current)
   {
      try
      {
         if (current == null) throw new Exception("Internal error: Data object is null");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      boolean updated = false;
      double value = this.obj.value(current);
      if (value < this.objValue)
      {
         this.objValue = value;
         this.solution = current;
         updated = true;
      }
      return updated;
   }

   // update neighbourhood
   private boolean updateNeighbourhood(Data current)
   {
      try
      {
         if (current == null) throw new Exception("Internal error: Data object is null");
         if (procedure < 0 || procedure > 5) throw new Exception("Internal error: unknown procedure for updating neighbourhoods");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // applying the chosen procedure for updating the neighbourhood
      boolean improved = false;
      switch (this.procedure)
      {
         case 0:  // sequential (basic)
         improved = this.updateSolution(current);
         if (improved)
            this.resetNeighbourhood();
         else
            this.nextNeighbourhood();
         break;

         case 1:  // cyclic
         improved = this.updateSolution(current);
         this.nextNeighbourhood();
         break;

         case 2:  // pipe
         improved = this.updateSolution(current);
         if (!improved)
            this.nextNeighbourhood();
         break;

         case 3:  // jumping
         improved = this.updateSolution(current);
         if (improved)
         {
            this.resetNeighbourhood();
         }
         else
         {
            Random R = new Random();
            int bits = this.solution.hammingDistanceTo(current);
            for (int k = 0; k < R.nextInt(1+bits); k++)  this.nextNeighbourhood();
         }

         case 4:  // skewed
         double value = this.obj.value(current);
         if (value - this.objValue < this.alpha*this.solution.hammingDistanceTo(current))
         {
            improved = true;
            this.objValue = value;
            this.solution = current;
            this.resetNeighbourhood();
         }
         else this.nextNeighbourhood();
         break;

         case 5:  // nested
         improved = this.updateSolution(current);
         if (improved)
         {
            this.resetNeighbourhood();
         }
         else  // attempting an improvement via an inner VNS call
         {
            Random R = new Random();
            int attempts;
            if (this.maxAttempts <= 10)
               attempts = 10;
            else
               attempts = Math.max(this.maxAttempts/10,10 + R.nextInt(this.maxAttempts - 10));
            long time = 1L;
            if (this.maxTime > 100)  time = time + this.maxTime/100;
            VariableNeighbourhoodSearch inner = new VariableNeighbourhoodSearch(this.shake(),this.obj,this.variant[R.nextInt(4)],attempts,time);
            inner.optimize();
            if (inner.objValue < this.objValue)
            {
               this.objValue = inner.objValue;
               this.solution = inner.solution;
               this.resetNeighbourhood();
            }
            else this.nextNeighbourhood();
         }
         break;

         default: break;
      }

      return improved;
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
      long startime = System.currentTimeMillis();

      // preliminary calculations
      int nbits = this.solution.numberOfBits();
      long localTime = Math.max(100L,this.maxTime/10L);
      LocalOpt local = new LocalOpt(this.solution,this.obj,localTime);
      local.optimize();
      this.updateSolution(local.getSolution());
      boolean[] exhaustive = this.neighbourhoodAnalysis();

      // main loop (trying all possible neighbourhoods out)
      this.resetNeighbourhood();
      while (System.currentTimeMillis() - startime < this.maxTime)
      {
         // verifying whether the current neighbourhood is small enough for an exhaustive search
         boolean smallenough = true;
         for (int k = this.l; k <= this.u; k++)  smallenough = smallenough & exhaustive[k];

         // searching for better solutions
         Data best = this.obj.solutionSample();
         if (smallenough)  // exhaustive search
         {
            Data current;
            do {
               current = this.exhaustivelyExploreNeighbourhood();
               if (current != null)  best = this.keepBest(current,best);
            }
            while (current != null);
         }
         else  // random search (Fleszar-Hindi extension with maxAttempts)
         {
            for (int k = 0; k < this.maxAttempts; k++)
            {
               Data current = this.shake();
               best = this.keepBest(current,best);
            }
         }

         // running local optimization on the best found solution
         if (!this.solution.equals(best))
         {
            local = new LocalOpt(best,this.obj,localTime);
            local.optimize();
            best = local.getSolution();
         }
         this.updateNeighbourhood(best);
      }
   }

   // value
   @Override
   public double value(Data D)
   {
      try
      {
         String msg = "VariableNeighbourhoodSearch: specified Data object in method 'value' ";
         if (D == null) throw new Exception(msg + "is null");
         if (D.numberOfBits() != 15) throw new Exception(msg + "does not have 15 bits, as expected");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // loading the VNS type
      Data D1 = new Data(D,0,3);
      this.procedure = D1.intValue()%6;  // the first 3 variants are represented twice

      // loading the maxAttempts parameter from Data object
      Data D2 = new Data(D,3,15);
      this.maxAttempts = D.posIntValue();

      // initializing starting point
      this.solution = this.obj.solutionSample();
      this.objValue = this.obj.value(this.solution);

      // running VariableNeighbourhoodSearch with this value for the parameter
      VariableNeighbourhoodSearch VNS = new VariableNeighbourhoodSearch(this.solution,this.obj,this.variant[this.procedure],this.maxAttempts,this.maxTime);

      // optimizing
      VNS.optimize();

      // VariableNeighbourhoodSearch objective value corresponds to value of found solution
      return VNS.objValue;
   }

   /* static methods defining some problem instances */

   // instance01 (SubsetSum, 2ms)
   public static VariableNeighbourhoodSearch instance01()
   {
      Random R = new Random();
      int n = 6 + R.nextInt(14);
      Objective obj = new SubsetSum(n,2,n,R);
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

