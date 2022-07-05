(ns onto-chan-oom
  (:require [clojure.core.async :refer [thread chan go go-loop close! onto-chan! onto-chan!! <!! <! >!! >!]]))

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

(defn nopro
  [_]
  (let [c (chan)]
    (onto-chan!! c (iterate inc 0))
    (while true
      (<!! c))))
