
/* BitCounter class
 *
 * binMeta project
 *
 * last update: May 4, 2021
 *
 * AM
 */

import java.util.Random;

public class BitCounter implements Objective
{
   private String name;
   private int nbits;  // sets up a fixed length for bit string
   private Double lastValue = null;

   // Constructor
   public BitCounter(int n)
   {
      try
      {
         if (n <= 0) throw new Exception("BitCounter: bit length is 0 or even negative");
         this.nbits = n;
         this.name = "BitCounter";
         this.lastValue = null;
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
      return this.name;
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      return new Data(this.nbits,0.5);
   }

   // value
   @Override
   public double value(Data D)
   {
      try
      {
         String msg = "Impossible to evaluate BitCounter objective: ";
         if (D == null) throw new Exception(msg + "the Data object is null");
         if (D.numberOfBits() != this.nbits) throw new Exception(msg + "unexpected bit string length in Data object");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // objective evaluation: counting the number of bits set to 1
      this.lastValue = (double) D.numberOfOnes();
      return this.lastValue;
   }

   // toString
   public String toString()
   {
      String print = "[" + this.name + ", ";
      if (this.lastValue != null)
         print = print + "last computed value = " + this.lastValue;
      else
         print = print + "objective was not evaluated yet";
      return print + "]";
   }

   // instance01 (instance with 10 bits)
   public static BitCounter instance01()
   {
      return new BitCounter(10);
   }

   // instance02 (instance with 100 bits)
   public static BitCounter instance02()
   {
      return new BitCounter(100);
   }

   // instance03 (instance with 1000 bits)
   public static BitCounter instance03()
   {
      return new BitCounter(1000);
   }

   // main
   public static void main(String[] args)
   {
      Random R = new Random();
      int n = 50 + R.nextInt(100);
      Objective obj = new BitCounter(n);
      Data D = obj.solutionSample();
      System.out.println(obj);
      System.out.println(D);
      System.out.println("Evaluating the objective function in D : " + obj.value(D));
      System.out.println(obj);
   }
}

