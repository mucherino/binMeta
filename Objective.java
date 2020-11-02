
/* Objective class (abstract)
 *
 * binMeta project
 *
 * last update: Nov 1, 2020
 *
 * AM
 */

public abstract class Objective
{
   // protected attributes
   protected String name;
   protected Double lastValue = null;

   // getter for objective name
   public String getName()
   {
      return this.name;
   }

   // abstract method "solutionSample" (possibly a random sample)
   public abstract Data solutionSample();

   // abstract method "value"
   public abstract double value(Data D);

   // toString
   public String toString()
   {
      String print = "[" + this.name + ", ";
      if (this.lastValue != null)
         print = print + "last computed value = " + this.lastValue;
      else
         print = print + "objective was not evaluated yet";
      return print + "]";
   }
}

