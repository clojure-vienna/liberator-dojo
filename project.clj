(defproject clojure-vienna/liberator-dojo "0.1.0"
  :description "Liberator examples from the Clojure Dojo"
  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [liberator "0.12.2"]
                 ;; Use this to start the dev server yourself instead
                 ;; of using the leiningen plugin
                 [ring/ring-jetty-adapter "1.3.2"]
                 [compojure "1.3.4"]
                 [net.cgrand/moustache "1.2.0-alpha2"]]
  :plugins [[lein-ring "0.9.3"]]
  :ring {:handler liberator-dojo.core/handler
         :nrepl {:start? true
                 :port 4000}})
