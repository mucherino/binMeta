
/* SubsetSum class
 *
 * binMeta project
 *
 * History:
 * - initial version coded by Franck Kouamelan (M1 Miage 2020-21)
 * - constructors rewritten (automatic generation)
 *
 * last update: April 16, 2023
 *
 * AM
 */

import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SubsetSum implements Objective
{
   // attributes
   private List<Integer> listOfIntegers;
   private int target;

   // constructor (from List)
   public SubsetSum(List<Integer> listOfIntegers,int target)
   {
      try
      {
         if (listOfIntegers == null) throw new Exception("SubsetSum: List object is null");
         if (listOfIntegers.size() == 0) throw new Exception("SubsetSum: List object contains no integers");
         this.listOfIntegers = new ArrayList<Integer> (listOfIntegers);
         if (target <= 0) throw new Exception("SubsetSum: specified target value is nonpositive");
         this.target = target;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // constructor (randomly generated instance)
   public SubsetSum(int n,int lb,int ub,double p,Random R)
   {
      try
      {
         if (n <= 0) throw new Exception("SubsetSum: cannot create instance with a nonpositive number of elements");
         if (n <= 2) throw new Exception("SubsetSum: cannot create instance with such a small list of integers");
         if (lb <= 0) throw new Exception("SubsetSum: the given lower bound is nonpositive");
         if (ub <= 0) throw new Exception("SubsetSum: the given upper bound is nonpositive");
         if (lb > ub) throw new Exception("SubsetSum: the given lower bound for the integers is strictly larger than the upper bounds");
         if (ub < 2) throw new Exception("Subsetsum: bounds [" + lb + "," + ub + "] are too strict");
         if (p < 0.1) throw new Exception("Subsetsum: the given probability is too small");
         if (p > 0.9) throw new Exception("Subsetsum: the given probability is too big");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      if (R == null)  R = new Random();

      // randomly generating the list of integers (and the target)
      this.listOfIntegers = new ArrayList<Integer> (n);
      this.target = 0;
      for (int i = 0; i < n; i++)
      {
         int number = lb + R.nextInt(ub - lb);
         this.listOfIntegers.add(number);
         if (R.nextDouble() < p)  this.target = this.target + number;
      }

      // verification for small percentage values
      if (this.target == 0)
      {
         int k = R.nextInt(n);
         this.target = this.listOfIntegers.get(k);
         if (R.nextBoolean())
         {
            int h = k;
            do {
               h = R.nextInt(n);
            }
            while (k == h);
            this.target = this.target + this.listOfIntegers.get(h);
         }
      }
   }

   // constructor (randomly generated instance, also p is random here, but R cannot be null)
   public SubsetSum(int n,int lb,int ub,Random R)
   {
      this(n,lb,ub,0.2 + 0.6*R.nextDouble(),R);
   }

   // getName
   @Override
   public String getName()
   {
      return "SubsetSum";
   }

   // getIntegerArrayListCopy
   public ArrayList<Integer> getIntegerArrayListCopy()
   {
      return new ArrayList<Integer> (this.listOfIntegers);
   }

   // getTargetValue
   public int getTargetValue()
   {
      return this.target;
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      return new Data(this.listOfIntegers.size(),0.5);
   }

   // upperBound
   @Override
   public Double upperBound()
   {
      return (Double) 0.0;
   }

   // value
   @Override
   public double value(Data D)
   {
      int n = 0;
      try
      {
         if (D == null) throw new Exception("Impossible to evaluate SubsetSum objective: the Data object is null");
         n = D.numberOfBits();
         if (n != this.listOfIntegers.size())
           throw new Exception("Impossible to evaluate SubsetSum objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // computing the partial sum for selected elements
      int sum = 0;
      Iterator<Integer> It1 = D.iterator();
      Iterator<Integer> It2 = this.listOfIntegers.iterator();
      while (It1.hasNext() && It2.hasNext())  sum = sum + (It1.next() * It2.next());

      // measuring the difference with the target value
      return (double) Math.abs(sum - this.target);
   }

   // toString
   public String toString()
   {
      return "[" + this.getName() + ": " + this.listOfIntegers.size() + " integers, target subset sum is " + this.target + "]";
   }

   // main
   public static void main(String[] args)
   {
      System.out.println("Objective SubsetSum\n");
      Random R = new Random();

      // predefined instance (instance p1 from https://people.sc.fsu.edu/~jburkardt/datasets/datasets.html) 
      List<Integer> listOfIntegers = Arrays.asList(new Integer[] {15,22,14,26,32,9,16,8});
      System.out.println("Constructor from List + Target");
      SubsetSum obj = new SubsetSum(listOfIntegers,53);
      System.out.println(obj);
      System.out.println("integer values : " + obj.getIntegerArrayListCopy());
      System.out.println("target value : " + obj.getTargetValue());
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));
      System.out.println();

      // random instance
      System.out.println("Constructor for random instances");
      int n = 6 + R.nextInt(94);
      obj = new SubsetSum(n,2,n,R);
      System.out.println(obj);
      if (n < 10)  System.out.println("integer values : " + obj.getIntegerArrayListCopy());
      System.out.println("target value : " + obj.getTargetValue());
      D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));
   }
}

