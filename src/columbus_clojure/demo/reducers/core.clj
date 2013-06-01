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

(defmacro reduce-multiplier [map-version coll rng]
  `(reduce (fn [reducible# multiplier#]
             (~map-version #(* (inc (mod multiplier# 5))
                               (mod % (/ Long/MAX_VALUE 5)))
                           reducible#))
           ~coll
           ~rng))

(defn core-multi-reducer
  "(*) Reduce + Core"
  [coll]
  (->> (range multiplier-count)
       (reduce-multiplier map coll)
       (reduce +)))

(defn reducer-fold-multi-reducer
  "(*) Fold + Reducers"
  [coll]
  (->> (range multiplier-count)
       (reduce-multiplier r/map coll)
       (r/fold +)))

(defn reducer-reduce-multi-reducer
  "(*) Reduce + Reducers"
  [coll]
  (->> (range multiplier-count)
       (reduce-multiplier r/map coll)
       (r/reduce +)))

(defn fs-reducer-multi-reducer
  "(*) Lazy Fold + Reducers"
  [coll]
  (let [foldable-coll (foldable-seq coll)]
    (->> (range multiplier-count)
         (reduce-multiplier r/map foldable-coll)
         (r/fold +))))

(def ^:dynamic *reducer-versions*
  [#'fs-reducer-multi-reducer
   #'reducer-fold-multi-reducer
   #'reducer-reduce-multi-reducer
   #'core-multi-reducer])

(defmacro with-versions
  [versions & body]
  `(binding [*reducer-versions* ~versions]
     ~@body))

(defn histogram-output
  "Create a histogram for the given symbols (should be tied to functions). If no
  symbols are given then use all of them"
  []
  (doseq [reducer-version *reducer-versions*
          coll [#'big-lazy-range #'big-non-lazy-range]]
    (let [reducer-doc (:doc (meta reducer-version))
          coll-doc (:doc (meta coll))]
      (-> (reducer-version (var-get coll))
          (bench/benchmark-times)
          (bench/data->times)
          (ic/histogram :nbins 10
                        :title (str reducer-doc " with a " coll-doc)
                        :x-label "Execution Time (ms)")
          (in/view)))))

(defn table-output
  "Output results as a table"
  []
  (pp/print-table
    [:reducer :lazy :non-lazy]
    (for [reducer-version *reducer-versions*]
      {:reducer (:doc (meta reducer-version))
       :lazy (bench/mean-bench (reducer-version big-lazy-range))
       :non-lazy (bench/mean-bench (reducer-version big-non-lazy-range))})))

(defn -main
  "Run both the histogram and table-output for the given symbols. If none are
  given do it for all symbols."
  []
  (table-output)
  (histogram-output))
