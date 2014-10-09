package org.openrepose.filters.cnorm.normalizer;

import com.rackspace.papi.components.normalization.config.HeaderFilterList;
import com.rackspace.papi.components.normalization.config.HttpHeader;
import org.openrepose.core.filter.logic.FilterDirector;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author jhopper
 */
public class HeaderNormalizer {

    private final List<HttpHeader> headers;
    private final boolean isBlacklist;

    public HeaderNormalizer(HeaderFilterList headerFilterList, boolean isBlacklist) {
        this.headers = isBlacklist ? headerFilterList.getBlacklist().getHeader() : headerFilterList.getWhitelist().getHeader();
        this.isBlacklist = isBlacklist;
    }

    public void normalizeHeaders(HttpServletRequest request, FilterDirector currentDirector) {
        final Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            final String requestHeaderName = headerNames.nextElement();

            boolean found = false;

            for (HttpHeader configuredHeader : headers) {
                if (configuredHeader.getId().equalsIgnoreCase(requestHeaderName)) {
                    found = true;
                    break;
                }
            }

            if (found && isBlacklist || !found && !isBlacklist) {
                currentDirector.requestHeaderManager().removeHeader(requestHeaderName);
            }
        }
    }
}
