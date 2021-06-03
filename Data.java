
/* Data class
 *
 * binMeta project
 *
 * last update: June 3, 2021
 *
 * AM
 */

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.NoSuchElementException;
import java.math.BigInteger;

public class Data implements Comparable<Data>, Iterable<Integer>
{
   protected int size;  // actual size (may not be a multiple of 8)
   protected ArrayList<Byte> data;  // the data are stored as lists of bytes

   /* constructors */

   // Data constructor: it generates a new Data object with its n bits set to 0 or 1,
   //                   depending on value of the boolean argument (0=false, 1=true)
   public Data(int n,boolean bitValue)
   {
      try
      {
         if (n <= 0) throw new Exception("Specified size for Data object is nonpositive");

         int N = 1 + (n - 1)/8;
         this.data = new ArrayList<Byte> (N);
         int bi = 0;  if (bitValue)  bi = ~bi;
         byte b = (byte) bi; 
         for (int i = 0; i < N - 1; i++)  this.data.add(b);
         int exb = 8 - n%8;
         if (exb != 8)  b = (byte) Data.mask_bits(bi,exb);
         this.data.add(b);
         this.size = n; 
      }
      catch (Exception e)
      {  
         e.printStackTrace();
         System.exit(1);
      }        
   }

   // Data constructor: it generates a random Data object consisting of n bits, 
   //                   and with probability p in [0,1] to have bits equal to 1
   public Data(int n,double p)
   {
      try
      {
         if (n <= 0) throw new Exception("Specified size for Data object is nonpositive");
         if (p < 0.0 || p > 1.0) throw new Exception("Specified probability p should be contained in the interval [0,1]");
         Random R = new Random();

         // randomly choosing the bits to be set to 1
         boolean [] bits = new boolean [n];
         for (int k = 0; k < n; k++)  bits[k] = false;
         int lb = (int) Math.floor(p*n);
         int ub = lb;  if (p < 0.99)  ub = (int) Math.floor((p + 0.01)*n);
         int nones = lb;
         if (ub - lb > 0)  nones = nones + R.nextInt(ub - lb);
         int c = 0;
         while (c != nones)
         {
            int k = R.nextInt(n);
            if (!bits[k])
            {
               bits[k] = true;
               c++;
            }
         }

         // constructing the Data object
         int k = 0; 
         int N = 1 + (n - 1)/8;
         this.data = new ArrayList<Byte> (N);
         for (int i = 0; i < N; i++)
         {  
            int bi = 0;
            for (int j = 0; j < 8; j++)
            {  
               if (k < n && bits[k])  bi = bi + 1;
               if (j != 7)  bi = bi << 1;
               k++;
            }
            byte b = (byte) bi;
            this.data.add(b);
         }
         this.size = n; 
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object by setting at 1 all bits indicated in input Set object
   //                   all other bits are set to 0 (bit indices out of bounds will be ignored)
   public Data(int n,Set<Integer> ones)
   {
      try
      {
         if (n <= 0) throw new Exception("Specified size for Data object is nonpositive");
         if (ones == null) throw new Exception("Specified Set object is null");

         int k = 0;
         int N = 1 + (n - 1)/8;
         this.data = new ArrayList<Byte> (N);
         for (int i = 0; i < N; i++)
         {
            int bi = 0;
            for (int j = 0; j < 8; j++)
            {
               if (k < n && ones.contains(8*i + j))  bi = bi + 1;
               if (j != 7)  bi = bi << 1;
               k++;
            }
            byte b = (byte) bi;
            this.data.add(b);
         }
         this.size = n;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object containing the bit sequence of another Data object
   //                   the bits are unchanged when flip is false; the bits are flipped when flip is true
   public Data(Data D,boolean flip)
   {
      try
      {
         if (D == null) throw new Exception("Input Data object is null");
         int nbytes = D.numberOfBytes();
         this.size = D.numberOfBits();
         this.data = new ArrayList<Byte> (nbytes);
         for (int k = 0; k < nbytes; k++)
         {
            byte b = D.data.get(k);
            if (flip)
            {
               int bi = b;
               bi = ~bi;
               if (k == nbytes - 1)
               {
                  int exb = 8 - this.size%8;
                  if (exb != 8)  bi = Data.mask_bits(bi,exb);
               }
               b = (byte) bi;
            }
            this.data.add(b);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object containing the same bit sequence of another Data object
   public Data(Data D)
   {
      this(D,false);
   }

   // Data constructor: it generates a new Data object consisting of the bit sequence specified in a String object
   //                   (method proposed by Manu Lagadec, M1 Info 2020-21)
   public Data(String s)
   {
      try
      {
         if (s == null) throw new Exception("Input String object is null");
         if (s.length() == 0) throw new Exception("Input String object is empty");
         this.size = s.length();
         int N = 1 + (this.size - 1)/8;
         this.data = new ArrayList<Byte> (N);
         int k = 1;
         int bi = 0;
         for (int i = 0; i < s.length(); i++)
         {
            char c = s.charAt(i);
            if (c == '1')
               bi = bi | 1;
            else if (c != '0')
               throw new Exception("The input String is supposed to contain '0' and '1' characters only");
            if (k < 8)
            {
               bi = bi << 1;
               k++;
            }
            else
            {
               byte b = (byte) bi;
               this.data.add(b);
               bi = 0;
               k = 1;
            }
         }
         if (k != 1)
         {
            bi = bi << (8 - k);
            byte b = (byte) bi;
            this.data.add(b);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object by concatenating two Data objects
   public Data(Data D1,Data D2)
   {
      try
      {
         if (D1 == null) throw new Exception("First Data object is null");
         if (D2 == null) throw new Exception("Second Data object is null");

         // setting up main attributes
         this.size = D1.numberOfBits() + D2.numberOfBits();
         int N = 1 + (this.size - 1)/8;
         this.data = new ArrayList<Byte> (N);

         // if the D1 has a number of bits multiple of 8, it's simple
         if (D1.numberOfBits()%8 == 0)
         {
            for (int k = 0; k < D1.numberOfBytes(); k++)  this.data.add(D1.data.get(k));
            for (int k = 0; k < D2.numberOfBytes(); k++)  this.data.add(D2.data.get(k));
         }
         else
         {
            // copying first Data object
            int k = 0;
            for (; k < D1.numberOfBytes() - 1; k++)  this.data.add(D1.data.get(k));

            // concatenating
            int outphase2 = D1.numberOfBits()%8;
            int outphase1 = 8 - outphase2;
            int mask = 0;
            for (int i = 0; i < outphase1; i++)  mask = mask | (1 << i);
            int bi = D1.data.get(k) | ((D2.data.get(0) >> outphase2) & mask);
            byte b = (byte) bi;
            this.data.add(b);

            // continuing with the second Data object
            bi = D2.data.get(0) << outphase1;
            k = 1;
            for (; k < D2.numberOfBytes(); k++)
            {
               bi = bi | ((D2.data.get(k) >> outphase2) & mask);
               b = (byte) bi;
               this.data.add(b);
               bi = D2.data.get(k) << outphase1;
            }
            if (this.data.size() < N)
            {
               b = (byte) bi;
               this.data.add(b);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object by extracting a specific subsequence of bits from an existing Data object
   //                   the first specified bit is included; the last specified bit is not included
   public Data(Data D,int first,int last)
   {
      try
      {
         if (D == null) throw new Exception("Input Data object is null");
         if (first < 0) throw new Exception("Specified first bit index is negative");
         if (first >= D.numberOfBits())
            throw new Exception("Specified first bit index " + first + " is out of range: [0," + D.numberOfBits() + ")");
         if (last < 0) throw new Exception("Specified last bit index is negative");
         if (last > D.numberOfBits())
            throw new Exception("Specified last bit index " + last + " is out of range: [0," + D.numberOfBits() + ")");
         if (last <= first) throw new Exception("Specified first bit index needs to be strictly smaller than specified last bit index");

         this.size = last - first;
         int N = 1 + (this.size - 1)/8;
         this.data = new ArrayList<Byte> (N);
         int pointer = first;
         for (int i = 0; i < N; i++)
         {
            int bi = 0;
            for (int j = 0; j < 8; j++)
            {
               if (pointer < last)
               {
                  int bit = D.getBit(pointer);
                  bi = bi + bit;
               }
               if (j != 7)  bi = bi << 1;
               pointer++;
            }
            byte b = (byte) bi;
            this.data.add(b);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object by combining two Data objects having the same size by applying 
   //                   one of the following bitwise operators: and, or, xor.
   public Data(Data D1,Data D2,String op)
   {
      try
      {
         if (D1 == null) throw new Exception("First Data object is null");
         if (D2 == null) throw new Exception("Second Data object is null");
         if (op == null) throw new Exception("The String supposed to contain the bitwise operation is null");
         if (D1.numberOfBits() != D2.numberOfBits()) throw new Exception("The two Data objects have different size (in terms of bits)");
         if (!op.equalsIgnoreCase("and") && !op.equalsIgnoreCase("or") && !op.equalsIgnoreCase("xor"))
            throw new Exception("Unknown bitwise operation: \"" + op + "\""); 

         int N = D1.numberOfBytes();
         this.data = new ArrayList<Byte> (N);
         this.size = D1.numberOfBits();

         if (op.equalsIgnoreCase("and"))  // and
         {
            for (int k = 0; k < N; k++)
            {
               int b = D1.data.get(k) & D2.data.get(k);
               this.data.add((byte) b);
            }
         }
         else if (op.equalsIgnoreCase("or"))  // or
         {
            for (int k = 0; k < N; k++)
            {
               int b = D1.data.get(k) | D2.data.get(k);
               this.data.add((byte) b);
            }
         }
         else // xor
         {
            for (int k = 0; k < N; k++)
            {
               int b = D1.data.get(k) ^ D2.data.get(k);
               if (k == N - 1)
               {
                  int exb = 8 - this.size%8;
                  if (exb != 8)  b = Data.mask_bits(b,exb);
               }
               this.data.add((byte) b);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object from an array of bytes
   //                   n indicates the actual number of bits (does not need to be a multiply of 8)
   public Data(int n,byte[] bytes)
   {
      try
      {
         if (n <= 0) throw new Exception("Specified size for Data object is nonpositive");
         if (bytes == null) throw new Exception("Array of bytes is null");
         if (bytes.length == 0) throw new Exception("Array of bytes is empty");
         if (8*bytes.length < n) throw new Exception("Not enough bytes in the array to cover " + n + " bits");

         int N = 1 + (n - 1)/8;
         this.data = new ArrayList<Byte> (N);
         for (int i = 0; i < N - 1; i++)  this.data.add(bytes[i]);
         byte b = bytes[N-1];
         int exb = 8 - n%8; 
         if (exb != 8)  b = (byte) Data.mask_bits((int) b,exb);
         this.data.add(b);
         this.size = n;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   /* basic methods */

   // Gives the number of bytes forming the Data object
   public int numberOfBytes()
   {
      return this.data.size();
   }

   // Gives the number of bits forming the Data object
   public int numberOfBits()
   {
      return this.size;
   }

   // Gives the specified bit 
   // (private method, it doesnt verify if the bit and byte indices are correct)
   private int getBit(int i,int j)
   {
      byte b = (byte) this.data.get(i);
      return (b >> (7 - j)) & 1;
   }

   // Gives the specified bit
   // (private method, it doesnt verify if the bit index is correct)
   private int getBit(int k)
   {
      int i = k/8;
      int j = k%8;
      return this.getBit(i,j);
   }

   // Flips the specified bit
   // (private method, it doesnt verify if the bit and byte indices are correct)
   private void flipBit(int i,int j)
   {
      byte x = (byte) (1 << (7 - j));
      byte b = (byte) this.data.get(i);
      this.data.set(i,(byte)(b^x));
   }

   // Flips the specified bit
   // (private method, it doesnt verify if the bit index is correct)
   private void flipBit(int k)
   {
      this.flipBit(k/8,k%8);
   }

   // Gives the number of bits equal to 0
   public int numberOfZeros()
   {
      int nZeros = 0;
      for (int k = 0; k < this.numberOfBits(); k++)  if (this.getBit(k) == 0)  nZeros++;
      return nZeros;
   }

   // Gives the number of bits equal to 1
   public int numberOfOnes()
   {
      int nOnes = 0;
      for (int k = 0; k < this.numberOfBits(); k++)  if (this.getBit(k) == 1)  nOnes++;
      return nOnes;
   }

   // Gives the number of bits equal to 0 or 1 (selected via the boolean argument)
   public int numberOfBits(boolean bitValue)
   {
      int n = 0;
      if (bitValue)
         n = this.numberOfOnes();
      else
         n = this.numberOfZeros();
      return n;
   }

   // Creates a new array of bytes with the bytes forming the Data object
   public byte[] toByteArray()
   {
      byte[] byteArray = new byte [this.data.size()];
      int k = 0;
      for (byte b : this.data)
      {
         byteArray[k] = b;
         k++;
      }
      return byteArray;
   }

   /* bitIterator 
    *
    * it allows for iterating on:
    * - the bits of the Data object (hasNext, next, hasPrevious, previous)
    * - the Data objects obtained by flipping the current bit (hasNext, withNextBitFlipped, hasPrevious, withPreviousBitFlipped)
    * - the Data objects belong to the "spiral" around the current bit (hasNextOnSpiral, nextOnSpiral)
    */

   @Override
   // Gives a bitIterator on the bits of the Data object
   public bitIterator iterator()
   {
      return new bitIterator(this);
   }

   // class bitIterator
   public class bitIterator implements Iterator<Integer>
   {
      // bitIterator attributes
      private Data data;  // reference to Data object
      private int bitPointer;  // bit pointer
      private Integer spiPointer;  // spiral pointer
      private Data spiral;

      // constructor
      public bitIterator(Data data)
      {
         super();
         this.data = data;
         this.bitPointer = -1;
         this.spiPointer = null;
         this.spiral = null;
      }

      // spiReset (private)
      private void spiReset()
      {
         this.spiPointer = null;
         this.spiral = null;
      }

      @Override
      // hasNext
      public boolean hasNext()
      {
         return this.bitPointer + 1 < this.data.numberOfBits();
      }

      @Override
      // next
      public Integer next() throws NoSuchElementException
      {
         if (!this.hasNext()) throw new NoSuchElementException();
         this.bitPointer++;
         this.spiReset();
         return this.data.getBit(this.bitPointer);
      }

      // hasPrevious
      public boolean hasPrevious()
      {
         return this.bitPointer - 1 >= 0;
      }

      // previous
      public Integer previous() throws NoSuchElementException
      {
         if (!this.hasPrevious()) throw new NoSuchElementException();
         this.bitPointer--;
         this.spiReset();
         return this.data.getBit(this.bitPointer);
      }

      // reset
      public void reset()
      {
         this.bitPointer = -1;
      }

      // setToEnd
      public void setToEnd()
      {
         this.bitPointer = this.data.numberOfBits();
      }

      // withNextBitFlipped
      // gives a new Data object equal to the Data reference but with next bit flipped
      public Data withNextBitFlipped() throws NoSuchElementException
      {
         this.next();
         Data D = new Data(this.data);
         D.flipBit(this.bitPointer);
         return D;
      }

      // withPreviousBitFlipped
      // gives a new Data object equal to the Data reference but with previous bit flipped
      public Data withPreviousBitFlipped() throws NoSuchElementException
      {
         this.previous();
         Data D = new Data(this.data);
         D.flipBit(this.bitPointer);
         return D;
      }

      // hasNextOnSpiral
      // -> initial version coded by Narcisse Kouadio (M2 Miage 2020-21)
      public boolean hasNextOnSpiral()
      {
         int n = this.data.numberOfBits();
         if (n < 3)  return false;
         if (this.bitPointer < 1 || this.bitPointer >= n - 1)  return false;
         if (this.spiPointer != null)
         {
            int nextPointer = 0;
            if (this.spiPointer < 0)
               nextPointer = this.bitPointer - this.spiPointer + 1;
            else
               nextPointer = this.bitPointer - this.spiPointer;
            if (nextPointer < 0 || nextPointer >= n)  return false;
         }
         return true;
      }

      // nextOnSpiral
      // -> initial version coded by Narcisse Kouadio (M2 Miage 2020-21)
      public Data nextOnSpiral() throws NoSuchElementException
      {
         int n = this.data.numberOfBits();
         NoSuchElementException E = new NoSuchElementException("Next Data object on spiral does not exist");
         if (n < 3) throw E;
         if (this.bitPointer < 1 || this.bitPointer >= n - 1) throw E;
         if (this.spiPointer == null)
         {
            this.spiPointer = 1;
            this.spiral = new Data(this.data);
            this.spiral.flipBit(this.bitPointer + this.spiPointer);
         }
         else
         {
            if (this.spiPointer > 0)
               this.spiPointer = -this.spiPointer;
            else
               this.spiPointer = -this.spiPointer + 1;
            int next = this.bitPointer + this.spiPointer;
            if (next < 0 || next >= n) throw E;
            this.spiral.flipBit(next);
         }
         return this.spiral;
      }
   }

   /* static methods for the generation of more complex Data objects */

   // Provides a Data object of the same size of the two input arguments where each bit is set to
   // -> 0 if the two corresponding bits in D1 and D2 coincide
   // -> 1 if they differ
   public static Data diff(Data D1,Data D2)
   {
      return new Data(D1,D2,"xor");
   }

   // Generates a new Data object which is a copy of the input Data object D where the number of bits
   // having value corresponding to "bitValue" is reduced to "nbits". The value of "nbits" is supposed
   // to be in the interval [0,m], where m is the number of bits corresponding to "bitValue".
   public static Data control(Data D,boolean bitValue,int nbits)
   {
      int cnbits = 0;
      try
      {
         if (D == null) throw new Exception("Input Data object is null");
         if (nbits < 0) throw new Exception("Specified number of bits is negative");
         cnbits = D.numberOfBits(bitValue);
         if (nbits > cnbits)  throw new Exception("Specified number of bits exceeds the current number of bits of the given type");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // nothing to do if nbits reflects the current state
      if (nbits == cnbits)  return new Data(D);

      // if no bits corresponding to the bitValue need to be left
      int n = cnbits - nbits;
      if (n == cnbits)  return new Data(D.numberOfBits(),!bitValue);

      // removing (flipping) "cnbits-nbits" randomly-selected bits corresponding to the bitValue
      Random R = new Random();
      int bit = 0;
      if (bitValue)  bit = 1;
      Data newD = new Data(D);
      while (n > 0)
      {
         int k = R.nextInt(newD.numberOfBits());
         if (newD.getBit(k) == bit)
         {
            newD.flipBit(k);
            n--;
         }
      }
      return newD;
   }

   // Generates a new Data object which is a copy of the input Data object D where the percentage of bits 
   // equal to "bitValue" are reduced to the given percentage p (p = 0 would make all these bits disappear, 
   // while p = 1 would keep all of them)
   public static Data control(Data D,boolean bitValue,double p)
   {
      try
      {
         if (D == null) throw new Exception("Input Data object is null");
         if (p < 0.0 || p > 1.0) throw new Exception("The specified probability is not in the interval [0,1]");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // changing probability p in number of bits
      int n = D.numberOfBits(bitValue);
      int nbits = (int) ((1.0 - p)*n);

      // invoking Data.control(Data,boolean,int) and returning
      return Data.control(D,bitValue,nbits);
   }

   // Generates a new Data object from a Data object given as an argument, where:
   // -> the bits are shifted in one of the two directions, and for the specified number of places;
   // -> the direction of the shift is given by the sign of the integer input argument
   public static Data shift(Data D,int nplaces)
   {
      // setting up direction and number of shifts
      int direction = 0;
      int steps = 0;
      if (nplaces >= 0)
      {
         direction = 1;
         steps = nplaces;
      }
      else
      {
         direction = -1;
         steps = -nplaces;
      }

      // verifying the input arguments
      try
      {
         if (D == null) throw new Exception("The input Data object is null");
         if (steps > D.numberOfBits()) throw new Exception("The number of shift steps is larger than the total number of bits in Data object");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      int n = D.numberOfBits();

      // constructing the new Data object
      Data result = null;
      if (steps == 0 || steps == n)
      {
         // no changes
         result = new Data(D);
      }
      else if (direction > 0)
      {
         // shifting bits on the right side
         result = new Data(new Data(steps,false),new Data(D,0,n-steps));
      }
      else
      {
         // shifting bits on the left side
         result = new Data(new Data(D,steps,n),new Data(steps,false));
      }

      return result;
   }

   // Generates a new Data object where the bits of a given Data object are shuffled
   public static Data shuffle(Data D)
   {
      try
      {
         if (D == null) throw new Exception("The input Data object is null");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      int n = D.numberOfBits();
      int nzeros = D.numberOfZeros();
      int nones = n - nzeros;
      if (nzeros == n || nones == n)  return new Data(D);

      Random R = new Random();
      Data result = null;
      do
      {
         Set<Integer> ones = new TreeSet<Integer> ();
         int target = nones;
         while (target > 0)
         {
            int i = R.nextInt(n);
            if (!ones.contains(i))
            {
               ones.add(i);
               target--;
            }
         }
         result = new Data(n,ones);
      }
      while (result.equals(D));

      return result;
   }

   // Concatenats a List of Data objects in one single Data object where the list order is preserved 
   public static Data concat(List<Data> lD)
   {
      try
      {
         if (lD == null) throw new Exception("The list of Data objects is null");
         if (lD.size() == 0) throw new Exception("The list of Data objects is empty");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      Data result = new Data(lD.get(0));
      int k = 1;
      while (k < lD.size())
      {
         result = new Data(result,lD.get(k));
         k++;
      }
      return result;
   }

   // Applies the "crossover" operator to generate a child object from a given list of object parents
   // -> all parent Data objects are supposed to have the same number of bits; at least 2 parents
   // -> initially coded by Issa Sanogo (M1 Miage 2020-21)
   public static Data crossover(List<Data> lD)
   {
      int n = 0;
      int m = 0;
      try
      {
         if (lD == null) throw new Exception("The list of parent Data objects is null");
         n = lD.size();
         if (n == 0) throw new Exception("The list of parent Data objects is empty");
         if (n < 2) throw new Exception("Cannot apply crossover operation with one parent");
         m = lD.get(0).numberOfBits();
         for (Data D : lD)  if (m != D.numberOfBits()) throw new Exception("All Data objects are supposed to have the same size (in terms of bits)");
         if (n > m) throw new Exception("Too many parents (" + n + ") for Data objects of size " + m);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // randomly positioning the pivots
      Random R = new Random();
      ArrayList<Integer> pivot = new ArrayList<Integer> (n);
      pivot.add(0);
      while (pivot.size() < n)
      {
         int ptest = R.nextInt(m-1);
         if (!pivot.contains(ptest))  pivot.add(ptest);
      }
      pivot.add(m);
      Collections.sort(pivot);

      // extracting the pieces, and concatenating in one object
      ArrayList<Data> pieces = new ArrayList<>();
      for (int i = 0; i < n; i++)
      {
         Data D = new Data(lD.get(i),pivot.get(i),pivot.get(i+1));
         pieces.add(D);
      }
      return Data.concat(pieces);
   }

   // Generates a random Data object obtained by flipping some bits of the Data object D to fit with those of an Attractor
   // -> the speed argument indicates the intensity of the attraction, in the real interval [0,1]
   //    (lower the speed, more calls to the method are necessary to make D become identical to Attractor)
   // -> coded by Mouhammadou Ba, Yaya Simpara, Vildan Ozturk (M2 Miage 2020-21)
   public static Data attract(Data D,Data Attractor,double speed)
   {
      try
      {
         if (D == null) throw new Exception("First input Data object D is null");
         if (Attractor == null) throw new Exception("Second input Data object is null");
         if (D.numberOfBits() != Attractor.numberOfBits()) throw new Exception("The two specified input Data objects differ in bit length");
         if (speed < 0.0 || speed > 1.0)
            throw new Exception("Specified speed argument is supposed to be contained in the interval [0,1]");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // constructing the attracted Data object
      Data result = null;
      if (speed == 0.0)
      {
         result = new Data(D);  // no changes
      }
      else if (speed == 1.0)
      {
         result = new Data(Attractor);  // the result is the Attractor
      }
      else
      {
         Data Diff = Data.diff(D,Attractor);
         Data cDiff = Data.control(Diff,true,speed);
         result = Data.diff(D,cDiff);
      }

      return result;
   }

   // Generates a random Data object obtained by flipping some bits of the Data object D to be the opposite of those of a Repellent
   // -> the speed argument indicates the intensity of the repulsion, in the real interval [0,1]
   //    (lower the speed, more calls to the method are necessary to make D become the opposite of Repellent)
   public static Data repel(Data D,Data Repellent,double speed)
   {
      try
      {
         if (Repellent == null) throw new Exception("Second input Data object is null");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return Data.attract(D,new Data(Repellent,true),speed);
   }

   /* Hamming-distance based methods */

   // Computes the Hamming distance between this Data object, and the argument Data object D
   public int hammingDistanceTo(Data D)
   {
      try
      {
         if (D == null) throw new Exception("Impossible to compute Hamming distance: Data object D is null");
         if (this.numberOfBits() != D.numberOfBits()) throw new Exception("Impossible to compute Hamming distance: number of bits differ");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      int h = 0;
      int l = this.numberOfBits();
      for (int i = 0; i < l; i++)  if (this.getBit(i) != D.getBit(i))  h++;
      return h;
   }

   // Verifies whether this Data object belongs to the neighbour of Data object D
   // (h is the hamming distance defining the neighbourhood)
   public boolean belongsToNeighbourOf(Data D,int h)
   {
      try
      {
         if (h < 0) throw new Exception("Impossible to verify neighbourhood of Data object: specified Hamming distance is negative");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }  
      
      return this.hammingDistanceTo(D) <= h;
   }

   // Selects a random Data object in the neighbourhood with Hamming distance [l,u] from this Data object
   public Data randomSelectInNeighbourhood(int l,int u)
   {
      String msg = "Neighbourhood of Data object: ";
      try
      {
         if (l <= 0) throw new Exception(msg + "lower bound on Hamming distance is nonpositive");
         if (u < 0)  throw new Exception(msg + "upper bound on Hamming distance is negative");
         if (l > u)  throw new Exception(msg + "upper bound is greater than lower bound on Hamming distance");
         if (this.numberOfBits() < l) throw new Exception(msg + "lower bound is greater than the total number of bits");
         if (this.numberOfBits() < u) throw new Exception(msg + "upper bound is greater than the total number of bits");
      }
      catch (Exception e)
      {  
         e.printStackTrace();
         System.exit(1);
      }

      Random R = new Random();
      Data random = new Data(this);
      int len = random.numberOfBits();
      boolean[] toFlip = new boolean[len];
      for (int i = 0; i < len; i++)  toFlip[i] = false;
      int actual = l + R.nextInt(u - l + 1);
      for (int j = 0; j < actual; j++)
      {  
         int bitindex = 0;
         do bitindex = R.nextInt(len);  while (toFlip[bitindex]);
         random.flipBit(bitindex);
         toFlip[bitindex] = true;
      }
      return random;
   }

   // Selects a random Data object in the neighbourhood with Hamming distance h from this Data object
   public Data randomSelectInNeighbourhood(int h)
   {
      return this.randomSelectInNeighbourhood(1,h);
   }

   // Selects a random Data object in the neighbourhood from this Data object with the following properties
   // 1. the Hamming distance between this and the new object is between l and u (as in previous method)
   // 2. only a selection of bit subsequences of (almost) equal length are allowed to have their bit flipped
   // -> nnseq indicates the number of subsequences (if n%nnseq != 0, the remaining bits are not covered)
   // -> subseqs is a Set containing the indices (from 0 to nnseq-1) of the selected subsequences
   public Data randomSelectInNeighbourhood(int l,int u,int nsseq,Set<Integer> subseqs)
   {
      int npbits;  // number of "potential" bits (which can be actually be flipped)
      String msg = "Neighbourhood of Data object: ";
      try
      {
         if (l <= 0) throw new Exception(msg + "lower bound on Hamming distance is nonpositive");
         if (u < 0)  throw new Exception(msg + "upper bound on Hamming distance is negative");
         if (l > u)  throw new Exception(msg + "upper bound is greater than lower bound on Hamming distance");
         if (nsseq < 0 || nsseq > this.numberOfBits()) throw new Exception(msg + "number of selected subsequences not consistent with Data object");
         if (subseqs == null) throw new Exception(msg + "Set of selected subsequences is null");
         if (subseqs.size() == 0) throw new Exception(msg + "Set of selected subsequences is empty");
         for (Integer s : subseqs)  if (s < 0 || s >= nsseq) 
            throw new Exception(msg + "subsequence index is out of bounds (value is " + s + ", min is 0, max is " + nsseq + ")");
         npbits = (this.numberOfBits()*subseqs.size()) / nsseq;
         if (npbits < l) throw new Exception(msg + "lower bound is greater than the total number of bits that can actually be flipped");
         if (npbits < u) throw new Exception(msg + "upper bound is greater than the total number of bits that can actually be flipped");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // separating the several bit subsequences 
      int ninseq = this.numberOfBits() / nsseq;  // number of bits in each subsequence
      List<Data> lD = new ArrayList<Data> (nsseq);
      int len = 0;
      for (int j = 0; j < nsseq; j++)
      {
         lD.add(new Data(this,len,len+ninseq));
         len = len + ninseq;
      }

      // adding to the list the extra bits (not included in any subsequence, if any)
      if (len != this.numberOfBits())  lD.add(new Data(this,len,this.numberOfBits()));

      // selecting the subsequences where bits can be flipped
      List<Data> selSubSeqs = new ArrayList<Data> (subseqs.size());
      for (Integer s : subseqs)  selSubSeqs.add(lD.get(s));

      // invoke randomSelectInNeighbourhood on the set of selected subsequences
      Data actual = Data.concat(selSubSeqs);
      Data result = actual.randomSelectInNeighbourhood(l,u);

      // extract the modified subsequences and placing them back in the original Data object
      len = 0;
      for (int j = 0; j < nsseq; j++)
      {
         if (subseqs.contains(j))
         {
            lD.set(j,new Data(result,len,len+ninseq));
            len = len + ninseq;
         }
      }
      return Data.concat(lD);
   }

   // Selects a random Data object in the neighbourhood from this Data object with the following properties
   // 1. the Hamming distance between this and the new object is smaller than h (as in previous method)
   // 2. only a selection of bit subsequences of (almost) equal length are allowed to have their bit flipped
   // -> nnseq indicates the number of subsequences (if n%nnseq != 0, the remaining bits are not covered)
   // -> subseqs is a Set containing the indices (from 0 to nnseq-1) of the selected subsequences
   public Data randomSelectInNeighbourhood(int h,int nsseq,Set<Integer> subseqs)
   {
      return this.randomSelectInNeighbourhood(0,h,nsseq,subseqs);
   }

   // Selects a random Data object so that the Hamming distance to every element in given array of Data objects
   // is at least equal to the specified value (the argument h); when this is not possible, a close guess is attempted
   // -> coded by Fatma Hamdi (M2 Miage 2020-21)
   public static Data randomSelectAtDistanceFrom(int h,Data[] DataArray)
   {
      int n = 0;
      try
      {
         if (h < 0) throw new Exception("Specified Hamming distance is negative");
         if (DataArray == null) throw new Exception("Specified array of Data objects is null");
         if (DataArray.length == 0) throw new Exception("Specified array of Data objects is empty");
         n = DataArray[0].numberOfBits();
         for (int i = 1; i < DataArray.length; i++)
             if (n != DataArray[i].numberOfBits())
                throw new Exception("Data objects in the array are supposed to have the same length (in terms of bits)");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // if only one Data object is in the array
      if (DataArray.length == 1)  return DataArray[0].randomSelectInNeighbourhood(h,h);

      // we compare the objects in the array against each other
      Data df = new Data(n,false); 
      Data cdf = null;
      for (int i = 0; i < DataArray.length; i++)
      {
         for (int j = i + 1; j < DataArray.length; j++)
         {
            cdf = Data.diff(DataArray[i],DataArray[j]);
            df = new Data(df,cdf,"or");
         }
      }

      // the bits equal to 0 in df allow us to move farther from each object in the array
      int mainBits = df.numberOfZeros();

      // if flipping all these bits can give us a Hamming distance larger than h
      if (mainBits > h)  df = Data.control(df,false,h);

      // if this number of bits is not sufficient to attain a Hamming distance equal to h
      if (mainBits < h)  df = Data.control(df,true,n-h);

      // we randomly select an object in DataArray, and we apply the changes specified in "not df"
      int k = new Random().nextInt(DataArray.length);
      return Data.diff(DataArray[k],new Data(df,true));
   }

   /* Data conversions from and to other data (mostly primitive) types */

   // Generats a new Data object from a boolean variable
   // (the second argument indicates the total number of bits, but only the last bit is significative)
   public static Data valueOf(boolean b,int length)
   {
      try
      {
         if (length <= 0) throw new Exception("Specified length for Data object is nonpositive");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      Data data = new Data(1,b);
      if (length > 1)  data = new Data(new Data(length-1,false),data);

      return data;
   }

   // Generats a new Data object from a boolean variable
   // (the number of bits in the resulting Data object is 1)
   public static Data valueOf(boolean b)
   {
      return Data.valueOf(b,1);
   }

   // Converts the Data object to boolean
   public boolean booleanValue()
   {
      try
      {
         boolean onlyonebit = true;
         for (int k = 0; k < this.numberOfBits() - 1 && onlyonebit; k++)
         {  
            if (this.getBit(k) != 0)  onlyonebit = false;
         }
         if (!onlyonebit) throw new Exception("Impossible to covert to boolean: the data structure is more complex");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return this.getBit(this.numberOfBits() - 1) == 1;
   }

   // Generats a new Data object from an integer variable
   // (the second argument indicates the total number of bits) 
   public static Data valueOf(int integer,int length)
   {
      try
      {
         if (length <= 0) throw new Exception("Specified length for Data object is nonpositive");
         if (length >= 32) throw new Exception("Specified length is larger than the number of bits in an integer (sign is ignored)");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      if (integer < 0)  integer = -integer;

      byte [] bytes = new byte [4];
      bytes[3] = (byte) (integer & 255);
      integer = integer >> 8;
      bytes[2] = (byte) (integer & 255);
      integer = integer >> 8;
      bytes[1] = (byte) (integer & 255);
      integer = integer >> 8;
      bytes[0] = (byte) (integer & 255);

      return new Data(new Data(32,bytes),32-length,32);
   }

   // Generates a new Data object from an integer variable
   // (the 31 bits representing the integer are all considered, the sign is ignored)
   public static Data valueOf(int integer)
   {
      return Data.valueOf(integer,31);
   }

   // Converts the Data object in a nonnegative integer (the sign bit is always 0)
   public int intValue()
   {
      try
      {
         if (this.numberOfBits() >= 32) throw new Exception("Impossible to convert in integer: too many bits (sign bit is not counted)");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      int value = 0;
      for (int k = 0; k < this.numberOfBits(); k++)
      {
         value = value << 1;
         value = value | this.getBit(k);
      }
      return value;
   }

   // Converts the Data object in a positive integer (values > 0)
   public int posIntValue()
   {
      int value = this.intValue();
      try
      {
         if (value == Integer.MAX_VALUE) throw new Exception("Impossible to convert to positive integer: value exceeds integer representability");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      
      return 1 + value;
   }

   // Generates a new Data object from a long integer
   // (the second argument indicates the total number of bits) 
   public static Data valueOf(long longint,int length)
   {
      try
      {
         if (length <= 0) throw new Exception("Specified length for Data object is nonpositive");
         if (length >= 64) throw new Exception("Specified length is larger than the number of bits in a long integer (sign is ignored)");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      if (longint < 0)  longint = -longint;

      byte [] bytes = new byte [8];
      int k = 7;
      while (k >= 0)
      {
         bytes[k] = (byte) (longint & 255);
         longint = longint >> 8;
         k--;
      }

      return new Data(new Data(64,bytes),64-length,64);
   }

   // Generates a new Data object from a long integer
   // (the 63 bits representing the long integer are all considered, the sign is ignored)
   public static Data valueOf(long longint)
   {
      return Data.valueOf(longint,63);
   }

   // Converts the Data object in a nonnegative long integer (the sign bit is always 0)
   public long longValue()
   {
      try
      {
         if (this.numberOfBits() >= 64) throw new Exception("Impossible to convert to long integer: too many bits");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      long value = 0;
      for (int k = 0; k < this.numberOfBits(); k++)
      {
         value = value << 1;
         value = value | (long) this.getBit(k);
      }
      return value;
   }

   // Converts the Data object in a positive long integer (values > 0)
   public long posLongValue()
   {
      long value = this.longValue();
      try
      {
         if (value == Long.MAX_VALUE) throw new Exception("Impossible to convert to positive long integer: value exceeds long representability");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return 1L + value;
   }

   // Generates a new Data object from a BitInteger object 
   // (it doesn't consider the sign)
   public static Data valueOf(BigInteger bigint)
   {
      try
      {
         if (bigint == null) throw new Exception("BitInteger object is null");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      Data D = null;
      BigInteger B = bigint.abs();
      int len = B.bitLength();
      if (len != 0)
      {
         byte [] bytes = B.toByteArray();
         int s = 8 - len%8;
         D = new Data(len+s,bytes);
         if (s != 0)  D = new Data(Data.shift(D,-s),0,len);
      }
      else D = new Data(1,false);

      return D;
   }

   // Converts the Data object in a nonnegative BigInteger object (the sign bit is always 0)
   public BigInteger BigIntegerValue()
   {
      Data D = new Data(new Data(1,false),this);
      int s = D.numberOfBits()%8;
      if (s != 0)
      {
         D = new Data(D,new Data(8-s,false));
         D = Data.shift(D,8-s);
      }
      return new BigInteger(D.toByteArray());
   }


   // Converts the Data object in a positive BigInteger object (the sign bit is always 0)
   public BigInteger posBigIntegerValue()
   {
      BigInteger B = this.BigIntegerValue();
      return B.add(BigInteger.ONE);
   }

   // Converts the Data object in nonnegative float of form 0.m (m is the long value of the bit sequence)
   public float floatValue()
   {
      int l = this.numberOfBits();
      Data mantissa = this;
      if (l > 30)
      {
         l = 30;
         mantissa = new Data(this,0,30);
      }
      return (float) mantissa.longValue() / (1 << l);
   }

   // Converts the Data object in a nonnegative float uniformly placed in the range [0,1]
   public float floatValueNormalized()
   {
      int l = this.numberOfBits();
      if (l > 30)  l = 30;
      Data max = new Data(l,true);
      float scale = 1.0f / max.floatValue();
      return scale*this.floatValue();
   }

   // Converts the Data object in nonnegative double of form 0.m (m is the long value of the bit sequence)
   public double doubleValue()
   {
      int l = this.numberOfBits();
      Data mantissa = this;
      if (l > 62)
      {
         l = 62;
         mantissa = new Data(this,0,62);
      }
      return (double) mantissa.longValue() / (1L << l);
   }

   // Converts the Data object in a nonnegative double uniformly placed in the range [0,1]
   public double doubleValueNormalized()
   {
      int l = this.numberOfBits();
      if (l > 62)  l = 62;
      Data max = new Data(l,true);
      double scale = 1.0 / max.doubleValue();
      return scale*this.doubleValue();
   }

   // Generates a new Data object from a char
   // (only 5 bits are used if the boolean argument specifies that the char is an alphabet letter)
   public static Data valueOf(char c,boolean isLetter)
   {
      int length = 8;
      byte [] bytes = new byte [1];
      try
      {
         if (isLetter)
         {
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) throw new Exception("The char is not an alphabet letter");
            if (c >= 'a' && c <= 'z')  c = (char) (c - 32);
            int bi = (int) (c - 'A');
            bi = bi << 3;
            bytes[0] = (byte) bi;
            length = 5;
         }
         else
         {
            bytes[0] = (byte) c;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return new Data(length,bytes);
   }

   // Generates a new Data object from a char (bit length is always 8 bits)
   public static Data valueOf(char c)
   {
      return Data.valueOf(c,false);
   }

   // Converts the Data object to an ASCII char
   public char charValue()
   {
      try
      {
         if (this.numberOfBits() != 8) throw new Exception("Impossible to convert in ASCII character: number of bits is not 8");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      int value = 0;
      for (int k = 0; k < 8; k++)
      {
         value = value << 1;
         value = value | this.getBit(k);
      }
      return (char) value;
   }

   // Converts the Data object to a capital letter (A->Z, 26 letters, 5 bits)
   public char letterValue()
   {
      char c = '\0';
      try
      {
         if (this.numberOfBits() > 5) throw new Exception("Impossible to convert in capital letter: too many bits (only 5 required)");
         int gap = this.intValue();
         if (gap > 25) throw new Exception("Impossible to convert in capital letter: no letter after 'Z' (bit code is " + gap + ">25)");
         c = (char) ('A' + gap);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      return c;
   }

   /* Data compression methods */

   // Compresses the Data object so that, for every subsequence of bits of equal length, 
   // only a maximal number of bit combinations can actually be represented
   public static Data compress(Data D,int sublength,long maxcomb)
   {
      int nsubs = 0;
      Data [] subbits = null;
      try
      {
         // initial verifications
         if (D == null) throw new Exception("The Data object to be compressed is null");
         if (sublength <= 0) throw new Exception("The length of bit subsequences is nonpositive");
         if (sublength > 62) throw new Exception("This implementation of 'compress' does not allow the bit subsequences to have more than 62 bits");
         if (sublength > D.numberOfBits()) throw new Exception("The length of bit sequences cannot exceed the number of bits in Data object");
         if (D.numberOfBits()%sublength != 0) 
            throw new Exception("The length of bit subsequences (" + sublength + 
                                ") needs to be multiple of Data object's bit length (" + D.numberOfBits() + ")");
         if (maxcomb <= 0) throw new Exception("The maximal number of combinations per subsequence is nonpositive");

         // verifying whether it is possible to have maxcomb combinations with specified sublength for bit sequences
         long max = (1L << sublength) - 1L;
         if (max < maxcomb) throw new Exception("Specified maximal number of combinations in subsequences cannot be attained with specified length");

         // extracting subsequences of bit and verifying that they do not exceed the specified maxcomb value
         nsubs = D.numberOfBits()/sublength;
         subbits = new Data[nsubs];
         for (int k = 0; k < nsubs; k++)
         {
            subbits[k] = new Data(D,k*sublength,(k+1)*sublength);
            if (subbits[k].longValue() > maxcomb)
               throw new Exception("Value of subsequence " + k + " exceeds the specified maximal number of combinations");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // converting to BigInteger and performing compression
      BigInteger MaxComb = BigInteger.valueOf(maxcomb);
      BigInteger result = subbits[0].BigIntegerValue();
      for (int k = 1; k < nsubs; k++)
      {
         result = result.multiply(MaxComb);
         result = result.add(subbits[k].BigIntegerValue());
      }

      return Data.valueOf(result);
   }

   // Uncompresses a Data object previously compressed with the method 'compress'
   // the values of sublength and maxcomb are supposed to be the same
   public static Data uncompress(int n,Data D,int sublength,long maxcomb)
   {
      try
      {
         // initial verifications
         if (n <= 0) throw new Exception("The original size of the Data object cannot be nonpositive");
         if (D == null) throw new Exception("The Data object to be uncompressed is null");
         if (D.numberOfBits () > n) throw new Exception("The original size of the Data object cannot be strictly smaller than its compressed version");
         if (sublength <= 0) throw new Exception("The length of bit subsequences is nonpositive");
         if (sublength > 62) throw new Exception("This implementation of 'uncompress' does not allow the bit subsequences to have more than 62 bits");
         if (sublength > n) throw new Exception("The length of bit sequences cannot exceed the number of bits in compressed Data object");
         if (n%sublength != 0)
            throw new Exception("The length of bit subsequences (" + sublength + 
                                ") needs to be multiple of the original Data object's bit length (" + n + ")");
         if (maxcomb <= 0) throw new Exception("The maximal number of combinations per subsequence is nonpositive");

         // verifying whether it is possible to have maxcomb combinations with specified sublength for bit sequences
         long max = (1L << sublength) - 1L;
         if (max < maxcomb) throw new Exception("Specified maximal number of combinations in subsequences cannot be attained with specified length");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // converting to BigInteger to uncompress
      BigInteger B = D.BigIntegerValue();
      BigInteger MaxComb = BigInteger.valueOf(maxcomb);

      // extracting the first subsequence
      BigInteger carry = B.mod(MaxComb);
      Data result = Data.valueOf(carry);
      if (result.numberOfBits() < sublength)
      {
         int margin = sublength - result.numberOfBits();
         result = new Data(new Data(margin,false),result);
      }
      B = B.divide(MaxComb);

      // extracting the other subsequences; creating uncompressed Data object
      for (int k = 1; k < n/sublength; k++)
      {
         carry = B.mod(MaxComb);
         Data tmp = Data.valueOf(carry);
         if (tmp.numberOfBits() < sublength)
         {
            int margin = sublength - tmp.numberOfBits();
            tmp = new Data(new Data(margin,false),tmp);
         }
         result = new Data(tmp,result);
         B = B.divide(MaxComb);
      }

      return result;
   }

   /* private methods */

   // Checks whether the Data object invariats are satisfied (private method)
   private boolean check_invariants()
   {
      if (this.size == 0)  return false;
      if (this.data == null)  return false;
      if (8*this.data.size() < this.size)  return false;
      int nbits = this.size%8;
      if (nbits != 0)
      {
         int last = this.data.get(this.data.size() - 1);
         last = (last << nbits) & 255;
         if (last != 0)  return false;
      }
      return true;
   }

   // Masks the extra bits that are not necessary in the Data object representation of the bytes
   // (they may cause some issues when the corresponding values are not set to 0; private method)
   private static int mask_bits(int b,int extrabits)
   {
      try
      {
         if (extrabits < 0 || extrabits > 7) throw new Exception("Parameter 'extrabits' cannot be equal to " + extrabits);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      int mask = 255;
      while (extrabits > 0)
      {
         mask = (mask << 1) & 255;
         extrabits--;
      }

      return b & mask;
   }

   /* comparison methods */

   // equals
   @Override
   public boolean equals(Object o)
   {
      boolean isData = (o instanceof Data);
      if (!isData)  return false;

      Data D = (Data) o;
      int l = this.numberOfBits();
      if (l != D.numberOfBits())  return false;

      l = this.data.size();  // comparison byte per byte
      for (int i = 0; i < l; i++)  if (!this.data.get(i).equals(D.data.get(i)))  return false;

      return true;
   }

   // hashCode
   @Override
   public int hashCode()
   {
      return this.data.hashCode();
   }

   // compareTo
   @Override
   public int compareTo(Data D)  // initial version coded by Theo Giraudet (M1 Info, 2020-21)
   {
      try
      {
         if (D == null) throw new Exception("Impossible to compare with null Data object");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      int compare = 0;
      int nD = D.numberOfBits();
      int nthis = this.numberOfBits();
      if (nthis < nD)
      {
         compare = -1;
      }
      else if (nthis > nD)
      {
         compare = 1;
      }
      else
      {
         int i = 0;
         while (i < nD && this.getBit(i) == D.getBit(i))  i++;
         if (i != nD)
         {
            if (this.getBit(i) < D.getBit(i))
               compare = -1;
            else
               compare = 1;
         }
      }

      return compare;
   }         

   // toString
   public String toString()
   {
      int k = 0;
      String print = "[";
      for (int i = 0; i < this.numberOfBytes(); i++)
      {
         if (i != 0)  print = print + "|";
         byte b = (byte) this.data.get(i);
         for (int j = 7; j >= 0; j--)
         {
            if (k < this.size)
            {
               print = print + (b>>j & 1);
               k++;
            }
         }
      }
      return print + "] (" + this.numberOfBits() + ";" + this.numberOfBytes() + ")";
   }

   // main (performing tests)
   public static void main(String[] args)
   {
      System.out.println("Data class\n");
      int NTESTS = 10000;
      int min = 1;
      int max = 100;
      long seed = 0;
      Random R = new Random();
      if (args != null && args.length > 0)
         seed = Long.parseLong(args[0]);
      else
         seed = Math.abs(R.nextLong());
      System.out.println("Random seed set to : " + seed);
      R = new Random(seed);
      ArrayList<Data> forcomparisons = new ArrayList<Data> ();
      double percentage = 100.0/NTESTS;

      // Constructors
      System.out.print("Testing constructors ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         int n = min + R.nextInt(max - min);
         Data another = new Data(n,0.5);

         try
         {
            // public Data(int,boolean)
            boolean bit = R.nextBoolean();
            Data D = new Data(n,bit);
            Exception E = new Exception("Constructor public Data(int,boolean)");
            if (D == null) throw E;
            if (D.numberOfBits() != n) throw E;
            if (D.numberOfBytes() < n/8) throw E;
            if (bit)  if (D.numberOfOnes() != n) throw E;
            if (!bit) if (D.numberOfZeros() != n) throw E;
            if (!D.check_invariants()) throw E;
            if (R.nextDouble() < 0.01)  another = D;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(int,double)
            double p = R.nextDouble();
            Data D = new Data(n,p);
            Exception E = new Exception("Constructor public Data(int)");
            if (D == null) throw E;
            if (D.numberOfBits() != n) throw E;
            if (D.numberOfBytes() < n/8) throw E;
            if (D.numberOfOnes() < Math.floor(p*n)) throw E;
            if (D.numberOfOnes() > Math.floor((p + 0.01)*n)) throw E;
            if (!D.check_invariants()) throw E;
            if (R.nextDouble() < 0.2)  another = D;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(int,Set<Integer>)
            Set<Integer> bits = new TreeSet<Integer> ();
            double p = R.nextDouble();
            for (int i = 0; i < n; i++)  if (R.nextDouble() < p)  bits.add(i);
            Data D = new Data(n,bits);
            Exception E = new Exception("Constructor public Data(int,Set<Integer>)");
            if (D == null) throw E;
            if (D.numberOfBits() != n) throw E;
            if (D.numberOfBytes() < n/8) throw E;
            for (int i = 0; i < n; i++)  if (bits.contains(i) != (D.getBit(i) == 1))  throw E;
            if (!D.check_invariants()) throw E;
            if (R.nextDouble() < 0.2)  another = D;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(Data)
            Data D = new Data(another);
            Exception E = new Exception("Constructor public Data(Data)");
            if (D == null) throw E;
            if (D.numberOfBits() != another.numberOfBits()) throw E;
            if (D.numberOfBytes() != another.numberOfBytes()) throw E;
            for (int i = 0; i < n; i++)  if (D.getBit(i) != another.getBit(i))  throw E;
            if (!D.check_invariants()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(Data,boolean)
            boolean flip = R.nextBoolean();
            Data D = new Data(another,flip);
            Exception E = new Exception("Constructor public Data(Data,boolean)");
            if (D == null) throw E;
            if (D.numberOfBits() != another.numberOfBits()) throw E;
            if (D.numberOfBytes() != another.numberOfBytes()) throw E;
            for (int i = 0; i < n; i++)  if (flip == (D.getBit(i) == another.getBit(i)))  throw E;
            if (!D.check_invariants()) throw E;
            if (flip)  if (R.nextDouble() < 0.3)  another = D;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(String)
            char[] ch = new char [2];
            ch[0] = '0';  ch[1] = '1';
            String bitstring = "";
            for (int i = 0; i < n; i++)  bitstring = bitstring + ch[R.nextInt(2)];
            Data D = new Data(bitstring);
            Exception E = new Exception("Constructor public Data(String)");
            if (D == null) throw E;
            if (D.numberOfBits() != n) throw E;
            if (D.numberOfBytes() < n/8) throw E;
            for (int i = 0; i < n; i++)
            {
               if (ch[D.getBit(i)] != bitstring.charAt(i)) throw E;
            }
            if (!D.check_invariants()) throw E;
            if (R.nextDouble() < 0.2)  another = D;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(Data,Data)
            int n1 = 2 + R.nextInt(8);
            Data D1 = new Data(n1,0.1 + 0.8*R.nextDouble());
            Data D = new Data(D1,another);
            Exception E = new Exception("Constructor public Data(Data,Data)");
            if (D == null) throw E;
            if (D.numberOfBits() != D1.numberOfBits() + another.numberOfBits()) throw E;
            if (8*D.numberOfBytes() - D.numberOfBits() >= 8) throw E;
            int k = 0;
            for (int i = 0; i < D1.numberOfBits(); i++)
            {
               if (D1.getBit(i) != D.getBit(i)) throw E;
               k++;
            }
            for (int j = 0; j < another.numberOfBits(); j++)
            {
               if (another.getBit(j) != D.getBit(k)) throw E;
               k++;
            }
            if (!D.check_invariants()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(Data,int,int)
            Exception E = new Exception("Constructor public Data(Data,int,int)");
            if (n > 2 && n < max/2)
            {
               int nn = 1 + R.nextInt(n/2);
               Data D1 = new Data(another,0,nn);
               Data D2 = new Data(another,nn,n);
               if (D1 == null || D2 == null) throw E;
               if (D1.numberOfBits() + D2.numberOfBits() != another.numberOfBits()) throw E;
               Data D = new Data(D1,D2);
               if (D == null) throw E;
               if (D.numberOfBits() != another.numberOfBits()) throw E;
               if (D.numberOfBytes() != another.numberOfBytes()) throw E;
               for (int i = 0; i < another.numberOfBits(); i++)
               {
                  if (another.getBit(i) != D.getBit(i)) throw E;
               }
               if (!D.check_invariants()) throw E;
            }
            else if (n > 6)
            {
               int n1 = 1 + R.nextInt(n/4);
               int n2 = n1 + 1 + R.nextInt(n/2);
               Data D1 = new Data(another,0,n1);
               Data D2 = new Data(another,n1,n2);
               Data D3 = new Data(another,n2,n);
               if (D1 == null || D2 == null || D3 == null) throw E;
               if (D1.numberOfBits() + D2.numberOfBits() + D3.numberOfBits() != another.numberOfBits()) throw E;
               Data D = new Data(D1,new Data(D2,D3));
               if (D.numberOfBits() != another.numberOfBits()) throw E;
               if (D.numberOfBytes() != another.numberOfBytes()) throw E;
               for (int i = 0; i < another.numberOfBits(); i++)
               {
                  if (another.getBit(i) != D.getBit(i)) throw E;
               }
               if (!D.check_invariants()) throw E;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(Data,Data,String)
            Exception E = new Exception("Constructor public Data(Data,Data,String)");
            Data A = new Data(n,0.3 + 0.4*R.nextDouble());
            Data B = new Data(n,0.3 + 0.4*R.nextDouble());

            // not (A and B) == (not A) or (not B)
            Data D1 = new Data(A,B,"and");  // A and B
            if (D1 == null) throw E;
            Data notA = new Data(A,true);  // not A
            Data notB = new Data(B,true);  // not B
            Data D2 = new Data(notA,notB,"or");  // (not A) or (not B)
            if (D2 == null) throw E;
            D1 = new Data(D1,true);  // not (A and B)
            if (D1.numberOfBits() != D2.numberOfBits()) throw E;
            if (D1.numberOfBytes() != D2.numberOfBytes()) throw E;
            for (int i = 0; i < D1.numberOfBits(); i++)
            {
               if (D1.getBit(i) != D2.getBit(i)) throw E;
            }
            if (!D1.check_invariants()) throw E;
            if (!D2.check_invariants()) throw E;
            if (R.nextDouble() < 0.2)  another = D1;

            // A or (not A) == "true"
            D1 = new Data(A,notA,"or");
            if (D1 == null) throw E;
            if (D1.numberOfBits() != A.numberOfBits()) throw E;
            if (D1.numberOfBytes() != A.numberOfBytes()) throw E;
            for (int i = 0; i < D1.numberOfBits(); i++)
            {
               if (D1.getBit(i) != 1) throw E;
            }
            if (!D1.check_invariants()) throw E;

            // B and not B == "false"
            D2 = new Data(B,notB,"and");
            if (D2 == null) throw E;
            if (D2.numberOfBits() != B.numberOfBits()) throw E;
            if (D2.numberOfBytes() != B.numberOfBytes()) throw E;
            for (int i = 0; i < D2.numberOfBits(); i++)
            {
               if (D2.getBit(i) != 0) throw E;
            }
            if (!D2.check_invariants()) throw E;

            // A xor B == (A and not B) or (not A and B)
            D1 = new Data(A,notB,"and");
            D2 = new Data(B,notA,"and");
            Data X = new Data(D1,D2,"or");
            Data Y = new Data(A,B,"xor");
            if (Y == null) throw E;
            if (X.numberOfBits() != Y.numberOfBits()) throw E;
            if (X.numberOfBytes() != Y.numberOfBytes()) throw E;
            for (int i = 0; i < X.numberOfBits(); i++)
            {
               if (X.getBit(i) != Y.getBit(i)) throw E;
            }
            if (!X.check_invariants()) throw E;
            if (R.nextDouble() < 0.2)  another = X;

            // (A xor B) xor B = A
            X = new Data(Y,B,"xor");
            if (X == null) throw E;
            if (X.numberOfBits() != Y.numberOfBits()) throw E;
            if (X.numberOfBytes() != Y.numberOfBytes()) throw E;
            for (int i = 0; i < X.numberOfBits(); i++)
            {
               if (X.getBit(i) != A.getBit(i)) throw E;
            }
            if (!X.check_invariants()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public Data(int,byte[])
            int nn = 0;
            if (another.numberOfBits() > 2)
               nn = 1 + R.nextInt(another.numberOfBits() - 1);
            else
               nn = another.numberOfBits();
            another = new Data(another,0,nn);
            byte[] byteArray = another.toByteArray();
            Data D = new Data(nn,byteArray);
            Exception E = new Exception("public Data(int,byte[])");
            if (D == null) throw E;
            if (D.numberOfBits() != another.numberOfBits()) throw E;
            if (D.numberOfBytes() != another.numberOfBytes()) throw E;
            if (new Data(D,another,"xor").numberOfOnes() > 0) throw E;
            if (!D.check_invariants()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         forcomparisons.add(another);
      }
      System.out.println("OK");

      // methods to get and flip bits
      System.out.print("Testing private methods get and flip bits ... ");
      try
      {
         for (int itest = 0; itest < NTESTS; itest++)
         {
            int n = min + R.nextInt(max - min);
            boolean [] b = new boolean[n];
            for (int i = 0; i < n; i++)  b[i] = R.nextBoolean();
            Exception E = new Exception("Private methods getBit and flipBit");
            Data D = new Data(n,0.4 + 0.2*R.nextDouble());
            Data clone = new Data(D);
            for (int i = 0; i < n; i++)  if (b[i])  clone.flipBit(i);
            for (int i = 0; i < n; i++)
            {
               if (b[i])
               {
                  if (D.getBit(i) == clone.getBit(i)) throw E;
               }
               else
               {
                  if (D.getBit(i) != clone.getBit(i)) throw E;
               }
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      System.out.println("OK");

      // methods to count bits
      System.out.print("Testing methods to count bits ... ");
      try
      {
         for (int itest = 0; itest < NTESTS; itest++)
         {
            int n = min + R.nextInt(max - min);
            Set<Integer> bits = new TreeSet<Integer> ();
            for (int k = 0; k < R.nextInt(n); k++)  bits.add(R.nextInt(n));
            Data D = new Data(n,bits);
            Exception E = new Exception("Methods to count bits");
            if (D.numberOfBits() != n) throw E;
            if (D.numberOfBits() != D.numberOfBits(false) + D.numberOfBits(true)) throw E;
            if (D.numberOfBits() != D.numberOfZeros() + D.numberOfOnes()) throw E;
            if (D.numberOfOnes() != D.numberOfBits(true)) throw E;
            if (D.numberOfZeros() != D.numberOfBits(false)) throw E;
            if (D.numberOfOnes() != bits.size()) throw E;
            if (D.numberOfZeros() != n - bits.size()) throw E;
            Data F = new Data(n,D.toByteArray());
            if (D.numberOfBits() != F.numberOfBits()) throw E;
            if (D.numberOfBits() != F.numberOfZeros() + F.numberOfOnes()) throw E;
            if (D.numberOfOnes() != F.numberOfOnes()) throw E;
            if (D.numberOfZeros() != F.numberOfZeros()) throw E;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      System.out.println("OK");

      // bitIterator
      System.out.print("Testing bitIterator ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         Exception E = new Exception("internal class bitIterator");
         try
         {
            // iterating on bits and Data objects with next (or previous) bit flipped
            int n = min + R.nextInt(max - min);
            Data D = new Data(n,0.4 + 0.2*R.nextDouble());
            bitIterator It = D.iterator();
            if (!It.hasNext()) throw E;
            if (It.hasPrevious()) throw E;
            Integer bit = It.next();
            if (bit != D.getBit(0)) throw E;
            int i = 1;
            while (It.hasNext())
            {
               if (It.next() != D.getBit(i)) throw E;
               i++;
            }
            i--;
            while (It.hasPrevious())
            {
               i--;
               if (It.previous() != D.getBit(i)) throw E;
            }
            It.setToEnd();
            if (It.hasNext()) throw E;
            if (!It.hasPrevious()) throw E;
            It.reset();
            if (!It.hasNext()) throw E;
            if (It.hasPrevious()) throw E;
            if (It.next() != bit) throw E;
            It.reset();
            Data bitFlipped = It.withNextBitFlipped();
            if (bitFlipped == null) throw E;
            if (!bitFlipped.check_invariants()) throw E;
            if (D.getBit(0) == bitFlipped.getBit(0)) throw E;
            if (new Data(D,bitFlipped,"xor").numberOfOnes() > 1) throw E;
            i = 1;
            while (It.hasNext())
            {
               Data tmp = It.withNextBitFlipped();
               if (tmp == null) throw E;
               if (!tmp.check_invariants()) throw E;
               if (D.getBit(i) == tmp.getBit(i)) throw E;
               if (new Data(D,tmp,"xor").numberOfOnes() > 1) throw E;
               if (R.nextDouble() < 2.0*percentage)  forcomparisons.add(tmp);
               i++;
            }
            i--;
            while (It.hasPrevious())
            {
               i--;
               Data tmp = It.withPreviousBitFlipped();
               if (tmp == null) throw E;
               if (!tmp.check_invariants()) throw E;
               if (D.getBit(i) == tmp.getBit(i)) throw E;
               if (new Data(D,tmp,"xor").numberOfOnes() > 1) throw E;
            }
            if (n > 1 && !It.hasNext()) throw E;
            It.reset();
            if (new Data(bitFlipped,It.withNextBitFlipped(),"xor").numberOfZeros() != D.numberOfBits()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // iterating on spiral Data objects generated from the current Data object and with center at current bit
            int n = min + R.nextInt(max - min);
            Data D = new Data(n,0.4 + 0.2*R.nextDouble());
            bitIterator It = D.iterator();
            int k = R.nextInt(n);
            for (int i = 0; i < k; i++)  It.next();
            if (k != 0 && k != n - 1)
            {
               int m = k;
               if (n - k - 1 < m)  m = n - k - 1;
               int h = R.nextInt(m);
               if (h > 0)
               {
                  Data pF = null;
                  Data F = null;
                  Data df = null;
                  for (int i = 0; i < 2*h; i++)
                  {
                     F = It.nextOnSpiral();
                     if (!F.check_invariants()) throw E;
                     if (pF != null)
                     {
                        df = Data.diff(F,pF);
                        if (df.numberOfOnes() != 1) throw E;
                     }
                     pF = new Data(F);
                  }
                  df = Data.diff(D,F);
                  if (df.numberOfOnes() != 2*h) throw E;
               }
               for (int i = 2*h; i < 2*m + 3; i++)  if (It.hasNextOnSpiral())  It.nextOnSpiral();
               if (It.hasNextOnSpiral()) throw E;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // static methods acting like constructors of more complex Data objects
      System.out.print("Testing static methods (extra constructors) ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         // collecting Data objects to test "concat" at the end of each iteration
         List<Data> lD = new ArrayList<Data> ();

         try
         {
            // public static Data diff(Data,Data)
            int n = min + R.nextInt(max - min);
            Data D1 = new Data(n,0.7);
            Data D2 = new Data(n,0.3);
            Exception E = new Exception("public static Data diff(Data,Data)");
            Data dD = Data.diff(D1,D2);
            if (dD == null) throw E;
            if (!dD.check_invariants()) throw E;
            if (dD.numberOfBits() != n) throw E;
            if (D1.hammingDistanceTo(D2) != dD.numberOfOnes()) throw E;

            // preparing for "concat"
            if (R.nextDouble() < 0.5)
              lD.add(D1);
            else
              lD.add(D2);
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data control(Data,boolean,int)
            int n = min + R.nextInt(max - min);
            Data D = new Data(n,0.6);
            Exception E = new Exception("public static Data control(Data,boolean,int)");
            boolean bitValue = R.nextBoolean();
            int cnbits = D.numberOfBits(bitValue);
            if (cnbits > 0)
            {
               int nbits = R.nextInt(cnbits);
               Data C = Data.control(D,bitValue,nbits);
               if (C == null) throw E;
               if (!C.check_invariants()) throw E;
               if (C.numberOfBits() != D.numberOfBits()) throw E;
               if (C.numberOfBytes() != D.numberOfBytes()) throw E;
               if (C.numberOfBits(bitValue) != nbits) throw E;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data control(Data,boolean,double)
            int n = min + R.nextInt(max - min);
            Data D = new Data(n,0.8);
            Exception E = new Exception("public static Data control(Data,boolean,double)");
            double p = 0.2 + 0.6*R.nextDouble();
            Data C1 = Data.control(D,true,p);
            if (C1 == null) throw E;
            if (!C1.check_invariants()) throw E;
            if (C1.numberOfBits() != D.numberOfBits()) throw E;
            if (C1.numberOfBytes() != D.numberOfBytes()) throw E;
            if (C1.numberOfOnes() > D.numberOfOnes()) throw E;
            int pp = (int) p*n;
            if (pp > 0)  if (D.numberOfOnes() - C1.numberOfOnes() > 2*pp) throw E;
            D = new Data(D,true);
            Data C2 = Data.control(D,false,p);
            if (C2 == null) throw E;
            if (!C2.check_invariants()) throw E;
            if (C2.numberOfBits() != D.numberOfBits()) throw E;
            if (C2.numberOfBytes() != D.numberOfBytes()) throw E;
            if (C2.numberOfZeros() > D.numberOfZeros()) throw E;
            if (C1.numberOfOnes() != C2.numberOfZeros()) throw E;
            if (C1.numberOfZeros() != C2.numberOfOnes()) throw E;
            if (pp > 0)  if (D.numberOfZeros() - C2.numberOfZeros() > 2*pp) throw E;

            // preparing for "concat"
            if (R.nextDouble() < 0.5)
               lD.add(C1);
            else
               lD.add(C2);
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data shift(Data,int)
            Data D = lD.get(R.nextInt(2));
            if (D.numberOfBits() < 2)  D = new Data(D,D);
            int n = D.numberOfBits();
            int nplaces = n - 1 - 2*R.nextInt(n-1);
            Exception E = new Exception("public static Data shift(Data,int)");
            Data S = Data.shift(D,nplaces);
            if (S == null) throw E;
            if (!S.check_invariants()) throw E;
            if (S.numberOfBits() != D.numberOfBits()) throw E;
            if (S.numberOfBytes() != D.numberOfBytes()) throw E;
            if (nplaces > 0)
            {
               Data C1 = new Data(D,0,n-nplaces);
               Data C2 = new Data(S,nplaces,n);
               if (Data.diff(C1,C2).numberOfOnes() != 0) throw E;
               if (S.numberOfOnes() != C2.numberOfOnes()) throw E;
            }
            else if (nplaces < 0)
            {
               Data C1 = new Data(D,-nplaces,n);
               Data C2 = new Data(S,0,n+nplaces);
               if (Data.diff(C1,C2).numberOfOnes() != 0) throw E;
               if (S.numberOfOnes() != C2.numberOfOnes()) throw E;
            }
            else
            {
               if (S.numberOfZeros() != D.numberOfZeros()) throw E;
               if (S.numberOfOnes() != D.numberOfOnes()) throw E;
               if (Data.diff(D,S).numberOfOnes() != 0) throw E;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data shuffle(Data)
            int n = 2 + R.nextInt(max - min);
            Data D = new Data(n,0.1 + 0.8*R.nextDouble());
            Exception E = new Exception("public static Data shuffle(Data)");
            Data S = Data.shuffle(D);
            if (S == null) throw E;
            if (!S.check_invariants()) throw E;
            if (S.numberOfBits() != D.numberOfBits()) throw E;
            if (S.numberOfBytes() != D.numberOfBytes()) throw E;
            if (S.numberOfZeros() != D.numberOfZeros()) throw E;
            if (S.numberOfOnes() != D.numberOfOnes()) throw E;
            if (S.numberOfZeros() != S.numberOfBits() && S.numberOfOnes() != S.numberOfBits())
               if (Data.diff(D,S).numberOfOnes() == 0) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data concat(List<Data>)
            int tn = 0;
            int m = lD.size() + R.nextInt(10);
            int j = 0;
            for (; j < lD.size(); j++)
            {
                tn = tn + lD.get(j).numberOfBits();
            }
            for (; j < m; j++)
            {
               int r = R.nextInt(5);
               int nn = min + R.nextInt(max - min);
               Data tmp = null;
               if (r == 0)
               {
                  tmp = new Data(nn,R.nextBoolean());
               }
               else if (r == 1)
               {
                  tmp = new Data(nn,0.2 + 0.6*R.nextDouble());
               }
               else if (r == 2)
               {
                  tmp = new Data(new Data(nn,0.5),true);
               }
               else if (r == 3)
               {
                  String str = "";
                  for (int k = 0; k < nn; k++)  str = str + R.nextInt(2);
                  tmp = new Data(str);
               }
               else if (r == 4)
               {
                  int f = R.nextInt(nn);
                  tmp = new Data(new Data(2*nn,0.5),f,f+nn);
               }
               lD.add(tmp);
               tn = tn + nn;
            }
            Data D = Data.concat(lD);
            Exception E = new Exception("public static Data concat(List<Data>)");
            if (D == null) throw E;
            if (!D.check_invariants()) throw E;
            if (tn != D.numberOfBits()) throw E;
            int len = 0;
            for (j = 0; j < m; j++)
            {
               Data ref = lD.get(j);
               int nn = ref.numberOfBits();
               Data tmp = new Data(D,len,len+nn);
               if (nn != tmp.numberOfBits()) throw E;
               if (Data.diff(ref,tmp).numberOfOnes() > 0) throw E;
               len = len + nn;
            }
            if (R.nextDouble() < percentage)  forcomparisons.add(D);
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data crossover(List<Data>)
            int n = 2 + min + R.nextInt(max - min - 2);
            int m = 2 + R.nextInt(n - 2);
            List<Data> list = new ArrayList<Data> (m);
            while (list.size() < m)  list.add(new Data(n,R.nextDouble()));
            Data cross = Data.crossover(list);
            List<Data> test = new ArrayList<Data> (m);
            for (Data D : list)  test.add(Data.diff(D,cross));
            Data result = test.get(0);
            for (int i = 1; i < m; i++)  result = new Data(result,test.get(i),"and");
            if (result.numberOfZeros() != n) throw new Exception("public static Data crossover(List<Data>)");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data attract(Data,Data,double)
            int n = min + R.nextInt(max - min);
            Data Attractor = new Data(n,0.5);
            Data D = new Data(n,0.1 + 0.8*R.nextDouble());
            double speed = 0.0;
            if (R.nextInt(9) != 0)
            {
               speed = 1.0;
               if (R.nextInt(9) != 0)  speed = R.nextDouble();
            } 
            Data S = Data.attract(D,Attractor,speed);
            Exception E = new Exception("public static Data attract(Data,Data,double)");
            if (S == null) throw E;
            if (!S.check_invariants()) throw E;
            if (S.numberOfBits() != D.numberOfBits() && S.numberOfBits() != Attractor.numberOfBits()) throw E;
            if (S.numberOfBytes() != D.numberOfBytes() && S.numberOfBytes() != Attractor.numberOfBytes()) throw E;
            if (speed == 0.0)
            {
               if (!S.equals(D)) throw E;
            }
            else if (speed == 1.0)
            {
               if (!S.equals(Attractor)) throw E;
            }
            else
            {
               if (Data.diff(D,Attractor).numberOfOnes() < Data.diff(S,Attractor).numberOfOnes()) throw E;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data repel(Data,Data,double)
            int n = min + R.nextInt(max - min);
            Data Repellent = new Data(n,0.4 + 0.2*R.nextDouble());
            Data D = new Data(n,0.2 + 0.6*R.nextDouble());
            double speed = 0.0;
            if (R.nextInt(11) != 0)
            {
               speed = 1.0;
               if (R.nextInt(8) != 0)  speed = R.nextDouble();
            }
            Data S = Data.repel(D,Repellent,speed);
            Exception E = new Exception("public static Data repel(Data,Data,double)");
            if (S == null) throw E;
            if (!S.check_invariants()) throw E;
            if (S.numberOfBits() != D.numberOfBits() && S.numberOfBits() != Repellent.numberOfBits()) throw E;
            if (S.numberOfBytes() != D.numberOfBytes() && S.numberOfBytes() != Repellent.numberOfBytes()) throw E;
            if (speed == 0.0)
            {
               if (!S.equals(D)) throw E;
            }
            else if (speed == 1.0)
            {
               if (!S.equals(new Data(Repellent,true))) throw E;
            }
            else
            {
               if (Data.diff(D,Repellent).numberOfOnes() > Data.diff(S,Repellent).numberOfOnes()) throw E;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");
 
      // Hamming-distance based methods
      System.out.print("Testing Hamming-distance based methods ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         // public int hammingDistanceTo(Data)
         try
         {
            int n = min + R.nextInt(max - min);
            Data D1 = new Data(n,0.2 + 0.4*R.nextDouble());
            Data D2 = new Data(n,0.4 + 0.4*R.nextDouble());
            Exception E = new Exception("public int hammingDistanceTo(Data)");
            Data diff = new Data(D1,D2,"xor");
            if (D1.hammingDistanceTo(D2) != D2.hammingDistanceTo(D1)) throw E;
            if (diff.numberOfOnes() != D1.hammingDistanceTo(D2)) throw E;
            if (diff.numberOfOnes() != D2.hammingDistanceTo(D1)) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // public boolean belongsToNeighbourOf(Data,int)
         try
         {
            int n = min + R.nextInt(max - min);
            Data D1 = new Data(n,0.3 + 0.3*R.nextDouble());
            Data D2 = new Data(n,0.5 + 0.3*R.nextDouble());
            Exception E = new Exception("public boolean belongsToNeighbourOf(Data,int)");
            int h = R.nextInt(n-min+1);
            if (D1.belongsToNeighbourOf(D2,h) && !D2.belongsToNeighbourOf(D1,h)) throw E;
            Data diff = new Data(D1,D2,"xor");
            h = diff.numberOfOnes();
            if (!D1.belongsToNeighbourOf(D2,h)) throw E;
            if (h > 1)
            {
               h = h - 1;
               if (D1.belongsToNeighbourOf(D2,h)) throw E;
            }
            if (h < n - min - 1)
            {
               h = h + 1;
               if (!D1.belongsToNeighbourOf(D2,h)) throw E;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // public Data randomSelectInNeighbourhood(?,int)
         try
         {
            int n = min + R.nextInt(max - min);
            int l = 1 + n/10;
            int u = 1 + 3*(l - 1);
            Data D = new Data(n,0.5);
            Exception E = new Exception("public Data randomSelectInNeighbourhood(?,int)");
            Data F = D.randomSelectInNeighbourhood(l,u);
            if (F == null) throw E;
            if (!F.check_invariants()) throw E;
            if (D.hammingDistanceTo(F) < l) throw E;
            if (F.hammingDistanceTo(D) > u) throw E;
            u = 1 + 5*(l - 1);
            F = D.randomSelectInNeighbourhood(u);
            if (F.hammingDistanceTo(D) > u) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // public Data randomSelectInNeighbourhood(?,int,int,Set)
         try
         {
            int n = max + R.nextInt(max);
            int m = 2 + R.nextInt(10);
            while (n%m != 0)  n++;
            int l = 1 + m/10;
            int u = 1 + 3*(l - 1);
            int nseqs = n/m;  // n%m == 0
            Set<Integer> S = new TreeSet<Integer> ();
            S.add(R.nextInt(nseqs));
            while (S.size() < 1)  S.add(R.nextInt(nseqs));
            if (m > 2 && R.nextDouble() < 0.5)  while(S.size() < 2)  S.add(R.nextInt(nseqs));
            Data D = new Data(n,0.5);
            Exception E = new Exception("public Data randomSelectInNeighbourhood(?,int,int,Set)");
            Data F = D.randomSelectInNeighbourhood(l,u,nseqs,S);
            if (F == null) throw E;
            if (!F.check_invariants()) throw E;
            if (D.numberOfBits() != F.numberOfBits()) throw E;
            if (D.hammingDistanceTo(F) < l) throw E;
            if (F.hammingDistanceTo(D) > u) throw E;
            ArrayList<Data> lD = new ArrayList<Data> ();
            for (Integer s : S)  lD.add(new Data(D,s*m,(s+1)*m));
            Data DD = Data.concat(lD);
            ArrayList<Data> lF = new ArrayList<Data> ();
            for (Integer s : S)  lF.add(new Data(F,s*m,(s+1)*m));
            Data FF = Data.concat(lF);
            if (DD.numberOfBits() != FF.numberOfBits()) throw E;
            if (DD.hammingDistanceTo(FF) < l) throw E;
            if (FF.hammingDistanceTo(DD) > u) throw E;
            if (D.hammingDistanceTo(F) != DD.hammingDistanceTo(FF)) throw E;
            if (R.nextDouble() < 2.0*percentage)  forcomparisons.add(FF);
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         try
         {
            // public static Data randomSelectAtDistanceFrom(int,Data[])
            int n = min + R.nextInt(max - min);
            int m = min + R.nextInt(n);
            Data df = new Data(n,false);
            Data[] array = new Data[m];
            for (int i = 0; i < m; i++)
            {
               array[i] = new Data(n,0.2 + 0.4*R.nextDouble());
               df = new Data(df,array[i],"or");
            }
            int k = df.numberOfZeros();
            if (k > 0)
            {
               int h = 1;
               if (k > 1)  h = 1 + R.nextInt(k-1);
               Data F = Data.randomSelectAtDistanceFrom(h,array);
               Exception E = new Exception("public static Data randomSelectAtDistanceFrom(int,Data[])");
               if (F == null) throw E;
               if (!F.check_invariants()) throw E;
               if (F.numberOfBits() != n) throw E;
               for (int i = 0; i < m; i++)  if (F.hammingDistanceTo(array[i]) < h) throw E;
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // Data conversions
      System.out.print("Testing methods for Data conversions ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         // boolean
         try
         {
            int n = min + R.nextInt(max - min);
            boolean b = R.nextBoolean();
            Data D = Data.valueOf(b,n);
            Exception E = new Exception("Exception raised when converting from or to 'boolean'");
            if (D == null) throw E;
            if (D.numberOfBits() != n) throw E;
            if (D.numberOfOnes() > 1) throw E;
            if (D.numberOfBits() > 1)  if (new Data(D,0,D.numberOfBits()-1).numberOfOnes() != 0) throw E;
            if (D.booleanValue() != b) throw E;
            if (!D.check_invariants()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // int
         try
         {
            int integer = Integer.MAX_VALUE;
            while (Math.abs(integer) == Integer.MAX_VALUE)  integer = R.nextInt();
            Data D = Data.valueOf(integer);
            Exception E = new Exception("Exception raised when converting from or to 'int'");
            if (D == null) throw E;
            if (D.numberOfBits() != 31) throw E;
            if (Math.abs(integer) != D.intValue()) throw E;
            if (Math.abs(integer) + 1 != D.posIntValue()) throw E;
            if (!D.check_invariants()) throw E;
            int n = min + R.nextInt(31 - min);
            D = new Data(n,0.5);
            integer = D.intValue();
            D = Data.valueOf(integer,n);
            if (D == null) throw E;
            if (D.intValue() != integer) throw E;
            if (D.posIntValue() != integer + 1) throw E;
            if (!D.check_invariants()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // long
         try
         {
            long longint = Long.MAX_VALUE;
            while (Math.abs(longint) == Long.MAX_VALUE)  longint = R.nextLong();
            Data D = Data.valueOf(longint);
            Exception E = new Exception("Exception raised when converting from or to 'long'");
            if (D == null) throw E; 
            if (D.numberOfBits() != 63) throw E;
            if (Math.abs(longint) != D.longValue()) throw E;
            if (Math.abs(longint) + 1 != D.posLongValue()) throw E;
            if (!D.check_invariants()) throw E;
            int n = min + R.nextInt(63 - min);
            D = new Data(n,0.5);
            longint = D.longValue();
            D = Data.valueOf(longint,n);
            if (D == null) throw E;
            if (D.longValue() != longint) throw E;
            if (D.posLongValue() != longint + 1) throw E;
            if (!D.check_invariants()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // BigInteger
         try
         {
            int n = min + R.nextInt(max - min);
            BigInteger B = new BigInteger(n,R);
            n = B.bitLength();
            if (n > 0)
            {
               Data D = Data.valueOf(B);
               Exception E = new Exception("Exception raised when converting from or to 'BitInteger'");
               if (D == null) throw E;
               if (D.numberOfBits() != n) throw E;
               if (!D.check_invariants()) throw E;
               if (!B.equals(D.BigIntegerValue())) throw E;
               if (!B.add(BigInteger.ONE).equals(D.posBigIntegerValue())) throw E;
               if (R.nextDouble() < percentage)  forcomparisons.add(D);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // float
         try
         {
            String s = "";
            int n = min + R.nextInt(28 - min);
            float f = R.nextFloat();
            float g = f;
            float eps = 1.0f;
            for (int i = 0; i < n; i++)
            {
               g = 2.0f*g;
               if (g < 1.0)
               {
                  s = s + "0";
               }
               else
               {
                  s = s + "1";
                  g = g - 1.0f;
               }
               eps = 0.5f*eps;
            }
            Data D = new Data(s);
            g = D.floatValue();
            if (Math.abs(g - f) > eps) throw new Exception("public float floatValue()");
            g = D.floatValueNormalized();
            if (g < 0.0f || g > 1.0f) throw new Exception("public float floatValueNormalized()");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // double
         try
         {
            String s = "";
            int n = min + R.nextInt(60 - min);
            double d = R.nextDouble();
            double g = d;
            double eps = 1.0; 
            for (int i = 0; i < n; i++)
            {
               g = 2.0*g;
               if (g < 1.0)
               {
                  s = s + "0";
               }
               else
               {
                  s = s + "1";
                  g = g - 1.0;
               }
               eps = 0.5*eps;
            }
            Data D = new Data(s);
            g = D.doubleValue(); 
            if (Math.abs(g - d) > eps) throw new Exception("public double doubleValue()");
            g = D.doubleValueNormalized();
            if (g < 0.0 || g > 1.0) throw new Exception("public double doubleValueNormalized()");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }

         // char
         try
         {
            char c = (char) R.nextInt(255);
            Data D = Data.valueOf(c);
            Exception E = new Exception("Exception raised when converting from or to 'char'");
            if (D == null) throw E;
            if (D.numberOfBits() != 8) throw E;
            if (c != D.charValue()) throw E;
            if (!D.check_invariants()) throw E;
            if (R.nextBoolean())
               c = (char) ('a' + R.nextInt(26));
            else
               c = (char) ('A' + R.nextInt(26));
            D = Data.valueOf(c,true);
            if (D == null) throw E;
            if (D.numberOfBits() != 5) throw E;
            if (Character.toUpperCase(c) != D.letterValue()) throw E;
            if (!D.check_invariants()) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // Data compression
      System.out.print("Testing methods for Data compression ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         // compress and uncompress
         try
         {
            int n = 10 + R.nextInt(max - 10);
            int length = n;
            while (n%length == 0 || length == n || length == 1 || length > 30)  length = 1 + R.nextInt(n - 1);
            int nsubs = n/length;
            int maxcomb = (1 << length) - 1;
            int actualmaxcomb = 1 + R.nextInt(maxcomb);
            int value = R.nextInt(actualmaxcomb);
            Data original = Data.valueOf(value,length);
            for (int k = 1; k < nsubs; k++)
            {
               value = R.nextInt(maxcomb);
               original = new Data(original,Data.valueOf(value,length));
            }
            Data compressed = Data.compress(original,length,(long) maxcomb);
            Exception E = new Exception("Exception raised when compressing and uncompressing Data objects");
            if (compressed == null) throw E;
            if (!compressed.check_invariants()) throw E;
            if (compressed.numberOfBits() > original.numberOfBits()) throw E;
            if (R.nextDouble() < percentage)  forcomparisons.add(compressed);
            Data uncompressed = Data.uncompress(original.numberOfBits(),compressed,length,(long) maxcomb);
            if (uncompressed == null) throw E;
            if (!uncompressed.check_invariants()) throw E;
            if (uncompressed.numberOfBits() < compressed.numberOfBits()) throw E;
            if (uncompressed.numberOfBits() != original.numberOfBits()) throw E;
            if (new Data(uncompressed,original,"xor").numberOfOnes() > 0) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // Comparison methods and toString
      System.out.print("Testing comparison methods (and toString) ... ");

      // toString
      try
      {
         for (Data D : forcomparisons)
         {
            String str = D.toString();
            String data = "";
            for (int i = 0; i < str.length() && str.charAt(i) != ']'; i++)
            {
               if (str.charAt(i) == '0' || str.charAt(i) == '1')  data = data + str.charAt(i);
            }
            Data F = new Data(data);
            Exception E = new Exception("public String toString");
            if (F.numberOfBits() != D.numberOfBits()) throw E;
            if (F.numberOfBytes() != F.numberOfBytes()) throw E;
            if (Data.diff(D,F).numberOfOnes() > 0) throw E;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // equals and compareTo
      try
      {
         int n = forcomparisons.get(R.nextInt(forcomparisons.size())).numberOfBits();
         forcomparisons.add(new Data(n,0.5));
         forcomparisons.add(new Data(forcomparisons.get(R.nextInt(forcomparisons.size()))));
         Exception E1 = new Exception("public boolean equals");
         Exception E2 = new Exception("public int compareTo");
         Data D1 = forcomparisons.get(R.nextInt(forcomparisons.size()));
         int n1 = D1.numberOfBits();
         for (Data D2 : forcomparisons)
         {
            int n2 = D2.numberOfBits();
            if (n1 == n2)
            {
               Data dD = Data.diff(D1,D2);
               boolean c1 = (dD.numberOfOnes() == 0);
               boolean c2 = D1.equals(D2);
               boolean c3 = D2.equals(D1);
               int c4 = D1.compareTo(D2);
               if (c1 != c2 || c1 != c3) throw E1;
               if (c1) if (c4 != 0) throw E2;
            }
            else
            {
               int c1 = D1.compareTo(D2);
               int c2 = D2.compareTo(D1);
               if (c1*c2 > 0) throw E2;
               if (n1 < n2)  if (c1 > -1) throw E2;
               if (n1 > n2)  if (c1 <  1) throw E2;
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      System.out.println("OK");

      // ending
      System.out.println("Control long : " + Math.abs(R.nextLong()));
      System.out.println();
   }
}

