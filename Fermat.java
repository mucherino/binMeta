
/* Fermat class
 *
 * binMeta project
 *
 * last update: Nov 1, 2020
 *
 * AM
 */

public class Fermat extends Objective
{
   private int exp;  // exponent for objective: | z^exp - x^exp - y^exp |
   private int ndigits;  // expected number of digits for the representation of the integers x, y and z

   // Constructor: it generates a Fermat objective with the specified exponent, and the
   //              specified number of digits for the representation of the integers 
   public Fermat(int exp,int ndigits)
   {
      try
      {
         if (exp <= 0) throw new Exception("Impossible to create Fermat objective: the exponent is 0 or negative");
         this.exp = exp;
         if (ndigits <= 0) throw new Exception("Impossible to create Fermat objective: the specified number of digits is 0 or negative");
         if (ndigits > 52) throw new Exception("Impossible to create Fermat objective: the specified number of digits is too large");
         this.ndigits = ndigits;
         this.name = "Fermat";
         this.lastValue = null;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   @Override
   public Data solutionSample()
   {
      return new Data(3*this.ndigits,0.5);
   }

   @Override
   public double value(Data D)
   {
      try
      {
         if (D.numberOfBits() != 3*this.ndigits) 
           throw new Exception("Impossible to evaluate Fermat objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // objective evaluation: it computes | z^exp - x^exp - y^exp |
      Data x = new Data(D,0,this.ndigits - 1);
      Data y = new Data(D,this.ndigits,2*this.ndigits - 1);
      Data z = new Data(D,2*this.ndigits,3*this.ndigits - 1);
      long lx = x.posLongValue();
      double xx = 1.0;  for (int k = 0; k < this.exp; k++)  xx = lx*xx;
      long ly = y.posLongValue();
      double yy = 1.0;  for (int k = 0; k < this.exp; k++)  yy = ly*yy;
      long lz = z.posLongValue();
      double zz = 1.0;  for (int k = 0; k < this.exp; k++)  zz = lz*zz;

      this.lastValue = Math.abs(zz - xx - yy);
      return this.lastValue;
   }

   // main
   public static void main(String[] args)
   {
      int exp = 2;
      int ndigits = 6;
      Objective obj = new Fermat(exp,ndigits);
      Data D = obj.solutionSample();
      Data x = new Data(D,0,ndigits - 1);
      Data y = new Data(D,ndigits,2*ndigits - 1);
      Data z = new Data(D,2*ndigits,3*ndigits - 1);
      System.out.println(obj);
      System.out.println("sample solution : " + D);
      System.out.print("corresponding to the equation : ");
      System.out.println(x.posLongValue() + "^(" + exp + ") + " + y.posLongValue() + "^(" + exp + ") ?= " + z.posLongValue() + "^(" + exp + ")");
      System.out.println("evaluating the objective function in the sample solution : " + obj.value(D));
      System.out.println(obj);
   }
}

