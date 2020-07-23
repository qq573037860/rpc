package com.sjq.rpc.support.codec;

public interface ObjectSerialize {

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class type);

}
