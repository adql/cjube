(ns cjube.engine
  (:refer-clojure)
  (:require [clojure.string :as s]))

(def solved-cube
  [[1 1 1 1 1 1 1 1 1]                  ; left
   [4 4 4 4 4 4 4 4 4]                  ; right
   [2 2 2 2 2 2 2 2 2]                  ; front
   [5 5 5 5 5 5 5 5 5]                  ; back
   [3 3 3 3 3 3 3 3 3]                  ; down
   [6 6 6 6 6 6 6 6 6]                  ; up
   ])

;; Four fundamental rotation schemes on which all other are based. Defined rather brutely here, maybe
;; I'll make it nicer when I understand better how

(defn- permute-front
  "Rotate the front face clockwise once"
  [cube]
  (let [[[l1 l2 l3 l4 l5 l6 l7 l8 l9]
         [r1 r2 r3 r4 r5 r6 r7 r8 r9]
         [f1 f2 f3 f4 f5 f6 f7 f8 f9]
         [b1 b2 b3 b4 b5 b6 b7 b8 b9]
         [d1 d2 d3 d4 d5 d6 d7 d8 d9]
         [u1 u2 u3 u4 u5 u6 u7 u8 u9]] cube]
    [[l1 l2 d1 l4 l5 d2 l7 l8 d3]
     [u7 r2 r3 u8 r5 r6 u9 r8 r9]
     [f7 f4 f1 f8 f5 f2 f9 f6 f3]
     [b1 b2 b3 b4 b5 b6 b7 b8 b9]
     [r7 r4 r1 d4 d5 d6 d7 d8 d9]
     [u1 u2 u3 u4 u5 u6 l9 l6 l3]]))

(defn- permute-x
  "Rotates the entire cube upwards around the x-axis once"
  [cube]
  (let [[[l1 l2 l3 l4 l5 l6 l7 l8 l9]
         [r1 r2 r3 r4 r5 r6 r7 r8 r9]
         [f1 f2 f3 f4 f5 f6 f7 f8 f9]
         [b1 b2 b3 b4 b5 b6 b7 b8 b9]
         [d1 d2 d3 d4 d5 d6 d7 d8 d9]
         [u1 u2 u3 u4 u5 u6 u7 u8 u9]] cube]
    [[l3 l6 l9 l2 l5 l8 l1 l4 l7]
     [r7 r4 r1 r8 r5 r2 r9 r6 r3]
     [d1 d2 d3 d4 d5 d6 d7 d8 d9]
     [u9 u8 u7 u6 u5 u4 u3 u2 u1]
     [b9 b8 b7 b6 b5 b4 b3 b2 b1]
     [f1 f2 f3 f4 f5 f6 f7 f8 f9]
     ]))

(defn- permute-y
  "Rotates the entire cube rightwards around the y-axis once"
  [cube]
  (let [[[l1 l2 l3 l4 l5 l6 l7 l8 l9]
         [r1 r2 r3 r4 r5 r6 r7 r8 r9]
         [f1 f2 f3 f4 f5 f6 f7 f8 f9]
         [b1 b2 b3 b4 b5 b6 b7 b8 b9]
         [d1 d2 d3 d4 d5 d6 d7 d8 d9]
         [u1 u2 u3 u4 u5 u6 u7 u8 u9]] cube]
    [[f1 f2 f3 f4 f5 f6 f7 f8 f9]
     [b1 b2 b3 b4 b5 b6 b7 b8 b9]
     [r1 r2 r3 r4 r5 r6 r7 r8 r9]
     [l1 l2 l3 l4 l5 l6 l7 l8 l9]
     [d3 d6 d9 d2 d5 d8 d1 d4 d7]
     [u7 u4 u1 u8 u5 u2 u9 u6 u3]]))

(defn- permute-z
  "Rotates the entire cube rightwards around the z-axis once"
  [cube]
  (let [[[l1 l2 l3 l4 l5 l6 l7 l8 l9]
         [r1 r2 r3 r4 r5 r6 r7 r8 r9]
         [f1 f2 f3 f4 f5 f6 f7 f8 f9]
         [b1 b2 b3 b4 b5 b6 b7 b8 b9]
         [d1 d2 d3 d4 d5 d6 d7 d8 d9]
         [u1 u2 u3 u4 u5 u6 u7 u8 u9]] cube]
    [[d7 d4 d1 d8 d5 d2 d9 d6 d3]
     [u7 u4 u1 u8 u5 u2 u9 u6 u3]
     [f7 f4 f1 f8 f5 f2 f9 f6 f3]
     [b3 b6 b9 b2 b5 b8 b1 b4 b7]
     [r7 r4 r1 r8 r5 r2 r9 r6 r3]
     [l7 l4 l1 l8 l5 l2 l9 l6 l3]]))

(def basic-rotations
  "Generates the map for all basic rotations (to be called by rotate-single)"
  (let [basic {:F [permute-front], :X [permute-x], :Y [permute-y], :Z [permute-z]}]
    (reduce (fn [current-map rotation]
              (assoc current-map (first rotation) (reduce #(into %1 (%2 current-map)) [] (second rotation))))
            basic
            [[:L [:Y :Y :Y :F :Y]]
             [:R [:Y :F :Y :Y :Y]]
             [:B [:Y :Y :F :Y :Y]]
             [:D [:X :F :X :X :X]]
             [:U [:X :X :X :F :X]]
             [:l [:R :X :X :X]]
             [:r [:L :X]]
             [:f [:B :Z]]
             [:b [:F :Z :Z :Z]]
             [:d [:U :Y :Y :Y]]
             [:u [:D :Y]]
             [:M [:l :L :L :L]]
             [:E [:d :D :D :D]]
             [:S [:f :F :F :F]]])))

(defn- rotate-single [cube face]
  "Perform a single movement of a face (that is, simple R, L, F, etc.)"
  ((apply comp (reverse (face basic-rotations))) cube))

(defn- get-face [rotation]
  "Extracts the face from a string of standard notation"
  (-> rotation first str keyword))

(defn- get-iteration [rotation]
  "Extracts the iteration number from a string of standard notation"
  (if (second rotation)
    (get {\2 2 \' 3} (second rotation))
    1))

(defn- rotate-full
  "Perform one move of full rotation (R, L', U2, etc.)"
  [cube rotation]
  (let [face (get-face rotation)]
    (loop [cube cube
           i (get-iteration rotation)]
      (if (= i 1) (rotate-single cube face) (recur (rotate-single cube face) (dec i))))))

(defn rotate
  "Perform a series of movements given as a string"
  [cube rotations]
  (let [rotations (s/split rotations #" ")]
    (reduce rotate-full cube rotations)))
