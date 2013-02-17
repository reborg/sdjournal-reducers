(ns examples.word-count
  (:require [clojure.core.reducers :as r]))

(defn lorem [howmany]
  (apply str (repeat howmany "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas convallis felis gravida dolor rutrum posuere. Donec dignissim ultricies augue, sit amet mollis nibh pulvinar et. Nunc metus risus, auctor in vulputate quis, lobortis sit amet ipsum. Nam molestie lectus vehicula ligula tempor vitae semper turpis suscipit. In nec massa orci, ut ultrices purus. Praesent quis dolor ligula. Curabitur enim sapien, porta vel ultricies eu, rhoncus a dolor. Morbi malesuada dolor nec velit consectetur cursus. Etiam luctus feugiat velit sed auctor. Cras leo est, vestibulum vel placerat a, dapibus a ante.")))


(defn count-words [text]
  (sort-by last > 
    (vec (reduce
       (fn [memo word]
         (assoc memo word (inc (get memo word 0))))
       {}
       (map #(.toLowerCase %) (into [] (re-seq #"\w+" text)))))))

(defn p-count-words [text]
  (sort-by last > 
    (vec (r/fold
      (r/monoid (partial merge-with +) hash-map)
      (fn [memo word]
        (assoc memo word (inc (get memo word 0))))
      (r/map #(.toLowerCase %) (into [] (re-seq #"\w+" text)))))))
