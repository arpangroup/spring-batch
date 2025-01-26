package com.arpan.batch.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;

public class EnrichmentProcessor implements ItemProcessor<Foo, Foo> {
    @Autowired
    RestTemplate restTemplate;

    @Recover
    public Foo fallback(Foo foo) {
        foo.setMessage("error");
        return foo;
    }

    @CircuitBreaker
    @Override
    public Foo process(Foo item) throws Exception {
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        foo.setMessage(responseEntity.getBody());

        return foo;
    }
}
