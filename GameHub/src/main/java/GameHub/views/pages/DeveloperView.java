package GameHub.views.pages;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("developers")
@PageTitle("Разработчики")
public class DeveloperView extends Composite<VerticalLayout> {

    public DeveloperView() {

        getContent().add(new H1("разработчики"));
        getContent().add(new Paragraph("1"));
    }
}
