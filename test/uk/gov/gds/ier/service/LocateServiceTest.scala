package uk.gov.gds.ier.service

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.client.{LocateApiClient, ApiClient}
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse, Address}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.WithConfig

class LocateServiceTest extends UnitTestSuite {

  class MockConfig extends Config {
    override def locateUrl = "http://locate/addresses"
    override def locateAuthorityUrl = "http://locate/authority"
    override def locateApiAuthorizationToken = "abc"
  }

  behavior of "LocateService.lookupAddress"
  it should "be able to parse a response from PostcodeAnywhere" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, headers: (String, String)*) : ApiResponse = {
        if (url == "http://locate/addresses?postcode=ab123cd") {
          Success("""[
            {
              "property": "1A Fake Flat",
              "street": "Fake House",
              "area": "123 Fake Street",
              "town": "Fakerton",
              "locality": "Fakesbury",
              "uprn": 12345678,
              "postcode": "AB12 3CD",
              "gssCode": "abc"
            }
          ]""", 0)
        } else {
          Fail("Bad postcode", 200)
        }
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    val addresses = service.lookupAddress("AB12 3CD")

    addresses.size should be(1)
    addresses(0).lineOne should be(Some("1A Fake Flat"))
    addresses(0).lineTwo should be(Some("Fake House"))
    addresses(0).lineThree should be(Some("Fakesbury"))
    addresses(0).city should be(Some("Fakerton"))
    addresses(0).county should be(Some("123 Fake Street"))
    addresses(0).uprn should be(Some("12345678"))
    addresses(0).postcode should be("AB12 3CD")
    addresses(0).gssCode should be (Some("abc"))
  }

  it should "strip postcode from special characters and trailing spaces" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, headers: (String, String)*) : ApiResponse = {
        if (url == "http://locate/addresses?postcode=ab123cd") {
          Success("""[
            {
              "property": "1A Fake Flat",
              "street": "Fake House",
              "area": "123 Fake Street",
              "town": "Fakerton",
              "locality": "Fakesbury",
              "uprn": 12345678,
              "postcode": "AB12 3CD",
              "gssCode": "abc"
            }
          ]""", 0)
        } else {
          Fail("Bad postcode", 200)
        }
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    val addresses = service.lookupAddress(" \t  Ab \t1<\t|>2 3 C			d  \t")

    addresses.size should be(1)
    addresses(0).lineOne should be(Some("1A Fake Flat"))
    addresses(0).lineTwo should be(Some("Fake House"))
    addresses(0).lineThree should be(Some("Fakesbury"))
    addresses(0).city should be(Some("Fakerton"))
    addresses(0).county should be(Some("123 Fake Street"))
    addresses(0).uprn should be(Some("12345678"))
    addresses(0).postcode should be("AB12 3CD")
    addresses(0).gssCode should be (Some("abc"))
  }

  behavior of "LocateService.lookupAuthority"
  it should "lookup an authority successfully" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, headers: (String, String)*) : ApiResponse = {
        if (url == "http://locate/authority?postcode=ab123cd") {
          Success("""
            {
              "name": "Fakeston Council",
              "gssCode": "A12345678",
              "postcode": "AB12 3CD",
              "country": "England"
            }
          """, 0)
        } else {
          Fail("Bad postcode", 200)
        }
      }
    }
    val service = new LocateService(
      new FakeApiClient,
      new JsonSerialiser,
      new MockConfig
    )
    val Some(authority) = service.lookupAuthority("AB12 3CD")
    authority should have(
      'name ("Fakeston Council"),
      'gssCode ("A12345678"),
      'postcode ("AB12 3CD"),
      'country ("England")
    )
  }

  it should "strip postcode from special characters and trailing spaces" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, headers: (String, String)*) : ApiResponse = {
        if (url == "http://locate/authority?postcode=ab123cd") {
          Success("""
            {
              "name": "Fakeston Council",
              "gssCode": "A12345678",
              "postcode": "AB12 3CD",
              "country": "England"
            }
                  """, 0)
        } else {
          Fail("Bad postcode", 200)
        }
      }
    }
    val service = new LocateService(
      new FakeApiClient,
      new JsonSerialiser,
      new MockConfig
    )
    val Some(authority) = service.lookupAuthority(" \t  Ab \t1<\t|>2 3 C			d  \t")
    authority should have(
      'name ("Fakeston Council"),
      'gssCode ("A12345678"),
      'postcode ("AB12 3CD"),
      'country ("England")
    )
  }

  //it should "return None for bad postcode" in {
  //  class FakeApiClient extends LocateApiClient(new MockConfig) {
  //    override def get(url: String, headers: (String, String)*) : ApiResponse = {
  //      Fail("Bad postcode", 200)
  //    }
  //  }
  //  val service = new LocateService(
  //    new FakeApiClient,
  //    new JsonSerialiser,
  //    new MockConfig
  //  )
  //  service.lookupAuthority("AB12 3CD") should be(None)
  //}

  behavior of "LocateService.lookupGssCode"
  it should "return gssCode for a given postcode" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, headers: (String, String)*) : ApiResponse = {
        if (url == "http://locate/authority?postcode=ab123cd") {
          Success("""
            {
              "name": "Fakeston Council",
              "postcode": "AB12 3CD",
              "country": "England",
              "gssCode": "A12345678"
            }
          """, 0)
        } else {
          Fail("Bad postcode", 200)
        }
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    val gssCode = service.lookupGssCode("AB12 3CD")

    gssCode should be(Some("A12345678"))
  }

  it should "strip postcode from special characters and trailing spaces" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, headers: (String, String)*) : ApiResponse = {
        if (url == "http://locate/authority?postcode=ab123cd") {
          Success("""
            {
              "name": "Fakeston Council",
              "postcode": "AB12 3CD",
              "country": "England",
              "gssCode": "A12345678"
            }
                  """, 0)
        } else {
          Fail("Bad postcode", 200)
        }
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    val gssCode = service.lookupGssCode(" \t  Ab \t1<\t|>2 3 C			d  \t")

    gssCode should be(Some("A12345678"))
  }

  //it should "fall back to address endpoint if authority can't be found" in {
  //  class FakeApiClient extends LocateApiClient(new MockConfig) {
  //    override def get(url: String, headers: (String, String)*) : ApiResponse = {
  //      if (url == "http://locate/addresses?postcode=ab123cd") {
  //        Success("""
  //          [{
  //            "property": "1A Fake Flat",
  //            "street": "Fake House",
  //            "area": "123 Fake Street",
  //            "town": "Fakerton",
  //            "locality": "Fakesbury",
  //            "uprn": 12345678,
  //            "postcode": "AB12 3CD",
  //            "gssCode": "A12345678"
  //          }]
  //        """, 0)
  //      } else if (url == "http://locate/authority?postcode=ab123cd") {
  //        Fail("Not Found", 404)
  //      } else {
  //        Fail("Bad postcode", 200)
  //      }
  //    }
  //  }
  //  val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
  //  val gssCode = service.lookupGssCode("AB12 3CD")
  //
  //  gssCode should be(Some("A12345678"))
  //}

  behavior of "LocateService.beaconFire"
  it should "return true if locate api is up" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, header: (String, String)*) : ApiResponse = {
        if (url.contains("status")) {
          Success("""{ "status" : "up" }""", 200)
        } else Fail("I'm not really locate", 200)
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(true)
  }
  it should "return false if locate api is down" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, header: (String, String)*) : ApiResponse = {
        if (url.contains("status")) {
          Success("""{ "status" : "down" }""", 200)
        } else Fail("I'm not really locate", 200)
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
  it should "return true if locate api doesn't respond" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, header: (String, String)*) : ApiResponse = {
        Fail("I'm not really locate", 200)
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
}
