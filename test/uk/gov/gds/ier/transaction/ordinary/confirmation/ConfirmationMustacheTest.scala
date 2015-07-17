package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.shared.BlockContent
import uk.gov.gds.ier.step.Routes
import play.api.mvc.Call

class ConfirmationMustacheTest
  extends MustacheTestSuite
  with ConfirmationForms
  with WithMockAddressService
  with WithMockOrdinaryControllers
  with MockitoHelpers
  with ConfirmationMustache {

  def routes(url:String) = Routes(
    get = Call("GET", url),
    post = Call("POST", url),
    editGet = Call("GET", url),
    editPost = Call("POST", url)
  )

  when(mockNameStep.routing).thenReturn(routes("/register-to-vote/edit/name"))
  when(mockDateOfBirthStep.routing).thenReturn(routes("/register-to-vote/edit/date-of-birth"))
  when(mockOtherAddressStep.routing).thenReturn(routes("/register-to-vote/edit/other-address"))
  when(mockNinoStep.routing).thenReturn(routes("/register-to-vote/edit/nino"))
  when(mockNationalityStep.routing).thenReturn(routes("/register-to-vote/edit/nationality"))
  when(mockAddressManualStep.routing).thenReturn(routes("/register-to-vote/edit/address/manual"))
  when(mockAddressSelectStep.routing).thenReturn(routes("/register-to-vote/edit/address/select"))
  when(mockPreviousAddressFirstStep.routing).thenReturn(routes("/register-to-vote/edit/previous-address"))
  when(mockOpenRegisterStep.routing).thenReturn(routes("/register-to-vote/edit/open-register"))
  when(mockPostalVoteStep.routing).thenReturn(routes("/register-to-vote/edit/postal-vote"))
  when(mockContactStep.routing).thenReturn(routes("/register-to-vote/edit/contact"))

  "In-progress application form with filled name and previous name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
        name = Some(Name(
          firstName = "John",
          middleNames = None,
          lastName = "Smith"
        )),
        previousName = Some(PreviousName(
          hasPreviousName = true,
          hasPreviousNameOption = "true",
          previousName = Some(Name(
            firstName = "Jan",
            middleNames = None,
            lastName = "Kovar"
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nameModel) = confirmation.name
    nameModel.content should be(BlockContent(List("John Smith")))
    nameModel.editLink should be("/register-to-vote/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be(BlockContent(List("Jan Kovar")))
    prevNameModel.editLink should be("/register-to-vote/edit/name")
  }

  "In-progress application form with filled name and previous name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
        name = Some(Name(
          firstName = "John",
          middleNames = Some("Walker Junior"),
          lastName = "Smith"
        )),
        previousName = Some(PreviousName(
          hasPreviousName = true,
          hasPreviousNameOption = "true",
          previousName = Some(Name(
            firstName = "Jan",
            middleNames = Some("Janko Janik"),
            lastName = "Kovar"
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nameModel) = confirmation.name
    nameModel.content should be(BlockContent(List("John Walker Junior Smith")))
    nameModel.editLink should be("/register-to-vote/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be(BlockContent(List("Jan Janko Janik Kovar")))
    prevNameModel.editLink should be("/register-to-vote/edit/name")
  }

  "In-progress application form with filled date of birth" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      dob = Some(DateOfBirth(
        dob = Some(DOB(
          year = 1978,
          month = 1,
          day = 22
        )),
        noDob = None))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(dateOfBirthModel) = confirmation.dateOfBirth
    dateOfBirthModel.content should be(BlockContent(List("22 January 1978")))
    dateOfBirthModel.editLink should be("/register-to-vote/edit/date-of-birth")
  }

  "In-progress application form with filled date of birth excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      dob = Some(DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("I have no idea!"),
          range = Some("18to70")
        ))))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(dateOfBirthModel) = confirmation.dateOfBirth
    dateOfBirthModel.content should be(BlockContent(List(
      "You are unable to provide your date of birth because: I have no idea!",
      "I am over 18 years old")))
    dateOfBirthModel.editLink should be("/register-to-vote/edit/date-of-birth")
  }

  "In-progress application form with british nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {

    val partiallyFilledApplicationForm =
      confirmationForm.fillAndValidate(completeOrdinaryApplication.copy(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ) )

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be(BlockContent(List("I am British")))
    nationalityModel.editLink should be("/register-to-vote/edit/nationality")
  }

  "In-progress application form with irish nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm =
      confirmationForm.fillAndValidate(completeOrdinaryApplication.copy(
      nationality = Some(PartialNationality(
        british = None,
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be(BlockContent(List("I am Irish")))
    nationalityModel.editLink should be("/register-to-vote/edit/nationality")
  }

  "In-progress application form with other nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm =
      confirmationForm.fillAndValidate(completeOrdinaryApplication.copy(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("Spain", "France", "Germany"),
        noNationalityReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be(
        BlockContent(List("I am a citizen of Spain, France and Germany")))
    nationalityModel.editLink should be("/register-to-vote/edit/nationality")
  }

  "In-progress application form with nationality excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = Some("I'm from Mars")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(nationalityModel) = confirmation.nationality
    nationalityModel.content should be(BlockContent(List(
      "I cannot provide my nationality because:",
      "I'm from Mars")))
    nationalityModel.editLink should be("/register-to-vote/edit/nationality")
  }

  "In-progress application form with valid nino" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(ninoModel) = confirmation.nino
    ninoModel.content should be(BlockContent(List("AB123456C")))
    ninoModel.editLink should be("/register-to-vote/edit/nino")
  }

  "In-progress application form with nino excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      nino = Some(Nino(
        nino = None,
        noNinoReason = Some("Recently arrived to the UK")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(ninoModel) = confirmation.nino
    ninoModel.content should be(BlockContent(List(
      "I cannot provide my national insurance number because:",
      "Recently arrived to the UK")))
    ninoModel.editLink should be("/register-to-vote/edit/nino")
  }

  "In-progress application form with valid address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street"),
        uprn = Some("12345678"),
        postcode = "AB12 3CD",
        manualAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(addressModel) = confirmation.address
    addressModel.content should be(BlockContent(List("123 Fake Street", "AB12 3CD")))
    addressModel.editLink should be("/register-to-vote/edit/address/select")
  }

  "In-progress application form with valid manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      address = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "AB12 3CD",
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester")))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(addressModel) = confirmation.address
    addressModel.content should be(BlockContent(List(
      "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester",
      "AB12 3CD")))
    addressModel.editLink should be("/register-to-vote/edit/address/manual")
  }


  "In-progress application form with valid previous address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromUk),
        previousAddress = Some(PartialAddress(
          addressLine = Some("123 Fake Street"),
          uprn = Some("12345678"),
          postcode = "AB12 3CD",
          manualAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(List("123 Fake Street", "AB12 3CD")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application form with valid previous manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromUk),
        previousAddress = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "AB12 3CD",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(List(
      "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester",
      "AB12 3CD")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application form with valid previous address registered at when living abroad" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromAbroadRegistered),
        previousAddress = Some(PartialAddress(
          addressLine = Some("123 Fake Street"),
          uprn = Some("12345678"),
          postcode = "AB12 3CD",
          manualAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(List("123 Fake Street", "AB12 3CD")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application form with valid previous manual address registered at when living abroad" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromAbroadRegistered),
        previousAddress = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "AB12 3CD",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(List(
      "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester",
      "AB12 3CD")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application form without previous address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.NotMoved),
        previousAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(
        BlockContent(List("I have not moved in the last 12 months")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application form with previous address being abroad but not registered" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromAbroadNotRegistered),
        previousAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(List(
      "I moved from abroad, but I was not registered to vote there")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application form with valid other/second address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(
        otherAddressOption =  OtherAddress.NoOtherAddress
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(secondAddressModel) = confirmation.secondAddress
    secondAddressModel.content should be(BlockContent(List("I don’t have a second address")))
    secondAddressModel.editLink should be("/register-to-vote/edit/other-address")
  }


  "In-progress application form with open register set to true" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      openRegisterOptin = Some(true)
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be(
        BlockContent(List("I want to include my name and address on the open register")))
    openRegisterModel.editLink should be("/register-to-vote/edit/open-register")
  }

  "In-progress application form without open register flag" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      openRegisterOptin = Some(false)
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be(
        BlockContent(List("I don’t want my name and address on the open register")))
    openRegisterModel.editLink should be("/register-to-vote/edit/open-register")
  }

  "In-progress application form with postal vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.Yes),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalVoteMode) = confirmation.postalVote
    postalVoteMode.content should be(
        BlockContent(List("I want you to mail me a postal vote application form")))
    postalVoteMode.editLink should be("/register-to-vote/edit/postal-vote")
  }

  "In-progress application form with postal vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.Yes),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("john@email.com")
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalVoteMode) = confirmation.postalVote
    postalVoteMode.content should be(BlockContent(List(
      "I want you to email a postal vote application form to:",
      "john@email.com")))
    postalVoteMode.editLink should be("/register-to-vote/edit/postal-vote")
  }

  "In-progress application form with no postal vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      postalVote = Some(PostalVote(
        postalVoteOption = Some(PostalVoteOption.NoAndVoteInPerson),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalVoteMode) = confirmation.postalVote
    postalVoteMode.content should be(BlockContent(List(
      "I don’t want to apply for a postal vote")))
    postalVoteMode.editLink should be("/register-to-vote/edit/postal-vote")
  }

  "In-progress application form with email contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      contact = Some(Contact(
        post = false,
        phone = None,
        email = Some(ContactDetail(true, Some("antoine@gds.com")))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be(BlockContent(List("By email: antoine@gds.com")))
    contactModel.editLink should be("/register-to-vote/edit/contact")
  }

  "In-progress application form with phone contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      contact = Some(Contact(
        post = false,
        phone = Some(ContactDetail(true, Some("+44 5678 907 546 ext. 3567-098"))),
        email = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be(BlockContent(List("By phone: +44 5678 907 546 ext. 3567-098")))
    contactModel.editLink should be("/register-to-vote/edit/contact")
  }

  "In-progress application form with post contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      contact = Some(Contact(
        post = true,
        phone = None,
        email = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be(BlockContent(List("By post")))
    contactModel.editLink should be("/register-to-vote/edit/contact")
  }

  behavior of "InProgressForm.confirmationNationalityString"

  it should "handle just irish checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be("I am Irish")
  }

  it should "handle just british checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be("I am British")
  }

  it should "handle british and irish checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be("I am British and Irish")
  }

  it should "handle british, irish and an other nationality checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand"
    )
  }

  it should "handle british, irish and two other nationalities checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand and India"
    )
  }

  it should "handle british, irish and three other nationalities checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = Some(true),
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India", "Japan"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am British, Irish and a citizen of New Zealand, India and Japan"
    )
  }

  it should "handle an other nationality checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am a citizen of New Zealand"
    )
  }

  it should "handle an three other nationalities checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India", "Japan"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am a citizen of New Zealand, India and Japan"
    )
  }

  it should "handle two other nationalities checked" in runningApp {
    val form = confirmationForm.fillAndValidate(InprogressOrdinary(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("New Zealand", "India"),
        noNationalityReason = None
      ))
    ))
    val confirmation = new ConfirmationBlocks(form)
    confirmation.confirmationNationalityString should be(
      "I am a citizen of New Zealand and India"
    )
  }

  "In-progress application form with valid previous UK address" should
  "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromUk),
        previousAddress = Some(PartialAddress(
          addressLine = Some("123 Fake Street"),
          uprn = Some("12345678"),
          postcode = "AB12 3CD",
          manualAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(List(
      "123 Fake Street", "AB12 3CD")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application form with valid previous UK manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromUk),
        previousAddress = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "AB12 3CD",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(List(
      "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester",
      "AB12 3CD")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }


  "In-progress application form with previous postcode being Northern Ireland" should
    "generate confirmation mustache model with an information for NI users" in runningApp {

    when(addressService.isNothernIreland("BT7 1AA")).thenReturn(true)

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromUk),
        previousAddress = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "BT7 1AA",
          manualAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress

    previousAddressModel.content should be(BlockContent(List(
      "BT7 1AA", "I was previously registered in Northern Ireland")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application form without previous UK address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in runningApp {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.NotMoved),
        previousAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(List("I have not moved in the last 12 months")))
    previousAddressModel.editLink should be("/register-to-vote/edit/previous-address")
  }

  "In-progress application confirmation form" should
    "display all postcodes uppercased" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      address = Some(PartialAddress(
          addressLine = Some("line1"),
          uprn = Some("1234"),
          postcode = "ab123cd",
          manualAddress = None
      )),
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromUk),
        previousAddress = Some(PartialAddress(
          addressLine = Some("line1"),
          uprn = Some("12341234"),
          postcode = "aa123bb",
          manualAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    confirmation.address.get.content should be(BlockContent(List("line1", "AB123CD")))
    confirmation.previousAddress.get.content should be(BlockContent(List("line1", "AA123BB")))
  }

  "In-progress application confirmation form" should
    "display NINO uppercased" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOrdinary(
      nino = Some(Nino(
        nino = Some("ab123456"),
        noNinoReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    confirmation.nino.get.content should be(BlockContent(List("AB123456")))
  }
}
