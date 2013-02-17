DISSECTING REDUCERS

# Introduction
Reducers is one of the many new features introduced by Clojure 1.5 (still release candidate at the time of this writing). Technically Reducers are just one side of the functionality that was introduced, the other being the function "fold" and the foldable protocol. In this article I'd like to introduce Reducers starting from the problem of designing code that runs in parallel over multiple cores. We'll work our way backward to demonstrate the peculiarities of Reducers in that context instead of the plain old "map" functions (like map or filter for example).

# thread parallelism with pmap
Parallelism for sequences at the thread level is present since Clojure 1.0 as pmap http://clojuredocs.org/clojure_core/clojure.core/pmap. Pmap is a simple approach to parallelism which works well for costly functions applied to relatively small sized dataset. Pmap approach is problematic otherwise (see for example http://stackoverflow.com/questions/2103599/better-alternative-to-pmap-in-clojure-for-parallelizing-moderately-inexpensive-f). Pmap implementation is based on futures, with threads that take one element from the collection and goes away for a while performing the job and come back at some later time. With a thread-based approach like this one, it would be more problematic to implement something like reduce where partial results are expected to be combined.

# fork-join paralellism with preduce
If you have a look into the now deprecated https://github.com/clojure/clojure/blob/master/src/clj/clojure/parallel.clj from Clojure sources, you'll see "preduce" and other parallel functions. Instead of working on top of threads, preduce is based on the fork-join model provided by jsr-166, now part of JDK 7 (http://www.oracle.com/technetwork/articles/java/fork-join-422606.html). The implementation of preduce translates a clojure vector into a ParallelArray, a jsr-166 version of a Java array suitable for parallel computation. A mapper function is present (called par) to translate from Clojure sequences into a parallel array. But this design doesn't work natively with Clojure sequences.

# fork-join parallelism with pvmap
Around 2009 some work started in a parallel branch called "par" to experiment on a fork-join solution that works on top of native Clojure sequences. That branch and the main work it introduced is still accessible from https://github.com/clojure/clojure/blob/par/src/clj/clojure/par.clj . The main fork-join mechanism is the same but now the implementation is based on PersistentVectors instead of jsr-166 ParallelArray demonstrating a way to avoid to implement (or reuse) specific parallel data types. Note however that this model still implies specific implementations of map/reduce and potentially other higher order functions and that a specific Clojure collection (PersistentVetor) is required.

# a better approach to parallelism
We just described three approaches used in Clojure to parallelize computation of elements in a collection. Some of them are available, some deprecated, some never made it to trunk. They all suffer some limitations:

- They require specific re-implementation of their sequential counterparts (pmap, preduce, pvmap, pvreduce and so on)
- They operate on data types specifically prepared for paralellism or on a restricted set of native sequences (ParallelArray, PersistentVector)

The first is a maintenance nightmare from the point of view of the language implementor, with changes to the sequential algorithm to be reflected on the parallel counterpart. It also generates problems for end users of the language in case they already implemented a Clojure application and they are interested in making some part of them parallel without to much hassle. The second limitation impacts even more on the end user side, especially for existing applications which are already based on native Clojure collections. What would be really nice to have is an approach that works out of the box with your current collections and at the same time doesn't become a nightmare for Clojure developers of the standard library.

# enter "fold"
Fold is a generic function
