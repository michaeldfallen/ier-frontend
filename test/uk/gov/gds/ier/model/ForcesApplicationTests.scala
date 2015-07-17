package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.service.apiservice.ForcesApplication

class ForcesApplicationTests extends UnitTestSuite {

  it should "generate the expected payload" in {
    lazy val application = createForcesApplication

    val apiMap = application.toApiMap

    val expected = Map(
      "applicationType" -> "forces",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "pfn" -> "George",
      "pmn" -> "Jeffrey",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "nat" -> "GB, IE",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "XX123 4XX",
      "pproperty" -> "The (fake) Cottage",
      "pstreet" -> "321 Fake Street",
      "plocality" -> "South Fake",
      "ptown" -> "Fakererly",
      "parea" -> "Fakesborough",
      "puprn" -> "54321",
      "ppostcode" -> "XX342 1XX",
      "corrcountry" -> "uk",
      "corrpostcode" -> "XX123 4XX",
      "corraddressline1" -> "The (fake) Manor House",
      "corraddressline2" -> "123 Fake Street",
      "corraddressline3" -> "North Fake",
      "corraddressline4" -> "Fakerton",
      "corraddressline5" -> "Fakesbury",
      "saf" -> "false",
      "rank" -> "Captain",
      "serv" -> "Royal Air Force",
      "servno" -> "1234567",
      "pvote" -> "true",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000339",
      "timeTaken" -> "1234",
      "webHash" -> "860da84c-74df-45b0-8ff8-d2d16ef8367a",
      "ukAddr" -> "resident"
    )

    apiMap should matchMap(expected)
  }

  it should "generate expected payload with stripped special characters and trailing spaces" in {
    lazy val application = createForcesApplication.copy(
      name = Some(Name(
        firstName = "Chars:<>|",
        middleNames = Some(" Trailing spaces\t  \t"),
        lastName = "	Tabs	are	here"
      )),
      nino = Some(Nino(
        nino = Some("\tAB\t123\t456\t"),
        noNinoReason = None
      ))
    )

    val expected = Map(
      "applicationType" -> "forces",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "fn" -> "Chars:",
      "mn" -> "Trailing spaces",
      "ln" -> "Tabs are here",
      "pfn" -> "George",
      "pmn" -> "Jeffrey",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "AB 123 456",
      "nat" -> "GB, IE",
      "regproperty" -> "The (fake) Manor House",
      "regstreet" -> "123 Fake Street",
      "reglocality" -> "North Fake",
      "regtown" -> "Fakerton",
      "regarea" -> "Fakesbury",
      "reguprn" -> "12345",
      "regpostcode" -> "XX123 4XX",
      "pproperty" -> "The (fake) Cottage",
      "pstreet" -> "321 Fake Street",
      "plocality" -> "South Fake",
      "ptown" -> "Fakererly",
      "parea" -> "Fakesborough",
      "puprn" -> "54321",
      "ppostcode" -> "XX342 1XX",
      "corrcountry" -> "uk",
      "corrpostcode" -> "XX123 4XX",
      "corraddressline1" -> "The (fake) Manor House",
      "corraddressline2" -> "123 Fake Street",
      "corraddressline3" -> "North Fake",
      "corraddressline4" -> "Fakerton",
      "corraddressline5" -> "Fakesbury",
      "saf" -> "false",
      "rank" -> "Captain",
      "serv" -> "Royal Air Force",
      "servno" -> "1234567",
      "pvote" -> "true",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000339",
      "timeTaken" -> "1234",
      "webHash" -> "860da84c-74df-45b0-8ff8-d2d16ef8367a",
      "ukAddr" -> "resident"
    )

    application.toApiMap should matchMap(expected)
  }

  private def createForcesApplication =
    ForcesApplication(
      statement = Some(Statement(
        memberForcesFlag = Some(true),
        partnerForcesFlag = None
      )),
      address = Some(Address(
        lineOne = Some("The (fake) Manor House"),
        lineTwo = Some("123 Fake Street"),
        lineThree = Some("North Fake"),
        city = Some("Fakerton"),
        county = Some("Fakesbury"),
        postcode = "XX12 34XX",
        uprn = Some("12345"),
        gssCode = Some("E09000007")
      )),
      previousAddress = Some(Address(
        lineOne = Some("The (fake) Cottage"),
        lineTwo = Some("321 Fake Street"),
        lineThree = Some("South Fake"),
        city = Some("Fakererly"),
        county = Some("Fakesborough"),
        postcode = "XX34 21XX",
        uprn = Some("54321"),
        gssCode = Some("E09000339")
      )),
      nationality = Some(IsoNationality(
        countryIsos = List("GB", "IE"),
        noNationalityReason = None
      )),
      dob = Some(DateOfBirth(
        dob = Some(DOB(
          year = 1980,
          month = 12,
          day = 1
        )),
        noDob = None
      )),
      name = Some(Name(
        firstName = "John",
        middleNames = Some("James"),
        lastName = "Smith"
      )),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        hasPreviousNameOption = "true",
        previousName = Some(Name(
          firstName = "George",
          middleNames = Some("Jeffrey"),
          lastName = "Smith"
        ))
      )),
      nino = Some(Nino(
        nino = Some("XX 12 34 56 D"),
        noNinoReason = None
      )),
      service = Some(Service(
        serviceName = Some(ServiceType.RoyalAirForce),
        regiment = None
      )),
      rank = Some(Rank(
        serviceNumber = Some("1234567"),
        rank = Some("Captain")
      )),
      contactAddress = Some (PossibleContactAddresses(
        contactAddressType = Some("uk"),
        ukAddressLine = Some("my uk address, london"),
        bfpoContactAddress = None,
        otherContactAddress = None
      )),
      openRegisterOptin = Some(false),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      )),
      contact = Some(Contact(
        post = true,
        email = Some(ContactDetail(
          contactMe = true,
          detail = Some("test@email.com")
        )),
        phone = Some(ContactDetail(
          contactMe = true,
          detail = Some("01234 5678910")
        ))
      )),
      referenceNumber = Some("12345678910"),
      ip = Some("256.256.256.256"),
      timeTaken = "1234",
      sessionId = "860da84c-74df-45b0-8ff8-d2d16ef8367a",
      ukAddr = Some("resident")
    )
}
