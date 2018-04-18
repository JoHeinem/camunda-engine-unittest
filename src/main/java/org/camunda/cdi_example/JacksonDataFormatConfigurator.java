package org.camunda.cdi_example;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormat;
import org.camunda.spin.spi.DataFormatConfigurator;

public class JacksonDataFormatConfigurator implements DataFormatConfigurator<JacksonJsonDataFormat> {

    /**
     * Overrides the default configuration of the jackson object mapper, to ignore the unknown filter ids
     *
     * @param dataFormat data format
     */
    public void configure(JacksonJsonDataFormat dataFormat) {
        final ObjectMapper objectMapper = dataFormat.getObjectMapper();
        FilterProvider filterProvider = objectMapper.getSerializationConfig().getFilterProvider();
        if (filterProvider == null) {
            filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
        } else if (filterProvider instanceof SimpleFilterProvider) {
            ((SimpleFilterProvider) filterProvider).setFailOnUnknownId(false);
        }
        System.out.println("I was called---------------xxxxxxxxxxxxxxxxxxxxxx-----------------");
        final SerializationConfig newConfig = objectMapper.getSerializationConfig().withFilters(filterProvider)
                .withSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setConfig(newConfig);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Class<JacksonJsonDataFormat> getDataFormatClass() {
        return JacksonJsonDataFormat.class;
    }

}
