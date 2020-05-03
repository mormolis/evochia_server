package com.multipartyloops.evochia.persistance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UuidPersistenceTransformerTest {


    private UuidPersistenceTransformer uuidPersistenceTransformer;

    @BeforeEach
    void setup() {
        uuidPersistenceTransformer = new UuidPersistenceTransformer();
    }

    @Test
    void uuidToBytesConversion() {

        String stringUuid = UUID.randomUUID().toString();

        byte[] bytes = uuidPersistenceTransformer.fromString(stringUuid);
        String transformed = uuidPersistenceTransformer.getUUIDFromBytes(bytes);

        assertThat(transformed).isEqualTo(stringUuid);
    }


}