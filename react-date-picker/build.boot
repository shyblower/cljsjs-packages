(def +lib-version+ "4.0.10")
(def +version+ (str +lib-version+ "-0"))

(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.1" :scope "test"]
                  [cljsjs/react "15.0.2-0" :scope "provided"]
                  [cljsjs/react-dom "15.0.2-0" :scope "provided"]
                  [cljsjs/moment "2.10.6-4"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all]
         '[boot.core :as c]
         '[clojure.java.io :as io])

(task-options!
  pom  {:project     'cljsjs/react-date-picker
        :version     +version+
        :description " A carefully crafted date picker for React"
        :url         "https://github.com/zippyui/react-date-picker"
        :scm         {:url "https://github.com/cljsjs/packages"}
        :license     {"MIT" "https://github.com/zippyui/react-date-picker/blob/master/LICENSE"}})

(deftask build-react-date-picker
  "use npm and webpack to download and build react-date-picker.inc.[min].js"
  []
  (with-pre-wrap fileset
    (let [npm-tmp (c/tmp-dir!)
          npm-tmp-path (.getAbsolutePath npm-tmp)
          out (c/tmp-dir!)
          out-path (.getAbsolutePath out)
          webpack-config-path (.getAbsolutePath (io/file "webpack.config.js"))]
      (binding [*sh-dir* npm-tmp]
        (dosh "npm" "install" (str "react-date-picker@" +lib-version+)))
      (let [main-src (str npm-tmp-path "/node_modules/react-date-picker/lib/index.js")]
        (dosh "webpack" "--config" webpack-config-path main-src (str out-path "/react-date-picker.inc.js"))
        (dosh "webpack" "--config" webpack-config-path "--optimize-minimize" main-src (str out-path "/react-date-picker.min.inc.js")))
      (-> fileset
        (c/add-resource out)
        (c/mv "react-date-picker.inc.js" "cljsjs/react-date-picker/development/react-date-picker.inc.js")
        (c/mv "react-date-picker.min.inc.js" "cljsjs/react-date-picker/production/react-date-picker.min.inc.js")
        (c/add-resource (io/file (str npm-tmp-path "/node_modules/react-date-picker")) :include #{#"^index\.css" #"^base\.css" #"^theme"})
        (c/mv "index.css" "cljsjs/react-date-picker/common/css/index.css")
        (c/mv "base.css" "cljsjs/react-date-picker/common/css/base.css")
        (c/mv "theme/default.css" "cljsjs/react-date-picker/common/css/theme/default.css")
        (c/mv "theme/hackerone.css" "cljsjs/react-date-picker/common/css/theme/hackerone.css")
        (c/commit!)))))

(deftask package []
  (comp
    (build-react-date-picker)
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.react-date-picker"
               :requires ["cljsjs.react"
                          "cljsjs.react.dom"
                          "cljsjs.moment"])
    (pom)
    (jar)))
