(ns tern.color
  "Adapted from clansi: https://github.com/ams-clj/clansi.")

(def ANSI-CODES
  {:reset              "[0m"
   :bright             "[1m"
   :blink-slow         "[5m"
   :underline          "[4m"
   :underline-off      "[24m"
   :inverse            "[7m"
   :inverse-off        "[27m"
   :strikethrough      "[9m"
   :strikethrough-off  "[29m"

   :default "[39m"
   :white   "[37m"
   :black   "[30m"
   :red     "[31m"
   :green   "[32m"
   :blue    "[34m"
   :yellow  "[33m"
   :magenta "[35m"
   :cyan    "[36m"

   :bg-default "[49m"
   :bg-white   "[47m"
   :bg-black   "[40m"
   :bg-red     "[41m"
   :bg-green   "[42m"
   :bg-blue    "[44m"
   :bg-yellow  "[43m"
   :bg-magenta "[45m"
   :bg-cyan    "[46m"
   })

(def ^{:doc "Allow turning off color output in case the user's terminal
            doesn't support it. Or they just hate color."}
  use-color (atom true))

(defn ansi
  "Output an ANSI escape code using a style key.
  (ansi :blue)
  (ansi :underline)
  If tern.config/*use-color* is bound to false, outputs an empty string
  instead of an ANSI code.
  "
  [code]
  (if @use-color
    (str \u001b (get ANSI-CODES code (:reset ANSI-CODES)))
    ""))

(defn style
  "Applies ANSI color and style to a text string.
  (style \"foo\" :red)
  (style \"foo\" :red :underline)
  (style \"foo\" :red :bg-blue :underline)
  "
  [s & codes]
  (str (apply str (map ansi codes)) s (ansi :reset)))

;; The code in this namespace was copied from `clansi` in order to avoid the
;; dependency on an old version of Clojure and to reduce the required
;; dependencies of `tern`. The original, MIT license of `clansi` follows.
;;
;; `clansi` can be found at https://github.com/ams-clj/clansi
;;
;; The MIT License
;; ===============
;;
;; Copyright (c) 2009-2014 Amsterdam Clojurians
;;
;; Permission is hereby granted, free of charge, to any person obtaining a copy
;; of this software and associated documentation files (the "Software"), to deal
;; in the Software without restriction, including without limitation the rights
;; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;; copies of the Software, and to permit persons to whom the Software is
;; furnished to do so, subject to the following conditions:
;;
;; The above copyright notice and this permission notice shall be included in
;; all copies or substantial portions of the Software.
;;
;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
;; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
;; THE SOFTWARE.
