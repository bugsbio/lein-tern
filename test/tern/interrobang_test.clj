(ns tern.interrobang-test
  (:require [tern.interrobang :refer :all]
            [expectations     :refer :all]))

(expect :cat
        (-> (atom :mouse) (reset!? :cat)))

(expect :mouse
        (-> (atom :mouse) (reset!? nil)))

(expect :mouse
        (-> (atom :mouse) (reset!? false)))

(expect :cat
        @(doto (atom :mouse) (reset!? :cat)))

(expect :mouse
        @(doto (atom :mouse) (reset!? nil)))

(expect :mouse
        @(doto (atom :mouse) (reset!? false)))
