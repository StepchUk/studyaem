package loc.stalex.studyaem.coreconsumer;

import loc.stalex.studyaem.core.stringgen.StringGeneratorImpl;

public class StringGeneratorVersion1Consumer {

    public String invokeGenerator() {
        return new StringGeneratorImpl().generateString();
    }
}
