package uk.gov.gds.ier.security

import uk.gov.gds.ier.test.MockingTestSuite
import uk.gov.gds.ier.config.Config

class EncryptionTests extends MockingTestSuite {

  private val jsonToEncrypt = """{key:"value"}"""
  private val mockedConfig = mock[Config]
  private val encryptionService = new EncryptionService(new Base64EncodingService, mockedConfig)

  when(mockedConfig.cookiesAesKey).thenReturn("J1gs7djvi9/ecFHj0gNRbHHWIreobplsWmXnZiM2reo=")

  it should "encrypt/decrypt a block using an AES key" in {
    val (encryptionOutput, encryptionIV) = encryptionService.encrypt(jsonToEncrypt)
    encryptionService.decrypt(encryptionOutput, encryptionIV) should be(jsonToEncrypt)
  }

  it should "encrypt with AES returns message different than original" in {
    val (encryptionOutput, encryptionIV) = encryptionService.encrypt(jsonToEncrypt)
    jsonToEncrypt should not be(encryptionOutput)
  }

  it should "encrypt twice with AES returns different encrypted content" in {
    val (encryptionOutput1, encryptionIV1) = encryptionService.encrypt(jsonToEncrypt)
    val (encryptionOutput2, encryptionIV2) = encryptionService.encrypt(jsonToEncrypt)
    encryptionOutput1 should not be (encryptionOutput2)
    encryptionIV1 should not be (encryptionIV2)
  }
}
