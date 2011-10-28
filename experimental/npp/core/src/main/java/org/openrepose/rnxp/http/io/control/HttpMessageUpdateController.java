package org.openrepose.rnxp.http.io.control;

import java.io.InputStream;
import org.openrepose.rnxp.decoder.partial.HttpMessagePartial;

/**
 *
 * @author zinic
 */
public interface HttpMessageUpdateController {

    void blockingRequestUpdate(UpdatableHttpMessage updatableMessage) throws InterruptedException;
    
    void applyPartial(HttpMessagePartial partial);
    
    InputStream connectInputStream();
}
