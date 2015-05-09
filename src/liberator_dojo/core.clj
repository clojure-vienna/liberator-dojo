(ns liberator-dojo.core
  (:require
   [clojure.pprint :refer [pprint]]
   [liberator.core :refer [defresource]]
   [liberator.dev :refer [wrap-trace]]
   [ring.util.response :refer [redirect]]
   ;; Use to start dev server yourself
   #_[ring.adapter.jetty :as jetty]))

(def db
  (atom {"res1" "RES1"
         "res2" "RES2"}))

(defn pprint-req [req]
  {:status 200
   :body (with-out-str
           (println "Your request:")
           (pprint req))})

(def resource-base
  {:available-media-types ["text/plain"]})

(defresource container-resource
  resource-base
  :allowed-methods #{:get :post}
  :post! (fn [{{body :body} :request}]
           (let [id (str (gensym "id-"))
                 s (slurp body)]
             (swap! db assoc id s)))
  :handle-ok (apply str (mapcat
                         (fn [[id val]]
                           (concat
                            ["id: " id " -> " val \newline]))
                         @db)))

(defresource handler-resource [id]
  resource-base
  :exists? (let [v (get @db id)]
             [v {:value v}])
  :handle-ok (fn [ctx]
               (str "ITEM " (:value ctx))))

(defn routes [{uri :uri :as req}]
  ;; Compare this with the syntax of
  ;; - moustache https://github.com/cgrand/moustache
  ;; - compojure https://github.com/weavejester/compojure
  (condp re-matches uri
    #"/?" (redirect "/items")
    #"/echo/?" (pprint-req req)
    #"/items/?" (container-resource req)
    #"/items/([^/].*)" :>> (fn [[_ i]]
                             ((handler-resource i)
                              req))
    {:status 404}))

(def handler
  (-> routes
      (wrap-trace :ui)))

;; Use to start dev server yourself
#_(defonce server
    (jetty/run-jetty #'handler {:port 8080}))
