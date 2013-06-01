(ns columbus-clojure.demo.reducers.core
  (:require [clojure.core.reducers :as r]
            [clojure.pprint :as pp]
            [foldable-seq.core :refer [foldable-seq]]
            (incanter [charts :as ic]
                      [core :as in])
            (columbus-clojure.demo.reducers
              [benchmark :as bench])))

(def big-number 10000)
(def multiplier-count 30)

; There might be a better way but this works
(def big-lazy-range
  "Lazy Range"
  (lazy-seq (into [] (range big-number))))

(def big-non-lazy-range
  "Non-Lazy Range"
  (into [] (range big-number)))

(defn core-sum-reducer
  "(+) Core"
  [coll]
  (reduce + coll))

(defn reducer-sum-reducer
  "(+) Reducers"
  [coll]
  (r/reduce + coll))

(defmacro reduce-multiplier [map-version coll rng]
  `(reduce (fn [reducible# multiplier#]
             (~map-version (partial * (mod multiplier# 5)) reducible#))
           ~coll
           ~rng))

(defn core-multi-reducer
  "(*) Core"
  [coll]
  (->> (range multiplier-count)
       (reduce-multiplier map coll)
       (reduce +)))

(defn reducer-multi-reducer
  "(*) Reducers"
  [coll]
  (->> (range multiplier-count)
       (reduce-multiplier r/map coll)
       (r/fold +)))

(defn fs-reducer-multi-reducer
  "(*) Lazy Fold & Reducers"
  [coll]
  (->> (range multiplier-count)
       foldable-seq
       (reduce-multiplier r/map coll)
       (r/fold +)))

(defn historgram-output
  "Create a histogram for the given symbols (should be tied to functions). If no
  symbols are given then use all of them"
  [& reducer-version-symbols]
  (doseq [reducer-version (if (empty? reducer-version-symbols)
                            [#'reducer-sum-reducer
                             #'core-sum-reducer
                             #'fs-reducer-multi-reducer
                             #'reducer-multi-reducer
                             #'core-multi-reducer]
                            reducer-version-symbols)
          coll [#'big-lazy-range #'big-non-lazy-range]]
    (let [reducer-doc (:doc (meta reducer-version))
          coll-doc (:doc (meta coll))]
      (-> (reducer-version (var-get coll))
          (bench/benchmark-times)
          (bench/data->times)
          (ic/histogram :nbins 5
                        :title (str reducer-doc " with a " coll-doc)
                        :x-label "Execution Time (ms)")
          (in/view)))))


(defn table-output
  "Output results as a table"
  [& reducer-version-symbols]
  (pp/print-table
    [:reducer :lazy :non-lazy]
    (for [reducer-version (if (empty? reducer-version-symbols)
                            [#'reducer-sum-reducer
                             #'core-sum-reducer
                             #'fs-reducer-multi-reducer
                             #'reducer-multi-reducer
                             #'core-multi-reducer]
                            reducer-version-symbols)]
      {:reducer (:doc (meta reducer-version))
       :lazy (bench/mean-bench (reducer-version big-lazy-range))
       :non-lazy (bench/mean-bench (reducer-version big-non-lazy-range))})))
