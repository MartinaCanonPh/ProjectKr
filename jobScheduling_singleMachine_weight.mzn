int: njobs;
set of int:indices=1..njobs;
array[indices] of int: p;%input tempi di processamento
array[indices] of int: w;%input dei pesi

constraint assert(njobs > 0, "ValueError: njobs must be non-negative, you have to schedule at least 1 job");
constraint assert(forall(i in indices)(p[i]>0),"ValueError: processing times must be at least 1");
constraint assert(forall(i in indices)(w[i]>0),"ValueError: weights must be at least one");

array[indices] of var int: c;% tempi di completamento
constraint c[1]=p[1];
set of int: range=2..njobs;
constraint forall(i in range)(c[i]=c[i-1]+p[i]);

solve minimize sum(i in indices)(c[i]*w[i]);
 
output[ "completion time per jobs = \(c)\n",
"total completion time minimized = \(sum(i in indices)(c[i]*w[i]))\n"];