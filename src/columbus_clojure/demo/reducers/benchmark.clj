(ns columbus-clojure.demo.reducers.benchmark
  (:require [criterium.core :refer :all]))

(defn benchmark-times*
  "Effectively a copy of the criterium version"
  [f {:as options}]
  (let [{:keys [samples warmup-jit-period target-execution-time gc-before-sample
                overhead] :as opts}
        (merge *default-benchmark-opts* options)]
    (run-benchmark
      samples warmup-jit-period target-execution-time f opts overhead)))

(defmacro quick-benchmark-times
  [expr & [options]]
  `(benchmark-times* (fn [] ~expr) (merge *default-quick-bench-opts*
                                          (or ~options {}))))

(defmacro benchmark-times
  [expr & [options]]
  `(benchmark-times* (fn [] ~expr) (or ~options {})))

(defmacro mean-bench
  "A mean benching macro"
  [expr]
  `(-> ~expr
       (quick-benchmark {})
       :mean
       first))

(defn data->times
  [{:keys [samples execution-count]}]
  (let [scale (/ 1e-6 execution-count)]
    (map (fn [datum] (* scale (double datum)))
         samples)))
