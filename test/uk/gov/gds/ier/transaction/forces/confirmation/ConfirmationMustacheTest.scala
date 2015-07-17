package uk.gov.gds.ier.transaction.forces.confirmation

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.WaysToVote
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.transaction.shared.{BlockContent, BlockError}

class ConfirmationMustacheTest
  extends MustacheTestSuite
  with ConfirmationForms
  with ConfirmationMustache
  with WithMockAddressService
  with WithMockForcesControllers
  with MockitoHelpers {
  
  when(mockNameStep.routing).thenReturn(routes("/register-to-vote/forces/edit/name"))
  when(mockDateOfBirthStep.routing).thenReturn(routes("/register-to-vote/forces/edit/date-of-birth"))
  when(mockNationalityStep.routing).thenReturn(routes("/register-to-vote/forces/edit/nationality"))
  when(mockNinoStep.routing).thenReturn(routes("/register-to-vote/forces/edit/nino"))
  when(mockAddressFirstStep.routing).thenReturn(routes("/register-to-vote/forces/edit/address/first"))
  when(mockPreviousAddressFirstStep.routing).thenReturn(routes("/register-to-vote/forces/edit/previous-address"))
  when(mockContactAddressStep.routing).thenReturn(routes("/register-to-vote/forces/edit/contact-address"))
  when(mockContactStep.routing).thenReturn(routes("/register-to-vote/forces/edit/contact"))
  when(mockOpenRegisterStep.routing).thenReturn(routes("/register-to-vote/forces/edit/open-register"))
  when(mockWaysToVoteStep.routing).thenReturn(routes("/register-to-vote/forces/edit/ways-to-vote"))
  when(mockServiceStep.routing).thenReturn(routes("/register-to-vote/forces/edit/service"))
  when(mockRankStep.routing).thenReturn(routes("/register-to-vote/forces/edit/rank"))

  "In-progress application form without a forces partner (member = true)" should
    "generate confirmation mustache model without forces partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      statement = Some(Statement(
        memberForcesFlag = Some(true),
        partnerForcesFlag = None
      ))
    ))

    displayPartnerBlock(partiallyFilledApplicationForm) should be(false)
  }

  "In-progress application form without a forces partner (member and partner = true)" should
    "generate confirmation mustache model without forces partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      statement = Some(Statement(
        memberForcesFlag = Some(true),
        partnerForcesFlag = Some(true)
      ))
    ))

    displayPartnerBlock(partiallyFilledApplicationForm) should be(false)
  }

  "In-progress application form with a forces partner" should
    "generate confirmation mustache model with forces partner block" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      statement = Some(Statement(
        memberForcesFlag = None,
        partnerForcesFlag = Some(true)
      ))
    ))

    displayPartnerBlock(partiallyFilledApplicationForm) should be(true)
  }


  "In-progress application form with filled name and previous name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
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
    nameModel.content should be(BlockContent("John Smith"))
    nameModel.editLink should be("/register-to-vote/forces/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be(BlockContent("Jan Kovar"))
    prevNameModel.editLink should be("/register-to-vote/forces/edit/name")
  }

  "In-progress application form with filled name and previous name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
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
    nameModel.content should be(BlockContent("John Walker Junior Smith"))
    nameModel.editLink should be("/register-to-vote/forces/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be(BlockContent("Jan Janko Janik Kovar"))
    prevNameModel.editLink should be("/register-to-vote/forces/edit/name")
  }

  "In-progress application form with filled date of birth" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
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
    dateOfBirthModel.content should be(BlockContent("22 January 1978"))
    dateOfBirthModel.editLink should be("/register-to-vote/forces/edit/date-of-birth")
  }

  "In-progress application form with filled date of birth excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
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
    dateOfBirthModel.editLink should be("/register-to-vote/forces/edit/date-of-birth")
  }

  "In-progress application form with british nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeForcesApplication.copy(
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
    nationalityModel.content should be(BlockContent("I am British"))
    nationalityModel.editLink should be("/register-to-vote/forces/edit/nationality")
  }

  "In-progress application form with irish nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeForcesApplication.copy(
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
    nationalityModel.content should be(BlockContent("I am Irish"))
    nationalityModel.editLink should be("/register-to-vote/forces/edit/nationality")
  }

  "In-progress application form with other nationality" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(completeForcesApplication.copy(
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
    nationalityModel.content should be(BlockContent(
      "I am a citizen of Spain, France and Germany"))
    nationalityModel.editLink should be("/register-to-vote/forces/edit/nationality")
  }

  "In-progress application form with nationality excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
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
    nationalityModel.editLink should be("/register-to-vote/forces/edit/nationality")
  }

  "In-progress application form with valid nino" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(ninoModel) = confirmation.nino
    ninoModel.content should be(BlockContent("AB123456C"))
    ninoModel.editLink should be("/register-to-vote/forces/edit/nino")
  }

  "In-progress application form with nino excuse" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
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
    ninoModel.editLink should be("/register-to-vote/forces/edit/nino")
  }

  "In-progress application form with valid service and regiment" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      service = Some(Service(
        serviceName = Some(ServiceType.BritishArmy),
        regiment = Some("regiment")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(serviceModel) = confirmation.service(false)
    serviceModel.content should be(BlockContent(List(
      "British Army",
      "regiment")))
    serviceModel.editLink should be("/register-to-vote/forces/edit/service")
  }

  "In-progress application form with valid service (no regiment)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      service = Some(Service(
        serviceName = Some(ServiceType.RoyalAirForce),
        regiment = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(serviceModel) = confirmation.service(false)
    serviceModel.content should be(BlockContent("Royal Air Force"))
    serviceModel.editLink should be("/register-to-vote/forces/edit/service")
  }

  behavior of "ConfirmationBlocks.rank"
  "In-progress application form with a valid rank" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      rank = Some(Rank(
        serviceNumber = Some("123456"),
        rank = Some("Captain")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(rankModel) = confirmation.rank
    rankModel.content should be(BlockContent(List(
      "123456",
      "Captain")))
    rankModel.editLink should be("/register-to-vote/forces/edit/rank")
  }


  "'has uk address' and valid uk address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          addressLine = Some("123 Fake Street"),
          uprn = Some("12345678"),
          postcode = "AB12 3CD",
          manualAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(addressModel) = confirmation.address
    addressModel.content should be(BlockContent(List(
      "123 Fake Street",
      "AB12 3CD")))
    addressModel.editLink should be("/register-to-vote/forces/edit/address/first")
  }

  "'has no uk address' currently, but provide last UK address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
        address = Some(PartialAddress(
          addressLine = Some("123 Fake Street"),
          uprn = Some("12345678"),
          postcode = "AB12 3CD",
          manualAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(addressModel) = confirmation.address
    addressModel.content should be(BlockContent(List("123 Fake Street", "AB12 3CD")))
    addressModel.editLink should be("/register-to-vote/forces/edit/address/first")
  }

  "'has uk address' and provide UK manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
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
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(addressModel) = confirmation.address
    addressModel.content should be(BlockContent(List(
      "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester",
      "AB12 3CD")))
    addressModel.editLink should be("/register-to-vote/forces/edit/address/first")
  }

  "'has no uk address' but provide valid UK manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.No),
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
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(addressModel) = confirmation.address
    addressModel.content should be(BlockContent(List(
      "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester",
      "AB12 3CD")))
    addressModel.editLink should be("/register-to-vote/forces/edit/address/first")
  }

  "In-progress application form with valid previous UK address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
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
      )),
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.Yes),
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
      "123 Fake Street",
      "AB12 3CD")))
    previousAddressModel.editLink should be("/register-to-vote/forces/edit/previous-address")
  }

  "In-progress application form with valid previous UK manual address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
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
      )),
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.Yes),
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
    previousAddressModel.editLink should be("/register-to-vote/forces/edit/previous-address")
  }


  "In-progress application form with previous postcode being Northern Ireland" should
    "generate confirmation mustache model with an information for NI users" in {

    when(addressService.isNothernIreland("BT7 1AA")).thenReturn(true)

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
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
      )),
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.Yes),
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
      "BT7 1AA",
      "I was previously registered in Northern Ireland")))
    previousAddressModel.editLink should be("/register-to-vote/forces/edit/previous-address")
  }

  "In-progress application form without previous UK address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
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
      )),
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.NotMoved),
        previousAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(previousAddressModel) = confirmation.previousAddress
    previousAddressModel.content should be(BlockContent(
      "I have not moved in the last 12 months"))
    previousAddressModel.editLink should be("/register-to-vote/forces/edit/previous-address")
  }

  "In-progress application form with valid previous UK address but invalid move option (recentlyMoved)" should
    "generate confirmation mustache model error from recently moved sub key" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
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
      )),
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
    previousAddressModel.content should be(BlockError("Please complete this step"))
    previousAddressModel.editLink should be("/register-to-vote/forces/edit/previous-address")
  }

  "In-progress application form with valid contact address" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
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
      )),
      contactAddress = Some (PossibleContactAddresses(
        contactAddressType = Some("uk"),
        ukAddressLine = Some("my uk address, london"),
        bfpoContactAddress = None,
        otherContactAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactAddressModel) = confirmation.contactAddress
    contactAddressModel.content should be(BlockContent(List(
      "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester",
      "AB12 3CD")))
    contactAddressModel.editLink should be("/register-to-vote/forces/edit/contact-address")
  }

  "In-progress application form with open register set to true" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      openRegisterOptin = Some(true)
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be(BlockContent(List("I want to include my name and address on the open register")))
    openRegisterModel.editLink should be("/register-to-vote/forces/edit/open-register")
  }

  "In-progress application form without open register flag" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      openRegisterOptin = Some(false)
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(openRegisterModel) = confirmation.openRegister
    openRegisterModel.content should be(BlockContent(List("I don't want my name and address on the open register")))
    openRegisterModel.editLink should be("/register-to-vote/forces/edit/open-register")
  }

  "application form with filled way to vote as in-person" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson))))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val Some(model) = confirmation.waysToVote
    model.content should be(BlockContent("I want to vote in person, at a polling station"))
    model.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }


  "In-progress application form with postal vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should be(BlockContent(List(
      "I want to vote by post",
      "Send me an application form in the post")))
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form with postal vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("test@test.com")
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should be(BlockContent(List(
      "I want to vote by post",
      "Send an application form to:",
      "test@test.com")))
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form with proxy vote (by post)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should be(BlockContent(List(
      "I want to vote by proxy (someone else voting for me)",
      "Send me an application form in the post")))
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form with proxy vote (by email)" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("email"),
          emailAddress = Some("test@test.com")
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should be(BlockContent(List(
      "I want to vote by proxy (someone else voting for me)",
      "Send an application form to:",
      "test@test.com")))
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form without applying for postal vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should be(BlockContent(List(
      "I want to vote by post",
      "I do not need a postal vote application form")))
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form without applying for proxy vote" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByProxy,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(postalOrProxyVoteModel) = confirmation.waysToVote
    postalOrProxyVoteModel.content should be(BlockContent(List(
      "I want to vote by proxy (someone else voting for me)",
      "I do not need a proxy vote application form")))
    postalOrProxyVoteModel.editLink should be("/register-to-vote/forces/edit/ways-to-vote")
  }

  "In-progress application form with email contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      contact = Some(Contact(
        post = false,
        phone = None,
        email = Some(ContactDetail(true, Some("antoine@gds.com")))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be(BlockContent("By email: antoine@gds.com"))
    contactModel.editLink should be("/register-to-vote/forces/edit/contact")
  }

  "In-progress application form with phone contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      contact = Some(Contact(
        post = false,
        phone = Some(ContactDetail(true, Some("+44 5678 907 546 ext. 3567-098"))),
        email = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be(BlockContent("By phone: +44 5678 907 546 ext. 3567-098"))
    contactModel.editLink should be("/register-to-vote/forces/edit/contact")
  }

  "In-progress application form with post contact" should
    "generate confirmation mustache model with correctly rendered values and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      contact = Some(Contact(
        post = true,
        phone = None,
        email = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(contactModel) = confirmation.contact
    contactModel.content should be(BlockContent("By post"))
    contactModel.editLink should be("/register-to-vote/forces/edit/contact")
  }

  behavior of "InProgressForm.confirmationNationalityString"

  it should "handle just irish checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  it should "handle just british checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  it should "handle british and irish checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  it should "handle british, irish and an other nationality checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  it should "handle british, irish and two other nationalities checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  it should "handle british, irish and three other nationalities checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  it should "handle an other nationality checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  it should "handle an three other nationalities checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  it should "handle two other nationalities checked" in {
    val form = confirmationForm.fillAndValidate(InprogressForces(
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

  "In-progress application confirmation form" should
    "display all postcodes uppercased" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          addressLine = Some("line1"),
          uprn = Some("1234"),
          postcode = "ab123cd",
          manualAddress = None))
      )),
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.Yes),
        previousAddress = Some(PartialAddress(
          addressLine = Some("line1"),
          uprn = Some("12341234"),
          postcode = "aa123bb",
          manualAddress = None
        ))
      )),
      contactAddress = Some(PossibleContactAddresses(
        contactAddressType = Some("uk"),
        ukAddressLine = None,
        bfpoContactAddress = None,
        otherContactAddress = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    confirmation.address.get.content should be(BlockContent(List("line1", "AB123CD")))
    confirmation.previousAddress.get.content should be(BlockContent(List("line1", "AA123BB")))
    confirmation.contactAddress.get.content should be(BlockContent(List("line1", "AB123CD")))
  }


  "In-progress application confirmation form" should
    "display NINO uppercased" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressForces(
      nino = Some(Nino(
        nino = Some("ab123456"),
        noNinoReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    confirmation.nino.get.content should be(BlockContent(List("AB123456")))
  }

}
