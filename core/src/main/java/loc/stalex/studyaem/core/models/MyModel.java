package loc.stalex.studyaem.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Session;

@Model(adaptables = Resource.class)
public class MyModel {

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    @Optional
    private String text;

    @Inject
    @Optional
    @Named(value = "textIsRich")
    private boolean isRich;

    @PostConstruct
    public void init() {
        text = text + " - updated with @PostConstruct";
        Session session = resourceResolver.adaptTo(Session.class);
        userId = session.getUserID();
    }

    private String userId;

    public String getText() {
        return text;
    }

    public boolean isRich() {
        return isRich;
    }

    public String getUserId() {
        return userId;
    }
}
