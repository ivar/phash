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
  (let [img (BufferedImage. )])
  )