package GameHub.views;


import GameHub.views.pages.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.*;


@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Class<? extends Component> view, String cssClass) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL, TextColor.BODY, cssClass);
            link.setRoute(view);
            link.setHighlightCondition(HighlightConditions.sameLocation());

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);

            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }
    }

    public MainLayout() {
        addToNavbar(createHeaderContent());
    }

    private Component createHeaderContent() {
        Header header = new Header();
        header.addClassName("main-layout-header-1");
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL, AlignItems.CENTER, JustifyContent.CENTER);

        Div layout = new Div();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER, Padding.Horizontal.LARGE);

        H1 appName = new H1("GameHub");
        RouterLink logo = new RouterLink("GameHub", HomeView.class);
        logo.addClassNames(Margin.Vertical.MEDIUM, FontSize.LARGE, "animated-text");
        logo.addClassNames(Margin.Vertical.MEDIUM, FontSize.LARGE);
        layout.add(logo);

        Nav nav = new Nav();
        nav.addClassNames(Display.FLEX, Overflow.AUTO, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL, AlignItems.CENTER, JustifyContent.CENTER);
        UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX, Gap.SMALL, ListStyleType.NONE, Margin.NONE, Padding.NONE, AlignItems.CENTER, JustifyContent.CENTER);
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            list.add(menuItem);
        }

        header.add(layout, nav);
        return header;
    }


    private MenuItemInfo[] createMenuItems() {

            return new MenuItemInfo[]{
                    new MenuItemInfo("Игры", GamesView.class, "games-link"),
                    new MenuItemInfo("Разработчики", DeveloperView.class, "developers-link"),
                    new MenuItemInfo("Издатели", PublisherView.class, "publishers-link"),
                    new MenuItemInfo("Дашборд", DashboardView.class, "dashboard-link")
            };

    }

}
