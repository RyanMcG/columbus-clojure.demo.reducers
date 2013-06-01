(ns columbus-clojure.demo.reducers.core
  (:require [clojure.core.reducers :as r]
            [clojure.pprint :as pp]
            [foldable-seq.core :refer [foldable-seq]]
            (columbus-clojure.demo.reducers [benchmark :refer [mean-bench]])))

(def big-number 10000)
(def multiplier-count 100)

; There might be a better way but this works
(def big-lazy-range
  "A lazy version of a range to big-numer"
  (lazy-seq (into [] (range big-number))))
(def big-non-lazy-range
  "A non-lazy version of a range to big-numer"
  (into [] (range big-number)))

(defn core-sum-reducer
  "C +"
  [coll]
  (reduce + coll))

(defn reducer-sum-reducer
  "R +"
  [coll]
  (r/reduce + coll))

(defmacro reduce-multiplier [map-version coll rng]
  `(reduce (fn [reducible# multiplier#]
             (~map-version (partial * (mod multiplier# 5)) reducible#))
           ~coll
           ~rng))

(defn core-multi-reducer
  "C *"
  [coll]
  (->> (range multiplier-count)
       (reduce-multiplier map coll)
       (reduce +)))

(defn reducer-multi-reducer
  "R *"
  [coll]
  (->> (range multiplier-count)
       (reduce-multiplier r/map coll)
       (r/fold +)))


(defn fs-reducer-multi-reducer
  "FS *"
  [coll]
  (->> (range multiplier-count)
       foldable-seq
       (reduce-multiplier r/map coll)
       (r/fold +)))

(def historgram-output
  "Create a histogram for eveything"
  []
  (doseq [reducer-version [#'reducer-sum-reducer
                           #'core-sum-reducer
                           #'fs-reducer-multi-reducer
                           #'reducer-multi-reducer
                           #'core-multi-reducer]
          coll [big-lazy-range lazy-range]]
    (let [reducer-doc (:doc (meta reducer-version))
          coll-doc (:doc (meta coll))]
      )))

(defn table-output
  "Output results as a table"
  []
  (pp/print-table
    [:reducer :lazy :non-lazy]
    (for [reducer-version [#'reducer-sum-reducer
                           #'core-sum-reducer
                           #'fs-reducer-multi-reducer
                           #'reducer-multi-reducer
                           #'core-multi-reducer]]
      {:reducer (:doc (meta reducer-version))
       :lazy (mean-bench (reducer-version big-lazy-range))
       :non-lazy (mean-bench (reducer-version big-non-lazy-range))})))
