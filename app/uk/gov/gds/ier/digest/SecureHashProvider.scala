package uk.gov.gds.ier.digest

import org.apache.commons.codec.digest.DigestUtils._
import org.apache.commons.codec.binary.Base64
import uk.gov.gds.common.config.Config
import com.google.inject.Singleton

trait SecureHashProvider {

  private val hashIterationsNumber = 1

  def getHashAsBase64(data: String, salt: Option[String] = Some(getStaticSalt)) =
    new String(Base64.encodeBase64(getHash(data, salt)))

  def getHash(data: String, salt: Option[String] = Some(getStaticSalt)) =
    getNthHash(getSaltedData(data, salt), hashIterationsNumber)

  private def getNthHash(data: Array[Byte], numberOfIterations: Int) : Array[Byte] =
    numberOfIterations match {
      case 0 => data
      case n => getNthHash(sha256(data), n - 1)
    }

  private def getSaltedData(data: String, salt: Option[String]) = (data + salt.getOrElse("")).getBytes("UTF-8")

  // TODO: change this when we have confidential data storage sorted out
  private def getStaticSalt = Config("nino.hash.salt", "SomeStaticTemporarySalt")
}

@Singleton
class ShaHashProvider extends SecureHashProvider