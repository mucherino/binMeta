
/* SubsetSum class
 *
 * binMeta project
 *
 * initial version coded by Franck Kouamelan (M1 Miage 2020-21)
 *
 * last update: April 26, 2021
 *
 * AM
 */

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Random;

public class SubsetSum implements Objective
{
   // attributes
   private ArrayList<Integer> listOfIntegers;
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

   // constructor (from Set)
   public SubsetSum(Set<Integer> setOfIntegers,int target)
   {
      try
      {
         if (setOfIntegers == null) throw new Exception("SubsetSum: Set object is null");
         if (setOfIntegers.size() == 0) throw new Exception("SubsetSum: Set object is empty");
         this.listOfIntegers = new ArrayList<Integer> (setOfIntegers);
         if (target <= 0) throw new Exception("SubsetSum: specified target value is nonpositive");
         this.target = target;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // constructor (from array)
   public SubsetSum(int[] integers,int target)
   {
      try
      {
         if (integers == null) throw new Exception("SubsetSum: array of integers is null");
         int n = integers.length;
         if (n == 0) throw new Exception("SubsetSum: array of integers contains no elements");
         this.listOfIntegers = new ArrayList<Integer> (n);
         for (int i = 0; i < n; i++)  this.listOfIntegers.add(integers[i]);
         if (target <= 0) throw new Exception("SubsetSum: specified target value is nonpositive");
         this.target = target;
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

   /* static methods defining some problem instances (from https://people.sc.fsu.edu/~jburkardt/datasets/datasets.html) */

   // instance01 (combines p01 and p02 of the dataset)
   public static SubsetSum instance01()
   {
      int [] ints = {15,22,14,26,32,9,16,8,267,493,869,961,1000,1153,1246,1598,1766,1922};
      return new SubsetSum(ints,5895);
   }

   // instance02 (corresponds to p03 in the database)
   public static SubsetSum instance02()
   {
      int [] ints = {518533,1037066,2074132,1648264,796528,1593056,686112,1372224,244448,488896,977792,1955584,1411168,322336,644672,1289344,78688,157376,314752,629504,1259008};
      return new SubsetSum(ints,2463098);
   }

   // instance03 (combines p04, p05, p06 and p07)
   public static SubsetSum instance03()
   {
      int [] ints = {41,34,21,20,8,7,7,4,3,3,81,80,43,40,30,26,12,11,9,1,2,4,8,16,32,25,27,3,12,6,15,9,30,21,19};
      return new SubsetSum(ints,222);
   }

   // main
   public static void main(String[] args)
   {
      System.out.println("Objective SubsetSum");
      Random R = new Random();
      SubsetSum obj = null;
      String constrName = null;
      int constructor = R.nextInt(3);
      if (constructor == 0)
      {
         constrName = "SubsetSum(List<Integer>,int)";
         ArrayList<Integer> loi = new ArrayList<Integer> (4);
         loi.add(3);  loi.add(4);  loi.add(2);  loi.add(4);
         obj = new SubsetSum(loi,8);
      }
      else if (constructor == 1)
      {
         constrName = "SubsetSum(Set<Integer>,int)";
         TreeSet<Integer> soi = new TreeSet<Integer> ();
         soi.add(3);  soi.add(5);  soi.add(2);
         obj = new SubsetSum(soi,8);
      }
      else
      {
         constrName = "SubsetSum(int[],int)";
         int [] array = {5,4,6,2,1,4};
         obj = new SubsetSum(array,19);
      }
      System.out.println("using constructor " + constrName);
      System.out.println(obj);
      System.out.println("integer values : " + obj.getIntegerArrayListCopy());
      System.out.println("target value : " + obj.getTargetValue());
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));
   }
}

