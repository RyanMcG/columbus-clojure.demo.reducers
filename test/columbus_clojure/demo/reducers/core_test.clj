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
    (doseq [reducer-version *reducer-versions*
            coll [small-lazy-range small-non-lazy-range]]
      (is (= output (reducer-version coll))))))
