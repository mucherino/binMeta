
/* Knapsack class
 *
 * binMeta project
 *
 * initial version coded by Alban Gutierrez Andre (M1 Info 2020-21) 
 *                      and Safietou Diallo (M1 Miage 2020-21)
 *
 * last update: May 29, 2021
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

public class Knapsack implements Objective
{
   // attributes
   private ArrayList<Double> listOfValues;
   private ArrayList<Double> listOfWeights;
   private double maxWeight;

   // constructor (from two lists, one with the values, the other with the weights)
   public Knapsack(List<Double> listOfValues,List<Double> listOfWeights,double maxWeight)
   {
      try
      {
         if (listOfValues == null) throw new Exception("Knapsack: specified list of values is null");
         if (listOfValues.isEmpty()) throw new Exception("Knapsack: specified list of values is empty");
         if (Collections.min(listOfValues) < 0.0) throw new Exception("Knapsack: specified list of values contains nonpositive elements");
         if (listOfWeights == null) throw new Exception("Knapsack: specified list of weights is null");
         if (listOfWeights.isEmpty()) throw new Exception("Knapsack: specified list of weights is empty");
         if (Collections.min(listOfWeights) < 0.0) throw new Exception("Knapsack: specified list of weights contains nonpositive elements");
         if (listOfValues.size() != listOfWeights.size()) throw new Exception("Knapsack: specified lists differ in length");
         this.listOfValues = new ArrayList<Double> (listOfValues);
         this.listOfWeights = new ArrayList<Double> (listOfWeights);
         if (maxWeight <= 0.0) throw new Exception("Knapsack: specified maximum weight must be strictly positive");
         this.maxWeight = maxWeight;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // constructor (from two arrays, one with the values, the other with the weights)
   public Knapsack(double[] listOfValues,double[] listOfWeights,double maxWeight)
   {
      try
      {
         if (listOfValues == null) throw new Exception("Knapsack: specified array of values is null");
         int n = listOfValues.length;
         if (n == 0) throw new Exception("Knapsack: specified array of values is empty");
         if (listOfWeights == null) throw new Exception("Knapsack: specified array of weights is null");
         if (listOfWeights.length == 0) throw new Exception("Knapsack: specified array of weights is empty");
         if (n != listOfWeights.length) throw new Exception("Knapsack: specified arrays differ in length");
         this.listOfValues = new ArrayList<Double> (n);
         for (int i = 0; i < n; i++)  this.listOfValues.add(listOfValues[i]);
         if (Collections.min(this.listOfValues) <= 0.0) throw new Exception("Knapsack: specified array of values contains nonpositive elements");
         this.listOfWeights = new ArrayList<Double> (n);
         for (int i = 0; i < n; i++)  this.listOfWeights.add(listOfWeights[i]);
         if (Collections.min(this.listOfWeights) <= 0.0) throw new Exception("Knapsack: specified array of weights contains nonpositive elements");
         if (maxWeight <= 0.0) throw new Exception("Knapsack: specified maximum weight must be strictly positive");
         this.maxWeight = maxWeight;
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
      return "Knapsack";
   }

   // getArrayListValues (copy)
   public ArrayList<Double> getArrayListValues()
   {
      return new ArrayList<Double> (this.listOfValues);
   }

   // getArrayListWeights (copy)
   public ArrayList<Double> getArrayListWeights()
   {
      return new ArrayList<Double> (this.listOfWeights);
   }

   // getMaxWeight
   public double getMaxWeight()
   {
      return this.maxWeight;
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      return new Data(this.listOfValues.size(),0.5);
   }

   // value (new implementation of the method proposed by Charly Colombu, M2 Miage 2020-21)
   @Override
   public double value(Data D)
   {
      int n = 0;
      try
      {
         if (D == null) throw new Exception("Impossible to evaluate Knapsack objective: the Data object is null");
         n = D.numberOfBits();
         if (n != this.listOfValues.size())
            throw new Exception("Impossible to evaluate Knapsack objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // preparing variables and iterators
      double sumValues = 0.0;
      double sumWeights = 0.0;
      Data.bitIterator ItData = D.iterator();
      Iterator<Double> ItValue = this.listOfValues.iterator();
      Iterator<Double> ItWeight = this.listOfWeights.iterator();

      // computing the sum of the values for the selected elements
      while (ItData.hasNext() && ItValue.hasNext())  sumValues = sumValues + ItData.next()*ItValue.next();
      ItData.reset(); 

      // computing the sum of the weights for the selected elements
      while (ItData.hasNext() && ItWeight.hasNext())  sumWeights = sumWeights + ItData.next()*ItWeight.next();

      // the constraint on the maximum weight is in the objective function
      double objvalue = 0.0;
      if (sumWeights <= this.maxWeight)
         objvalue = -sumValues;
      else
         objvalue = sumWeights - this.maxWeight;
      return objvalue;
   }

   // toString
   public String toString()
   {
      return "[" + this.getName() + ": " + this.listOfValues.size() + " items, max weight is " + this.maxWeight +  "]";
   }

   /* static methods defining some problem instances (from https://people.sc.fsu.edu/~jburkardt/datasets/datasets.html) */

   // instance01 (corresponds to p01 in the dataset)
   public static Knapsack instance01()
   {
      double [] weights = {23,31,29,44,53,38,63,85,89,82};
      double [] values = {92,57,49,68,60,43,67,84,87,72};
      return new Knapsack(values,weights,165.0);
   }

   // instance02 (combines p02, p03, p04, p05 of the dataset)
   public static Knapsack instance02()
   {
      double [] weights = {12,7,11,8,9,56,59,80,64,75,17,31,10,20,19,4,3,6,25,35,45,5,25,3,2,2};
      double [] values = {24,13,23,15,16,50,50,64,46,50,5,70,20,39,37,7,5,10,350,400,450,20,70,8,5,5};
      return new Knapsack(values,weights,370.0);
   }

   // instance03 (combines p06 and p07 of the dataset)
   public static Knapsack instance03()
   {
      double [] weights = {41,50,49,59,55,57,60,70,73,77,80,82,87,90,94,98,106,110,113,115,118,120};
      double [] values = {442,525,511,593,546,564,617,135,139,149,150,156,163,173,184,192,201,210,214,221,229,240};
      return new Knapsack(values,weights,1627.0);
   }

   // main
   public static void main(String[] args)
   {
      System.out.println("Objective Knapsack");
      Random R = new Random();
      Knapsack obj = null;
      String constrName = null;
      int constructor = R.nextInt(2);
      if (constructor == 0)
      {
         constrName = "Knapsack(List<Double>,List<Double>,double)";
         ArrayList<Double> values = new ArrayList<Double> (10);
         ArrayList<Double> weights = new ArrayList<Double> (10);
         values.add(5.0);  weights.add(7.0);  // solution 27 with indices 0, 1, 2, 7, 8 (weight is 15)
         values.add(4.0);  weights.add(2.0);  // solution 27 with indices 1, 2, 6, 7, 8, 9 (weight is 13)
         values.add(7.0);  weights.add(1.0);
         values.add(2.0);  weights.add(9.0);
         values.add(1.0);  weights.add(5.0);
         values.add(8.0);  weights.add(10.0);
         values.add(3.0);  weights.add(2.0);
         values.add(5.0);  weights.add(1.0);
         values.add(6.0);  weights.add(4.0);
         values.add(2.0);  weights.add(3.0);
         obj = new Knapsack(values,weights,15);
      }
      else
      {
         constrName = "Knapsack(double[],double[],double)";
         double [] values = new double [] {1,4,5,8,1,3,9,7,12,8};
         double [] weights = new double [] {7,8,2,6,3,12,11,9,4,6};
         obj = new Knapsack(values,weights,27);  // solution 37 with indices 1, 2, 3, 8, 9
      }
      System.out.println("using constructor " + constrName);
      System.out.println(obj);
      System.out.println("values of the elements : " + obj.getArrayListValues());
      System.out.println("weights of the elements : " + obj.getArrayListWeights());
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));
   }
}

