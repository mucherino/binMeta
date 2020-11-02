
/* Data class
 *
 * binMeta project
 *
 * last update: Nov 1, 2020
 *
 * AM
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Data
{
   private int size;  // actual size (may not be a multiple of 8)
   private ArrayList<Byte> data;  // the data are stored as lists of bytes
   private int current;  // the current bit address

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

   // Data constructor: it generates a new Data object consisting of the bits in a given byte
   //                   the bit pointer is set to 0
   public Data(byte b)
   {
      this.size = 8;
      this.data = new ArrayList<Byte> (1);
      this.data.add(b);
      this.current = 0;
   }

   // Data constructor: it generates a new Data object consisting of the bits in a given integer
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

   // Data constructor: it generates a new Data object consisting of the bits in a given long integer
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

   // Data constructor: it generates a new Data object consisting of the bits in a given char
   //                   only 5 bits are used if the boolean argument specifies that the char is a alphabet letter
   //                   the bit pointer is set to 0
   public Data(char c,boolean isLetter)
   {
      try
      {
         byte b;
         if (isLetter)
         {
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) throw new Exception("Impossible to create Data object: the char is not an alphabet letter");
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

   // Data constructor: it generates a new Data object consisting of the bits in a given char
   //                   the bit pointer is set to 0
   public Data(char c)
   {
      this(c,false);
   }

   // Data constructor: it generates a new Data object consisting of an existing Data object
   //                   where all bits are flipped if the boolean argument is true 
   //                   the bit pointer is set to 0
   public Data(Data D,boolean flip)
   {
      try
      {
         if (D == null) throw new Exception("Input Data object is null");
         this.size = D.size;
         this.data = new ArrayList<Byte> (D.data.size());
         for (byte b : D.data)
         {
            if (flip)
            {
               int bi = b;
               b = (byte) ~bi;
            }
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

   // Data constructor: it generates a new Data object consisting of an existing Data object
   //                   the bit pointer is set to 0
   public Data(Data D)
   {
      this(D,false);
   }

   // Data constructor: it generates a new Data object by concatenating the bits in a List of Data objects
   //                   the bit pointer is set to 0
   public Data(List<Data> lD)
   {
      try
      {
         if (lD == null) throw new Exception("List of Data objects is null");
         if (lD.size() == 0) throw new Exception("List of Data objects is empty");
         this.size = 0;
         for (Data D : lD)
         {
            if (D == null) throw new Exception("At least one Data object in List is null");
            this.size = this.size + D.numberOfBits();
         }
         int N = 1 + (this.size - 1)/8;
         this.data = new ArrayList<Byte> (N);
         int bi = 0;
         int pointer = 0;
         for (int iD = 0; iD < lD.size(); iD++)
         {
            Data D = lD.get(iD);
            if (D.numberOfBits() > 0)
            {
               for (int i = 0; i < D.numberOfBits(); i++)
               {
                  if (pointer > 0 && pointer%8 == 0)
                  {
                     byte b = (byte) bi;
                     this.data.add(b);
                     bi = 0;
                  }
                  bi = bi << 1;
                  bi = bi + D.getBit(i);
                  pointer++;
               }
            }
         }
         while (pointer%8 != 0)
         {
            bi = bi << 1;
            pointer++;
         }
         byte b = (byte) bi;
         this.data.add(b);
         this.current = 0;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Data constructor: it generates a new Data object by extracting a specific data word from an existing Data object
   //                   the bits are flipped if the boolean argument is true, the bit pointer is set to 0
   public Data(Data D,int first,int last,boolean flip)
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
                  if (flip)  bit = (~bit) & 1;
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

   // Data constructor: it generates a new Data object by extracting a specific data word from an existing Data object
   //                   the bit pointer is set to 0
   public Data(Data D,int first,int last)
   {
      this(D,first,last,false);
   }

   // Gives the number of bits forming the Data object
   public int numberOfBits()
   {
      return this.size;
   }

   // Gives the number of bytes forming the Data object
   public int numberOfBytes()
   {
      return this.data.size();
   }

   // Gives a specified bit 
   // (private method, it doesnt verify if the bit and byte indices are correct)
   private int getBit(int i,int j)
   {
      byte b = (byte) this.data.get(i);
      return (b >> (7 - j)) & 1;
   }

   // Gives a specified bit
   // (private method, it doesnt verify if the bit index is correct)
   private int getBit(int k)
   {
      int i = k/8;
      int j = k%8;
      return this.getBit(i,j);
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
      return 1 + this.longValue();
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

   // toString (does not change the bit pointer)
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
      Data D10 = new Data(D09,false);
      System.out.println("D10 = " + D10);
      Data D11 = new Data(D01,true);
      System.out.println("D11 = " + D11);
      Data D12 = new Data(D03,true);
      System.out.println("D12 = " + D12);
      ArrayList<Data> lD = new ArrayList<Data> (4);
      lD.add(D03);  lD.add(D04);  lD.add(D05);  lD.add(D06);
      Data D13 = new Data(lD);
      System.out.println("D13 = " + D13);
      Data D14 = new Data(D13,8,31);
      System.out.println("D14 = " + D14);
      Data D15 = new Data(D13,8,31,true);
      System.out.println("D15 = " + D15);
      System.out.println();

      System.out.print("Testing iterators ... ");
      while (D14.hasNextBit() && D15.hasNextBit())
      {
         if (D14.getCurrentBit() == D15.getCurrentBit())  System.out.println("ooops, an error occurred ... ");
         D14.moveToNextBit();
         D15.moveToNextBit();
      }
      while (D14.hasPrevBit() && D15.hasPrevBit())
      {
         if (D14.getCurrentBit() == D15.getCurrentBit())  System.out.println("ooops, an error occurred ... ");
         D14.moveToPrevBit();
         D15.moveToPrevBit();
      }
      System.out.println("done\n");

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
   }
}

