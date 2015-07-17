package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.service.apiservice.OrdinaryApplication

class OrdinaryApplicationTests extends UnitTestSuite {

  behavior of "OrdinaryApplication.toApiMap"

  it should "generate the expected payload map - simple case" in {
    lazy val application = createOrdinaryApplication

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "ordinary",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "nat" -> "GB, IE",
      "oadr" -> "none",
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
      "pvote" -> "false",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000032",
      "timeTaken" -> "1234",
      "lang" -> "en",
      "webHash" -> "860da84c-74df-45b0-8ff8-d2d16ef8367a"
    )

    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }

  it should "generate the expected payload when registered while abroad" in {
    lazy val application = createOrdinaryApplication.copy(
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Overseas
      ))
    )

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "ordinary",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "nat" -> "GB, IE",
      "oadr" -> "none",
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
      "pvote" -> "false",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000032",
      "lastcategory" -> "overseas",
      "timeTaken" -> "1234",
      "lang" -> "en",
      "webHash" -> "860da84c-74df-45b0-8ff8-d2d16ef8367a"
    )

    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }

  it should "generate the expected payload when application submitted in Welsh" in {
    lazy val application = createOrdinaryApplication.copy(language = "cy")

    val expected = Map(
      "fn" -> "John",
      "mn" -> "James",
      "ln" -> "Smith",
      "applicationType" -> "ordinary",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "XX 12 34 56 D",
      "nat" -> "GB, IE",
      "oadr" -> "none",
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
      "pvote" -> "false",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000032",
      "timeTaken" -> "1234",
      "lang" -> "cy",
      "webHash" -> "860da84c-74df-45b0-8ff8-d2d16ef8367a"
    )

    val apiMap = application.toApiMap

    apiMap should matchMap(expected)
  }

  it should "generate expected payload with stripped special characters and trailing spaces" in {
    lazy val application = createOrdinaryApplication.copy(
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
      "fn" -> "Chars:",
      "mn" -> "Trailing spaces",
      "ln" -> "Tabs are here",
      "applicationType" -> "ordinary",
      "pfn" -> "James",
      "pmn" -> "John",
      "pln" -> "Smith",
      "dob" -> "1980-12-01",
      "nino" -> "AB 123 456",
      "nat" -> "GB, IE",
      "oadr" -> "none",
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
      "pvote" -> "false",
      "opnreg" -> "false",
      "post" -> "true",
      "email" -> "test@email.com",
      "phone" -> "01234 5678910",
      "refNum" -> "12345678910",
      "ip" -> "256.256.256.256",
      "gssCode" -> "E09000007",
      "pgssCode" -> "E09000032",
      "timeTaken" -> "1234",
      "lang" -> "en",
      "webHash" -> "860da84c-74df-45b0-8ff8-d2d16ef8367a"
    )


    application.toApiMap should matchMap(expected)
  }

  it should "generate expected payload with postal vote and email delivery" in {
    lazy val application = createOrdinaryApplication.copy(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.Yes),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("test@email.com")
        ))
      ))
    )
    val expected = ordinaryApplicationPayload ++ Map(
      "pvote" -> "true",
      "pvoteemail" -> "test@email.com"
    )

    application.toApiMap should matchMap(expected)
  }

  it should "generate expected payload with postal vote and post delivery" in {
    lazy val application = createOrdinaryApplication.copy(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.Yes),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    )
    val expected = ordinaryApplicationPayload + ("pvote" -> "true")

    application.toApiMap should matchMap(expected)
  }

  it should "generate expected payload with no postal vote - prefer in person" in {
    lazy val application = createOrdinaryApplication.copy(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.NoAndVoteInPerson),
        deliveryMethod = None
      ))
    )
    val expected = ordinaryApplicationPayload + ("pvote" -> "false")

    application.toApiMap should matchMap(expected)
  }

  it should "generate expected payload with no postal vote - already has one" in {
    lazy val application = createOrdinaryApplication.copy(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.NoAndAlreadyHave),
        deliveryMethod = None
      ))
    )
    val expected = ordinaryApplicationPayload + ("pvote" -> "true")

    application.toApiMap should matchMap(expected)
  }

  private val ordinaryApplicationPayload = Map(
    "fn" -> "John",
    "mn" -> "James",
    "ln" -> "Smith",
    "applicationType" -> "ordinary",
    "pfn" -> "James",
    "pmn" -> "John",
    "pln" -> "Smith",
    "dob" -> "1980-12-01",
    "nino" -> "XX 12 34 56 D",
    "nat" -> "GB, IE",
    "oadr" -> "none",
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
    "pvote" -> "false",
    "opnreg" -> "false",
    "post" -> "true",
    "email" -> "test@email.com",
    "phone" -> "01234 5678910",
    "refNum" -> "12345678910",
    "ip" -> "256.256.256.256",
    "gssCode" -> "E09000007",
    "pgssCode" -> "E09000032",
    "timeTaken" -> "1234",
    "lang" -> "en",
    "webHash" -> "860da84c-74df-45b0-8ff8-d2d16ef8367a"
  )

  private def createOrdinaryApplication =
    OrdinaryApplication(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("James"),
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        hasPreviousNameOption = "true",
        previousName = Some(Name(
          firstName = "James",
          middleNames = Some("John"),
          lastName = "Smith"
        ))
      )),
      lastRegisteredToVote = None,
      dob = Some(DateOfBirth(
        dob = Some(DOB(
          year = 1980,
          month = 12,
          day = 1
        )),
        noDob = None
      )),
      nationality = Some(IsoNationality(
        countryIsos = List("GB", "IE"),
        noNationalityReason = None
      )),
      nino = Some(Nino(
        nino = Some("XX 12 34 56 D"),
        noNinoReason = None
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
        gssCode = Some("E09000032")
      )),
      otherAddress = Some(OtherAddress(
        otherAddressOption = OtherAddress.NoOtherAddress
      )),
      openRegisterOptin = Some(false),
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.NoAndVoteInPerson),
        deliveryMethod = None
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
      language = "en",
      sessionId = "860da84c-74df-45b0-8ff8-d2d16ef8367a"
    )
}
