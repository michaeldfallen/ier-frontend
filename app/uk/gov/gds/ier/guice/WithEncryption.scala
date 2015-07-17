package uk.gov.gds.ier.guice

import uk.gov.gds.ier.security.EncryptionService

trait WithEncryption {

  val encryptionService : EncryptionService

}
