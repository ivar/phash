(ns phash.core-test
  (:import (java.awt.image BufferedImage))
  (:require [clojure.test :refer :all]
            [phash.core :refer :all]))

(deftest distance-test
  (testing "That two same hashes return 0"
    (is (= 0 (distance "010" "010")))
    (is (= 0 (distance "abcdefgjj" "abcdefgjj")))
    (is (= 0 (distance "11111111111" "11111111111"))))

  (testing "two hashes varied by 1 bit should return 1"
    (is (= 1 (distance "010" "011")))
    (is (= 1 (distance "0100001" "0100000")))
    (is (= 1 (distance "ghfjdfdfdws" "ghfjdwdfdws"))))
  (testing "two hashes which are completly different"
    (is (= 10 (distance "0000000000" "1111111111")))))

(deftest greyscale-test
  ;TODO maybe create small white image and check that is stays white
  ;maybe another one which will then have appropriate color equivalent in GS
  ;do one in black which should have max value, i guess
  )

(deftest get-grey-arrays-test
  ;small image should be processed and
  ;(let [img (BufferedImage. )])
  )

(deftest get-subarray-test
  (testing "Correct single array is returned"
    (let [original-array (make-array Double/TYPE 3 3)
          _ (aset original-array 0 (double-array [1.0 2.0 3.0]))
          _ (aset original-array 1 (double-array [4.0 5.0 6.0]))
          _ (aset original-array 2 (double-array [7.0 8.0 9.0]))
          sub-array (double-array [1.0 2.0 4.0 5.0])
          result (get-subarray original-array 2)]
      (print "result" result)
      (is (= 0 (compare sub-array result))))))