(ns onto-chan-oom
  (:require [clojure.core.async :refer [thread chan go go-loop close! onto-chan! onto-chan!! <!! <! >!! >!]]))

(defn onto-chan!2
  "Puts the contents of coll into the supplied channel.

  By default the channel will be closed after the items are copied,
  but can be determined by the close? parameter.

  Returns a channel which will close after the items are copied.

  If accessing coll might block, use onto-chan!! instead"
  ([ch coll] (onto-chan!2 ch coll true))
  ([ch coll close?]
   (let [coll-fn (^:once fn* [] coll)]
     (go-loop [vs (seq (coll-fn))]
       (if (and vs (>! ch (first vs)))
         (recur (next vs))
         (when close?
           (close! ch)))))))

(defn fix
  [_]
  (let [c (chan)]
    (onto-chan!2 c (iterate inc 0))
    (while true
      (<!! c))))

(defn constantly-onto-chan!
  ([ch f] (constantly-onto-chan! ch f true))
  ([ch f close?]
   (go-loop []
     (if (>! ch (f))
       (recur)
       (when close?
         (close! ch))))))

(defn const
  [_]
  (let [c (chan)]
    (constantly-onto-chan! c (constantly (random-uuid)))
    (while true
      (<!! c))))

(defn repro
  [_]
  (let [c (chan)]
    (onto-chan! c (iterate inc 0))
    (while true
      (<!! c))))

(defn repro2
  [_]
  (let [c (chan)]
    (onto-chan! c (repeatedly #(random-uuid)))
    (while true
      (<!! c))))

(defn nopro
  [_]
  (let [c (chan)]
    (onto-chan!! c (iterate inc 0))
    (while true
      (<!! c))))
