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


(defn init-coefficients
  [size]
  (let [coeffs (double-array size 1)
        _ (aset coeffs 0 (/ 1 (Math/sqrt 2.0)))]
    coeffs))

(defn dct
  [i j u v value size]
  (* value
     (Math/cos (* u Math/PI (/ (* 2 (+ i 1)) (* 2 size))))
     (Math/cos (* v Math/PI (/ (* 2 (+ j 1)) (* 2 size))))))


(defn apply-dct
  [values coefficients size]
  (let [f-array (make-array Double/TYPE size size)]
    (dotimes [u size]
      (dotimes [v size]
                (let [sum (areduce
                            (make-array Double/TYPE (* size size))
                            idx
                            sum
                            0
                            (let [i (rem idx size)
                                  j (quot idx size)]
                              (dct i j u v (aget values i j) size)))
                      sum (* sum (/ (aget coefficients u v) 4.0))]
                  (aset f-array u v sum))
                ))
    f-array))

(defn get-dct-total
  [dct-values small-size]
  (let [total (areduce
                (make-array Double/TYPE (* small-size small-size))
                idx
                sum
                0
                (let [i (rem idx small-size)
                      j (quot idx small-size)]
                  (aget dct-values i j)))]
    (- total (aget dct-values 0 0))))

(defn get-subarray
  [dct-values small-size]
  (let [sub-array (make-array Double/TYPE (* small-size small-size))]
    (dotimes [i small-size]
      (dotimes [j small-size]
        (aset sub-array (+ (* i small-size) j) (aget dct-values i j))))))


(defn reduce-dct
  [dct-values small-size]
  (let [reduced-array (get-subarray dct-values small-size)
        total (get-dct-total dct-values small-size)
        avg (/  total (- (* small-size small-size) 1))]
    nil))

(defn get-hash
  "Returns a binary string hash of the image"
  ([^BufferedImage img] (get-hash img default-size default-small-size))
  ([^BufferedImage img size small-size]
  (->
    img
    (resize size)
    greyscale
    (get-grey-arrays size)
    (apply-dct (init-coefficients size) size)
    (reduce-dct small-size)))
  ); use hex to return hash
;bin-> hex new BigInteger(binaryString, 2).toString(16)
;hex-> bin new BigInteger(s, 16).toString(2)

(defn distance
  "Calculates hamming distance between two phashes"
  [hash1 hash2]
   (reduce (fn [distance value]
             (if (= (first value) (second value))
               distance
               (inc distance)))
           0
           (map #(vector %1 %2) hash1 hash2)))
