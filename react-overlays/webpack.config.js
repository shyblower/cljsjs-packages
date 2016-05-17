module.exports = {
    output: {
        // export itself to a global var
        libraryTarget: "var",
        // name of the global var: "Foo"
        library: "ReactOverlays"
    },
    externals: {
        // require("jquery") is external and available
        //  on the global var jQuery
        "react": "React",
        "react-dom": "ReactDom"
    }
}
