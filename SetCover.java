
/* SetCover class
 *
 * binMeta project
 *
 * coded by Clyde Jannel and Martin Souhil (CNI 2022-23)
 *
 * last update: January 12, 2023
 *
 * AM
 */

import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SetCover implements Objective 
{
   // attributes
   private List<Set<Integer>> setOfSets;
   private Set<Integer> Universe;

   // constructor (from a set and a list of sets)
   public SetCover(Set<Integer> Universe,List<Set<Integer>> setOfSets)
   {
      try
      {
         if (Universe == null) throw new Exception("SetCover: specified Universe set is null");
         if (Universe.isEmpty()) throw new Exception("SetCover: specified Universe set is empty");
         this.Universe = Universe;
         if (setOfSets == null) throw new Exception("SetCover: specified Set containing the sets is null");
         if (setOfSets.isEmpty()) throw new Exception("SetCover: specified Set containing the sets is empty");
         for (Set<Integer> s : setOfSets)
             if (s == null || s.isEmpty()) throw new Exception("SetCover: it looks like one of the sets in setOfSets is null or empty");
         this.setOfSets = setOfSets;
         HashSet<Integer> everything = new HashSet<Integer> (this.Universe);
         for (Set<Integer> s : setOfSets)  everything.removeAll(s);
         if (!everything.isEmpty()) throw new Exception("SetCover: the specified Universe cannot be covered with the given set of sets");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   // constructor (from a int[][] array for the sets, and an int[] array for the Universe)
   public SetCover(int[] Universe,int[][] setOfSets)
   {
      try
      {
         if (Universe == null) throw new Exception("SetCover: int[] array supposed to contain the Universe is null");
         this.Universe = new HashSet<Integer> ();
         for (Integer i : Universe)  this.Universe.add(i);
         if (setOfSets == null) throw new Exception("SetCover: int[][] array for the set of sets is null");
         this.setOfSets = new ArrayList<Set<Integer>> ();
         HashSet<Integer> everything = new HashSet<Integer> (this.Universe);
         for (int[] s : setOfSets)
         {
            if (s == null) throw new Exception("SetCover: int[][] array contains at least one null pointer");
            Set<Integer> set = new HashSet<Integer> ();
            for (Integer i : s)  set.add(i);
            this.setOfSets.add(set);
            everything.removeAll(set);
         }
         if (!everything.isEmpty()) throw new Exception("SetCover: the specified Universe cannot be covered with the given set of sets");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
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
      return new Data(this.setOfSets.size(),0.5);
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
         if (n != this.setOfSets.size())
            throw new Exception("Impossible to evaluate SetCover objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // preparing variables and iterators
      Data.bitIterator ItData = D.iterator();
      Iterator<Set<Integer>> ItValue = this.setOfSets.iterator();

      // computing the union of the set for the selected elements
      HashSet<Integer> union = new HashSet<Integer> ();
      while (ItData.hasNext() && ItValue.hasNext())
      {
         if (ItData.next() == 1)
            union.addAll(ItValue.next());
         else
            ItValue.next();
      }
      ItData.reset();

      // a penalty on the objective function is added when the set of sets does not cover the Universe
      if (!this.Universe.equals(union))
         return (double) (D.numberOfBits() + D.numberOfOnes());

      // otherwise we simply count the number of selected sets
      return (double) D.numberOfOnes();
   }

   // toString
   public String toString()
   {
      return "[" + this.getName() + ": " + this.setOfSets.size() + " sets, Universe with " + this.Universe.size() + " elements]";
   }

   // main
   public static void main(String[] args)
   {
      System.out.print("Objective SetCover");

      // this instance covers the Univers with its 4 sets; the other 3 repeat items
      int[] set1 = new int[]{1,2,3};
      int[] set2 = new int[]{4,5,6,7};
      int[] set3 = new int[]{8,9};
      int[] set4 = new int[]{10,11,12};
      int[] set5 = new int[]{2,5,9};
      int[] set6 = new int[]{8,10};
      int[] set7 = new int[]{4,11};
      int[][] setOfSets = new int[][]{set1,set2,set3,set4,set5,set6,set7};
      int[] Universe = new int[]{1,2,3,4,5,6,7,8,9,10,11,12};

      // initializing SetCover instance
      SetCover obj = new SetCover(Universe,setOfSets);
      System.out.println(obj);

      // trying the other constructor
      obj = new SetCover(obj.Universe,obj.setOfSets);

      // trying to evaluate in a Random solution
      Data solution = obj.solutionSample();
      System.out.println("Random solution (it is unlikely it will be covering the Universe) : " + solution);
      System.out.println("Value in random solution : " + obj.value(solution));

      // constructing a solution which covers the Universe
      solution = new Data(new Data(4,true),new Data(3,0.5));
      System.out.println("Covering solution (but probably not optimal) : " + solution);
      System.out.println("Value in covering non-optimal solution : " + obj.value(solution));

      // constructing the known optimal solution
      solution = new Data(new Data(4,true),new Data(3,false));
      System.out.println("Optimal solution : " + solution);
      System.out.println("Value in optimal solution : " + obj.value(solution));
   }
}

