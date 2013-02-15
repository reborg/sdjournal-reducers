(ns examples.myprotocols-test
  (:use midje.sweet
        examples.myprotocols))

(facts "a protocol based map operation"
       (fact "it just works as map"
             (protomap inc [1 2 3]) => [2 3 4]))
