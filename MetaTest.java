
/* MetaTest class
 *
 * binMeta project
 *
 * last update: April 26, 2021
 *
 * AM
 */

public class MetaTest
{
   // test parameters
   static int NPARAM = 3;  // number of parameter sets
   static int TIMEMAX = 1000;  // max time for meta-heuristic methods
   static String [] objectives = {"BitCounter","ColorPartition","Fermat","SubsetSum","NumberPartition","Knapsack"};
   static String [] methods = {"LocalOpt","RandomWalk","WolfSearch"};

   // Main
   public static void main(String[] args)
   {
      // instance definition
      for (int iparam = 0; iparam < NPARAM; iparam++)
      {
         System.out.println("Parameter set " + (iparam + 1) + "\n");

         // selection of the objective
         for (int iobj = 0; iobj < objectives.length; iobj++)
         {
            Objective obj = null;
            if (objectives[iobj].equals("BitCounter"))
            {
               if (iparam == 0)
                  obj = new BitCounter(10);
               else if (iparam == 1)
                  obj = new BitCounter(100);
               else
                  obj = new BitCounter(1000);
            }
            else if (objectives[iobj].equals("ColorPartition"))
            {
               if (iparam == 0)
                  obj = new ColorPartition(10,6);
               else if (iparam == 1)
                  obj = new ColorPartition(20,10);
               else
                  obj = new ColorPartition(50,20);
            }
            else if (objectives[iobj].equals("Fermat"))
            {
               if (iparam == 0)
                  obj = new Fermat(2,10);
               else if (iparam == 1)
                  obj = new Fermat(3,15);
               else
                  obj = new Fermat(4,20);
            }
            else if (objectives[iobj].equals("SubsetSum"))
            {
               if (iparam == 0)
                  obj = SubsetSum.instance01();
               else if (iparam == 1)
                  obj = SubsetSum.instance02();
               else
                  obj = SubsetSum.instance03();
            }
            else if (objectives[iobj].equals("NumberPartition"))
            {
               if (iparam == 0)
                  obj = NumberPartition.instance01();
               else if (iparam == 1)
                  obj = NumberPartition.instance02();
               else
                  obj = NumberPartition.instance03();
            }
            else if (objectives[iobj].equals("Knapsack"))
            {
               if (iparam == 0)
                  obj = Knapsack.instance01();
               else if (iparam == 1)
                  obj = Knapsack.instance02();
               else
                  obj = Knapsack.instance03();
            }

            System.out.println(obj);

            if (obj != null)
            {
               Data D = obj.solutionSample();

               // solving the instance with the meta-heuristic methods
               for (int imeth = 0; imeth < methods.length; imeth++)
               {
                  binMeta mh = null;
                  int size = D.numberOfBits();
                  System.out.println("-> running " + methods[imeth] + " (maxtime " + TIMEMAX + "ms)");
                  if (methods[imeth].equals("LocalOpt"))
                     mh = new LocalOpt(D,obj,TIMEMAX);
                  else if (methods[imeth].equals("RandomWalk"))
                     mh = new RandomWalk(D,obj,TIMEMAX);
                  else if (methods[imeth].equals("WolfSearch"))
                     mh = new WolfSearch(obj,100,100,2,(int) Math.floor(0.7*size),0.1,0.4,TIMEMAX,TIMEMAX);
                  mh.optimize();
                  System.out.println(mh);
                  if (size <= 100)
                     System.out.println("solution : " + mh.getSolution());
                  else
                     System.out.println("solution omitted (number of bits is " + size + ")");
               }
            }
            System.out.println();
         }
      }
   }
}


