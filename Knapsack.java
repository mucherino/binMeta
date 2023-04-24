
/* Knapsack class
 *
 * binMeta project
 *
 * History:
 * - initial version coded by Alban Gutierrez Andre (M1 Info 2020-21) and Safietou Diallo (M1 Miage 2020-21)
 * - constructors rewritten (automatic generation)
 *
 * last update: April 21, 2023
 *
 * AM
 */

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Knapsack implements Objective
{
   // attributes
   private ArrayList<Double> listOfValues;
   private ArrayList<Double> listOfWeights;
   private double maxWeight;
   private Double ub;

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

   // constructor (randomy generated instance)
   public Knapsack(int n,int lb,int ub,double c,Random R)
   {
      try
      {
         if (n <= 0) throw new Exception("Knapsack: cannot create instance with a nonpositive number of elements");
         if (n <= 9) throw new Exception("Knapsack: cannot create instance with such a small number of elements");
         if (lb <= 0) throw new Exception("Knapsack: the given lower bound for values and weights is nonpositive");
         if (ub <= 0) throw new Exception("Knapsack: the given upper bound for values and weights is nonpositive");
         if (lb > ub) throw new Exception("Knapsack: the given lower bound for values and weights is strictly larger than the upper bounds");
         if (c < 0.0) throw new Exception("Knapsack: the given percentage for the capacity is negative");
         if (c > 1.0) throw new Exception("SetCover: the given percentage for the capacity is larger than 1.0");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      if (R == null)  R = new Random();

      // preparing the lists
      this.listOfValues = new ArrayList<Double>(n);
      this.listOfWeights = new ArrayList<Double> (n);

      // generating all (value,weight) pairs
      this.ub = 0.0;
      this.maxWeight = 0.0;
      double range = ub - lb;
      for (int i = 0; i < n; i++)
      {
         double rnd = 0.05 + 0.9*R.nextDouble();
         double lB = range*(rnd - 0.05);
         double uB = range*(rnd + 0.05);
         double newWeight = lb + lB + (uB - lB)*R.nextDouble();
         double newValue = lb + lB + (uB - lB)*R.nextDouble();
         this.listOfWeights.add(newWeight);
         this.listOfValues.add(newValue);
         if (R.nextDouble() < c)
         {
            this.ub = this.ub + newValue;
            this.maxWeight = this.maxWeight + newWeight;
         }
      }
      this.ub = -this.ub;
   }

   // constructor (randomy generated instance, also p is random here, but R cannot be null)
   public Knapsack(int n,int lb,int ub,Random R)
   {
      this(n,lb,ub,0.5*R.nextDouble(),R);
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

   // upperBound
   @Override
   public Double upperBound()
   {
      return this.ub;
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

   // main
   public static void main(String[] args)
   {
      System.out.println("Objective Knapsack");
      Random R = new Random();

      // random instance
      int n = 10 + R.nextInt(90);
      Knapsack obj = new Knapsack(n,1,n+1,R);
      System.out.println(obj);
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));

      // other constructor
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
      System.out.println(obj);
      System.out.println("values of the elements : " + obj.getArrayListValues());
      System.out.println("weights of the elements : " + obj.getArrayListWeights());
      D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));
   }
}

