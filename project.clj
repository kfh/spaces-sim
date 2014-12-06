(defproject spaces-sim "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.cognitect/transit-clj "0.8.259"]
                 [com.stuartsierra/component "0.2.2"]
                 [http-kit "2.1.19"]
                 [com.taoensso/timbre "3.3.1"]
                 [org.clojure/test.check "0.6.1"]
                 [org.clojure/data.generators "0.1.2"]]

  :profiles {:dev {:source-paths ["dev" "src/clj"]
                   :dependencies [[org.clojure/tools.namespace "0.2.7"]
                                  [mvxcvi/puget "0.6.4"]
                                  [org.clojure/java.classpath "0.2.2"]
                                  [criterium "0.4.3"]
                                  [com.cemerick/piggieback "0.1.3"]
                                  [figwheel "0.1.4-SNAPSHOT"] ]}})
