DISSECTING REDUCERS

# Introduction
Reducers is one of the many new features introduced by Clojure 1.5 (still release candidate at the time of this writing). Technically Reducers are just one side of the feature, the other being the function "fold" and the foldable protocol which makes good use of them. In this article I'd like to illustrate Reducers starting from the problem of designing code that runs in parallel over multiple cores. 

Our goal is to find a way to process sequences in parallel without the need of new collection types or a different algorithm for each collection type (fold). We'll work our way backward to demonstrate the advantages of using Reducers with fold for parallel computation instead of plain old mapping functions (like map or filter for example).

# thread parallelism with pmap
Parallelism for sequences at the thread level is present in Clojure as pmap http://clojuredocs.org/clojure_core/clojure.core/pmap. Pmap is a simple approach to parallelism which works well for costly functions applied to relatively small sized dataset but is problematic otherwise (see http://stackoverflow.com/questions/2103599/better-alternative-to-pmap-in-clojure-for-parallelizing-moderately-inexpensive-f for example). Pmap is based on futures, threads that take one element from the collection and goes away for a while performing the job and come back at some later time. An approach like this one would be more problematic to implement something like reduce where partial results are expected to be combined. Maybe this is the reason there isn't a preduce implemented in such a way.

# fork-join paralellism with preduce
If you have a look into the now deprecated src/clj/clojure/parallel.clj from current Clojure sources, you'll see preduce and other parallel functions. The implementation is based on the fork-join model provided by the jsr-166 which is now part of JDK 7 (http://www.oracle.com/technetwork/articles/java/fork-join-422606.html). Clojure sequences need to be translated into ParallelArrays before being processed by fork-join and although a mapper function is present (called par) this design doesn't work natively with all Clojure sequences.

# fork-join parallelism with pvmap
Around 2009 some work started in a parallel branch called "par" to allow parallelism based on fork-join . That branch is still accessible on [link] github. 

With this approach, multiple versions of the same algorithm exist to deal with parallelism, as well as different data structures, requiring developers an explicit commit to parallelism in their code. 

