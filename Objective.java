
/* Objective interface
 *
 * binMeta project
 *
 * last update: April 14, 2021
 *
 * AM
 */

public interface Objective
{
   // getter for objective name
   public String getName();

   // abstract method "solutionSample" (possibly a random sample)
   public abstract Data solutionSample();

   // abstract method "value" (computation of objective function)
   public abstract double value(Data D);
}

