package uk.gov.gds.ier.test

import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.service.LocateService
import uk.gov.gds.ier.model.{Success, Fail, ApiResponse, Address}
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.controller.MockConfig
import play.api.GlobalSettings

/**
 * Intended as an extension to TestHelpers; every test using FakeApplication also extends
 * TestHelpers so changes here have broad impact.
 *
 * This is a workaround to know issue with inability to set mocks to FakeApplication for individual
 * tests. First test calling FakeApplication also sets injected implementations for all downstream
 * test in the suite. That makes testing unpredictable, dependent on the test order!
 *
 * Whatever FakeApplication test is run first, sets this one test context, hence predicable results
 * whatever the order. Having not been able to set different mocks for individual tests is a
 * limitation, but at least there is the option to define behaviour dynamically in this trait.
 *
 * Locate service is emulated, with few (semi) realistic results (test config is ignored)
 * IER-API client submit against real service, based on test config settings
 */
trait FakeApplicationRedefined {

  /**
   * Override standard FakeApplication() so that every test in our suite calls this adapter instead.
   * Define one global binding, same for all test.
   */
  object FakeApplication {
    def apply() = {
      play.api.test.FakeApplication(
        withGlobal = Some(testGlobal))
    }

    def apply(withGlobal: Option[GlobalSettings]) = {
      play.api.test.FakeApplication(
        withGlobal = withGlobal)
    }

    def apply(additionalConfiguration: Map[String, String]) = {
      play.api.test.FakeApplication(
        withGlobal = Some(testGlobal),
        additionalConfiguration = additionalConfiguration)
    }
  }

  /**
   * one and only Guice test context, define bindings here
   */
  val testGlobal = new DynamicGlobal {
    override def bindings = { binder =>
      binder bind classOf[uk.gov.gds.ier.service.apiservice.IerApiService] to classOf[uk.gov.gds.ier.service.apiservice.ConcreteIerApiService]
      binder bind classOf[uk.gov.gds.ier.client.IerApiClient] toInstance dummyApiClient
      binder bind classOf[uk.gov.gds.ier.service.LocateService] toInstance dummyLocateService
    }
  }

  /**
   * Emulate behaviour of Locate Service
   */
  val dummyLocateService = new LocateService(null, null, null) {
    override def lookupGssCode(postcode: String): Option[String] = {
      Some("E09000030")
    }

    override def lookupAddress(postcode: String): List[Address] =
      postcode.trim.replaceAll(" ", "") match {
      case "WR26NJ" =>
        // real English address, real GssCode, use when calling application submit against real API service
        List(
          Address(
            lineOne = Some("1 The Cottages"),
            lineTwo = Some("Moseley Road"),
            lineThree = None,
            city = Some("Hallow"),
            county = Some("Worcestershire"),
            uprn = Some("100120595384"),
            postcode = "WR2 6NJ",
            gssCode = Some("abc")
          )
        )
      // Scottish postcode, good to test 'Scot Exit'
      case "EH37AL" =>
        List(
          Address(
            lineOne = Some("2 Coates Crescent"),
            lineTwo = None,
            lineThree = None,
            city = Some("Edinburgh"),
            county = Some("City Of Edinburgh"),
            uprn = Some("906053845"),
            postcode = "EH3 7AL",
            gssCode = Some("S12000036")
          )
        )
      case "AB123CD" =>
        List(
          Address(
            lineOne = Some("123 Fake Street"),
            lineTwo = None,
            lineThree = None,
            city = Some("Fakerton"),
            county = Some("Fakesbury"),
            uprn = Some("12345"),
            postcode = "AB12 3CD",
            gssCode = Some("E09000007")
          )
        )
      case _ => List[Address]()
    }
  }

  val dummyApiClient = new IerApiClient(new MockConfig) {
    override def post(url: String, content: String, headers: (String, String)*): ApiResponse = {
      Success("""
      {
        "id": "5360fe69036424d9ec0a1657",
        "localAuthority": {
          "name": "Malvern Hills (test)",
          "urls": [
            "http://www.malvernhills.gov.uk/",
            "http://www.malvernhills.gov.uk/cms/council-and-democracy/elections.aspx"],
          "email": "worcestershirehub@malvernhills.gov.uk.test",
          "phone": "01684 862151",
          "addressLine1": "Council House",
          "addressLine2": "Avenue Road",
          "addressLine3": "Malvern",
          "addressLine4": "",
          "postcode": "WR14 3AF"
        }
      }
      """, 0)
    }
  }
}
