
/* binMeta class
 *
 * binMeta project
 *
 * last update: Dec 3, 2020
 *
 * AM
 */

public abstract class binMeta
{
   protected String metaName;  // meta-heuristic name
   protected Objective obj;    // objective function
   protected Double objValue;  // objective function value in solution
   protected Data solution;    // Data object containing solution
   protected long maxTime;     // maximum execution time (ms)
   private Long startTime = null;  // for method "monitor"

   // getName
   public String getName()
   {
      return this.metaName;
   }

   // getObj
   public Objective getObj()
   {
      return this.obj;
   }

   // getObjVal (in the current solution)
   public Double getObjVal()
   {
      return this.objValue;
   }

   // getSolution
   public Data getSolution()
   {
      return this.solution;
   }

   // abstract method "optimize" (it runs the meta-heuristic method)
   public abstract void optimize();

   // monitor
   public void monitor()
   {
      if (this.startTime == null)
      {
         System.out.print(this.metaName + " === Getting started");
         this.startTime = System.currentTimeMillis();
      }
      else
      {
         System.out.print("\r");
         long time = System.currentTimeMillis() - this.startTime;
         System.out.print(this.metaName + " === Best solution " + this.objValue + "; time " + time/1000.0 + "ms");
      }
   }

   // reset the monitor
   public void reset()
   {
      this.startTime = null;
   }

   // toString
   public String toString()
   {
      String print = "[" + this.metaName;
      if (this.solution != null)
      {
         if (this.objValue == null)  this.objValue = this.obj.value(this.solution);
         print = print + ": objective " + this.obj.getName() + " has value " + this.objValue + " in current solution";
      }
      else
      {
         print = print + ": with objective " + this.obj.getName() + ", no known solutions up to now";
      }
      return print + "]";
   }
}

