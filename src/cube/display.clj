(ns cube.display
  (:refer-clojure))

(def face-colors
  "Maps the numbering of faces in the engine to color keywords"
  {1 :blue 2 :red 3 :white 4 :green 5 :orange 6 :yellow})

(def tile-colors
  {:red    "\033[101;97m"
   :green  "\033[102;30m"
   :yellow "\033[103;30m"
   :blue   "\033[104;97m"
   :white  "\033[107;30m"
   :orange "\033[48;5;208;97m"})

(def tile-letters
  {:red \R
   :green \G
   :yellow \Y
   :blue \B
   :white \W
   :orange \O})

(defn- print-tile
  "Print one tile with given color"
  [color]
  (print (str (color tile-colors) (color tile-letters) "\033[0m")))

(defn- print-line
  "Prints a vector of tiles"
  [colors]
  (doseq [color colors] (print-tile color))
  (println ""))

(defn- cube->lines
  "Converts a cube to a vector of color lines to be printed"
  [cube]
  (let [[[l1 l2 l3 l4 l5 l6 l7 l8 l9]
         [r1 r2 r3 r4 r5 r6 r7 r8 r9]
         [f1 f2 f3 f4 f5 f6 f7 f8 f9]
         [b1 b2 b3 b4 b5 b6 b7 b8 b9]
         [d1 d2 d3 d4 d5 d6 d7 d8 d9]
         [u1 u2 u3 u4 u5 u6 u7 u8 u9]] cube]
    [[(face-colors u1) (face-colors u2) (face-colors u3)]
     [(face-colors u4) (face-colors u5) (face-colors u6)]
     [(face-colors u7) (face-colors u8) (face-colors u9)]
     [(face-colors l1) (face-colors l2) (face-colors l3) (face-colors f1) (face-colors f2) (face-colors f3) (face-colors r1) (face-colors r2) (face-colors r3) (face-colors b1) (face-colors b2) (face-colors b3)]
     [(face-colors l4) (face-colors l5) (face-colors l6) (face-colors f4) (face-colors f5) (face-colors f6) (face-colors r4) (face-colors r5) (face-colors r6) (face-colors b4) (face-colors b5) (face-colors b6)]
     [(face-colors l7) (face-colors l8) (face-colors l9) (face-colors f7) (face-colors f8) (face-colors f9) (face-colors r7) (face-colors r8) (face-colors r9) (face-colors b7) (face-colors b8) (face-colors b9)]
     [(face-colors d1) (face-colors d2) (face-colors d3)]
     [(face-colors d4) (face-colors d5) (face-colors d6)]
     [(face-colors d7) (face-colors d8) (face-colors d9)]]))

(defn print-cube
  [cube]
  (doseq [lines (cube->lines cube)]
    (if (= (count lines) 3) (print "   "))
    (print-line lines)))
