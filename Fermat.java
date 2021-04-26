
/* Fermat class
 *
 * binMeta project
 *
 * last update: April 21, 2021
 *
 * AM
 */

import java.util.Random;

public class Fermat implements Objective
{
   private int exp;  // exponent for objective: | z^exp - x^exp - y^exp |
   private int ndigits;  // number of digits for the representation of the integers 
                         /// (ndigits for x, ndigits for y - x, and ndigits for z - (x + y)

   // Constructor: it generates a Fermat objective with the specified exponent, and the
   //              specified number of digits for the representation of the integers 
   public Fermat(int exp,int ndigits)
   {
      try
      {
         if (exp <= 0) throw new Exception("Fermat: the exponent is 0 or negative");
         this.exp = exp;
         if (ndigits <= 0) throw new Exception("Fermat: the specified number of digits is 0 or negative");
         if (ndigits > 52) throw new Exception("Fermat: this implementation does not allow for more than 52 bits per integer");
         this.ndigits = ndigits;
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
      return "Fermat";
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      return new Data(3*this.ndigits,0.5);
   }

   // value
   @Override
   public double value(Data D)
   {
      try
      {
         if (D == null) throw new Exception("Impossible to evaluate Fermat objective: the Data object is null");
         if (D.numberOfBits() != 3*this.ndigits) 
           throw new Exception("Impossible to evaluate Fermat objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // objective evaluation: it computes | z^exp - x^exp - y^exp |
      Data x = new Data(D,0,this.ndigits);
      Data y = new Data(D,this.ndigits,2*this.ndigits);
      Data z = new Data(D,2*this.ndigits,3*this.ndigits);
      long lx = x.posLongValue();
      double xx = 1.0;  for (int k = 0; k < this.exp; k++)  xx = lx*xx;
      long ly = lx + y.longValue();
      double yy = 1.0;  for (int k = 0; k < this.exp; k++)  yy = ly*yy;
      long lz = ly + z.longValue();
      double zz = 1.0;  for (int k = 0; k < this.exp; k++)  zz = lz*zz;

      return Math.abs(zz - xx - yy);
   }

   // equationWith
   public String equationWith(Data D,boolean evaluate)
   {
      try
      {
         if (D == null) throw new Exception("Fermat: the Data object is null");
         if (D.numberOfBits() != 3*this.ndigits)
           throw new Exception("Fermat: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // writing down the equation
      String print = "";
      Data x = new Data(D,0,this.ndigits);
      Data y = new Data(D,this.ndigits,2*this.ndigits);
      Data z = new Data(D,2*this.ndigits,3*this.ndigits);
      long lx = x.posLongValue();
      print = print + lx + "^" + this.exp + " + ";
      long ly = lx + y.posLongValue();
      print = print + ly + "^" + this.exp + " = ";
      long lz = lx + ly + z.posLongValue();
      print = print + lz + "^" + this.exp;
      if (evaluate)  print = print + " (value is " + this.value(D) + ")";

      return print;
   }

   // equationWith
   public String equationWith(Data D)
   {
      return this.equationWith(D,false);
   }

   // toString
   public String toString()
   {
      return "[" + this.getName() + ": exponent is " + this.exp + "; " + this.ndigits + " digits per integer]";
   }

   // main
   public static void main(String[] args)
   {
      Random R = new Random();
      int exp = 2 + R.nextInt(6);
      int ndigits = 3 + R.nextInt(13);
      Fermat obj = new Fermat(exp,ndigits);
      System.out.println(obj);
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println(obj.equationWith(D));
      System.out.println(obj.equationWith(D,true));
   }
}

