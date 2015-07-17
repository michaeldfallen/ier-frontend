package uk.gov.gds.ier.service

import uk.gov.gds.ier.test.MockingTestSuite
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import uk.gov.gds.ier.digest.ShaHashProvider
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.service.apiservice.{EroAuthorityDetails, IerApiApplicationResponse, IerApiService, ConcreteIerApiService}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class IerApiServiceTests extends MockingTestSuite {

  val testHelper = new IerApiServiceTestsHelper {}

  val successMessage = Success(s"""
  {
    "id": "5360fe69036424d9ec0a1657",
    "localAuthority": {
      "name": "Local authority name",
      "urls": ["url1", "url2"],
      "email": "some@email.com",
      "phone": "0123456789",
      "addressLine1": "line one",
      "addressLine2": "line two",
      "addressLine3": "line three",
      "addressLine4": "line four",
      "postcode": "WR26NJ"
    }
  }
  """, 0)

  behavior of "submitOrdinaryApplication"
  it should "deserialize result correctly and return expected response" in {
    val application = completeOrdinaryApplication

    val r = testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"ordinary\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitOrdinaryApplication(None, application, None, Some("1234"), "en")

    r should be(IerApiApplicationResponse(
      id = Some("5360fe69036424d9ec0a1657"),
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  "submitOrdinaryApplication with specified IP and refNum" should
    "deserialize result correctly and return expected response" in {
    val application = completeOrdinaryApplication

    val r = testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"ordinary\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitOrdinaryApplication(Some("127.0.0.1"), application, Some("55631D"), Some("1234"), "en")

    r should be(IerApiApplicationResponse(
      id = Some("5360fe69036424d9ec0a1657"),
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  it should "submit application with web hash from the application payload" in {
    val application = completeOrdinaryApplication.copy(sessionId = Some("test session id"))

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("webHash\":\"test session id\"")
        successMessage
      }
    ).submitOrdinaryApplication(None, application, None, None, "en")
  }

  behavior of "submitOverseasApplication"
  it should "deserialize result correctly and return expected response" in {
    val application = completeOverseasApplication

    val r = testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"overseas\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitOverseasApplication(None, application, None, Some("1234"))

    r should be(IerApiApplicationResponse(
      id = Some("5360fe69036424d9ec0a1657"),
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  it should "submit application with web hash from the application payload" in {
    val application = completeOverseasApplication.copy(sessionId = Some("test session id"))

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("webHash\":\"test session id\"")
        successMessage
      }
    ).submitOverseasApplication(None, application, None, None)
  }

  behavior of "submitCrownApplication"
  it should "deserialize result correctly and return expected response" in {
    val application = completeCrownApplication

    val r = testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"crown\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitCrownApplication(None, application, None, Some("1234"))

    r should be(IerApiApplicationResponse(
      id = Some("5360fe69036424d9ec0a1657"),
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  it should "submit application with web hash from the application payload" in {
    val application = completeCrownApplication.copy(sessionId = Some("test session id"))

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("webHash\":\"test session id\"")
        successMessage
      }
    ).submitCrownApplication(None, application, None, None)
  }

  it should "have ukAddr:resident when hasAddress:YesAndLivingThere" in {
    val application = completeCrownApplication.copy(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"),
          Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("ukAddr\":\"resident\"")
        successMessage
      }
    ).submitCrownApplication(None, application, None, None)
  }

  it should "have ukAddr:not-resident when hasAddress:YesAndNotLivingThere" in {
    val application = completeCrownApplication.copy(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndNotLivingThere),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"),
          Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("ukAddr\":\"not-resident\"")
        successMessage
      }
    ).submitCrownApplication(None, application, None, None)
  }

  it should "have ukAddr:no-connection when hasAddress:No" in {
    val application = completeCrownApplication.copy(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"),
          Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("ukAddr\":\"no-connection\"")
        successMessage
      }
    ).submitCrownApplication(None, application, None, None)
  }

  behavior of "submitForcesApplication"
  it should "deserialize result correctly and return expected response" in {
    val application = completeForcesApplication

    val r = testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"forces\"")
        requestJson should include("John")
        requestJson should include("Smith")
        successMessage
      }
    ).submitForcesApplication(None, application, None, Some("1234"))

    r should be(IerApiApplicationResponse(
      id = Some("5360fe69036424d9ec0a1657"),
      localAuthority = EroAuthorityDetails(
        name = "Local authority name",
        urls = "url1" :: "url2" :: Nil,
        email = Some("some@email.com"),
        phone = Some("0123456789"),
        addressLine1 = Some("line one"),
        addressLine2 = Some("line two"),
        addressLine3 = Some("line three"),
        addressLine4 = Some("line four"),
        postcode = Some("WR26NJ")
      )
    ))
  }

  it should "submit application with web hash from the application payload" in {
    val application = completeForcesApplication.copy(sessionId = Some("test session id"))

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("webHash\":\"test session id\"")
        successMessage
      }
    ).submitForcesApplication(None, application, None, None)
  }

  it should "have ukAddr:resident when hasAddress:YesAndLivingThere" in {
    val application = completeForcesApplication.copy(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"),
          Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("ukAddr\":\"resident\"")
        successMessage
      }
    ).submitForcesApplication(None, application, None, None)
  }

  it should "have ukAddr:not-resident when hasAddress:YesAndNotLivingThere" in {
    val application = completeForcesApplication.copy(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndNotLivingThere),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"),
          Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("ukAddr\":\"not-resident\"")
        successMessage
      }
    ).submitForcesApplication(None, application, None, None)
  }

  it should "have ukAddr:no-connection when hasAddress:No" in {
    val application = completeForcesApplication.copy(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"),
          Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("ukAddr\":\"no-connection\"")
        successMessage
      }
    ).submitForcesApplication(None, application, None, None)
  }

  behavior of "submitCrownApplication address hack"
  it should "cause nat being resetted and explanation with nationality inserted as nonat" in {
    val application = completeCrownApplication.copy(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("Czech"),
        noNationalityReason = None
      )),
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
            Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"crown\"")
        requestJson should not include("\"nat\"")
        requestJson should include("\"nonat\":\"Nationality is British, Irish and Czech. " +
          "This person has no UK address so needs to be set as an 'other' elector: IER-DS.\"")
        successMessage
      }
    ).submitCrownApplication(None, application, None, None)
  }

  "submitCrownApplication address hack with no nationality" should "should cause nat being resetted and explanation with nationality appended to nonat" in {
    val application = completeCrownApplication.copy(
      nationality = Some(PartialNationality(
        british = Some(false),
        irish = Some(false),
        hasOtherCountry = Some(false),
        otherCountries = Nil,
        noNationalityReason = Some("Where I was born is a mystery to me.")
      )),
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"crown\"")
        requestJson should not include("\"nat\"")
        requestJson should include("\"nonat\":\"Where I was born is a mystery to me.\\n" +
          "Nationality is unspecified. " +
          "This person has no UK address so needs to be set as an 'other' elector: IER-DS.\"")
        successMessage
      }
    ).submitCrownApplication(None, application, None, None)
  }

  behavior of "submitForcesApplication address hack"
  it should "cause 'nat' being resetted and explanation with nationality inserted as 'nonat'" in {
    val application = completeForcesApplication.copy(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("Czech"),
        noNationalityReason = None
      )),
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"forces\"")
        requestJson should include("ukAddr\":\"no-connection\"")
        requestJson should not include("\"nat\"")
        requestJson should include("\"nonat\":\"Nationality is British, Irish and Czech. " +
          "This person has no UK address so needs to be set as an 'other' elector: IER-DS.\"")
        successMessage
      }
    ).submitForcesApplication(None, application, None, None)
  }

  "submitForcesApplication address hack with no nationality" should "cause 'nat' being resetted and explanation with nationality appended to 'nonat'" in {
    val application = completeForcesApplication.copy(
      nationality = Some(PartialNationality(
        british = Some(false),
        irish = Some(false),
        hasOtherCountry = Some(false),
        otherCountries = Nil,
        noNationalityReason = Some("Where I was born is a mystery to me.")
      )),
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None
        ))
      ))
    )

    testHelper.fakeServiceCall(
      requestJson => {
        requestJson should include("applicationType\":\"forces\"")
        requestJson should not include("\"nat\"")
        requestJson should include("\"nonat\":\"Where I was born is a mystery to me.\\n" +
          "Nationality is unspecified. " +
          "This person has no UK address so needs to be set as an 'other' elector: IER-DS.\"")
        successMessage
      }
    ).submitForcesApplication(None, application, None, None)
  }

  behavior of "getLocalAuthroityByGssCode"
  it should "support missing fields for local authority contact info" in {

    val json = """
      {
        "gssCode": "E09000030",
        "contactDetails": {
          "addressLine1": "address_line_1",
          "postcode": "a11aa",
          "emailAddress": "email@address.com",
          "phoneNumber": "0123456789",
          "name": "Tower Hamlets"
        },
        "eroIdentifier": "tower-hamlets",
        "eroDescription": "Tower Hamlets"
      }
    """
    val mockApiClient = mock[IerApiClient]
    val mockConfig = mock[Config]
    val mockAddressService = mock[AddressService]
    val mockSharHashProvider = mock[ShaHashProvider]
    val mockIsoCountryService = mock[IsoCountryService]

    val ierApiService = new ConcreteIerApiService (mockApiClient, jsonSerialiser, mockConfig,
        mockAddressService, mockSharHashProvider, mockIsoCountryService)

    when(mockApiClient.get(any[String], any[(String, String)])).thenReturn(Success(json, 0))
    val authority = ierApiService.getLocalAuthorityByGssCode("123")

    authority should have (
      'gssCode (Some("E09000030")),
      'eroIdentifier (Some("tower-hamlets")),
      'eroDescription (Some("Tower Hamlets")),
      'contactDetails (Some( LocalAuthorityContactDetails(
        addressLine1 = Some("address_line_1"),
        postcode =Some("a11aa"),
        emailAddress = Some("email@address.com"),
        phoneNumber = Some("0123456789"),
        name = Some("Tower Hamlets"))))
    )
  }

}
