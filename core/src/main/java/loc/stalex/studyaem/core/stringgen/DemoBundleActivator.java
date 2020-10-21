package loc.stalex.studyaem.core.stringgen;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DemoBundleActivator implements BundleActivator {
    private Logger logger = LoggerFactory.getLogger(DemoBundleActivator.class);

    @Override
    public void start(BundleContext bundleContext) throws Exception {
//        logger.error("Starting activation");
//        TimeUnit.SECONDS.sleep(30);
//        logger.error("Activation performed");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}
