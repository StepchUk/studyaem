package loc.stalex.studyaem.core.filters;

import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;
import loc.stalex.studyaem.core.imagetranscofmconfig.ImageTransformConf;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.*;
import java.io.IOException;
import java.util.Optional;

@Component(service = Filter.class,
        property = {
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_COMPONENT,
                EngineConstants.SLING_FILTER_PATTERN + "=" + "/content/we-retail/.*",
                Constants.SERVICE_RANKING + ":Integer=-700"
        })
@Designate(ocd = ImageTransformConf.class)
public class ImageTransformFilter implements Filter {
    private final static double IMAGE_QUALITY = 0.82;
    private final static double DEGREES = 180.0;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String mode;

    @Activate
    @Modified
    public void activate(ImageTransformConf config) {
        this.mode = config.mode();
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) servletRequest;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) servletResponse;

        if (!mode.isEmpty() && getImageType(slingRequest.getResponseContentType())) {
            try {
                transformImages(mode, slingRequest, slingResponse);
            } catch (RepositoryException e) {
                logger.debug("ImageTransformFilter {}", e.getMessage());
            }
        }

        filterChain.doFilter(slingRequest, slingResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    private boolean getImageType(String contentType)
    {
        return Optional.ofNullable(contentType)
                .filter(c -> c.contains("image")).isPresent();
    }

    private void transformImages(String mode, SlingHttpServletRequest slingRequest,
                                 SlingHttpServletResponse slingResponse) throws IOException, RepositoryException {
        logger.debug("imagePath {} content type {} mode {}",
                slingRequest.getRequestURI().substring(slingRequest.getContextPath().length()),
                slingRequest.getResponseContentType(), mode);

        final Image image = getImage(slingRequest.getResource());
        final Layer layer = image.getLayer(false, false, false);

        if ("mode1".equals(mode)) {
            layer.grayscale();
        }

        if ("mode2".equals(mode)) {
            layer.rotate(DEGREES);
        }

        if ("mode3".equals(mode)) {
            layer.grayscale();
            layer.rotate(DEGREES);
        }

        layer.write(image.getMimeType(), IMAGE_QUALITY, slingResponse.getOutputStream());
    }

    private Image getImage(Resource resource) {
        if (DamUtil.isRendition(resource) || resource.isResourceType(JcrConstants.NT_FILE)
                || resource.isResourceType(JcrConstants.NT_RESOURCE)) {
            final Image image = new Image(resource);
            image.set(DownloadResource.PN_REFERENCE, resource.getPath());
            return image;
        }

        return new Image(resource);
    }
}
