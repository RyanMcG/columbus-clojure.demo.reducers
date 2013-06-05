(ns columbus-clojure.demo.reducers.my
  (:refer-clojure :exclude [map filter]))

(defn map [f reducing-function]
  (fn [memo value]
    (reducing-function memo (f value))))

(defn filter [f reducing-function]
  (fn [memo value]
    (if (f value)
      (reducing-function memo value)
      memo)))
