package loc.stalex.studyaem.core.imagetranscofmconfig;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface ImageTransformConf {

    @AttributeDefinition(name = "Mode", description = "Element the string is generated from",
            type = AttributeType.STRING)
    String mode() default "";
}
