(ns columbus-clojure.demo.reducers.my-test
  (:require [clojure.test :refer :all]
            [columbus-clojure.demo.reducers.my :as my]))

(deftest my-reducers-work-like-core-reducers
  (let [coll (range -10 11)]
    (are [my-version core-version] (= my-version core-version)
         (reduce (->> conj
                      (my/map inc)
                      (my/filter pos?))
                 []
                 coll)

         (->> coll
              (filter pos?)
              (map inc)))))
