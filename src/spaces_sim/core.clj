(ns spaces-sim.core
  (:require [clojure.edn :as edn]
            [clojure.core.async :as async :refer [go-loop alts! chan <! >! timeout]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]
            [clojure.java.io :as io]
            [org.httpkit.client :as http]
            [cognitect.transit :as transit])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(timbre/refer-timbre)

(defn edn->transit [edn]
  (let [out (ByteArrayOutputStream. 4096)
        w (transit/writer out :json)]
    (transit/write w edn)
    (.toString out)))

(defn post-ad! [url ad]
  (http/post url {:headers {"Content-Type" "application/clojure"}
                  :form-params ad}
             (fn [{:keys [status headers body error] :as resp}]
               (if error
                 (errorf "[post-ad] oops: " resp)
                 (infof "[post-ad] status: %s" status)))))

(defn load-adresses []
  (-> (io/resource "oslo.edn")
      slurp
      edn/read-string))

(defn parse-address [address]
  (let [[_ street num] (re-find #"(\D.*) (\d.*)" address)]
    [street num]))

(defn gen-title []
  (rand-nth ["Praktfull leilighet med utsikt"
             "Gammel rønne"
             "En leilighet for de optimistiske"
             "En leilighet du kan dø i"
             "Flott leilighet med perfekt beliggenhet"]))

(defn gen-cost []
  (->> (rand-int 70)
      (format "%d00000")
      read-string))

(defn gen-size []
  (+ 20 (rand-int 100)))

(defn make-random-ad [sim]
  (let [{:keys [address postal_code city county]} (rand-nth (:adresses sim))
        [street street-number] (parse-address address)]
    {:ad-type "ad.type/real-estate"
     :ad-start-time "14:45"
     :ad-end-time "20:00"
     :ad-active true
     :res-title address
     :res-desc (gen-title)
     :res-type "real-estate.type/apartment"
     :res-cost (gen-cost)
     :res-size (format "%s m2" (gen-size))
     :res-bedrooms (+ 1 (rand-int 3))
     :res-features ["real-estate.feature/aircondition" "real-estate.feature/elevator"]
     :loc-name address
     :loc-street street
     :loc-street-num street-number
     :loc-zip-code postal_code
     :loc-city city}))

(defn simulate! [sim]
  (infof "[simulate] Starting loop")
  (let [stop-ch (chan)]
    (go-loop
      []
      (let [[_ port] (alts! [(timeout 500) stop-ch])]
        (when-not (= port stop-ch)
          (let [ad (make-random-ad sim)]
            (infof "[simulate] Created an ad for adress %s" (:loc-name ad))
            (post-ad! "http://127.0.0.1:3333/api/ads" ad)
            (recur)))))
    stop-ch))

(defrecord Sim []
  component/Lifecycle
  (start [this]
    (info "[sim] Starting")
    (let [adresses (load-adresses)
          this (assoc this :adresses adresses)
          stop! (simulate! this)]
      (infof "[sim] Loaded %s adresses from file." (count adresses))
      (assoc this :stop! stop!)))
  (stop [this]
    (info "[sim] Stopping")
    (when (:stop! this)
      (async/put! (:stop! this) :stop))
    (dissoc this :adresses :stop!)))

(defn sim-system []
  (component/system-map
    :sim (Sim.)))