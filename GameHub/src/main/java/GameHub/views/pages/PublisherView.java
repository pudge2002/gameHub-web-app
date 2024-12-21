package GameHub.views.pages;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("publishers")
@PageTitle("Издатели")
public class PublisherView extends Composite<VerticalLayout> {

    public PublisherView() {

        getContent().add(new H1("Издатели"));
        getContent().add(new Paragraph("2"));
    }
}
