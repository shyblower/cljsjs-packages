(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.1" :scope "test"]
                  [cljsjs/react "15.0.2-0"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "1.1.13")
(def +version+ (str +lib-version+ "-0"))

(task-options!
  pom  {:project 'cljsjs/react-responsive
        :version +version+
        :description "Media queries in react for responsive design"
        :url "https://github.com/contra/react-responsive"
        :scm {:url "https://github.com/contra/react-responsive"}
        :license {"MIT" "https://raw.githubusercontent.com/contra/react-responsive/master/LICENSE"}})

(deftask download-react-responsive []
  (download :url "https://raw.githubusercontent.com/contra/react-responsive/master/dist/react-responsive.js"
            :checksum "A3B3F1BAC52E4137220E3E141E078907"))

(deftask package []
  (comp
    (download-react-responsive)
    (sift :move {#"^react-responsive.js$"
                 "cljsjs/react-responsive/production/react-responsive.inc.js"})
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.react-responsive"
               :requires ["cljsjs.react.dom"])
    (pom)
    (jar)))
