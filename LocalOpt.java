
/* LocalOpt class
 *
 * binMeta project
 *
 * last update: Jan 3, 2021
 *
 * AM
 */

import java.util.TreeSet;
import java.util.TreeMap;

public class LocalOpt extends binMeta
{
   // LocalOpt constructor
   public LocalOpt(Data startPoint,Objective obj,long maxTime)
   {
      try
      {
         String msg = "Impossible to create LocalOpt object: ";
         if (maxTime <= 0) throw new Exception(msg + "the maximum execution time is 0 or even negative");
         this.maxTime = maxTime;
         if (startPoint == null) throw new Exception(msg + "the reference to the starting point is null");
         this.solution = new Data(startPoint);
         if (obj == null) throw new Exception(msg + "the reference to the objective is null");
         this.obj = obj;
         this.objValue = this.obj.value(this.solution);
         this.metaName = "LocalOpt";
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   @Override
   public void optimize()  // by LocalOpt
   {
      long startime = System.currentTimeMillis();
      Data D = new Data(this.solution);
      double value = this.objValue;

      // main loop
      do
      {
         // preserving current solution
         this.solution = new Data(D);
         this.objValue = value;

         // gradient computation
         TreeMap<Double,Integer> g = new TreeMap<Double,Integer> ();
         double gvalue = this.obj.value(D.withCurrentBitFlipped()) - value;
         if (gvalue < 0.0)  g.put(gvalue,0);
         while (D.hasNextBit())
         {
            gvalue = this.obj.value(D.withNextBitFlipped()) - value;
            if (gvalue < 0.0)  g.put(gvalue,D.getCurrentBitPointer());
         }

         // performing full step along the opposite gradient direction
         if (!g.isEmpty())
         {
            Data G = new Data(D.numberOfBits(),new TreeSet<Integer> (g.values()));
            Data E = new Data(D,G,"xor");
            value = this.obj.value(E);

            // if it is necessary to perform a partial step to get an improvement
            while (value >= this.objValue && !g.isEmpty() && System.currentTimeMillis() - startime < this.maxTime)
            {
               // remove the current largest g value (the less important)
               g.pollLastEntry();
               G = new Data(D.numberOfBits(),new TreeSet<Integer> (g.values()));
               E = new Data(D,G,"xor");
               value = this.obj.value(E);
            }

            // saving the best solution in D
            D = E;
         }
      }
      while (System.currentTimeMillis() - startime < this.maxTime && value < this.objValue);
   }

   // main
   public static void main(String[] args)
   {
      int TIMEMAX = 10000;  // max time

      // BitCounter
      int n = 50;
      Objective obj = new BitCounter(n);
      Data D = obj.solutionSample();
      LocalOpt opt = new LocalOpt(D,obj,TIMEMAX);
      System.out.println(opt);
      System.out.println("starting point : " + opt.getSolution());
      System.out.println("optimizing ...");
      opt.optimize();
      System.out.println();
      System.out.println(opt);
      System.out.println("solution : " + opt.getSolution());
      System.out.println();

      // Fermat
      int exp = 2;
      int ndigits = 10;
      obj = new Fermat(exp,ndigits);
      D = obj.solutionSample();
      opt = new LocalOpt(D,obj,TIMEMAX);
      System.out.println(opt);
      System.out.println("starting point : " + opt.getSolution());
      System.out.println("optimizing ...");
      opt.optimize();
      System.out.println();
      System.out.println(opt);
      System.out.println("solution : " + opt.getSolution());
      Data x = new Data(opt.solution,0,ndigits-1);
      Data y = new Data(opt.solution,ndigits,2*ndigits-1);
      Data z = new Data(opt.solution,2*ndigits,3*ndigits-1);
      System.out.print("equivalent to the equation : " + x.posLongValue() + "^" + exp + " + " + y.posLongValue() + "^" + exp);
      if (opt.objValue == 0.0)
         System.out.print(" == ");
      else
         System.out.print(" ?= ");
      System.out.println(z.posLongValue() + "^" + exp);
      System.out.println();

      // ColorPartition
      n = 4;  int m = 14;
      ColorPartition cp = new ColorPartition(n,m);
      D = cp.solutionSample();
      opt = new LocalOpt(D,cp,TIMEMAX);
      System.out.println(opt);
      System.out.println("starting point : " + opt.getSolution());
      System.out.println("optimizing ...");
      opt.optimize();
      System.out.println();
      System.out.println(opt);
      System.out.println("solution : " + opt.getSolution());
      cp.value(opt.solution);
      System.out.println("corresponding to the matrix :\n" + cp.show());
   }
}

