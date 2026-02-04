package org.cswteams.ms3.ai.priority;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;

@Component
public class PriorityScaleLoader {

    private final PriorityScaleProperties properties;
    private final ResourceLoader resourceLoader;
    private final PriorityScaleValidator validator;
    private final ObjectMapper objectMapper;

    public PriorityScaleLoader(PriorityScaleProperties properties,
                               ResourceLoader resourceLoader,
                               PriorityScaleValidator validator,
                               ObjectMapper objectMapper) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    public PriorityScaleConfig load() {
        PriorityScaleConfig base = readConfig(properties.getDefaultResource());
        PriorityScaleConfig override = null;
        if (properties.getOverrideResource() != null && !properties.getOverrideResource().isBlank()) {
            override = readConfig(properties.getOverrideResource());
        }
        PriorityScaleConfig merged = PriorityScaleMerger.merge(base, override);
        validator.validate(merged);
        return merged;
    }

    private PriorityScaleConfig readConfig(String resourcePath) {
        Resource resource = resourceLoader.getResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, PriorityScaleConfig.class);
        } catch (IOException exception) {
            throw new ValidationException("Unable to read priority scale config: " + resourcePath, exception);
        }
    }
}
