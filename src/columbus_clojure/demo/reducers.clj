(ns columbus-clojure.demo.reducers
  (:require [clojure.core.reducers :as r]
            [criterium.core :refer :all]))

(def big-number 10000000)

; There might be a better way but this works
(def big-lazy-range (lazy-seq (into [] (range big-number))))
(def big-non-lazy-range (into [] (range big-number)))

(defn sum-reducer [memo v]
  ;(Thread/sleep 10)
  (+ memo v))

(defn core-sum-reducer [coll]
  (reduce sum-reducer coll))

(defn reducer-sum-reducer [coll]
  (r/reduce sum-reducer 0 coll))

(defn -main
  "Just a main function"
  []
  (doseq [reduce-version [reducer-sum-reducer core-sum-reducer]
          coll [big-lazy-range big-non-lazy-range]]
    (with-progress-reporting
      (quick-bench (core-sum-reducer coll)))))
