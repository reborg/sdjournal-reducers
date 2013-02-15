(ns examples.foldmap
  (:use examples.myprotocols))

(defn mapping [f]
  (fn [f1]
    (fn [input]
      (f1 (f input)))))

(defn filtering [pred]
  (fn [f1]
    (fn [input]
      (if (pred input)
        (f1 input)
        nil))))

(defn composer
  ([coll xf]
  "a collection enhancing composer that invokes a new
   transforming behaviour to a collection."
  (reify
    examples.myprotocols/CollMap
    (coll-map [_ f1]
      (examples.myprotocols/coll-map coll (xf f1))))))

(def pool (delay (java.util.concurrent.ForkJoinPool.)))

(defn fjtask [^Callable f]
  (java.util.concurrent.ForkJoinTask/adapt f))

(defn fjinvoke [f]
  (if (java.util.concurrent.ForkJoinTask/inForkJoinPool)
    (f)
    (.invoke ^java.util.concurrent.ForkJoinPool @pool ^java.util.concurrent.ForkJoinTask (fjtask f))))

(defn fjfork [task] (.fork ^java.util.concurrent.ForkJoinTask task))

(defn fjjoin [task] (.join ^java.util.concurrent.ForkJoinTask task))

(defn foldmap [vect split-size mapf]
  "foldmap is like foldvec from reducers.clj but it forks a map operation.
  There is no need for a combinef, since the map will just concatenate."
  (cond
    (empty? vect) vect
    (<= (count vect) split-size) (protomap mapf vect)
    :else
    (let [split (quot (count vect) 2)
          v1 (subvec vect 0 split)
          v2 (subvec vect split (count vect))
          fc (fn [child] #(foldmap child split-size mapf))]
      (fjinvoke
        #(let [f1 (fc v1)
               t2 (fjtask (fc v2))]
           (fjfork t2)
           (vec (concat (f1) (fjjoin t2))))))))
