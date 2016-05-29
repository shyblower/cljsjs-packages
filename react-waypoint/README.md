# cljsjs/react-waypoint

[](dependency)
```clojure
[cljsjs/react-waypoint "2.0.2-0"] ;; latest release
```
[](/dependency)

This jar comes with `deps.cljs` as used by the [Foreign Libs][flibs] feature
of the Clojurescript compiler. After adding the above dependency to your project
you can require the packaged library like this:

```clojure
(ns application.core 
  (:require cljsjs.react-waypoint))
```

To use this with Boot require "boot-less" like this:
```clojure
(set-env!
  :dependencies '[[deraen/boot-less "0.3.0" :scope "test"]
                  [cljsjs/react-waypoint "2.0.2-0"]])

```

[flibs]: https://github.com/clojure/clojurescript/wiki/Packaging-Foreign-Dependencies
