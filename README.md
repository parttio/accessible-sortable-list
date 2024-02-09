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

```
  SortableList<PersonRecord> personRecordSortableList = new SortableList<>(PersonRecord::id);
  personRecordSortableList.setItems(SampleDataGenerator.getPersonRecordList());
  personRecordSortableList.setItemLabelGenerator(PersonRecord::name);
  add(personRecordSortableList);
```
This will create a list that can be reordered with drag and drop and also with a keyboard (navigates on the item then press Space to activate, Arrows to move and Space to validate or Escape to cancel).

The component requires a list of Beans (or Record) with 2 generators, a generator of Id (unique number) and String (that will be displayed as text).

It has been tested with a screen reader (VoiceOver) and Safari/Chrome on MacOs.

The pattern implemented for the component is coming from here: https://medium.com/salesforce-ux/4-major-patterns-for-accessible-drag-and-drop-1d43f64ebf09

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
