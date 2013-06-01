(ns columbus-clojure.demo.reducers.core-test
  (:require [clojure.test :refer :all]
            [columbus-clojure.demo.reducers.core :refer :all]))

(def small-number 1000)

; There might be a better way but this works
(def small-lazy-range (lazy-seq (into [] (range (inc small-number)))))
(def small-non-lazy-range (into [] (range (inc small-number))))

(defn gaussian-sum
  "Figure out what the sum should be."
  [nu]
  (-> nu
      (+ 1)
      (* nu)
      (/ 2)))

(deftest sum-reducer-versions-equivalent
  (let [output (gaussian-sum small-number)]
    (are [sum-reducer coll] (= output (sum-reducer coll))
         reducer-sum-reducer small-non-lazy-range
         reducer-sum-reducer small-lazy-range
         core-sum-reducer small-non-lazy-range
         core-sum-reducer small-lazy-range)))

(deftest multi-reducer-versions-equivalent
  (let [output (core-multi-reducer small-lazy-range)]
    (is (> output 0))
    (are [multi-reducer coll] (= output (multi-reducer coll))
         fs-reducer-multi-reducer small-non-lazy-range
         fs-reducer-multi-reducer small-lazy-range
         reducer-multi-reducer small-non-lazy-range
         reducer-multi-reducer small-lazy-range
         core-multi-reducer small-lazy-range)))
