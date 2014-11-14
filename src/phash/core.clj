(ns phash.core
  (:require [hiphip.double :as dbl])
  (:import (java.awt.image BufferedImage ColorConvertOp)
           (java.awt.color ColorSpace)
           [org.imgscalr Scalr Scalr$Method Scalr$Mode]))


(def default-size 32)
(def default-small-size 8)

(defn resize
  [img default-size]
  (Scalr/resize img Scalr$Method/SPEED Scalr$Mode/FIT_EXACT default-size default-size nil))


(defn greyscale
  "Image is converted to greyscale to simplify calculations"
  [img]
  (let [color-space (ColorSpace/getInstance ColorSpace/CS_GRAY)
        color-convert (ColorConvertOp. color-space nil)]
    (.filter color-convert img img)))

(defn get-pixel-val
  [img x y]
  (bit-and (.getRGB img x y) 0xFF))

(defn get-grey-arrays
  [img default-size]
  (dbl/afill! [x default-size y default-size] (get-pixel-val img x y)))

(defn get-hash
  "Returns a binary string hash of the image"
  ([^BufferedImage img] (get-hash img default-size default-size))
  ([^BufferedImage img width height]
  (->
    img
    (resize default-size)
    greyscale
    (get-grey-arrays default-size)))
  )

(defn distance
  "Calculates hamming distance between two phashes"
  [hash1 hash2]
   (reduce (fn [distance value]
             (if (= (first value) (second value))
               distance
               (inc distance)))
           0
           (map #(vector %1 %2) hash1 hash2)))
