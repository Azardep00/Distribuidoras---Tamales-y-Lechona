package com.tamaleslechona.tamaleslechona.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PagoWompiRepositoryTest {

    @Autowired
    private PagoWompiRepository pagoRepository;

    @Test
    void startsWithTheWompiPaymentRepositoryAndEntityMappings() {
        assertNotNull(pagoRepository);
    }
}
