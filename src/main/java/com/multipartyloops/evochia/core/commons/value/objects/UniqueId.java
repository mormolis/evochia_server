package com.multipartyloops.evochia.core.commons.value.objects;

import com.fasterxml.jackson.annotation.JsonValue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class UniqueId {

    @JsonValue
    private final UUID id;

    public UniqueId() {
        this.id = UUID.randomUUID();
    }

    public UniqueId(String uniqueId) {
        this.id = UUID.fromString(uniqueId);
    }

    public UniqueId(byte[] uniqueId) {
        this.id = getUUIDFromBytes(uniqueId);
    }

    public byte[] toBytes(){
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(id.getMostSignificantBits())
                .putLong(id.getLeastSignificantBits());
        return bytes;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    private UUID getUUIDFromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
}
