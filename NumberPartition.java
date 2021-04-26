
/* NumberPartition class
 *
 * binMeta project
 *
 * initial version coded by Issa Sanago (M1 Miage 2020-21)
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

   // constructor (from Set)
   public NumberPartition(Set<Integer> setOfIntegers)
   {
      try
      {
         if (setOfIntegers == null) throw new Exception("NumberPartition: Set object is null");
         if (setOfIntegers.size() == 0) throw new Exception("NumberPartition: Set object is empty");
         this.listOfIntegers = new ArrayList<Integer> (setOfIntegers);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // constructor (from array)
   public NumberPartition(int[] integers)
   {
      try
      {
         if (integers == null) throw new Exception("NumberPartition: array of integers is null");
         int n = integers.length;
         if (n == 0) throw new Exception("NumberPartition: array of integers contains no elements");
         this.listOfIntegers = new ArrayList<Integer> (n);
         for (int i = 0; i < n; i++)  this.listOfIntegers.add(integers[i]);
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

   /* static methods defining some problem instances (from https://people.sc.fsu.edu/~jburkardt/datasets/datasets.html) */

   // instance01 (corresponds to p01 in the dataset)
   public static NumberPartition instance01()
   {
      int [] ints = {2,10,3,8,5,7,9,5,3,2};
      return new NumberPartition(ints);
   }

   // instance02 (combines p02 and p03 of the dataset)
   public static NumberPartition instance02()
   {
      int [] ints = {771,121,281,854,885,734,486,1003,83,62,484,114,205,288,506,503,201,127,410};
      return new NumberPartition(ints);
   }

   // instance03 (combines p04 and p05 of the dataset)
   public static NumberPartition instance03()
   {
      int [] ints = {19,17,13,9,6,3,4,3,1,3,2,3,2,1};
      return new NumberPartition(ints);
   }

   // main
   public static void main(String[] args)
   {
      System.out.println("Objective NumberPartition");
      Random R = new Random();
      NumberPartition obj = null;
      String constrName = null;
      int constructor = R.nextInt(3);
      if (constructor == 0)
      {
         constrName = "NumberPartition(List<Integer>)";
         ArrayList<Integer> loi = new ArrayList<Integer> (4);
         loi.add(3);  loi.add(4);  loi.add(4);  loi.add(3);
         obj = new NumberPartition(loi);
      }
      else if (constructor == 1)
      {
         constrName = "NumberPartition(Set<Integer>)";
         TreeSet<Integer> soi = new TreeSet<Integer> ();
         soi.add(5);  soi.add(7);  soi.add(2);
         obj = new NumberPartition(soi);
      }
      else
      {
         constrName = "NumberPartition(int[])";
         int [] array = {5,4,6,3,3,9};
         obj = new NumberPartition(array);
      }
      System.out.println("using constructor " + constrName);
      System.out.println(obj);
      System.out.println("integer values : " + obj.getIntegerArrayListCopy());
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));
   }
}

