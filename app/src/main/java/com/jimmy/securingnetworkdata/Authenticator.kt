package com.jimmy.securingnetworkdata

import java.security.*
import java.security.spec.X509EncodedKeySpec

class Authenticator {
    private val publicKey: PublicKey
    private val privateKey: PrivateKey


    init {
//        Created a KeyPairGenerator instance for the Elliptic Curve (EC) type
        val keyPairGenerator = KeyPairGenerator.getInstance("EC") // 1

//        Initialized the object with the recommended key size of 256 bits
        keyPairGenerator.initialize(256) // 2

//        Generated a key pair, which contains both the public and private key.
        val keyPair = keyPairGenerator.genKeyPair() // 3

        // Set the publicKey and privateKey variables of your class to those newly generated keys.
        publicKey = keyPair.public
        privateKey = keyPair.private
    }


    /**
     * This method takes in a ByteArray. It initializes a Signature object with the private key that is used for signing,
     * adds the ByteArray data and then returns a ByteArray signature.
     *
     */
    fun sign(data: ByteArray): ByteArray {
        val signature = Signature.getInstance("SHA1withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }


    /**
     * the Signature object is initialized with the public key that is needed for verification.
     * the signature object is updated with the data to be verified and then the update method is called to do the verification.
     * The method returns true if the verification was successful.
     *
     */
    fun verify(signature: ByteArray, data: ByteArray): Boolean {
        val verifySignature = Signature.getInstance("SHA1withECDSA")
        verifySignature.initVerify(publicKey)
        verifySignature.update(data)
        return verifySignature.verify(signature)
    }

    /**
     * verify data for a public key that is sent to you. verify method that accepts an external public key
     * converts a Base64 public key string into a PublicKey object. Base64 is a format that allows raw data bytes
     * to be easily passed over the network as a string.
     */
    fun verify(signature: ByteArray, data: ByteArray, publicKeyString: String): Boolean {
        val verifySignature = Signature.getInstance("SHA1withECDSA")
        val bytes = android.util.Base64.decode(publicKeyString, android.util.Base64.DEFAULT)
        val publicKey =  KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(bytes))
        verifySignature.initVerify(publicKey)
        verifySignature.update(data)
        return verifySignature.verify(signature)
    }
}