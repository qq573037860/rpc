package com.sjq.rpc.support.codec;

import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerialize implements ObjectSerialize {

    private static LinkedBuffer buffer = LinkedBuffer.allocate();

    @Override
    public byte[] serialize(Object obj) {
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        try {
            return GraphIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class type) {
        Schema schema = RuntimeSchema.getSchema(type);
        Object result = schema.newMessage();
        GraphIOUtil.mergeFrom(bytes, result, schema);
        return result;
    }
}
