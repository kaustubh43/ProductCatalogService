package org.ecommerce.productcatalogservice.configurations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

    @Test
    @DisplayName("restTemplate creates a non-null RestTemplate instance")
    void restTemplate_createsNonNullInstance() {
        RestTemplateConfig config = new RestTemplateConfig();

        RestTemplate restTemplate = config.restTemplate();

        assertThat(restTemplate).isNotNull();
    }

    @Test
    @DisplayName("restTemplate creates new instances on each call")
    void restTemplate_createsNewInstances() {
        RestTemplateConfig config = new RestTemplateConfig();

        RestTemplate first = config.restTemplate();
        RestTemplate second = config.restTemplate();

        assertThat(first).isNotSameAs(second);
    }
}

