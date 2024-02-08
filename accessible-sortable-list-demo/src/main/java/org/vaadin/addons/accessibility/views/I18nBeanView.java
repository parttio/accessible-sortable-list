package org.vaadin.addons.accessibility.views;

import org.vaadin.addons.accessibility.MainLayout;
import org.vaadin.addons.accessibility.SortableList;
import org.vaadin.addons.accessibility.data.PersonBean;
import org.vaadin.addons.accessibility.data.SampleDataGenerator;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * @author jcgueriaud
 */
@PageTitle("Translated sortable List")
@Route(value = "i18n", layout = MainLayout.class)
public class I18nBeanView extends Div {

    public I18nBeanView() {
        SortableList<PersonBean> personBeanSortableList = new SortableList<>(PersonBean::getId);
        personBeanSortableList.getElement().setAttribute("lang", "fr");
        personBeanSortableList.setI18n(new SortableList.SortableListI18n()
                .setOperation("Appuyer sur la barre Espace pour trier la liste")
                .setLiveTextCancelled("${itemDescription}, tri annulé.")
                .setLiveTextDropped("${itemDescription}, déposé. Position finale dans la liste:  ${dropItemIndex} sure ${itemsLength}.")
                .setTitle("Liste à trier")
                .setLiveTextGrabbed("${itemDescription}, attrapé. Position finale dans la liste: ${dragItemIndex} sue ${itemsLength}. Appuyer sur les flèches Haut et Bas pour modifier la position, barre Espace pour déposer, Echap pour annuler.")
        );
        personBeanSortableList.setItems(SampleDataGenerator.getPersonBeanList());
        personBeanSortableList.setItemLabelGenerator(PersonBean::getFullName);
        add(personBeanSortableList);
    }
}
