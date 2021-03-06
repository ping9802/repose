/*
 * _=_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=
 * Repose
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Copyright (C) 2010 - 2015 Rackspace US, Inc.
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=_
 */
package org.openrepose.commons.utils.logging.apache.format.stock;

import org.apache.commons.lang3.StringUtils;
import org.openrepose.commons.utils.http.CommonHttpHeader;
import org.openrepose.commons.utils.logging.apache.format.FormatterLogic;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseBytesClfHandler implements FormatterLogic {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ResponseBytesClfHandler.class);
    private static final String NO_DATA = "-";

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        String contentLength = "-1";
        String contentLengthHeader = response.getHeader(CommonHttpHeader.CONTENT_LENGTH.toString());

        if (StringUtils.isNotBlank(contentLengthHeader)) {
            try {
                int parsedContentLength = Integer.parseInt(contentLengthHeader);
                contentLength = parsedContentLength == 0 ? NO_DATA : String.valueOf(parsedContentLength);
            } catch (NumberFormatException nfe) {
                LOG.warn("Unparsable integer value in Content-Length header. Value: " + contentLengthHeader, nfe);
            }
        } else {
            LOG.debug("No Content-Length header could be found");
        }

        return contentLength;
    }
}
