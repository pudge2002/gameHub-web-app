package GameHub.views.pages;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("games")
@PageTitle("Игры")
public class GamesView extends Composite<VerticalLayout> {

    public GamesView() {

        getContent().add(new H1("игры"));
        getContent().add(new Paragraph("3"));
    }
}
