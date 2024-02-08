# 

## Development instructions
Run
```
mvn install
```
to install the latest SNAPSHOT version.

Starting the demo server:

Go to `accessible-sortable-list-demo` and run:
```
mvn spring-boot:run
```

This deploys demo at http://localhost:8080

Open the devtools and check if there is an `Accessibility Checker` tab.

## How to use it

TODO


## How to setup a development environment

Run the application the first time to generate the node_modules
Create a symbolic link from the node_modules of the demo to the addon folder.

In the accessible-sortable-list folder, run:
```
ln -s ../accessible-sortable-list-demo/node_modules node_modules
```
That should help for the typescript definitions in your IDE.

Run the project from IntelliJ with the "Resolve workspace artifacts".
If you change the Java code and run "Recompile"it will be taken into account after a refresh of the page.

For the typescript changes, the folder in the addon library is already listened.

## Publishing to Vaadin Directory / Maven Central

You need to be authenticated with GitHub from Git using HTTPS, see the [official documentation](https://docs.github.com/en/get-started/getting-started-with-git/set-up-git#connecting-over-https-recommended)

Push all your changes in the main branch, then run in the `accessible-sortable-list` folder (the addon folder):

    mvn release:prepare release:clean

Configured GH action will build a release and push to Maven Central.

You need to update the pom.xml for the `accessible-sortable-list-demo` project to manually update the version of the addon (for example `0.0.7-SNAPSHOT` to `0.0.8-SNAPSHOT`)

## Licence

Apache 2.0