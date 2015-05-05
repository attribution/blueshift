(ns uswitch.blueshift.main
  (:require [clojure.tools.logging :refer (info)]
            [clojure.tools.cli :refer (parse-opts)]
            [uswitch.blueshift.system :refer (build-system)]
            [com.stuartsierra.component :refer (start stop)])
  (:gen-class))

(def cli-options
  [["-c" "--config CONFIG" "Path to EDN configuration file"
    :default "./etc/config.edn"
    :validate [string?]]
   ["-i" "--s3id ID" "S3 ID"
    :default nil]
   ["-k" "--s3key KEY" "S3 KEY"
    :default nil]
   ["-b" "--s3bucket BUCKET" "S3 BUCKET"
    :default nil]
    ["-h" "--help"]])

(defn wait! []
  (let [s (java.util.concurrent.Semaphore. 0)]
    (.acquire s)))

(defn -main [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (when (:help options)
      (println summary)
      (System/exit 0))
    (let [{:keys [config s3id s3key s3bucket]} options]
      (info "Starting Blueshift with configuration" config)
      (def config-options (read-string (slurp config)))
      (def merged (merge-with merge
                              config-options
                              {:s3 {:credentials {:access-key s3id :secret-key s3key} :bucket s3bucket}}))
      (let [system (build-system merged)]
        (start system)
        (wait!)))))
