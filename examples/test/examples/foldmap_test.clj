(ns examples.foldmap-test
  (:use midje.sweet
        examples.foldmap
        examples.myprotocols))

(facts "composable operations"
       (fact "I can compose a map into another map " 
             (map ((mapping inc) #(* % %)) [0 1 2]) => '(1 4 9))
       (fact "the composer is so simple that there is a core function for it"
             (protomap (comp inc #(* % %)) [1 2 3]) => '(2 5 10))
       (fact "but I want the behaviour injected into the collection itself" 
             (let [coll (composer [0 1 2] (mapping inc))] 
               (protomap #(* % %) coll) => '(1 4 9)))
       ;(fact "and by the way, it doesn't work very well with filter"
       ;      (protomap (comp inc even?) [1 2 3]) => '(1)))
       (fact "that works with filter as well (with a surprise)"
             (protomap inc (composer [1 2 3] (filtering even?))) => '(nil 3 nil)))

(facts "why always reduce? I can have foldmap for map/filter operations"
       (fact "the same logic of fold can operate on maps in parallel"
             (last (foldmap (vec (range 1000)) (/ 1000 4) #(* % %))) => 998001)
       (fact "I can compose multiple maps at leafs processing"
             (foldmap (composer [1 2 3] (filtering even?)) 2 #(* % %)) => 998001))
