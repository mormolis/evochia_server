package com.multipartyloops.evochia.core.commons.value.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UniqueIdTest {

    @Test
    void uniqueIdIsSerializable() throws JsonProcessingException {
        class TestObject {
            private UniqueId uniqueId;

            public UniqueId getUniqueId() {
                return uniqueId;
            }

            public void setUniqueId(UniqueId uniqueId) {
                this.uniqueId = uniqueId;
            }
        }

        UniqueId uniqueId = new UniqueId();
        TestObject value = new TestObject();
        value.setUniqueId(uniqueId);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(value);
        assertThat(s).isEqualTo("{\"uniqueId\":\"" + uniqueId + "\"}");
    }


}