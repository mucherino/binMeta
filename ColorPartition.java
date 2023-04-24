
/* ColorPartition class
 *
 * binMeta project
 *
 * last update: April 16, 2023
 *
 * AM
 */

import java.util.Iterator;
import java.util.Random;

public class ColorPartition implements Objective
{
   // attributes
   private int n;  // number of rows of color matrix
   private int m;  // number of columns of color matrix
   private boolean[][] matrix;  // color matrix (two colors)
   private double value;  // objective function value related to current matrix

   // Constructor
   public ColorPartition(int n,int m)
   {
      try
      {
         if (n <= 0) throw new Exception("ColorPartition: number of rows is zero or even negative");
         this.n = n;
         if (m <= 0) throw new Exception("ColorPartition: number of columns is zero or even negative");
         this.m = m;
         this.matrix = null;
         this.value = 0.0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Constructor
   public ColorPartition(int n)
   {
      this((int) Math.sqrt(n),(int) Math.sqrt(n));
   }

   // getName
   @Override
   public String getName()
   {
      return "ColorPartition";
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      return new Data(this.n*this.m,0.5);
   }

   // upperBound
   @Override
   public Double upperBound()
   {
      Double smaller = Double.valueOf(n);
      if (m < n)  smaller = Double.valueOf(m);
      return smaller;
   }

   // value
   @Override
   public double value(Data D)
   {
      try
      {
         if (D.numberOfBits() != this.n*this.m)
           throw new Exception("Impossible to evaluate ColorPartition objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // verifying whether memory was already allocated for 'matrix'
      if (this.matrix == null)  this.matrix = new boolean[this.n][this.m];

      // loading the data structure (the bits) in the 2-dimensional matrix
      Iterator<Integer> It = D.iterator();
      for (int i = 0; i < this.n; i++)
      {
         for (int j = 0; j < this.m; j++)
         {
            this.matrix[i][j] = (It.next() == 1);
         }
      }

      // computing the objective function value:
      // -->  difference in number of "true" vs "false" + number of neighbors of different kind
      int count = 0;
      double value = 0.0;
      for (int i = 0; i < this.n; i++)
      {
         for (int j = 0; j < this.m; j++)
         {
            boolean current = this.matrix[i][j];
            if (current)  count++;  else count--;
            if (i > 0)
            {
               if (j > 0 && current != this.matrix[i-1][j-1])  value = value + 1.0;
               if (current != this.matrix[i-1][j])  value = value + 1.0;
               if (j < this.m - 1 && current != this.matrix[i-1][j+1])  value = value + 1.0;
            }
            if (j > 0 && current != this.matrix[i][j-1])  value = value + 1.0;
            if (j < this.m - 1 && current != this.matrix[i][j+1])  value = value + 1.0;
            if (i < this.n - 1)
            {
               if (j > 0 && current != this.matrix[i+1][j-1])  value = value + 1.0;
               if (current != this.matrix[i+1][j])  value = value + 1.0;
               if (j < this.m - 1 && current != this.matrix[i+1][j+1])  value = value + 1.0;
            }
         }
      }
      if (count < 0)  count = -count;

      this.value = value + count;
      return this.value;
   }

   // toString
   public String toString()
   {
      if (this.matrix == null)  return "[" + this.getName() + ": not evaluated yet]";

      String print = "";
      for (int i = 0; i < n; i++)    // graphic representation of the last matrix
      {                              // (true = "x"; false = "o")
         for (int j = 0; j < m; j++)
         {
            if (this.matrix[i][j])
               print = print + "x";
            else
               print = print + "o";
         }
         print = print + "\n";
      }
      print = print + "> value: " + this.value + "\n";

      return print;
   }

   // main
   public static void main(String[] args)
   {
      Random R = new Random();
      int n = 5 + R.nextInt(20);
      int m = 10 + R.nextInt(40);
      ColorPartition obj = new ColorPartition(n,m);
      System.out.println(obj);
      Data D = obj.solutionSample();
      Double value = obj.value(D);
      System.out.println(obj);
   }
}

