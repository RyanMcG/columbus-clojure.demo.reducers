(ns columbus-clojure.demo.reducers.core-test
  (:require [clojure.test :refer :all]
            [columbus-clojure.demo.reducers.core :refer :all]))

(def small-number 1000)

; There might be a better way but this works
(def small-lazy-range (lazy-seq (into [] (range (inc small-number)))))
(def small-non-lazy-range (into [] (range (inc small-number))))

(deftest multi-reducer-versions-equivalent
  (let [output (core-multi-reducer small-lazy-range)]
    (is (> output 0))
    (are [multi-reducer coll] (= output (multi-reducer coll))
         fs-reducer-multi-reducer small-non-lazy-range
         fs-reducer-multi-reducer small-lazy-range
         reducer-multi-reducer small-non-lazy-range
         reducer-multi-reducer small-lazy-range
         core-multi-reducer small-lazy-range)))
