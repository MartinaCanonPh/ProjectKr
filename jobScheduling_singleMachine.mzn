int: njobs;
set of int:indices=1..njobs;
array[indices] of int: p;

constraint assert(njobs > 0, "ValueError: njobs must be non-negative, you have to schedule at least 1 job");
constraint assert(forall(i in indices)(p[i]>0),"ValueError: processing times must be at least 1");


array[indices] of var int: c;% tempi di completamento
constraint c[1]=p[1];
set of int: range=2..njobs;
constraint forall(i in range)(c[i]=c[i-1]+p[i]);

solve minimize sum(c);
 
output[ "completion time per jobs = \(c)\n",
"total completion time minimized =\(sum(c))\n"];