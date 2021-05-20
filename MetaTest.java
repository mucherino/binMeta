
/* MetaTest class
 *
 * binMeta project
 *
 * last update: May 20, 2021
 *
 * AM
 */

import java.lang.reflect.*;

public class MetaTest
{
   // test parameters
   static boolean LaTex = true;
   static int TIMEMAX = 1000;  // max time for meta-heuristic methods
   static String [] objectives = {"BitCounter","ColorPartition","Pi","Fermat","SubsetSum","NumberPartition","Knapsack","WolfSearch"};
   static String [] instances = {"instance01","instance02","instance03"};
   static String [] methods = {"LocalOpt","RandomWalk","WolfSearch"}; 

   // Main
   public static void main(String[] args)
   {
      // using Java reflection to get objectives and instances
      Class<?> obj = null;

      // first two lines of LaTex table (optional)
      if (LaTex)
      {
         System.out.print("\\multicolumn{3}{c}{instance}");
         String second = "objective & name & size";
         for (int imeth = 0; imeth < methods.length; imeth++)
         {
            System.out.print(" & \\multicolumn{2}{c}{" + methods[imeth] + "}");
            second = second + " & value & time";
         }
         System.out.println(" \\\\");
         System.out.println(second + " \\\\");
      }

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

         // selection of the three instances
         Objective current = null;
         for (int iinst = 0; iinst < instances.length; iinst++)
         {
            try
            {
               Method instanceGen = obj.getMethod(instances[iinst]);
               Object result = instanceGen.invoke(null);
               current = (Objective) result;
            }
            catch (Exception e)
            {
               System.out.println("Instance " + instances[iinst] + " not found in " + objectives[iobj] + "; skipping...");
               continue;
            }
            if (current == null)  continue;
            Data D = current.solutionSample();
            int size = D.numberOfBits();

            // printing info on the screen (may be LaTex table format)
            if (LaTex)
               System.out.print(objectives[iobj] + " & " + instances[iinst] + " & " + size);
            else
               System.out.println(current);

            // solving the instance with the meta-heuristic methods
            for (int imeth = 0; imeth < methods.length; imeth++)
            {
               binMeta mh = null;
               if (!LaTex)  System.out.println("-> running " + methods[imeth] + " (maxtime " + TIMEMAX + "ms)");
               if (methods[imeth].equals("LocalOpt"))
                  mh = new LocalOpt(D,current,TIMEMAX);
               else if (methods[imeth].equals("RandomWalk"))
                  mh = new RandomWalk(D,current,TIMEMAX);
               else if (methods[imeth].equals("WolfSearch"))
                  mh = new WolfSearch(current,100,100,2,(int) Math.floor(0.7*size),0.1,0.4,TIMEMAX);
               else
               {
                  System.out.println("Unknown meta-heuristic search " + methods[imeth]);
                  continue;
               }

               long startime = System.currentTimeMillis();
               mh.optimize();
               long time = System.currentTimeMillis() - startime;
               if (LaTex)
               {
                  System.out.print(" & " + current.value(mh.getSolution()) + " & " + time);
               }
               else
               {
                  System.out.println(mh);
                  if (size <= 100)
                  {
                     if (current instanceof WolfSearch)
                     {
                        WolfSearch ws = (WolfSearch) current;
                        System.out.println("optimal parameters : " + ws.parametersToString(mh.getSolution()));
                     }
                     else
                     {
                        System.out.println("solution : " + mh.getSolution());
                     }
                  }
                  else
                  {
                     System.out.println("solution omitted (number of bits is " + size + ")");
                  }
               }
            }
            if (LaTex)
               System.out.println(" \\\\");
            else
               System.out.println();
         }
      }
   }
}

