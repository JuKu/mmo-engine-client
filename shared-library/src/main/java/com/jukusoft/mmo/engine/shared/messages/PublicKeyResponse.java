package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.EncryptionUtils;
import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SBytes;

import java.security.PublicKey;
import java.util.Objects;

@MessageType(type = 0x01, extendedType = 0x01)
@ProtocolVersion(1)
public class PublicKeyResponse implements SerializableObject {

    @SBytes
    protected byte[] publicKeyBytes = new byte[0];

    public PublicKeyResponse () {
        //
    }

    public PublicKeyResponse (PublicKey publicKey) {
        this.setPublicKey(publicKey);
    }

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }

    public void setPublicKey (PublicKey publicKey) {
        Objects.requireNonNull(publicKey);

        //convert public key to byte array
        this.publicKeyBytes = EncryptionUtils.convertPublicKeyToByteArray(publicKey);
    }

    public PublicKey getPublicKey () {
        //generate public key from byte array
        try {
            return EncryptionUtils.getPubKeyFromArray(this.publicKeyBytes);
        } catch (Exception e) {
            Log.e("Encryption", "received RSA public key is invalide.", e);
            throw new IllegalArgumentException("received RSA public key is invalide.");
        }
    }

}
