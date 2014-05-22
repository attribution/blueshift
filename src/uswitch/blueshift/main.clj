(ns uswitch.blueshift.main
  (:require [clojure.tools.logging :refer (info)]
            [clojure.tools.cli :refer (parse-opts)]
            [uswitch.blueshift.system :refer (build-system)]
            [com.stuartsierra.component :refer (start)])
  (:gen-class))

(def cli-options
  [["-c" "--config CONFIG" "Path to EDN configuration file"
    :default "./etc/config.edn"
    :validate [string?]]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (when (:help options)
      (println summary)
      (System/exit 0))
    (let [{:keys [config]} options]
      (info "Starting Blueshift with configuration" config)
      (start (build-system (read-string (slurp config)))))))
