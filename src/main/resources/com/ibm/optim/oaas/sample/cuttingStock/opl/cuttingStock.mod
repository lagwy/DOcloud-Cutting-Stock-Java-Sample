// This is the Master Problem

tuple Item {
	key string id;
	int width; // Not used in the Master Problem
	int order; // Ordered number of this item
}
{Item} items = ...;

// From the Subproblem
tuple Pattern {
   key int id;
   int cost;
}
{Pattern} patterns = ...;

tuple Slice {
	key string item;
	key int pattern;
	int number; // Number of slices of this item in this pattern
}
{Slice} slices = ...;

dvar float rolls[patterns] in 0..1000000; // Number of rolls cut with each pattern
constraint ctOrder[items];

dexpr float cost = sum( p in patterns ) p.cost * rolls[p];

minimize cost;
  
subject to {
  forall( i in items ) 
    ctOrder[i]: // Cut enough rolls to fill the order amount for each item
      sum( p in patterns, c in slices : p.id==c.pattern && i.id==c.item )
         c.number * rolls[p] >= i.order;
}

// The optimal solution
tuple Objective {
	float value;
}
Objective objective = <cost>;

tuple Usage {
	key int pattern;
	float number; // Number of times the pattern is used
}
{Usage} use = {<p.id, rolls[p]> | p in patterns};


// To the Subproblem
tuple Dual {
	key string item;
	float price;
}
{Dual} duals = {<i.id, dual(ctOrder[i])> | i in items};

execute DISPLAY {
  writeln("use = ", rolls);
  for(var p in patterns) 
    writeln("Use of pattern ", p, " is : ", rolls[p]);
}

