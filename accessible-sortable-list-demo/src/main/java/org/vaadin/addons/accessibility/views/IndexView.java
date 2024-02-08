package org.vaadin.addons.accessibility.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.vaadin.addons.accessibility.MainLayout;
import org.vaadin.addons.accessibility.SortableList;
import org.vaadin.addons.accessibility.data.PersonRecord;
import org.vaadin.addons.accessibility.data.SampleDataGenerator;

/**
 * @author jcgueriaud
 */
@PageTitle("Demo application for accessible sortable list")
@RouteAlias(value = "", layout = MainLayout.class)
@Route(value = "index", layout = MainLayout.class)
public class IndexView extends Div {

    public IndexView() {
        SortableList<PersonRecord> personRecordSortableList = new SortableList<>(PersonRecord::id);
        personRecordSortableList.setItems(SampleDataGenerator.getPersonRecordList());
        personRecordSortableList.setItemLabelGenerator(PersonRecord::name);
        add(personRecordSortableList);
    }
}
