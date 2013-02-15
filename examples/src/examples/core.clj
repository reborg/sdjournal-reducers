(ns examples.core)

(defn alter-operand-and-invoke 
  "A funtion that takes a binary function and invokes
  that function over the two operands altering the second first"
  [binary-f first-op altering-f second-op]
  (binary-f first-op (altering-f second-op)))

(defn mapping [mapping-function]
  "needs to be invoked with a binary and unary functions
  and it will apply the unary transformation to the second operand
  of the binary operation. When used with reduce, it is altering
  the second operand of the associative function."
  (fn [reducing-function]
    (fn [accumulator next-element]
      (reducing-function accumulator (mapping-function next-element)))))

(defn explicit-mapping [mapping-function]
  "this second version just makes explicit use of a function created outside.
  No real change, just showing the altered binary function can also be standolone
  and it is returned as an anonymous function to be suitable for reduce."
  (fn [reducing-function]
    (fn [accumulator next-element]
      (alter-operand-and-invoke reducing-function accumulator mapping-function next-element))))

(defn add-custom-reduce
  [coll xf]
     (reify
      clojure.core.protocols/CollReduce
      (coll-reduce [this f1]
                   (clojure.core.protocols/coll-reduce this f1 (f1)))
      (coll-reduce [_ f1 init]
                   (clojure.core.protocols/coll-reduce coll (xf f1) init))))
