
/* Objective interface
 *
 * binMeta project
 *
 * last update: April 16, 2023
 *
 * AM
 */

public interface Objective
{
   // getter for objective name
   public String getName();

   // abstract method "solutionSample" (possibly a random sample)
   public abstract Data solutionSample();

   // abstract method "upperBound" (gives an estimation of an upper bound on the objective function value)
   public abstract Double upperBound();

   // abstract method "value" (computation of objective function)
   public abstract double value(Data D);
}

