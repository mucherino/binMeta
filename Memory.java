
/* Memory class
 *
 * binMeta project
 *
 * last update: Dec 13, 2020
 *
 * AM
 */

public class Memory
{
   private int n;  // number of Data records currently in Memory
   private int capacity;  // Memory capacity
   private Data[] data;  // Data objects
   private Double[] value;  // objective function values for Data objects
   private long[] loadTime;  // time (ms) when the data object was loaded in the Record object
   private long[] accessTime;  // time (ms) when the data object was accessed last time
   private int nExtraParam;  // number of additional (optional) parameters
   private Object[][] param;  // additional (optional) parameters

   // Memory constructor
   public Memory(int capacity,int nExtraParam)
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
         if (nExtraParam < 0) throw new Exception("Specified number of extra parameters for Memory object is negative");
         this.nExtraParam = nExtraParam;
         this.param = null;
         if (this.nExtraParam > 0)
         {
            this.param = new Object[this.capacity][this.nExtraParam];
            if (this.param == null) throw new Exception("Not enough memory resources for Memory object");
            for (int i = 0; i < this.nExtraParam; i++)
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

   // isFull
   public boolean isFull()
   {
      return this.n >= this.capacity;
   }

   // checkIndex (private)
   private boolean checkIndex(int i)
   {
      try
      {
         if (i < 0 || i > this.capacity) throw new Exception("Data record index out of bounds");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return true;
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

   // set
   public void set(int i,Data data,double value)
   {
      try
      {
         if (data == null) throw new Exception("the input Data object to be set is null");
         if (this.checkIndex(i))
         {
            if (this.data == null) throw new Exception("Data record with index " + i + " is not defined; use method add instead");
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

   // checkParamIndex (private)
   private boolean checkParamIndex(int j)
   {
      try
      {
         if (j < 0 || j > this.nExtraParam) throw new Exception("Extra paramater index out of bounds");
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
            if (this.data == null || this.value == null || this.param == null) throw new Exception("Data record with index " + i + " is not defined");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      this.param[i][j] = param;
   }

   // contains
   public boolean contains(Data D)
   {
      if (D == null)  return false;
      int i = 0;
      boolean present = false;
      while (!present && i < this.capacity)
      {
         if (this.data[i] != null && this.data[i].equals(D))  present = true;
         i++;
      }
      return present;
   }

   // getWorstIndex (gives -1 when Memory is empty)
   public int getWorstIndex()
   {
      int worst = -1;
      if (this.n > 0)
      {
         double worstValue = -Double.MAX_VALUE;
         for (int i = 0; i < this.capacity; i++)
         {
            if (this.data[i] != null && this.value[i] != null)
            {
               if (this.value[i] > worstValue)
               {
                  worst = i;
                  worstValue = this.value[i];
               }
            }
         }
      }
      return worst;
   }

   // getBestIndex (gives -1 when Memory is empty)
   public int getBestIndex()
   {
      int best = -1;
      if (this.n > 0)
      {
         double bestValue = Double.MAX_VALUE;
         for (int i = 0; i < this.capacity; i++)
         {
            if (this.data[i] != null && this.value[i] != null)
            {
               if (this.value[i] < bestValue)
               {
                  best = i;
                  bestValue = this.value[i];
               }
            }
         }
      }
      return best;
   }

   // getOldestIndex (private)
   private int getOldestIndex()
   {
      int oldest = -1;
      long longestTime = System.currentTimeMillis();
      for (int i = 0; i < this.capacity; i++)
      {
         if (this.data[i] != null && this.value[i] != null)
         {
            if (this.loadTime[i] < longestTime)
            {
               oldest = i;
               longestTime = this.loadTime[i];
            }
         }
      }
      return oldest;
   }

   // getUselessIndex (private)
   private int getUselessIndex()
   {
      int useless = -1;
      long useTime = System.currentTimeMillis();
      for (int i = 0; i < this.capacity; i++)
      {
         if (this.data[i] != null && this.value[i] != null)
         {
            if (this.accessTime[i] < useTime)
            {  
               useless = i; 
               useTime = this.accessTime[i];
            }
         }
      }
      return useless;
   }

   // add
   // the returning value is the assigned index in the Memory object
   // the FIFO strategy is applied when memory is full
   // !! strategies other than FIFO may thereafter be implemented !!
   public int add(Data data,double value)
   {
      try
      {
         if (data == null) throw new Exception("the input Data object to be added is null");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // identifying the position in Memory
      int pos = 0;
      if (!this.isFull())
      {
         // looking for an "empty" space
         while (pos < this.capacity && this.data[pos] != null)  pos++;  // an internal Exception may be managed here
         this.n++;
      }
      else
      {
         // looking for the "victim"
         pos = this.getOldestIndex();
      }

      // including the new data in Memory
      this.data[pos] = new Data(data);
      this.value[pos] = value;
      this.loadTime[pos] = System.currentTimeMillis();
      this.accessTime[pos] = this.loadTime[pos];
      if (this.nExtraParam > 0)  for (int j = 0; j < this.nExtraParam; j++)  this.param[pos][j] = null;

      // the returning value is the index in Memory where the new Data record was included
      return pos;
   }

   // add
   // (as above, but with value set up automatically to 0.0)
   public int add(Data data)
   {
      return this.add(data,0.0);
   }

   // remove
   public boolean remove(int i)
   {
      try
      {
         if (i < 0 || i > this.capacity) throw new Exception("Data record index out of bounds");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      boolean removed = false;
      if (this.data[i] != null)
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
}

