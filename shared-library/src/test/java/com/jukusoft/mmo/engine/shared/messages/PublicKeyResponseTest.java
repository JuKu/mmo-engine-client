package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.Serializer;
import com.jukusoft.vertx.serializer.TypeLookup;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;
import org.mockito.Mockito;

import java.security.PublicKey;

public class PublicKeyResponseTest {

    @Test
    public void testConstructor () {
        new PublicKeyResponse();
    }

    @Test
    public void testConstructor1 () {
        new PublicKeyResponse(Mockito.mock(PublicKey.class));
    }

    @Test
    public void testSerializeAndUnserialize () {
        TypeLookup.register(PublicKeyResponse.class);

        Buffer buffer = Serializer.serialize(new PublicKeyResponse());
        Serializer.unserialize(buffer);

        TypeLookup.unregister(PublicKeyResponse.class);
    }

}
