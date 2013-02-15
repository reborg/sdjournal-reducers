(ns examples.myprotocols)

(defprotocol CollMap
  (coll-map [coll f]))

(extend-protocol CollMap
  nil
  (coll-map
   ([coll f] nil))

  Object
  (coll-map
   ([coll f] (map f coll)))

  clojure.lang.ASeq
  (coll-map
   ([coll f] (map f coll)))

  clojure.lang.PersistentVector
  (coll-map
   ([coll f] (map f coll))))

(defn protomap [f coll]
  "MyMap just mimic the map operation. Collections are expected
  to override the behaviour of the protocol at runtime. Created here
  because clojure.core map is not protocol based."
  (coll-map coll f))
