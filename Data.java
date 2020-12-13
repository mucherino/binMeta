
/* Data class
 *
 * binMeta project
 *
 * last update: Dec 13, 2020
 *
 * AM
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Data implements Comparable<Data>
{
   protected int size;  // actual size (may not be a multiple of 8)
   protected ArrayList<Byte> data;  // the data are stored as lists of bytes
   protected int current;  // the current bit address

   // Data constructor: it generates a new Data object with its n bits set to 0 or 1,
   //                   depending on value of the boolean argument (0=false, 1=true)
   //                   the bit pointer is set to 0
   public Data(int n,boolean bitValue)
   {
      try
      {
         if (n <= 0) throw new Exception("Specified size for random Data object is zero or even negative");
         int N = 1 + (n - 1)/8;
         this.data = new ArrayList<Byte> (N);

         int bi = 0;  if (bitValue)  bi = ~bi;
         byte b = (byte) bi;
         for (int i = 0; i < N; i++)  this.data.add(b);
         this.size = n;
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a random Data object consisting of n bits, 
   //                   and with probability p in [0,1] to have bits equal to 1
   //                   the bit pointer is set to 0
   public Data(int n,double p)
   {
      try
      {
         if (n <= 0) throw new Exception("Specified size for random Data object is zero or even negative");
         if (p < 0.0 || p > 1.0) throw new Exception("Specified probability of 1's in random Data object should be contained in [0,1]");
         int N = 1 + (n - 1)/8;

         int k = 0;
         Random R = new Random();
         this.data = new ArrayList<Byte> (N);
         for (int i = 0; i < N; i++)
         {
            int bi = 0;
            for (int j = 0; j < 8; j++)
            {
               if (k < n && R.nextDouble() < p)  bi = bi + 1;
               if (j != 7)  bi = bi << 1;
               k++;
            }
            byte b = (byte) bi;
            this.data.add(b);
         }
         this.size = n;
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object by concatenating two Data objects
   //                   the bit pointer is set to 0
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

         // copying first Data object
         int k = 0;
         for (; k < D1.numberOfBytes() - 1; k++)
         {
            this.data.add(D1.data.get(k));
         }

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
         if (k < N - 1)
         {
            b = (byte) bi;
            this.data.add(b);
         }
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object containing the same bit sequence of another Data object
   //                   the bit pointer is set to 0
   public Data(Data D)
   {
      try
      {
         if (D == null) throw new Exception("Input Data object is null");
         this.size = D.numberOfBits();
         this.data = new ArrayList<Byte> (D.numberOfBytes());
         for (byte b : D.data)  this.data.add(b);
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object consisting of an existing Data object where
   //                   one of the two following operations is applied:
   //                   1. flipping all bits : when the int argument h is 0
   //                   2. shuffle its bits : when the int argument h is an even number in [2,numberOfBits()]
   //                   --> h indicates the Hamming distance between D and the new constructed Data object
   //                   the bit pointer is set to 0
   public Data(Data D,int h)
   {
      try
      {
         if (D == null) throw new Exception("Input Data object is null");
         if (h < 0) throw new Exception("The parameter h can only be 0 or positive");
         int nb = D.numberOfBits();
         if (h != 0)
         {
            if (h%2 != 0) throw new Exception("The specified Hamming distance must be an even number");
            if (h >= nb) throw new Exception("The specified Hamming distance is larger than the total number of bits");
         }
         this.size = nb;
         this.data = new ArrayList<Byte> (D.numberOfBytes());
         for (byte b : D.data)
         {
            if (h == 0)  // flipping
            {
               int bi = b;
               b = (byte) ~bi;
            }
            this.data.add(b);
         }
         if (h != 0)  // shuffling
         {  
            Random R = new Random();
            boolean [] select = new boolean[nb];
            for (int k = 0; k < nb; k++)  select[k] = false;
            while (h > 0)
            {
               int i = R.nextInt(nb);
               int j = R.nextInt(nb);
               if (this.getBit(i) != this.getBit(j))
               {
                  this.flipBit(i);
                  this.flipBit(j);
                  h = h - 2;
               }
            }
         }
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object by extracting a specific data word from an existing Data object
   //                   the bit pointer is set to 0
   public Data(Data D,int first,int last)
   {
      try
      {
         if (D == null) throw new Exception("Input Data object is null");
         if (first < 0) throw new Exception("Specified first bit index is negative");
         if (first >= D.numberOfBits()) throw new Exception("Specified first bit index is out of range");
         if (last < 0) throw new Exception("Specified last bit index is negative");
         if (last >= D.numberOfBits()) throw new Exception("Specified last bit index is out of range");
         if (last < first) throw new Exception("Specified first bit index is larger than specified last bit index");

         this.size = last - first + 1;
         int N = 1 + (this.size - 1)/8;
         this.data = new ArrayList<Byte> (N);
         int pointer = first;
         for (int i = 0; i < N; i++)
         {
            int bi = 0;
            for (int j = 0; j < 8; j++)
            {
               if (pointer <= last)
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
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object by combining two Data objects having the same size with one 
   //                   of the following bitwise operators: and, or, xor.
   //                   the bit pointer is set to 0
   public Data(Data D1,Data D2,String op)
   {
      try
      {
         if (D1 == null) throw new Exception("First Data object is null");
         if (D2 == null) throw new Exception("Second Data object is null");
         if (op == null) throw new Exception("The String supposed to contain the bitwise operation name is null");
         if (D1.numberOfBits() != D2.numberOfBits()) throw new Exception("The two Data objects have different size (in terms of bits)");
         if (!op.equalsIgnoreCase("and") && !op.equalsIgnoreCase("or") && !op.equalsIgnoreCase("xor"))
            throw new Exception("Unknown bitwise operation: \"" + op + "\""); 

         int N = D1.numberOfBytes();
         this.data = new ArrayList<Byte> (N);
         this.size = D1.numberOfBits();
         for (int k = 0; k < N; k++)
         {
            int b = 0;
            int d1 = D1.data.get(k);
            int d2 = D2.data.get(k);
            if (op.equalsIgnoreCase("and"))
               b = d1 & d2;
            else if (op.equalsIgnoreCase("or"))
               b = d1 | d2;
            else
               b = d1 ^ d2;
            this.data.add((byte) b);
         }
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object consisting of the bits forming a given byte
   //                   the bit pointer is set to 0
   public Data(byte b)
   {
      this.size = 8;
      this.data = new ArrayList<Byte> (1);
      this.data.add(b);
      this.current = 0;
   }

   // Data constructor: it generates a new Data object consisting of the bits forming a given integer
   //                   the bit pointer is set to 0
   public Data(int i)
   {
      this.size = 32;
      this.data = new ArrayList<Byte> (4);
      byte b = (byte) (i >> 24);
      this.data.add(b);
      b = (byte) (i >> 16);
      this.data.add(b);
      b = (byte) (i >> 8);
      this.data.add(b);
      b = (byte) i;
      this.data.add(b);
      this.current = 0;
   }

   // Data constructor: it generates a new Data object consisting of the bits forming a given long integer
   //                   the bit pointer is set to 0
   public Data(long l)
   {
      this.size = 64;
      this.data = new ArrayList<Byte> (8);
      byte b = (byte) (l >> 56);
      this.data.add(b);
      b = (byte) (l >> 48);
      this.data.add(b);
      b = (byte) (l >> 40);
      this.data.add(b);
      b = (byte) (l >> 32);
      this.data.add(b);
      b = (byte) (l >> 24);
      this.data.add(b);
      b = (byte) (l >> 16);
      this.data.add(b);
      b = (byte) (l >> 8);
      this.data.add(b);
      b = (byte) l;
      this.data.add(b);
      this.current = 0;
   }

   // Data constructor: it generates a new Data object consisting of the bits forming a given char
   //                   only 5 bits are used if the boolean argument specifies that the char is a alphabet letter
   //                   the bit pointer is set to 0
   public Data(char c,boolean isLetter)
   {
      try
      {
         byte b;
         if (isLetter)
         {
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) throw new Exception("The char is not an alphabet letter");
            if (c >= 'a' && c <= 'z')  c = (char) (c - 32);
            int bi = (int) (c - 'A');
            bi = bi << 3;
            b = (byte) bi;
            this.size = 5;
         }
         else
         {
            b = (byte) c;
            this.size = 8;
         }
         this.data = new ArrayList<Byte> (1);
         this.data.add(b);
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object consisting of the bits forming a given char
   //                   the bit pointer is set to 0
   public Data(char c)
   {
      this(c,false);
   }

   // Data constructor: it generates a new Data object consisting of the bits specified in a String object
   //                   the bit pointer is set to 0
   //                   (proposed by Manu Lagadec, M1 Info 2020-21)
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
               throw new Exception("The input String is supposed to contain only 0 and 1 characters");
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

   // Resets the bit pointer to the first bit
   public void reset()
   {
      this.current = 0;
   }

   // Sets the bit pointer to the last bit
   public void setToLast()
   {
      this.current = this.numberOfBits() - 1;
   }

   // Gives the current bit
   public int getCurrentBit()
   {
      return this.getBit(this.current);
   }

   // Is there any next bit?
   public boolean hasNextBit()
   {
      return this.current + 1 < this.numberOfBits();
   }

   // Moves to next bit
   // (moves pointer to the beginning if it does not exist)
   public void moveToNextBit()
   {
      if (this.hasNextBit())
         this.current = this.current + 1;
      else
         this.current = 0;
   }

   // Gives the next bit
   // (moves pointer to the beginning if it does not exist)
   public int getNextBit()
   {
      this.moveToNextBit();
      return this.getCurrentBit();
   }

   // Is there any previous bit?
   public boolean hasPrevBit()
   {
      return this.current > 0;
   }

   // Moves to previous bit
   // (moves pointer to the end if it does not exist)
   public void moveToPrevBit()
   {
      if (this.hasPrevBit())
         this.current = this.current - 1;
      else
         this.current = this.numberOfBits() - 1;
   }

   // Gives the previous bit
   // (moves pointer to the end if it does not exist)
   public int getPrevBit()
   {
      this.moveToPrevBit();
      return this.getCurrentBit();
   }

   // Computes the Hamming distance between this Data object, and the Data object D
   // (the pointers of the two Data objects are not modified)
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
         if (h < 0) throw new Exception("Impossible to verify neighbourhood of Data object: specified hamming distance is negative");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return this.hammingDistanceTo(D) <= h;
   }

   // Selects a random Data object in the neighbourhood with hamming distance [l,u] of this Data object
   public Data randomSelectInNeighbour(int l,int u)
   {
      try
      {
         if (l <= 0) throw new Exception("Impossible to verify neighbourhood of Data object: lower bound on hamming distance is nonpositive");
         if (u < 0)  throw new Exception("Impossible to verify neighbourhood of Data object: upper bound on hamming distance is negative");
         if (l > u)  throw new Exception("Impossible to verify neighbourhood of Data object: upper bound is greater than lower bound on hamming distance");
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

   // Selects a random Data object in the neighbourhood with hamming distance h of this Data object
   public Data randomSelectInNeighbour(int h)
   {
      return this.randomSelectInNeighbour(1,h);
   }

   // Provides a Data object of the same size of the two input arguments where each bit is set to
   // -> 0 if the two corresponding bits in D1 and D2 coincide
   // -> 1 if they differ
   public static Data diff(Data D1,Data D2)
   {
      return new Data(D1,D2,"xor");
   }

   // Generates a new Data object which is basically a copy of the Data object D where the percentage
   // of bits equal to "bitValue" are reduced to the given percentage p (p = 0 would make all these bits
   // disappear, and p = 1 would keep all of them)
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

      // preparing variables
      int bit = 0;
      if (bitValue)  bit = 1;
      int n = D.numberOfZeros();
      if (bitValue)  n = D.numberOfBits() - n;

      // removing (flipping) a percantage of bits corresponding to bitValue
      Random R = new Random();
      Data newD = new Data(D);
      int np = (int) ((1.0 - p)*n);

      while (np > 0)
      {
         int k = R.nextInt(n);
         if (newD.getBit(k) == bit)
         {
            newD.flipBit(k);
            np--;
         }
      }
      return newD;
   }

   // Concatenats a List of Data objects into one where the list order is preserved
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

   // Converts the Data object in Boolean
   public boolean booleanValue()
   {
      try
      {
         boolean onlyonebit = true;
         for (int k = 0; k < this.numberOfBits() - 1 && onlyonebit; k++)
         {
            if (this.getBit(k) != 0)  onlyonebit = false;
         }
         if (!onlyonebit) throw new Exception("Impossible to covert in boolean: the data structure is more complex");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return this.getBit(this.numberOfBits() - 1) == 1;
   }

   // Converts the Data object in nonnegative integer (includes 0)
   public int intValue()
   {
      try
      {
         if (this.numberOfBits() >= 32) throw new Exception("Impossible to convert in integer: too many bits");
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

   // Converts the Data object in positive integer (values > 0)
   public int posIntValue()
   {
      return 1 + this.intValue();
   }

   // Converts the Data object in nonnegative long (includes 0)
   public long longValue()
   {
      try
      {
         if (this.numberOfBits() >= 64) throw new Exception("Impossible to convert in long integer: too many bits");
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
         value = value | this.getBit(k);
      }
      return value;
   }

   // Converts the Data object in positive long (values > 0)
   public long posLongValue()
   {
      return 1L + this.longValue();
   }

   // Converts the Data object in positive float of form 0.m
   public float floatValue()
   {
      int l = this.numberOfBits();
      if (l > 30)  l = 30;
      Data mantissa = new Data(this,0,l-1);
      return (float) mantissa.longValue() / (1 << l);
   }

   // Converts the Data object in positive double of form 0.m
   public double doubleValue()
   {
      int l = this.numberOfBits();
      if (l > 62)  l = 62;
      Data mantissa = new Data(this,0,l-1);
      return (double) mantissa.longValue() / (1L << l);
   }

   // Converts the Data object in ASCII char
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

   // Converts the Data object in capital letter (A->Z, 26 letters, 5 bits)
   public char letterValue()
   {
      try
      {
         if (this.numberOfBits() > 5) throw new Exception("Impossible to convert in capital letter: too many bits (only 5 required)");
         int gap = this.intValue();
         if (gap > 25) throw new Exception("Impossible to convert in capital letter: no letter after 'Z' (bit code is " + gap + ">25)");
         return (char) ('A' + gap);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      return '\0';
   }

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

   @Override
   public int hashCode()
   {
      return this.data.hashCode();
   }

   @Override
   public int compareTo(Data D)
   {
      if (this.equals(D))  return 0;

      int index = 0;
      while (D.data.size() > index && this.data.size() > index && D.data.get(index) == this.data.get(index))  index++;

      if (D.data.size() <= index)
         return 1;
      else if (this.data.size() <= index)
         return -1;
      else
         return Byte.compareUnsigned(this.data.get(index),D.data.get(index)) < 0 ? -1 : 1;
   }

   // toString (does not use the bit pointer)
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

   // main (performing some basic tests)
   public static void main(String[] main)
   {
      System.out.println("Data class\n");
      Random R = new Random();

      System.out.println("Testing constructors");
      Data D01 = new Data(10,false);
      System.out.println("D01 = " + D01);
      Data D02 = new Data(10,true);
      System.out.println("D02 = " + D02);
      Data D03 = new Data(10,0.5);
      System.out.println("D03 = " + D03);
      Data D04 = new Data(10,0.1);
      System.out.println("D04 = " + D04);
      Data D05 = new Data(Byte.parseByte("01010101",2));
      System.out.println("D05 = " + D05);
      int i = 1111;
      Data D06 = new Data(i);
      System.out.println("D06 = " + D06);
      long l = (long) i;
      l = l << 32;
      Data D07 = new Data(l);
      System.out.println("D07 = " + D07);
      Data D08 = new Data('b');
      System.out.println("D08 = " + D08);
      Data D09 = new Data('b',true);
      System.out.println("D09 = " + D09);
      Data D10 = new Data(D09);
      System.out.println("D10 = " + D10 + " (copy of D09)");
      Data D11 = new Data(D05,0);
      System.out.println("D11 = " + D11 + " (D05 flipped)");
      Data D12 = new Data(D03,2);
      System.out.println("D12 = " + D12 + " (D03 shuffled with h=2)");
      Data D13 = new Data(D12,4);
      System.out.println("D13 = " + D13 + " (D12 shuffled with h=4)");
      Data D14 = new Data(D13,1,4);
      System.out.println("D14 = " + D14 + " (D13 from 1 to 4)");
      Data D15 = new Data(D13,7,9);
      System.out.println("D15 = " + D15 + " (D13 from 7 to 9)");
      Data D16 = new Data(D03,D04,"and");
      System.out.println("D16 = " + D16);
      Data D17 = new Data(D03,D04,"or");
      System.out.println("D17 = " + D17);
      Data D18 = new Data(D03,D04,"xor");
      System.out.println("D18 = " + D18);
      Data D19 = new Data(D17,D18);
      System.out.print("D19 = " + D19 + " ... ");
      Data D17d = new Data(D19,0,D17.numberOfBits() - 1);
      Data D18d = new Data(D19,D17.numberOfBits(),D17.numberOfBits() + D18.numberOfBits() - 1);
      if (D17d.equals(D17) && D18d.equals(D18))  System.out.println("OK!!!");
      Data D20 = new Data("000101010101010111");
      System.out.println("D20 = " + D20);
      Data D21 = new Data("000101010101010110");
      System.out.println("D21 = " + D21);
      System.out.print("Method equals : D09 and D10 are ");
      if (D09.equals(D10))
         System.out.println("equal");
      else
         System.out.println("different");
      System.out.print("Method equals : D20 and D21 are ");
      if (D20.equals(D21))
         System.out.println("equal");
      else
         System.out.println("different");
      System.out.print("Method compareTo indicates : D20 ");
      if (D20.compareTo(D21) < 0)
         System.out.print("< ");
      else
         System.out.print("> ");
      System.out.println("D21");
      Data Diff = Data.diff(D17,D18);
      System.out.println("Static method diff (D17 <> D18) : " + Diff);
      Diff = Data.control(Diff,false,0.5);
      System.out.println("Static method control (Diff with -50% of 0s) : " + Diff);
      System.out.print("Static method concat (D03 + D04 + D05 + D06) ... ");
      ArrayList<Data> lD = new ArrayList<Data> (4);
      lD.add(D03);  lD.add(D04);  lD.add(D05);  lD.add(D06);
      Data Dconc = Data.concat(lD);
      int n03 = D03.numberOfBits();
      int n04 = D04.numberOfBits();
      int n05 = D05.numberOfBits();
      int n06 = D06.numberOfBits();
      boolean OK = true;
      Data test = new Data(Dconc,0,n03 - 1);
      if (!test.equals(D03))  OK = false;
      test = new Data(Dconc,n03,n03 + n04 - 1);
      if (!test.equals(D04))  OK = false;
      test = new Data(Dconc,n03 + n04,n03 + n04 + n05 - 1);
      if (!test.equals(D05))  OK = false;
      test = new Data(Dconc,n03 + n04 + n05,n03 + n04 + n05 + n06 - 1);
      if (!test.equals(D06))  OK = false;
      if (OK)  System.out.println("OK!!!");
      System.out.println("Dconc = " + Dconc + "\n");
      System.out.print("Testing iterators ... ");
      while (D17.hasNextBit() && D17d.hasNextBit())
      {
         if (D17.getCurrentBit() != D17d.getCurrentBit())  System.out.println("ooops, an error occurred ... ");
         D17.moveToNextBit();
         D17d.moveToNextBit();
      }
      D17d.reset();  D17d.setToLast();
      while (D17.hasPrevBit() && D17d.hasPrevBit())
      {
         if (D17.getCurrentBit() != D17d.getCurrentBit())  System.out.println("ooops, an error occurred ... ");
         D17.moveToPrevBit();
         D17d.moveToPrevBit();
      }
      System.out.println("done");

      System.out.println("Testing flipBit methods (starting point is D12)");
      for (int k = 0; k < D12.numberOfBits(); k++)
      {
         D12.flipBit(k);
         System.out.println(D12);
      }
      System.out.println();

      System.out.println("Testing Hamming distance methods");
      System.out.println("Distance between D01 and D02 is " + D01.hammingDistanceTo(D02));
      System.out.println("Distance between D12 and D13 is " + D12.hammingDistanceTo(D13));
      System.out.println("Distance between D16 and D17 is " + D16.hammingDistanceTo(D17));
      System.out.println("Distance between D09 and D10 is " + D10.hammingDistanceTo(D09));
      System.out.println("D09 belongsToNeighbourOf D10 (h = 1) : " + D09.belongsToNeighbourOf(D10,1));
      System.out.println("D12 belongsToNeighbourOf D13 (h = 9) : " + D12.belongsToNeighbourOf(D13,9));
      int h = 1 + R.nextInt(6);
      System.out.println("randomSelectInNeighbour(" + h + "," + h + ") ==> " + D02.randomSelectInNeighbour(h,h)); 
      System.out.println();

      System.out.println("Testing methods *Value()");
      System.out.println("booleanValue of D01 = " + D01.booleanValue());
      System.out.println("booleanValue of D09 = " + D09.booleanValue());
      System.out.println("intValue of D08 = " + D08.intValue());
      System.out.println("intValue of D09 = " + D09.intValue());
      System.out.println("longValue of D06 = " + D06.longValue());
      System.out.println("posIntValue of D09 = " + D09.posIntValue());
      System.out.println("posLongValue of D06 = " + D06.posLongValue());
      System.out.println("floatValue of D05 = " + D05.floatValue());
      System.out.println("doubleValue of D05 = " + D05.doubleValue());
      System.out.println("charValue of D05 = " + D05.charValue());
      System.out.println("charValue of D08 = " + D08.charValue());
      System.out.println("letterValue of D09 = " + D09.letterValue());
      System.out.println();
   }
}

