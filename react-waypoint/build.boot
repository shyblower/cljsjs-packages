(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.1" :scope "test"]
                  [cljsjs/react "15.1.0-0" :scope "provided"]
                  [cljsjs/react-dom "15.1.0-0" :scope "provided"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all]
         '[boot.core :as c]
         '[clojure.java.io :as io])

(def +lib-version+ "2.0.2")
(def +version+ (str +lib-version+ "-0"))


(task-options!
  pom  {:project     'cljsjs/react-waypoint
        :version     +version+
        :description "A React component to execute a function whenever you scroll to an element."
        :url         "https://brigade.github.io/react-waypoint"
        :scm         {:url "https://github.com/brigade/react-waypoint"}
        :license     {"MIT" "https://raw.githubusercontent.com/brigade/react-waypoint/master/LICENSE"}})

(deftask build-react-waypoint
  "use npm and webpack to download and build react-waypoint.inc.[min].js"
  []
  (with-pre-wrap fileset
    (let [npm-tmp (c/tmp-dir!)
          npm-tmp-path (.getAbsolutePath npm-tmp)
          out (c/tmp-dir!)
          out-path (.getAbsolutePath out)
          webpack-config-path (.getAbsolutePath (io/file "webpack.config.js"))]
      (binding [*sh-dir* npm-tmp]
        (dosh "npm" "install" (str "react-waypoint@" +lib-version+)))
      (let [main-src (str npm-tmp-path "/node_modules/react-waypoint/build/npm/waypoint.js")]
        (dosh "webpack" "--config" webpack-config-path main-src (str out-path "/react-waypoint.inc.js"))
        (dosh "webpack" "--config" webpack-config-path "--optimize-minimize" main-src (str out-path "/react-waypoint.min.inc.js")))
      (-> fileset
        (c/add-resource out)
        (c/mv "react-waypoint.inc.js" "cljsjs/react-waypoint/development/react-waypoint.inc.js")
        (c/mv "react-waypoint.min.inc.js" "cljsjs/react-waypoint/production/react-waypoint.min.inc.js")
        (c/commit!)))))

(deftask package []
  (comp
    (build-react-waypoint)
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.react-waypoint"
               :requires ["cljsjs.react"
                          "cljsjs.react.dom"])
    (pom)
    (jar)))
