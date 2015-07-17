package uk.gov.gds.ier.session

import uk.gov.gds.ier.guice.{WithConfig, WithEncryption}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.security.{Base64EncodingService, EncryptionService}
import uk.gov.gds.ier.controller.MockConfig

class ResultHandlingTests extends ControllerTestSuite {

  val resultHandling = new ResultHandling
      with WithEncryption
      with WithConfig
      with WithSerialiser {
    val serialiser = jsonSerialiser
    val config = new MockConfig
    val encryptionService = new EncryptionService (new Base64EncodingService, config)
  }

  behavior of "ResultHandling.getDomain"

  it should "return None if on localhost" in {
    val request = FakeRequest("GET", "/").withHeaders("Host" -> "localhost:9000")
    resultHandling.getDomain(request) should be(None)
  }

  it should "return the HOST header from the request" in {
    val request = FakeRequest("GET", "/").withHeaders("Host" -> "foo.com")
    resultHandling.getDomain(request) should be(Some("foo.com"))
  }

  it should "strip port number from the domain" in {
    val request = FakeRequest("GET", "/").withHeaders("Host" -> "foo.com:1234")
    resultHandling.getDomain(request) should be(Some("foo.com"))
  }

  it should "return None when no Host header" in {
    val request = FakeRequest("GET", "/")
    resultHandling.getDomain(request) should be(None)
  }

  it should "return None on an empty Host header" in {
    val request = FakeRequest("GET", "/").withHeaders("Host" -> "")
    resultHandling.getDomain(request) should be(None)
  }
}
