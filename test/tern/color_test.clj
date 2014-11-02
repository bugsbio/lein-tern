(ns tern.color-test
  (:require [tern.color   :refer [style]]
            [expectations :refer :all]))

(expect "\u001b[4m\u001b[34mhello!\u001b[0m"
        (style "hello!" :underline :blue))
