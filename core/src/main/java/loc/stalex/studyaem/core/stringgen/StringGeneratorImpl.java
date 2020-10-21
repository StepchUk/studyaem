package loc.stalex.studyaem.core.stringgen;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Random;

@Component(immediate = true, service = StringGenerator.class)
@Designate(ocd = StringGeneratorConfig.class)
public class StringGeneratorImpl implements StringGenerator {

    private String[] parts;
    private int length;
    private boolean truncateToFit;

    @Activate
    @Modified
    public void activate(StringGeneratorConfig config) {
        this.parts = config.parts();
        this.length = config.length();
        this.truncateToFit = config.truncateToFit();
    }

    @Override
    public String generateString() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        while (sb.length() < length) {
            sb.append(parts[r.nextInt(parts.length)]);
        }

        if (truncateToFit && sb.length() > length) {
            sb.delete((int) length, sb.length());
        }

        return sb.toString();
    }
}
