package uk.gov.gds.ier.security

import javax.crypto.{BadPaddingException, Cipher}
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import java.security.SecureRandom
import org.joda.time.DateTime

class EncryptionService @Inject ()(base64EncodingService:Base64EncodingService, config:Config) {

    private def aesKeyFromBase64EncodedString(key: String) =
      new SecretKeySpec(base64EncodingService.decode(key), "AES")

    def encrypt(content: String): (String, String) = {
      val encipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
      val ivSpecBytes = generateInitializationVector
      encipher.init(Cipher.ENCRYPT_MODE,
        aesKeyFromBase64EncodedString(config.cookiesAesKey),
        new IvParameterSpec(ivSpecBytes))
      (base64EncodingService.encode(encipher.doFinal(content.getBytes)),
        base64EncodingService.encode(ivSpecBytes))
    }

    def decrypt(content: String, iv: String): String = {
      val ivBytes = base64EncodingService.decode(iv)
      val encipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

      try {
        encipher.init(Cipher.DECRYPT_MODE,
          aesKeyFromBase64EncodedString(config.cookiesAesKey),
          new IvParameterSpec(ivBytes))
        new String (encipher.doFinal(base64EncodingService.decode(content)))
      } catch {
        case ex: BadPaddingException => throw new DecryptionFailedException(
          "Most likely caused by incorrect IV", ex)
      }
    }

    private def generateInitializationVector: Array[Byte] = {
      val bytes = Array.ofDim[Byte](16)
      val rnd = new SecureRandom()
      rnd.setSeed(DateTime.now.getMillis())
      rnd.nextBytes(bytes)
      bytes
    }
}
