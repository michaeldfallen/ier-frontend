package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.service.apiservice.CrownApplication
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class CrownApplicationTests extends UnitTestSuite {

  behavior of "InprogressCrown.displayPartner"

  it should "return false when statement is incomplete" in {
    val application = InprogressCrown(statement = None)
    application.displayPartner should be (false)
  }

  it should "return false when crownServant & crownPartner = true" in {
    val application = InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = true,
        crownPartner = true,
        councilEmployee = false,
        councilPartner = false
      ))
    )
    application.displayPartner should be (false)
  }

  it should "return false when crownServant = true" in {
    val application = InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = true,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = false
      ))
    )
    application.displayPartner should be (false)
  }

  it should "return false when councilEmployee & councilPartner = true" in {
    val application = InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = false,
        councilEmployee = true,
        councilPartner = true
      ))
    )
    application.displayPartner should be (false)
  }

  it should "return true when councilPartner = true" in {
    val application = InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = true
      ))
    )
    application.displayPartner should be (true)
  }

  it should "return false when councilEmployee = true" in {
    val application = InprogressCrown(
      statement = Some(CrownStatement(
        crownServant = false,
        crownPartner = false,
        councilEmployee = true,
        councilPartner = false
      ))
    )
    application.displayPartner should be (false)
  }

  behavior of "CrownApplication.toApi"

  it should "generate the expected payload" in {
    lazy val application = createCrownApplication

    val apiMap = application.toApiMap

    val expected = Map(
      "applicationType" -> "crown",
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
      "pproperty" -> "Fake House 2",
      "pstreet" -> "222 Fake Street",
      "plocality" -> "South Fake 2",
      "ptown" -> "Fakerton 2",
      "parea" -> "Fakesbury 2",
      "puprn" -> "12345",
      "ppostcode" -> "SW123 4AB",
      "corrcountry" -> "uk",
      "corrpostcode" -> "XX123 4XX",
      "corraddressline1" -> "The (fake) Manor House",
      "corraddressline2" -> "123 Fake Street",
      "corraddressline3" -> "North Fake",
      "corraddressline4" -> "Fakerton",
      "corraddressline5" -> "Fakesbury",
      "crwn" -> "true",
      "scrwn" -> "false",
      "bc" -> "false",
      "sbc" -> "false",
      "role" -> "my job title",
      "payr" -> "123456",
      "dept" -> "MoJ",
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
    lazy val application = createCrownApplication.copy(
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
      "applicationType" -> "crown",
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
      "pproperty" -> "Fake House 2",
      "pstreet" -> "222 Fake Street",
      "plocality" -> "South Fake 2",
      "ptown" -> "Fakerton 2",
      "parea" -> "Fakesbury 2",
      "puprn" -> "12345",
      "ppostcode" -> "SW123 4AB",
      "corrcountry" -> "uk",
      "corrpostcode" -> "XX123 4XX",
      "corraddressline1" -> "The (fake) Manor House",
      "corraddressline2" -> "123 Fake Street",
      "corraddressline3" -> "North Fake",
      "corraddressline4" -> "Fakerton",
      "corraddressline5" -> "Fakesbury",
      "crwn" -> "true",
      "scrwn" -> "false",
      "bc" -> "false",
      "sbc" -> "false",
      "role" -> "my job title",
      "dept" -> "MoJ",
      "payr" -> "123456",
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

  private def createCrownApplication =
    CrownApplication(
      statement = Some(CrownStatement(
        crownServant = true,
        crownPartner = false,
        councilEmployee = false,
        councilPartner = false
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
        lineOne = Some("Fake House 2"),
        lineTwo = Some("222 Fake Street"),
        lineThree = Some("South Fake 2"),
        city = Some("Fakerton 2"),
        county = Some("Fakesbury 2"),
        postcode = "SW12 34AB",
        uprn = Some("12345"),
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
      job = Some(Job(
        jobTitle = Some("my job title"),
        payrollNumber = Some("123456"),
        govDepartment = Some("MoJ")
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
