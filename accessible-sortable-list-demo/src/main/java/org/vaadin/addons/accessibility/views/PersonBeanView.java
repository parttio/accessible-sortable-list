package org.vaadin.addons.accessibility.views;

import org.vaadin.addons.accessibility.MainLayout;
import org.vaadin.addons.accessibility.SortableList;
import org.vaadin.addons.accessibility.data.PersonBean;
import org.vaadin.addons.accessibility.data.SampleDataGenerator;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * @author jcgueriaud
 */
@PageTitle("Sortable List with a Bean")
@Route(value = "bean", layout = MainLayout.class)
public class PersonBeanView extends Div {

    public PersonBeanView() {
        SortableList<PersonBean> personBeanSortableList = new SortableList<>(PersonBean::getId);
        personBeanSortableList.setItems(SampleDataGenerator.getPersonBeanList());
        personBeanSortableList.setItemLabelGenerator(PersonBean::getFullName);
        personBeanSortableList.addReorderedListener(event -> {
            Notification.show("New order of the list " + event.getItems());
        });
        add(personBeanSortableList);
    }
}
