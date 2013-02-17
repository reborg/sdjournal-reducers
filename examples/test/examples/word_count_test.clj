(ns examples.word-count-test
  (:use midje.sweet
        examples.word-count)
  (:require [clojure.core.reducers :as r]))

(facts "plain word count"
       (fact "it should return the most recurring word first"
             (time (first (count-words (lorem 10000)))) => ["dolor" 50000]))

(facts "parallel word count"
       (fact "should return just the same as the normal word count"
             (p-count-words (lorem 1)) => (count-words (lorem 1))))

(facts "pushing the limits"
       (fact "many more words to count"
             (time (first (p-count-words (lorem (* 1 10000))))) => ["dolor" (* 5 10000)]))
