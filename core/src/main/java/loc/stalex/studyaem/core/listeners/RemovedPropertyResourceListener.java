package loc.stalex.studyaem.core.listeners;

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
            while (eventIterator.hasNext()) {
                Node varNode = adminSession.getNode("/var");

                String nodePath = "log/removedProperties";

                log.debug("Node exist: {}", varNode.hasNode(nodePath));

                if (!varNode.hasNode(nodePath)) {
                    Node logNode = varNode.addNode("log", NodeType.NT_FOLDER);
                    logNode.addNode("removedProperties", NodeType.NT_FOLDER);
                }

                //TODO: add method for generation random numbers for name
                Node removedNode = varNode.getNode(nodePath).addNode(eventIterator.nextEvent().getIdentifier());

                log.debug("removed property: identifier - {}; path : {}. Node exist: {}", eventIterator.nextEvent().getIdentifier(), eventIterator.nextEvent().getPath(), varNode.hasNode(nodePath));
            }
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
}
