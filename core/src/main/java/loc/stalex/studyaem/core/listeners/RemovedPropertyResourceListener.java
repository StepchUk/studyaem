package loc.stalex.studyaem.core.listeners;

import com.day.cq.commons.jcr.JcrUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import java.util.UUID;

@Component(immediate = true, service = EventListener.class)
public class RemovedPropertyResourceListener implements EventListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Session adminSession;

    @Reference
    SlingRepository repository;

    public void activate(ComponentContext context) throws Exception {
        log.debug("activating example observation");

        try {
            adminSession = repository.loginService("testwritelistener", null);
            adminSession.getWorkspace().getObservationManager().addEventListener(
                    this,
                    Event.PROPERTY_REMOVED,
                    "/content/studyaem",
                    true,
                    null,
                    null,
                    false
            );
        } catch (RepositoryException e) {
            log.debug("unable to register session {}", e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        try {
            String nodePath = "var/log/removedProperties";
            Node rootNode = adminSession.getRootNode();

            Node removedPropertyNode = rootNode.hasNode(nodePath) ?
                    rootNode.getNode(nodePath) :
                    JcrUtil.createPath(rootNode.getPath() + nodePath, "sling:Folder", adminSession);

            while (eventIterator.hasNext()) {
                Event event = eventIterator.nextEvent();
                String propertyPath = event.getPath();

                Node newNode = removedPropertyNode.addNode(getUniqueName(), NodeType.NT_UNSTRUCTURED);
                newNode.setProperty("propertyName", getPropertyName(propertyPath));
                newNode.setProperty("propertyPath", propertyPath);
            }

            adminSession.save();
        } catch (RepositoryException e) {
            log.debug("something wrong in RemovedPropertyResourceListener {}", e.getMessage());
        }
    }

    @Deactivate
    public void deactivate() {
        if (adminSession != null) {
            adminSession.logout();
        }
    }

    private String getUniqueName() {
        return UUID.randomUUID().toString();
    }

    private String getPropertyName(String propertyPath) {
        return propertyPath.substring(propertyPath.lastIndexOf("/") + 1);
    }
}
