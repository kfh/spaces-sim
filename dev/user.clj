(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint print-table]]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all set-refresh-dirs)]
            [clojure.core.reducers :as r]
            [clojure.stacktrace :refer [print-stack-trace print-cause-trace print-trace-element]]
            [com.stuartsierra.component :as component]
            [puget.printer :as puget :refer [cprint]]
            [criterium.core :as crit]
            [spaces-sim.core :as sim]
            [spaces-sim.core :as sim]))

(def system nil)

(defn init []
  (alter-var-root #'system (constantly (sim/sim-system))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))
