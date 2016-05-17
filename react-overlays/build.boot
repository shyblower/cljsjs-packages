(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.1" :scope "test"]
                  [cljsjs/react "15.0.2-0"]
                  [cljsjs/react-dom "15.0.2-0"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all]
         '[boot.core :as c]
         '[clojure.java.io :as io])

(def +lib-version+ "0.6.3")
(def +version+ (str +lib-version+ "-0"))


(task-options!
  pom  {:project     'cljsjs/react-overlays
        :version     +version+
        :description "Utilities for creating robust overlay components."
        :url         "https://github.com/react-bootstrap/react-overlays"
        :scm         {:url "https://github.com/react-bootstrap/react-overlays"}
        :license     {"MIT" "https://github.com/react-bootstrap/react-overlays/blob/master/LICENSE"}})

(deftask download-react-overlays []
  (download :url      (format "https://github.com/react-bootstrap/react-bootstrap-bower/archive/v%s.zip" +lib-version+)
            :checksum "3045BD5605CCB2F9E58870C6A5032F82" ;;MD5
            :unzip    true))

(deftask build-react-overlays
  "use npm and webpack to download and build react-oveerlays.inc.[min].js"
  []
  (with-pre-wrap fileset
    (let [tmp (c/tmp-dir!)
          tmp-path (.getCanonicalPath tmp)
          webpack-config-path (.getCanonicalPath (io/file "webpack.config.js"))]
      (binding [*sh-dir* tmp]
        (dosh "npm" "install" (str "react-overlays@" +lib-version+))
        (dosh "webpack" "--config" webpack-config-path "node_modules/react-overlays/lib/index.js" "react-overlays.inc.js")
        (dosh "webpack" "--config" webpack-config-path "--optimize-minimize" "node_modules/react-overlays/lib/index.js" "react-overlays.min.inc.js")
        (dosh "rm" "-rf" "node_modules"))
      (-> fileset
        (c/add-resource tmp)
        (c/mv "react-overlays.inc.js" "cljsjs/react-overlays/development/react-overlays.inc.js")
        (c/mv "react-overlays.min.inc.js" "cljsjs/react-overlays/production/react-overlays.min.inc.js")
        (c/commit!)))))

(deftask package []
  (comp
    (build-react-overlays)
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.react-overlays"
               :requires ["cljsjs.react"
                          "cljsjs.react.dom"])
    (pom)
    (jar)))
