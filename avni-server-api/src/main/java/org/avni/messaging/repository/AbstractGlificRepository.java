package org.avni.messaging.repository;

import org.avni.server.util.ObjectMapperSingleton;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public abstract class AbstractGlificRepository {
    protected String getJson(String fileNameWithoutExtension) {
        try {
            return ObjectMapperSingleton.getObjectMapper().readTree(this.getClass().getResource(String.format("/external/glific/%s.json", fileNameWithoutExtension))).toString();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    protected String populatePaginationDetails(String requestTemplate, Pageable pageable) {
        return requestTemplate.replace("\"${offset}\"", Long.toString(pageable.getOffset()))
                .replace("\"${limit}\"", Integer.toString(pageable.getPageSize()));
    }
}
