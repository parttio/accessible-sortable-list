package org.vaadin.addons.accessibility;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author jcgueriaud
 */
public class MainLayout extends Div implements RouterLayout, AfterNavigationObserver {

    private final Main main;
    private final H1 mainTitle;

    public MainLayout() {
        addClassName("main-layout");
        add(buildNav());
        main = new Main();
        mainTitle = new H1("test");
        mainTitle.setId("main-title");
        main.add(new Header(mainTitle));
        add(main);
    }

    private Component buildNav() {

        SideNav nav = new SideNav();
        List<RouteData> routes = RouteConfiguration.forSessionScope().getAvailableRoutes();
        List<RouteData> myRoutes = routes.stream()
                .filter(routeData -> MainLayout.class.equals((routeData.getParentLayout())))
                .sorted(Comparator.comparing(RouteBaseData::getTemplate))
                .toList();
        for (RouteData myRoute : myRoutes) {
            if (myRoute.getTemplate().isEmpty()) {
                nav.addItem(new SideNavItem("Home", myRoute.getNavigationTarget()));
            } else {
                nav.addItem(new SideNavItem(myRoute.getTemplate(), myRoute.getNavigationTarget()));
            }
        }
        return nav;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            main.getElement().appendChild(Objects.requireNonNull(content.getElement()));
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        // update the main header of the page
        getUI().ifPresent(ui -> {
            PageTitle pageTitle = ui.getCurrentView().getClass().getAnnotation(PageTitle.class);
            if (pageTitle != null) {
                mainTitle.setText(pageTitle.value());
            }
        });
    }

}
