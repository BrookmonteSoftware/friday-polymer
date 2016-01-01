/**
 * 
 */
package com.brookmonte.friday.FridayPolymer;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.stereotype.Component;

/**
 * @author Pete
 *
 */
@Component
public class ServletCustomizer implements EmbeddedServletContainerCustomizer
{

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container)
    {
        //MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        //mappings.add("js", "application/javascript");
        //container.setMimeMappings(mappings);
    }
}