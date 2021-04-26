
/* Memory class
 *
 * binMeta project
 *
 * last update: April 14, 2021
 *
 * AM
 */

import java.util.ArrayList;
import java.util.Random;

public class Memory
{
   private int n;  // number of Data entries currently in Memory
   private int capacity;  // Memory capacity
   private Data[] data;  // Data objects
   private Double[] value;  // objective function values for Data objects
   private long[] loadTime;  // time (ms) when the data object was loaded in the Record object
   private long[] accessTime;  // time (ms) when the data object was accessed last time
   private String method;  // name of the method used to select "victim" entries
   private int nExtraParam;  // number of additional (optional) parameters
   private Object[][] param;  // additional (optional) parameters

   // Memory constructor
   public Memory(int capacity,String method,int nExtraParam)
   {
      try
      {
         this.n = 0;
         if (capacity <= 0) throw new Exception("Memory needs a positive maximal capacity");
         this.capacity = capacity;
         this.data = null;
         this.data = new Data[this.capacity];
         if (this.data == null) throw new Exception("Not enough memory resources for Memory object");
         for (int i = 0; i < this.capacity; i++)  this.data[i] = null;
         this.value = null;
         this.value = new Double[this.capacity];
         if (this.value == null) throw new Exception("Not enough memory resources for Memory object");
         for (int i = 0; i < this.capacity; i++)  this.value[i] = null;
         this.loadTime = null;
         this.loadTime = new long[this.capacity];
         if (this.loadTime == null) throw new Exception("Not enough memory resources for Memory object");
         for (int i = 0; i < this.capacity; i++)  this.loadTime[i] = 0;
         this.accessTime = null;
         this.accessTime = new long[this.capacity];
         if (this.accessTime == null) throw new Exception("Not enough memory resources for Memory object");
         for (int i = 0; i < this.capacity; i++)  this.accessTime[i] = 0;
         if (method == null) throw new Exception("String containing method for 'victim' selection is null");
         if (method.length() == 0) throw new Exception("String containing method for 'victim' selection is empty");
         this.method = "FIFO";
         if (!method.equalsIgnoreCase("FIFO"))
         {
            if (method.equalsIgnoreCase("LRU"))
               this.method = "LRU";
            else throw new Exception("Unknown method for 'victim' selection : " + method);
         }
         if (nExtraParam < 0) throw new Exception("Specified number of extra parameters for Memory object is negative");
         this.nExtraParam = nExtraParam;
         this.param = null;
         if (this.nExtraParam > 0)
         {
            this.param = new Object[this.capacity][this.nExtraParam];
            if (this.param == null) throw new Exception("Not enough memory resources for Memory object");
            for (int i = 0; i < this.capacity; i++)
            {
               for (int j = 0; j < this.nExtraParam; j++)  this.param[i][j] = null;
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // Memory constructor
   public Memory(int capacity,String method)
   {
      this(capacity,method,0);
   }

   // Memory constructor
   public Memory(int capacity,int nExtraParam)
   {
      this(capacity,"FIFO",nExtraParam);
   }

   // Memory constructor
   public Memory(int capacity)
   {
      this(capacity,0);
   }

   // getCapacity
   public int getCapacity()
   {
      return this.capacity;
   }

   // numberOfEntries
   public int numberOfEntries()
   {
      return this.n;
   }

   // isEmpty
   public boolean isEmpty()
   {
      return this.n == 0;
   }

   // isFull
   public boolean isFull()
   {
      return this.n == this.capacity;
   }

   // contains
   public boolean contains(Data data)
   {
      if (data == null)  return false;
      int i = 0;
      int n = 0;
      boolean present = false;
      while (!present && n < this.n && i < this.capacity)
      {
         if (this.data[i] != null)
         {
            if (this.data[i].equals(data))  present = true;
            n++;
         }
         i++;
      }
      return present;
   }

   // indexOf (gives -1 if the Data object is not contained in Memory)
   public int indexOf(Data data)
   {
      if (data == null)  return -1;
      int i = 0;
      int n = 0;
      int index = -1;
      while (index == -1 && n < this.n && i < this.capacity)
      {
         if (this.data[i] != null)
         {
            if (this.data[i].equals(data))  index = i;
            n++;
         }
         i++;
      }
      return index;
   }

   // checkIndex (private)
   private boolean checkIndex(int i)
   {
      try
      {
         if (i < 0 || i >= this.capacity) 
            throw new Exception("Data entry index " + i + " out of bounds (capacity is " + this.capacity + ")");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return true;
   }

   // add
   // -> the returning value is the assigned index in the Memory object
   // -> if the Data object is already contained in the memory, only the associated value is updated
   // -> FIFO or LRU strategies are applied when memory is full (defined in constructor)
   public int add(Data data,double value)
   {
      int pos = 0;
      try
      {
         // verification of input Data object
         if (data == null) throw new Exception("Data object to be added is null");

         // verifying whether the Data object is already in Memory object
         int i = this.indexOf(data);
         if (i != -1)
         {
            if (this.value[i] != value)  this.value[i] = value;
            return i;
         }

         // identifying the position in Memory
         if (!this.isFull())
         {
            // looking for an "empty" place
            while (pos < this.capacity && this.data[pos] != null)  pos++;
            if (pos == this.capacity) throw new Exception("Memory: internal error");
            this.n++;
         }
         else if (!this.isEmpty())
         {
            // looking for the "victim"
            if (this.method.equals("FIFO"))
            {
               pos = this.indexOfOldest();
            }
            else // LRU
            {
               pos = this.indexOfUseless();
            }
         }
         this.checkIndex(pos);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // including the new data in Memory
      this.data[pos] = new Data(data);
      this.value[pos] = value;
      this.loadTime[pos] = System.currentTimeMillis();
      this.accessTime[pos] = this.loadTime[pos];
      if (this.nExtraParam > 0)  for (int j = 0; j < this.nExtraParam; j++)  this.param[pos][j] = null;

      // the returning value is the index in Memory where the new Data entry was included
      return pos;
   }

   // add
   // -> as above, but with value set up automatically to 0.0
   public int add(Data data)
   {
      return this.add(data,0.0);
   }

   // getData
   public Data getData(int i)
   {
      Data D = null;
      if (this.checkIndex(i))
      {
         this.accessTime[i] = System.currentTimeMillis();
         D = this.data[i];
      }
      return D;
   }

   // getValue
   public Double getValue(int i)
   {
      Double value = null;
      if (this.checkIndex(i))
      {
         this.accessTime[i] = System.currentTimeMillis();
         value = this.value[i];
      }
      return value;
   }

   // getValueOf
   public Double getValueOf(Data data)
   {
      int i = this.indexOf(data);
      if (i == -1)  return null;
      return this.getValue(i);
   }

   // set
   public void set(int i,Data data,double value)
   {
      try
      {
         if (data == null) throw new Exception("Data object to be set is null");
         if (this.checkIndex(i))
         {
            if (this.data == null) throw new Exception("Data entry with index " + i + " is not defined; use method add instead");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      this.data[i] = data;
      this.value[i] = value;
      this.accessTime[i] = System.currentTimeMillis();
   }

   // set
   // -> as above, but with value set up automatically to 0.0
   public void set(int i,Data data)
   {
      this.set(i,data,0.0);
   }

   // remove (via index)
   public boolean remove(int i)
   {
      boolean removed = false;
      if (this.checkIndex(i) && this.data[i] != null)
      {
         this.n--;
         this.data[i] = null;
         this.value[i] = null;
         this.loadTime[i] = 0;
         this.accessTime[i] = 0;
         if (this.nExtraParam > 0)  for (int j = 0; j < this.nExtraParam; j++)  this.param[i][j] = null;
         removed = true;
      }
      return removed;
   }

   // remove (via Data object)
   public boolean remove(Data data)
   {
      int i = this.indexOf(data);
      if (i == -1)  return false;
      return this.remove(i);
   }

   // compact
   public void compact()
   {
      if (this.isEmpty() || this.isFull())  return;

      int i = 0;
      int j = this.capacity - 1;
      try
      {
         while (i < this.capacity && this.data[i] != null)  i++;
         if (i == this.capacity) throw new Exception("Memory: internal error");

         while (j >= 0 && this.data[j] == null)  j--;
         if (j == -1) throw new Exception("Memory: internal error");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      while (i < j)
      {
         this.data[i] = this.data[j];
         this.value[i] = this.value[j];
         this.data[j] = null;  this.value[j] = null;
         this.loadTime[i] = this.loadTime[j];
         this.accessTime[i] = this.accessTime[j];
         if (this.nExtraParam > 0)
         {
            Object [] tmp = this.param[i];
            this.param[i] = this.param[j];
            this.param[j] = tmp;
         }
         i++;  while (i < j && this.data[i] != null)  i++;
         j--;  while (i < j && this.data[j] == null)  j--;
      }
   }

   // indexOfWorst
   // -> the worst is the one with the largest double value
   // -> gives -1 when Memory is empty
   public int indexOfWorst()
   {
      int worst = -1;
      if (this.n > 0)
      {
         int i = 0;
         int n = 0;
         double worstValue = -Double.MAX_VALUE;
         while (i < this.capacity && n < this.n)
         {
            if (this.data[i] != null && this.value[i] != null)
            {
               if (this.value[i] > worstValue)
               {
                  worst = i;
                  worstValue = this.value[i];
               }
               n++;
            }
            i++;
         }
      }
      return worst;
   }

   // indexOfBest
   // -> the best is the one with the smallest double value
   // -> gives -1 when the Memory is empty
   public int indexOfBest()
   {
      int best = -1;
      if (this.n > 0)
      {
         int i = 0;
         int n = 0;
         double bestValue = Double.MAX_VALUE;
         while (i < this.capacity && n < this.n)
         {
            if (this.data[i] != null && this.value[i] != null)
            {
               if (this.value[i] < bestValue)
               {
                  best = i;
                  bestValue = this.value[i];
               }
               n++;
            }
            i++;
         }
      }
      return best;
   }

   // indexOfOldest
   // -> the entry that is in Memory since longer time (time verification in terms of milliseconds)
   // -> gives -1 when the Memory is empty
   public int indexOfOldest()
   {
      if (this.isEmpty())  return -1;

      int oldest = 0;
      try
      {
         while (oldest < this.capacity && (this.data[oldest] == null || this.value[oldest] == null))  oldest++;
         if (oldest == this.capacity) throw new Exception("Memory: internal error");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      long longestTime = this.loadTime[oldest];

      int i = oldest + 1;
      int n = 1;
      while (i < this.capacity && n < this.n)
      {
         if (this.data[i] != null && this.value[i] != null)
         {
            if (this.loadTime[i] < longestTime)
            {
               oldest = i;
               longestTime = this.loadTime[i];
            }
            n++;
         }
         i++;
      }
      return oldest;
   }

   // indexOfUseless
   // -> the entry that was not accessed since longer time (time verification in terms of milliseconds)
   // -> gives -1 when Memory is empty
   public int indexOfUseless()
   {
      if (this.isEmpty())  return -1;

      int useless = 0;
      try
      {
         while (useless < this.capacity && (this.data[useless] == null || this.value[useless] == null))  useless++;
         if (useless == this.capacity) throw new Exception("Memory: internal error");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      long useTime = this.accessTime[useless];

      int i = useless + 1;
      int n = 1;
      while (i < this.capacity && n < this.n)
      {
         if (this.data[i] != null && this.value[i] != null)
         {
            if (this.accessTime[i] < useTime)
            {  
               useless = i; 
               useTime = this.accessTime[i];
            }
            n++;
         }
         i++;
      }
      return useless;
   }

   // checkParamIndex (private)
   private boolean checkParamIndex(int j)
   {
      try
      {
         if (j < 0 || j >= this.nExtraParam) throw new Exception("Extra paramater index out of bounds");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return true;
   }

   // getParameter
   public Object getParameter(int i,int j)
   {
      Object p = null;
      if (this.checkIndex(i) && this.checkParamIndex(j))
      {
         this.accessTime[i] = System.currentTimeMillis();
         p = this.param[i][j];
      }
      return p;
   }

   // setParameter
   public void setParameter(int i,int j,Object param)
   {
      try
      {
         if (param == null) throw new Exception("the input Object representing the parameter is null");
         if (this.checkIndex(i) && this.checkParamIndex(j))
         {
            if (this.data == null || this.value == null || this.param == null) throw new Exception("Data entry with index " + i + " is not defined");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      this.accessTime[i] = System.currentTimeMillis();
      this.param[i][j] = param;
   }

   // toString (only basic information about the Memory object, not its content)
   // TO BE IMPLEMENTED!

   // toString
   public String toString()
   {
      String print = "Memory : [";
      if (this.isEmpty())
         print = print + "empty, ";
      else
         print = print + "entries: " + this.n + ", ";
      print = print + "capacity: " + this.capacity + ", ";
      print = print + "parameters: " + this.nExtraParam + ", ";
      return print + this.method + "]";
   }

   // main (performing some basic tests)
   public static void main(String[] args)
   {
      System.out.println("Memory class\n");
      int NTESTS = 2000;
      int min = 2;
      int max = 200;
      long seed = 0;
      Random R = new Random();
      if (args != null && args.length > 0)
         seed = Long.parseLong(args[0]);
      else
         seed = Math.abs(R.nextLong());
      System.out.println("Random seed set to : " + seed);
      R = new Random(seed);

      // constructors and basic methods
      System.out.print("Testing constructors and basic methods ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         try
         {
            // random arguments
            int capacity = min + R.nextInt(max - min);
            int nextra = min + R.nextInt(max - min);
            if (R.nextDouble() < 0.01)  nextra = 0;

            // constructors
            Memory M = null;
            Exception E = null;
            String[] methods = {"fifo","lru"};
            int rand = R.nextInt(4);
            if (rand == 0)
            {
               M = new Memory(capacity,methods[R.nextInt(2)],nextra);
               E = new Exception("public Memory(int,String,int)");
            }
            else if (rand == 1)
            {
               M = new Memory(capacity,methods[R.nextInt(2)]);
               E = new Exception("public Memory(int,String)");
            }
            else if (rand == 2)
            {
               M = new Memory(capacity,nextra);
               E = new Exception("public Memory(int,int)");
            }
            else
            {
               M = new Memory(capacity);
               E = new Exception("public Memory(int)");
            }
            if (M == null) throw E;

            // getCapacity
            if (M.getCapacity() != capacity) throw new Exception("public int getCapacity()");

            // numberOfEntries
            if (M.numberOfEntries() != 0) throw new Exception("public int numberOfEntries()");

            // isEmpty
            if (!M.isEmpty()) throw new Exception("public boolean isEmpty()");

            // isFull
            if (M.isFull()) throw new Exception("public boolean isFull()");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // add, get, set, contains, remove
      System.out.print("Testing add, get, set, contains and remove ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         try
         {
            // random arguments
            int capacity = min + R.nextInt(max - min);
            Memory M = new Memory(capacity,"FIFO");

            // exceptions
            Exception E1 = new Exception("public int add(Data,double)");
            Exception E2 = new Exception("public Data getData()");
            Exception E3 = new Exception("public Double getValue()");
            Exception E4 = new Exception("public boolean contains()");
            Exception E5 = new Exception("public int indexOf()");
            Exception E6 = new Exception("public void set(int,Data,double)");
            Exception E7 = new Exception("public boolean remove(?)");
            Exception E8 = new Exception("public Double getValueOf(Data)");

            // adding and getting entries
            int n = min + R.nextInt(max - min);
            for (int i = 0; i < n; i++)
            {
               Data D = new Data(15,R.nextDouble());
               if (!M.isEmpty())  while (M.contains(D))  D = new Data(15,R.nextDouble());
               double value = R.nextDouble();
               if (M.contains(D)) throw E4;
               int k = M.add(D,value);
               M.checkIndex(k);
               if (!M.contains(D))  throw E1;
               int h = M.indexOf(D);
               if (k != h) throw E5;
               if (i < capacity) 
               {
                  if (M.numberOfEntries() != i + 1) throw E1;
               }
               else
               {
                  if (M.numberOfEntries() != capacity) throw E1;
               }
               if (k >= capacity) throw E1;
               if (!M.getData(k).equals(D)) throw E2;
               if (M.getValue(k) != value) throw E3;
               if (M.getValueOf(D) != value) throw E8;
            }

            // filling up Memory object
            while (!M.isFull())  M.add(new Data(10,0.5),R.nextDouble());

            // more verifications
            if (M.isEmpty()) throw E1;
            if (!M.isFull()) throw E1;
            Data D = M.getData(0);
            int k = M.indexOf(D);
            if (k != 0) throw E5;
            if (!M.contains(D)) throw E4;
            double value = M.getValue(0);
            if (value != 1.0)
            {
               M.add(D,1.0);
               if (!M.contains(D)) throw E4;
               if (M.getValue(0) != 1.0) throw E1;
            }

            // adding more than capacity
            while (M.contains(D))  D = new Data(15,0.5);
            int nentries = M.numberOfEntries();
            M.add(D,R.nextDouble());
            if (nentries != M.numberOfEntries()) throw E1;
            if (!M.isFull()) throw E1;

            // checking one specific Data object
            int h = R.nextInt(capacity);
            D = M.getData(h);
            if (M.indexOf(D) != h) throw E5;
            while (M.contains(D))  D = new Data(15,R.nextDouble());
            M.set(h,D,0.0);
            if (M.indexOf(D) != h) throw E6;
            if (!M.getData(h).equals(D)) throw E6;

            // removing a specific Data object
            M.remove(h);
            if (M.contains(D)) throw E7;
            if (M.indexOf(D) != -1) throw E7;
            if (M.isFull()) throw E7;
            while (M.contains(D))  D = new Data(15,0.5);
            M.add(D);
            if (!M.isFull()) throw E1;
            if (!M.contains(D)) throw E1;
            M.remove(D);
            if (M.isFull()) throw E7;
            if (M.contains(D)) throw E7;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // indexOfWorst and indexOfBest
      System.out.print("Testing indexOfWorst and indexOfBest ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         try
         {
            // random arguments
            int capacity = min + R.nextInt(max - min);
            Memory M = new Memory(capacity,"LRU");

            // filling up the memory while checking the random double values
            Data Best = null;
            Data Worst = null;
            double best = Double.MAX_VALUE;
            double worst = -Double.MAX_VALUE;
            Data D = new Data(16,0.5);
            while (!M.isFull())
            {
               while (M.contains(D))  D = new Data(16,R.nextDouble());
               double value = R.nextDouble();
               M.add(D,value);
               if (value > worst)
               {
                  Worst = D;
                  worst = value;
               }
               if (value < best)
               {
                  Best = D;
                  best = value;
               }
            }
            int i = M.indexOfWorst();
            M.checkIndex(i);
            if (!M.getData(i).equals(Worst) || M.getValue(i) != worst) throw new Exception("public int indexOfWorst");
            int j = M.indexOfBest();
            M.checkIndex(j);
            if (!M.getData(j).equals(Best) || M.getValue(j) != best) throw new Exception("public int indexOfBest");
            if (!Best.equals(Worst))  if (i == j) throw new Exception("indexOfWorst <> indexOfBest");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // indexOfOldest and indexOfUseless
      System.out.print("Testing indexOfOldest and indexOfUseless ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         try
         {
            // random arguments
            int n = min + R.nextInt(2*min) + 1;
            int capacity = 2 + n + R.nextInt(max - n);
            Memory M = new Memory(capacity,"fifo");

            // adding the first entry and then waiting a little...
            Data first = new Data(15,0.5);
            M.add(first);
            Thread.sleep(1);

            // adding other entries
            while (M.numberOfEntries() < 1 + n)
            {
               Data other = new Data(15,R.nextDouble());
               if (!M.contains(other))  M.add(other);
            }

            // waiting a little... before adding the last entry
            Thread.sleep(1);
            Data last = first;
            while (M.contains(last))  last = new Data(15,R.nextDouble());
            M.add(last);

            // two exceptions
            Exception E1 = new Exception("public int indexOfOldest()");
            Exception E2 = new Exception("public int indexOfUseless()");

            // verifying
            int i = M.indexOfOldest();
            M.checkIndex(i);
            if (i == -1) throw E1;
            if (i != M.indexOf(first)) throw E1;
            if (i == M.indexOf(last)) throw E1;
            int j = M.indexOfUseless();
            M.checkIndex(j);
            if (j == -1) throw E2;
            if (i != j) throw E2;
            if (j != M.indexOf(first)) throw E2;
            Thread.sleep(1);
            M.getData(i);
            i = M.indexOfOldest();
            if (i == -1 || i != M.indexOf(first)) throw E1;
            M.getValue(i);
            j = M.indexOfUseless();
            if (j == -1 || j == i) throw E2;
            if (j == M.indexOf(first)) throw E2;
            M.getValueOf(last);
            j = M.indexOfUseless();
            if (j == M.indexOf(last)) throw E2;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // get and set parameters
      System.out.print("Testing getParameter and setParameter ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         try
         {
            // random arguments
            int nextra = 1 + R.nextInt(3*min);
            int capacity = min + R.nextInt(max - min);
            Memory M = new Memory(capacity,nextra);
            Exception E = new Exception("public void setParameter(int,int,Object) / public Object getParameter(int,int)");
            if (M == null) throw new Exception("Something wrong with the constructor");

            // setting and getting parameters
            int m = min + R.nextInt(capacity);
            boolean isStringType = R.nextBoolean();
            for (int k = 0; k < m; k++)
            {
               Data D = new Data(10,0.5);
               while (M.contains(D))  D = new Data(10,R.nextDouble());
               M.add(D,R.nextDouble());
               int i = M.indexOf(D);
               if (isStringType)
               {
                  // the parameters are String objects
                  String prefix = "abc";
                  for (int j = 0; j < nextra; j++)  M.setParameter(i,j,prefix+(j+1));
               }
               else
               {
                  // the parameters are Double objects
                  for (int j = 0; j < nextra; j++)  M.setParameter(i,j,1.0/(j+1));
               }

               // getting
               int j = R.nextInt(nextra);
               Object o = M.getParameter(i,j);
               if (o == null) throw E;
               if (R.nextDouble() < 0.5)
               {
                  if (isStringType)
                  {
                     String str = (String) o;
                     if (str.length() <= 3) throw E;
                     if (!str.substring(0,3).equals("abc")) throw E;
                  }
                  else
                  {
                     Double d = (Double) o;
                     if (d != 1.0/(j+1)) throw E;
                  }
               }
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // compact
      System.out.print("Testing compact ... ");
      for (int itest = 0; itest < NTESTS; itest++)
      {
         try
         {
            // random arguments
            int capacity = min + R.nextInt(max - min);
            Memory M = new Memory(capacity,"lru",1);
            int jump = 2 + R.nextInt(min + capacity/5);
            ArrayList<Data> lD = new ArrayList<Data> (capacity);
            while (lD.size() < capacity)
            {
               Data D = new Data(15,R.nextDouble());
               if (!lD.contains(D))  lD.add(D);
            }

            // exceptions
            Exception E = new Exception("public void compact()");

            // entering the Data object in Memory
            for (Data D : lD)
            {
               M.add(D);
               int i = M.indexOf(D);
               M.setParameter(i,0,i);
            }

            // removing all elements with index "const + k*jump" ...
            int n = capacity;
            int k = capacity - 1;
            for (; k >= 0; k = k - jump)
            {
               lD.remove(k);
               M.remove(k);
               n--;
            }
            k = k + jump;

            // compacting!
            M.compact();

            // the Memory cannot be full
            if (M.isFull()) throw E;

            // the number of entries must be equal to n
            if (M.numberOfEntries() != n) throw E;

            // the elements of lD are still in Memory
            for (Data D : lD)  if (!M.contains(D)) throw E;

            // verification of the parameter of elements still in Memory
            if (n > 0)
            {
               int i = R.nextInt(n);
               int h = (Integer) M.getParameter(i,0);
               while (h != k && h < capacity)  h = h + jump;
               if (h == k) throw E;
            }

            // all Memory entries from n+1 to the end are null
            for (k = n; k < capacity; k++)  if (M.getData(k) != null || M.getValue(k) != null) throw E;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            System.exit(1);
         }
      }
      System.out.println("OK");

      // ending
      System.out.println();
   }
}

