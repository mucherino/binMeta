
/* SetCover class
 *
 * binMeta project
 *
 * History:
 * - coded by Clyde Jannel and Martin Souhil (CNI 2022-23)
 * - objective function remodeled to avoid large discontinuities
 * - constructors rewritten (automatic generation)
 *
 * last update: April 24, 2023
 *
 * AM
 */

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SetCover implements Objective 
{
   // attributes
   private List<Set<Integer>> listOfSets;
   private Set<Integer> Universe;
   private Double ub;

   // constructor (from a set and a list of sets)
   public SetCover(Set<Integer> Universe,List<Set<Integer>> listOfSets)
   {
      try
      {
         if (Universe == null) throw new Exception("SetCover: specified Universe set is null");
         if (Universe.isEmpty()) throw new Exception("SetCover: specified Universe set is empty");
         this.Universe = Universe;
         if (listOfSets == null) throw new Exception("SetCover: specified Set containing the sets is null");
         if (listOfSets.isEmpty()) throw new Exception("SetCover: specified Set containing the sets is empty");
         for (Set<Integer> s : listOfSets)
             if (s == null || s.isEmpty()) throw new Exception("SetCover: it looks like one of the sets in listOfSets is null or empty");
         this.listOfSets = listOfSets;
         HashSet<Integer> everything = new HashSet<Integer> (this.Universe);
         for (Set<Integer> s : listOfSets)  everything.removeAll(s);
         if (!everything.isEmpty()) throw new Exception("SetCover: the specified Universe cannot be covered with the given set of sets");
         this.ub = null;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // constructor (randomy generated instance)
   public SetCover(int n,int lb,int ub,double p,Random R)
   {
      try
      {
         if (n <= 0) throw new Exception("SetCover: cannot create instance with a nonpositive number of elements in the universe");
         if (n <= 9) throw new Exception("SetCover: cannot create instance with such a small number of elements in the universe");
         if (lb <= 0) throw new Exception("SetCover: the given lower bound for the subset size is nonpositive");
         if (ub <= 0) throw new Exception("SetCover: the given upper bound for the subset size is nonpositive");
         if (lb > ub) throw new Exception("SetCover: the given lower bound for the subset size is strictly larger than the upper bounds");
         if (p < 0.0) throw new Exception("SetCover: the given percentage of duplicated elements is negative");
         if (p > 1.0) throw new Exception("SetCover: the given percentage of duplicated elements is larger than 1.0");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      if (R == null)  R = new Random();

      // generating a set of random subsets, which will compose the Universe
      int size = 1 + (int) Math.floor(p*n);
      this.listOfSets = new ArrayList<Set<Integer>> ();
      this.Universe = new HashSet<Integer> ();
      this.ub = 0.0;
      for (int i = 0; i < size; i++)
      {
         int setsize = lb + R.nextInt(ub - lb);
         Set<Integer> subset = new HashSet<Integer> (setsize);
         while (subset.size() < setsize)  subset.add(R.nextInt());
         this.listOfSets.add(subset);
         this.Universe.addAll(subset);
         this.ub = this.ub + subset.size();
      }

      // filling up with other subsets (how many, it depends on p)
      ArrayList<Integer> everything = new ArrayList<Integer> (this.Universe);
      while (this.listOfSets.size() < n)
      {
         int setsize = lb + R.nextInt(ub - lb);
         Set<Integer> subset = new HashSet<Integer> (setsize);
         int k = 0;
         while (k < size && subset.size() < setsize)
         {
            subset.add(everything.get(R.nextInt(everything.size())));
            k++;
         }
         this.listOfSets.add(subset);
      }

      // shuffling the list of sets
      Collections.shuffle(this.listOfSets);
   }

   // constructor (randomy generated instance, also p is random here, but R cannot be null)
   public SetCover(int n,int lb,int ub,Random R)
   {
      this(n,lb,ub,0.5*R.nextDouble(),R);
   }

   // auxialiary private method for constructor
   private Set<Integer> nextSubset(int m,List<Integer> everything,Random R)
   {
      Set<Integer> subset = new HashSet<Integer> ();
      while (subset.size() != m)  subset.add(everything.get(R.nextInt(everything.size())));
      return subset;
   }

   @Override
   public String getName()
   {
      return "SetCover";
   }

   // solutionSample
   @Override
   public Data solutionSample()
   {
      return new Data(this.listOfSets.size(),0.5);
   }

   // upper bound on function value
   @Override
   public Double upperBound()
   {
      return this.ub;
   }

   // value
   @Override
   public double value(Data D)
   {
      int n = 0;
      try
      {
         if (D == null) throw new Exception("Impossible to evaluate SetCover objective: the Data object is null");
         n = D.numberOfBits();
         if (n != this.listOfSets.size())
            throw new Exception("Impossible to evaluate SetCover objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // preparing variables and iterators
      Data.bitIterator ItData = D.iterator();
      Iterator<Set<Integer>> ItValue = this.listOfSets.iterator();
      HashSet<Integer> remaining = new HashSet<Integer> (this.Universe);

      // computing the union of the set for the selected elements
      int value = 0;
      while (ItData.hasNext() && ItValue.hasNext())
      {
         Set<Integer> subset = ItValue.next();
         if (ItData.next() == 1)
         {
            value = value + subset.size();
            remaining.removeAll(subset);
         }
      }
      ItData.reset();

      // we count the number of selected sets, and we penalize if the universe is not covered
      return (double) (value + remaining.size());
   }

   // toString
   public String toString()
   {
      return "[" + this.getName() + ": " + this.listOfSets.size() + " sets, Universe with " + this.Universe.size() + " elements]";
   }

   // main
   public static void main(String[] args)
   {
      System.out.print("Objective SetCover\n");

      // random instance
      Random R = new Random();
      int n = 10 + R.nextInt(90);
      SetCover obj = new SetCover(n,1,n+1,R);
      System.out.println(obj);
      Data D = obj.solutionSample();
      System.out.println("sample solution : " + D);
      System.out.println("objective function value in sample solution : " + obj.value(D));

      // trying the other constructor
      obj = new SetCover(obj.Universe,obj.listOfSets);
      System.out.println(obj);
   }
}

