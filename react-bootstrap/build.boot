(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.1.shyblower.1" :scope "test"]
                  [cljsjs/react "15.1.0-0" :scope "provided"]
                  [org.webjars.bower/bootstrap "3.3.6"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "0.29.5")
(def +version+ (str +lib-version+ "-1"))


(task-options!
  pom  {:project     'cljsjs/react-bootstrap
        :version     +version+
        :description "The most popular front-end framework, rebuilt for React."
        :url         "https://react-bootstrap.github.io/index.html"
        :scm         {:url "https://github.com/cljsjs/packages"}
        :license     {"MIT" "https://raw.githubusercontent.com/react-bootstrap/react-bootstrap/master/LICENSE"}})

(deftask download-react-bootstrap []
  (download :url      (format "https://github.com/react-bootstrap/react-bootstrap-bower/archive/v%s.zip" +lib-version+)
            :checksum "914C2A4885947440FFBABBAD4965B321" ;;MD5
            :unzip    true))

(deftask package []
  (comp
    (download-react-bootstrap)
    (sift :add-jar {'org.webjars.bower/bootstrap #"^META-INF/resources/webjars/bootstrap/[^/]*/dist/(css|fonts)"})
    (sift :move {#"^react-bootstrap-bower-.*/react-bootstrap.js"
                 "cljsjs/react-bootstrap/development/react-bootstrap.inc.js"
                 #"^react-bootstrap-bower-.*/react-bootstrap.min.js"
                 "cljsjs/react-bootstrap/production/react-bootstrap.min.inc.js"
                 #"^META-INF/resources/webjars/bootstrap/[^/]+/dist/css/(.+\.min\..+)" "cljsjs/react-bootstrap/production/css/$1"
                 #"^META-INF/resources/webjars/bootstrap/[^/]+/dist/css/(.+\.css.*)" "cljsjs/react-bootstrap/development/css/$1"
                 #"^META-INF/resources/webjars/bootstrap/[^/]+/dist/fonts/(.+)" "cljsjs/react-bootstrap/common/fonts/$1"})
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.react-bootstrap"
               :requires ["cljsjs.react.dom"])
    (pom)
    (jar)))
