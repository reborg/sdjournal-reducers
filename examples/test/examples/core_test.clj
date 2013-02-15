(ns examples.core-test
  (:use midje.sweet
        examples.core)
  (:require [clojure.core.reducers :as r]))

(facts "a function that does something to one of the operands
       before invoking a function on them"
       (fact "summing a number to another which is incremented first"
             (alter-operand-and-invoke + 10 inc 0) => 11)
             (alter-operand-and-invoke * 5 inc 1) => 10)


(facts "an explicit mapping"
       (fact "now passing a special reducing function that increments each element before adding"
             (reduce ((explicit-mapping inc) +) 0 [1 2 3 4]) => 
               (+ (+ (+ (+ 0 (inc 1)) (inc 2)) (inc 3)) (inc 4))))

(facts "reduce using a reducing transformer"
       (fact "standard reduce using + as reducing function"
             (reduce + 0 [1 2 3 4]) => 
             (+ (+ (+ (+ 0 1) 2) 3) 4))
       (fact "now passing a special reducing function that increments each element before adding"
             (reduce ((mapping inc) +) 0 [1 2 3 4]) => 
               (+ (+ (+ (+ 0 (inc 1)) (inc 2)) (inc 3)) (inc 4))))

(facts "adding 'reducing' instructions to a collection on the fly"
       (fact "will use standard vector based reduce implemenetation"
             (reduce + 0 [1 2 3 4]) => 10)
       (fact "will use the special override over collections which does nothing"
             (reduce + 0 (add-custom-reduce [1 2 3 4] (mapping identity))) => 10)
       (fact "and now the custom implementation of reduce is passed an increment modifier"
             (reduce + 0 (add-custom-reduce [1 2 3 4] (mapping inc))) => 14))

(facts "fold"
       (fact "has the same semantic as reduce when combine is the same as reduce"
             (reduce + (map inc [1 2 3]))   => 9
             (reduce + (r/map inc [1 2 3])) => 9
             (r/fold + (r/map inc [1 2 3])) => 9
       (fact "works fine with normal map"
             (r/fold + (map inc [1 2 3]))   => 9)))
