package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{PossibleAddress, Addresses}
import uk.gov.gds.ier.transaction.forces.{InprogressForces, WithForcesControllers}
import uk.gov.gds.ier.service.WithAddressService

trait PreviousAddressSelectMustache
  extends StepTemplate[InprogressForces] {
    self: WithAddressService
    with WithForcesControllers
    with WithSerialiser =>

  val title = "What was your previous UK address?"

  case class SelectModel (
    question: Question,
    lookupUrl: String,
    manualUrl: String,
    postcode: Field,
    address: Field,
    possibleJsonList: Field,
    possiblePostcode: Field,
    hasAddresses: Boolean,
    hasAuthority: Boolean
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/previousAddressSelect") {
    (form, post, application) =>

    implicit val progressForm = form

    val selectedUprn = form(keys.previousAddress.uprn).value
    val postcode = form(keys.previousAddress.postcode).value

    val storedAddresses = for(
      jsonList <- form(keys.possibleAddresses.jsonList).value;
      postcode <- form(keys.possibleAddresses.postcode).value
    ) yield {
      PossibleAddress(
        jsonList = serialiser.fromJson[Addresses](jsonList),
        postcode = postcode
      )
    }

    //IER0091 : Temp removing the storedAddresses section of the code checks to remove populating against the hidden input field
    //val possibleAddresses = storedAddresses orElse postcode.map { pc =>
    val possibleAddresses = postcode.map { pc =>
      val addresses = addressService.lookupPartialAddress(pc)
      PossibleAddress(
        jsonList = Addresses(addresses),
        postcode = pc
      )
    }

    val options = for (
      address <- possibleAddresses.map {_.jsonList.addresses}.toList.flatten
    ) yield {
      SelectOption(
        value = address.uprn.getOrElse(""),
        text = address.addressLine.getOrElse(""),
        selected = if (address.uprn == selectedUprn) {
          "selected=\"selected\""
        } else ""
      )
    }

    val hasAddresses = possibleAddresses.exists { possible =>
      !possible.jsonList.addresses.isEmpty
    }
    //IER0055: Check authority table too to allow for manual entry
    val hasAuthority = hasAddresses || addressService.validAuthority(postcode)

    val addressSelect = SelectField(
      key = keys.previousAddress.uprn,
      optionList = options,
      default = SelectOption(
        value = "",
        text = s"${options.size} addresses found"
      )
    )
    val addressSelectWithError = addressSelect.copy(
      classes = if (!hasAddresses) {
        "invalid"
      } else {
        addressSelect.classes
      }
    )

    SelectModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = forces.PreviousAddressPostcodeStep.routing.get.url,
      manualUrl = forces.PreviousAddressManualStep.routing.get.url,
      postcode = TextField(keys.previousAddress.postcode),
      address = addressSelectWithError,  // this is model data for <select>
      possibleJsonList = HiddenField(
        key = keys.possibleAddresses.jsonList,
        value = possibleAddresses.map { poss =>
          serialiser.toJson(poss.jsonList)
        }.getOrElse("")
      ),
      possiblePostcode = HiddenField(
        key = keys.possibleAddresses.postcode,
        value = form(keys.previousAddress.postcode).value.getOrElse("")
      ),
      hasAddresses = hasAddresses,
      hasAuthority = hasAuthority
    )
  }
}
