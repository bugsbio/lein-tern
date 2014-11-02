(ns tern.misc-test
  (:require [tern.misc    :refer :all]
            [expectations :refer :all]))

(expect {:a 1 :b 2 :c 3 :sum 6}
        (assoc-result {:a 1 :b 2 :c 3}
                      :sum
                      (comp (partial apply +) vals)))

(expect {:a {:b 1 :c 2} :d 3 :e 4}
        (deep-merge {:a {:b 1} :d 3}
                    {:a {:c 2} :e 4}))
