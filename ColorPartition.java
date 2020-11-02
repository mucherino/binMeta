
/* ColorPartition class
 *
 * binMeta project
 *
 * last update: Nov 1, 2020
 *
 * AM
 */

public class ColorPartition extends Objective
{
   private int n;  // number of rows of color matrix
   private int m;  // number of columns of color matrix
   private boolean[][] matrix;  // color matrix (two colors)

   // Constructor
   public ColorPartition(int n,int m)
   {
      try
      {
         if (n <= 0) throw new Exception("Impossible to create ColorPartition object: number of rows is zero or even negative");
         this.n = n;
         if (m <= 0) throw new Exception("Impossible to create ColorPartition object: number of columns is zero or even negative");
         this.m = m;
         this.matrix = new boolean[this.n][this.m];
         for (int i = 0; i < this.n; i++)  for (int j = 0; j < this.m; j++)  this.matrix[i][j] = false;
         this.name = "ColorPartition";
         this.lastValue = null;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Shows a graphic representation of the last matrix
   // (true = "x"; false = "o")
   public String show()
   {
      String print = "";
      for (int i = 0; i < n; i++)
      {
         for (int j = 0; j < m; j++)
         {
            if (this.matrix[i][j])
               print = print + "x";
            else
               print = print + "o";
         }
         print = print + "\n";
      }
      return print;
   }

   @Override
   public Data solutionSample()
   {
      return new Data(this.n*this.m,0.5);
   }

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

      // loading the data structure (the bits) in the 2-dimensional matrix
      for (int i = 0; i < this.n; i++)
      {
         for (int j = 0; j < this.m; j++)
         {
            this.matrix[i][j] = (D.getCurrentBit() == 1);
            D.moveToNextBit();
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
      this.lastValue = value + count;
      return this.lastValue;
   }

   // main
   public static void main(String[] args)
   {
      int n = 8;
      int m = 25;
      ColorPartition obj = new ColorPartition(n,m);
      Data D = obj.solutionSample();
      Double value = obj.value(D);
      System.out.println(obj);
      System.out.println(obj.show());
   }
}

