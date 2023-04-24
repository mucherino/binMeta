
/* NumberPartition class
 *
 * binMeta project
 *
 * History:
 * - initial version coded by Issa Sanago (M1 Miage 2020-21)
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

public class NumberPartition implements Objective
{
   // attributes
   private ArrayList<Integer> listOfIntegers;

   // constructor (from List)
   public NumberPartition(List<Integer> listOfIntegers)
   {
      try
      {
         if (listOfIntegers == null) throw new Exception("NumberPartition: List object is null");
         if (listOfIntegers.size() == 0) throw new Exception("NumberPartition: List object contains no integers");
         this.listOfIntegers = new ArrayList<Integer> (listOfIntegers);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // constructor (randomy generated instance)
   public NumberPartition(int n,int lb,int ub,Random R)
   {
      try
      {
         if (n <= 0) throw new Exception("NumberPartition: cannot create instance with a nonpositive number of elements");
         if (n <= 9) throw new Exception("NumberPartition: cannot create instance with such a small number of elements");
         if (lb <= 0) throw new Exception("NumberPartition: the given lower bound for the integers is nonpositive");
         if (ub <= 0) throw new Exception("NumberPartition: the given upper bound for the integers is nonpositive");
         if (lb > ub) throw new Exception("NumberPartition: the given lower bound for the integers is strictly larger than the upper bounds");
         if (lb + 1 >= ub) throw new Exception("NumberPartition: the given bounds are too strict");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      if (R == null)  R = new Random();

      // computing the integers in the two expected partitions (at the same time)
      int sum1 = 0;
      int sum2 = 0;
      ArrayList<Integer> l1 = new ArrayList<Integer> ();
      ArrayList<Integer> l2 = new ArrayList<Integer> ();
      while (l1.size() + l2.size() < n - 1)
      {
         int number = lb + R.nextInt(ub - lb);
         if (sum1 <= sum2)
         {
            l1.add(number);
            sum1 = sum1 + number;
         }
         else
         {
            l2.add(number);
            sum2 = sum2 + number;
         }
      }

      // the last element allows us to make the two sums match
      if (sum1 != sum2)
      {
         int last = Math.abs(sum1 - sum2);
         if (sum1 < sum2)  l1.add(last);  else  l2.add(last);
      }

      // joining and mixing the two partitions
      this.listOfIntegers = l1;
      this.listOfIntegers.addAll(l2);
      Collections.shuffle(this.listOfIntegers);
   }

   // getName
   @Override
   public String getName()
   {
      return "NumberPartition";
   }

   // getIntegerArrayListCopy
   public ArrayList<Integer> getIntegerArrayListCopy()
   {
      return new ArrayList<Integer> (this.listOfIntegers);
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
         if (D == null) throw new Exception("Impossible to evaluate NumberPartition objective: the Data object is null");
         n = D.numberOfBits();
         if (n != this.listOfIntegers.size())
            throw new Exception("Impossible to evaluate NumberPartition objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // summing up the two partial sums
      int sum = 0;
      Iterator<Integer> It1 = D.iterator();
      Iterator<Integer> It2 = this.listOfIntegers.iterator();
      while (It1.hasNext() && It2.hasNext())
      {
         sum = sum + (2*It1.next() - 1) * It2.next();
      }

      return (double) Math.abs(sum);
   }

   // toString
   public String toString()
   {
      return "[" + this.getName() + ": " + this.listOfIntegers.size() + " integers]";
   }

   // main
   public static void main(String[] args)
   {
      System.out.println("Objective NumberPartition\n");
      Random R = new Random();

      // predefined instance (instance p1 from from https://people.sc.fsu.edu/~jburkardt/datasets/datasets.html)
      List<Integer> listOfIntegers = Arrays.asList(new Integer[] {2,10,3,8,5,7,9,5,3,2});
      System.out.println("Constructor from List");
      NumberPartition obj = new NumberPartition(listOfIntegers);
      System.out.println(obj);
      System.out.println("integer values : " + obj.getIntegerArrayListCopy());
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));
      System.out.println();

      // random instance
      System.out.println("Constructor for random instances");
      int n = 10 + R.nextInt(90);
      obj = new NumberPartition(n,1,n,R);
      if (n < 10)  System.out.println("integer values : " + obj.getIntegerArrayListCopy());
      D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));
   }
}

