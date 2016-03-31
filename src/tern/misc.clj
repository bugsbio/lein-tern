(ns tern.misc)

(defn deep-merge
  "Deep-merge maps. Limited to two maps at the moment."
  [a b]
  (if (and (map? a)
           (map? b))
    (merge-with deep-merge a b)
    b))

(defn assoc-result
  "Assocs the result of calling a function on a map into that map at the given key."
  [map k f]
  (assoc map k (f map)))

(def last-but-one
  ^{:doc "Return the last but one element of a sequence."}
  (comp last butlast))

(defn trunc-str
  [s n]
  (if (> (count s) n)
    (str (subs s 0 n) "...")
    s))
