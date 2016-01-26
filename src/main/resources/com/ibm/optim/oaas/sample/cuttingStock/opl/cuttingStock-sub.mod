// This is the Subproblem

tuple Parameters {
	int rollWidth;
	int nbPatterns; // The number of patterns generated so far
}
Parameters parameter = ...;

tuple Item {
	key string id;
	int width;
	int order; // Not used in the subproblem
}
{Item} items = ...;

// From the Master Problem
tuple Dual {
	key string item;
	float price;
}
{Dual} duals = ...;

dvar int slice[items] in 0..100000; // Number of slices of this item in the new pattern
dexpr float reducedCost = 
  1 - sum(i in items, d in duals : i.id==d.item) d.price * slice[i];

minimize reducedCost;
subject to {
  ctFill:
    sum(i in items) i.width * slice[i] <= parameter.rollWidth;
}

// To the Master Problem
tuple Objective {
	float value;
}
Objective objective = <reducedCost>;

// Assign an ID to the new pattern: parameter.nbPatterns+1
tuple Pattern {
   key int id;
   int cost;
}
Pattern newPattern = <parameter.nbPatterns+1, 1>;

tuple Slice {
	key string item;
	key int pattern;
	int number; // Number of slices of this item in this pattern
}
{Slice} slices = {<i.id, parameter.nbPatterns+1, slice[i]> | i in items};

