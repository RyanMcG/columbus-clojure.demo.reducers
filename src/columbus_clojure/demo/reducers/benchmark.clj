(ns columbus-clojure.demo.reducers.benchmark
  (:require [criterium.core :refer :all]))

(defn benchmark-times*
  [f {:as options}]
  (let [{:keys [samples warmup-jit-period target-execution-time gc-before-sample
                overhead] :as opts}
        (merge *default-benchmark-opts* options)]
    (run-benchmark
      samples warmup-jit-period target-execution-time f opts overhead)))

(defmacro quick-benchmark-times
  [expr options]
  `(benchmark-times* (fn [] ~expr) (merge *default-quick-bench-opts* ~options)))

(defmacro mean-bench
  "A mean benching macro"
  [expr]
  `(-> ~expr
       (quick-benchmark {})
       :mean
       first))
