package loc.stalex.studyaem.core.stringgen;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = StringGenerator.class)
public class StringGeneratorImpl implements StringGenerator {

    @Override
    public String generateString() {
        return "Version 1.1";
    }
}
