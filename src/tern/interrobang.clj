(ns tern.interrobang)

(defn reset!?
  "How can I resist a function name such as this?!
  Resets the value of the atom `a` to `v` when `v` is truthy."
  [a v]
  (if v (reset! a v) @a))
