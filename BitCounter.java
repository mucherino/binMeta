
/* BitCounter class
 *
 * binMeta project
 *
 * last update: Nov 1, 2020
 *
 * AM
 */

public class BitCounter extends Objective
{
   private int nbits;  // sets up a fixed length for bit string

   // Constructor
   public BitCounter(int n)
   {
      try
      {
         if (n <= 0) throw new Exception("Impossible to create BitCounter objective: bit length is 0 or even negative");
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

   @Override
   public Data solutionSample()
   {
      return new Data(this.nbits,0.5);
   }

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

      // objective evaluation: it counts the number of bits set to 1
      Data bitstring = new Data(D);
      int count = bitstring.getCurrentBit();
      while (bitstring.hasNextBit())  count = count + bitstring.getNextBit();
      this.lastValue = (double) count;
      return this.lastValue;
   }

   // main
   public static void main(String[] args)
   {
      Objective obj = new BitCounter(100);
      Data D = obj.solutionSample();
      System.out.println(obj);
      System.out.println(D);
      System.out.println("Evaluating the objective function in D : " + obj.value(D));
      System.out.println(obj);
   }
}

