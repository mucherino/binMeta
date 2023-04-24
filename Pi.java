
/* Pi class
 *
 * binMeta project
 *
 * last update: April 16, 2023
 *
 * AM
 */

import java.util.Random;

public class Pi implements Objective
{
   // attributes
   private int n;  // number of points on unit circle (objective is sum of distances between consecutive points on unit circle)
   private int ndigits;  // number of digits to represent angles that identify the n points

   // Constructor
   public Pi(int n,int ndigits)
   {
      try
      {
         if (n <= 1) throw new Exception("Pi: the number of points on the unit circle cannot be smaller than 1");
         this.n = n;
         if (ndigits <= 0) throw new Exception("Pi: the specified number of digits is 0 or negative");
         this.ndigits = ndigits;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Constructor (with n = 10)
   // - it is not verified that ndigits%10 is 0
   public Pi(int ndigits)
   {
      this(10,ndigits/10);
   }

   // getName
   @Override
   public String getName()
   {
      return "Pi";
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      return new Data(this.n*this.ndigits,0.5);
   }

   // upperBound
   @Override
   public Double upperBound()
   {
      return -Math.PI;
   }

   // value
   @Override
   public double value(Data D)
   {
      try
      {
         if (D == null) throw new Exception("Impossible to evaluate Pi objective: the Data object is null");
         if (D.numberOfBits() != this.n*this.ndigits)
           throw new Exception("Impossible to evaluate Pi objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // preparing temporary variables
      double [] x = new double [this.n];
      double [] y = new double [this.n];

      // extracting info from Data object
      double previous = 0.0;
      double maxAngle = 2.0*Math.PI/this.n;
      for (int i = 0; i < this.n; i++)
      {
         Data Angle = new Data(D,i*this.ndigits,(i+1)*this.ndigits);
         double angle = previous + Angle.doubleValue();
         if (angle > 2.0*Math.PI)  angle = 2.0*Math.PI;
         x[i] = Math.cos(angle);
         y[i] = Math.sin(angle);
         previous = angle;
      }

      // evaluation of Pi objective
      double pi = Pi.distance2d(this.n-1,0,x,y);
      for (int i = 0; i < this.n - 1; i++)  pi = pi + Pi.distance2d(i,i+1,x,y);

      return -0.5*pi;
   }

   // toString
   public String toString()
   {
      return "[" + this.getName() + ": # points on unit circle " + this.n + "; " + this.ndigits + " digits per angle]";
   }

   // 2d Euclidean distance (static method, arguments not verified)
   public static double distance2d(int i,int j,double[] x,double[] y)
   {
      double dx = x[j] - x[i];
      double dy = y[j] - y[i];
      return Math.sqrt(dx*dx + dy*dy);
   }

   // main
   public static void main(String[] args)
   {
      Random R = new Random();
      int n = 10 + R.nextInt(10);
      int ndigits = 3 + R.nextInt(8);
      Pi obj = new Pi(n,ndigits);
      System.out.println(obj);
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value : " + obj.value(D));
   }
}

