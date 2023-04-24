
/* MetaTest class
 *
 * binMeta project
 *
 * This version of MetaTest was used for the computational experiments presented in:
 *    S.B. Hengeveld, A. Mucherino,
 *    Variable Neighborhood Search in Hamming Space, 2023.
 *
 * The output is always printed in LaTex format.
 * The LaTex table indicates how close the meta-heuristic search was able to approach
 * the known upper bound. When the meta-heuristic search finds a solution that is
 * better than the upper bound, the printed value will be negative. When the upper 
 * bound is not known, then it's the objective function value that is reported 
 * (a star* shows that this is the case in the table).
 *
 * last update: April 24, 2023
 *
 * AM
 */

import java.lang.reflect.*;
import java.util.Random;

public class MetaTest
{
   // test parameters
   static int NTESTS = 9;  // number of tests performed per Objective and per meta-heuristics
   static int TIMEMAX = 1000;  // max time for meta-heuristic methods
   static boolean SHOWTIME = false;

   // objectives for experiments reported in the table in the article given above
   static String[] objectives = {"NumberPartition","SubsetSum","Knapsack"};
   static int[] problemSize = {600,600,600};
   static int[] problemStep = {100,100,100};

   // objectives for other experiments not reported in the article
//   static String[] objectives = {"BitCounter","ColorPartition","Pi","Fermat","SetCover"};
//   static int[] problemSize = {1000,100,100,5,100};
//   static int[] problemStep = {200,100,100,5,10};

   // meta-heuristics
   static String[] methods = {"LocalOpt","RandomWalk","MultiStart","WolfSearch","VariableNeighbourhoodSearch"};
   static String[] vnsTypes = {"basic","cyclic","pipe","jumping","skewed","nested"};

   // Main
   public static void main(String[] args)
   {
      // using Java reflection to get objectives and instances
      Class<?> obj = null;

      // first two lines of LaTex table
      int number = 1;
      if (SHOWTIME)  number = 2;
      System.out.print("\\multicolumn{3}{c}{instance}");
      String second = "objective & name & size";
      for (int imeth = 0; imeth < methods.length; imeth++)
      {
         if (!methods[imeth].equals("VariableNeighbourhoodSearch"))
         {
            System.out.print(" & \\multicolumn{" + number + "}{c}{" + methods[imeth] + "}");
            second = second + " & value"; 
            if (SHOWTIME)  second = second + " & time";
         }
         else
         {
            for (int itype = 0; itype < vnsTypes.length; itype++)
            {
               System.out.print(" & \\multicolumn{" + number + "}{c}{" + methods[imeth] + "(" + vnsTypes[itype] + ")}");
               second = second + " & value";
               if (SHOWTIME)  second = second + " & time";
            }
         }
      }
      System.out.println(" \\\\");
      System.out.println(second + " \\\\");

      // selection of the objective
      for (int iobj = 0; iobj < objectives.length; iobj++)
      {
         try
         {
            obj = Class.forName(objectives[iobj]);
         }
         catch (Exception e)
         {
            System.out.println("Objective " + objectives[iobj] + " not found; skipping...");
            continue;
         }

         // getting the constructor for random instance generation
         //
         // Two different lists of arguments are possible:
         // - one for the toy objectives, 
         // - another for the ones related to classical combinatorial problems.
         Constructor<?> constructor = null;
         int nargs = 0;
         try
         {
            constructor = obj.getConstructor(int.class);
            nargs = 1;
         }
         catch (Exception e)
         {
            try
            {
               constructor = obj.getConstructor(int.class,int.class,int.class,Random.class);
               nargs = 4;
            }
            catch (Exception f)
            {
               System.out.println("Something went wrong with the constructor of Objective " + objectives[iobj] + "; skipping...");
               continue;
            }
         }

         // performing tests
         Random R = new Random(999);
         Object objobj = null;
         int n = problemSize[iobj];
         for (int itest = 0; itest < NTESTS; itest++)
         {
            try
            {
               if (nargs == 1)
                  objobj = constructor.newInstance(n);
               else
                  objobj = constructor.newInstance(n,1,n+1,R);
            }
            catch (Exception e)
            {
               System.out.println("Something went wrong when invoking constructor of Objective " + objectives[iobj] + "; skipping...");
               continue;
            }
            if (objobj == null)  continue;
            Objective current = (Objective) objobj;
            Data D = current.solutionSample();
            int size = D.numberOfBits();

            // printing main info in the LaTex table
            System.out.print(objectives[iobj] + " & " + (itest+1) + " & " + size);

            // solving the instance with the meta-heuristic methods
            Class<?> meta = null;
            Constructor<?> metaConstructor = null;
            Object metaObject = null;
            for (int imeth = 0; imeth < methods.length; imeth++)
            {
               try
               {
                  meta = Class.forName(methods[imeth]);
                  metaConstructor = meta.getConstructor(Data.class,Objective.class,long.class);
                  metaObject = metaConstructor.newInstance(D,current,TIMEMAX);
               }
               catch (Exception e)
               {
                  System.out.println("Unknown meta-heuristic search " + methods[imeth] + "; skipping...");
                  continue;
               }
               if (meta == null)  continue;
               binMeta mh = (binMeta) metaObject;

               // running!
               int itype = 0;
               do
               {
                  long startime = System.currentTimeMillis();
                  if (!methods[imeth].equals("VariableNeighbourhoodSearch"))
                  {
                     itype = -1;
                     mh.optimize();
                  }
                  else
                  {
                     VariableNeighbourhoodSearch vns = (VariableNeighbourhoodSearch) mh;
                     vns.setType(vnsTypes[itype]);
                     vns.optimize();
                  }
                  long time = System.currentTimeMillis() - startime;

                  // printing the results
                  Double ub = current.upperBound();
                  double result = current.value(mh.getSolution());
                  if (ub != null)  result = result - ub;
                  System.out.print(" & " + result);
                  if (ub == null)  System.out.print("*");
                  if (SHOWTIME)  System.out.print(" & " + time);

                  // more tests?
                  itype++;
               }
               while (itype != 0 && itype < vnsTypes.length);
            }
            System.out.println(" \\\\");
            n = n + problemStep[iobj];
         }
      }
   }
}

