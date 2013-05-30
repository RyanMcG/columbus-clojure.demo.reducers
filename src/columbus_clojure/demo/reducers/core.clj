(ns columbus-clojure.demo.reducers.core
  (:require [clojure.core.reducers :as r]
            [clojure.pprint :as pp]
            [foldable-seq.core :refer [foldable-seq]]
            (columbus-clojure.demo.reducers [benchmark :refer [mean-bench]])))

(def big-number 10000)

; There might be a better way but this works
(def big-lazy-range (lazy-seq (into [] (range big-number))))
(def big-non-lazy-range (into [] (range big-number)))

(defn core-sum-reducer
  "C +"
  [coll]
  (reduce + coll))

(defn reducer-sum-reducer
  "R +"
  [coll]
  (r/reduce + coll))

(defn core-multi-reducer
  "C *"
  [coll]
  (->> (range 100)
       (reduce (fn [reducible multiplier]
                 (map (partial * (mod multiplier 5)) reducible))
               coll)
       (reduce +)))

(defn reducer-multi-reducer
  "R *"
  [coll]
  (->> (range 100)
       (reduce (fn [reducible multiplier]
                 (r/map (partial * (mod multiplier 5)) reducible))
               coll)
       (r/fold +)))

(defn fs-reducer-multi-reducer
  "FS *"
  [coll]
  (->> (range 100)
       foldable-seq
       (reduce (fn [reducible multiplier]
                 (r/map (partial * (mod multiplier 5)) reducible))
               coll)
       (r/fold +)))

(defn -main
  "Just a main function"
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
