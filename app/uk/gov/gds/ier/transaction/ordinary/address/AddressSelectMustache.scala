package uk.gov.gds.ier.transaction.ordinary.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.model.{PossibleAddress, Addresses}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AddressSelectMustache extends StepTemplate[InprogressOrdinary] {
  val serialiser:JsonSerialiser
  val addressService:AddressService

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

  val mustache = MultilingualTemplate("ordinary/addressSelect") { implicit lang => (form, post) =>

    implicit val progressForm = form

    val selectedUprn = form(keys.address.uprn).value
    val postcode = form(keys.address.postcode).value.orElse {
      form(keys.possibleAddresses.postcode).value
    }

    val storedAddresses = for(
      jsonList <- form(keys.possibleAddresses.jsonList).value;
      postcode <- postcode
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

    val options = possibleAddresses.map { possibleAddress =>
      possibleAddress.jsonList.addresses
    }.getOrElse(List.empty).map { address =>
      SelectOption(
        value = address.uprn.getOrElse(""),
        text = address.addressLine.getOrElse(""),
        selected = if (address.uprn == selectedUprn) {
          "selected=\"selected\""
        } else ""
      )
    }

    val hasAddresses = possibleAddresses.exists (!_.jsonList.addresses.isEmpty)
    val hasAuthority = hasAddresses || addressService.validAuthority(postcode)

    val addressSelect = SelectField(
      key = keys.address.uprn,
      optionList = options,
      default = SelectOption(
        value = "",
        text = Messages("ordinary_address_nAddressFound", options.size)
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
        number = Messages("step_a_of_b", 6, 11),
        title = Messages("ordinary_address_postcode_title"),
        errorMessages = Messages.translatedGlobalErrors(form)
      ),
      lookupUrl = routes.AddressStep.get.url,
      manualUrl = routes.AddressManualStep.get.url,
      postcode = TextField(keys.address.postcode, default = postcode),
      address = addressSelectWithError,
      possibleJsonList = HiddenField(
        key = keys.possibleAddresses.jsonList,
        value = possibleAddresses.map { poss =>
          serialiser.toJson(poss.jsonList)
        }.getOrElse("")
      ),
      possiblePostcode = HiddenField(
        key = keys.possibleAddresses.postcode,
        value = form(keys.address.postcode).value.getOrElse("")
      ),
      hasAddresses = hasAddresses,
      hasAuthority = hasAuthority
    )
  }
}
