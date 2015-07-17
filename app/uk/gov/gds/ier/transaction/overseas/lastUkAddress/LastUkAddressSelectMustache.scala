package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.service.WithAddressService
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.transaction.overseas.{InprogressOverseas, WithOverseasControllers}
import uk.gov.gds.ier.model.PossibleAddress

trait LastUkAddressSelectMustache extends StepTemplate[InprogressOverseas] {
  self: WithAddressService
    with WithOverseasControllers
    with WithSerialiser =>

    val title = "What was the UK address where you were last registered to vote?"
    val questionNumber = ""

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

    val mustache = MustacheTemplate("overseas/lastUkAddressSelect") { (form, post) =>

      implicit val progressForm = form

      val selectedUprn = form(keys.lastUkAddress.uprn).value

      val postcode = form(keys.lastUkAddress.postcode).value.orElse {
        form(keys.possibleAddresses.postcode).value
      }

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
      //val maybeAddresses = storedAddresses orElse lookupAddresses(postcode)
      val maybeAddresses = lookupAddresses(postcode)

      val options = maybeAddresses.map { possibleAddress =>
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

      val hasAddresses = maybeAddresses.exists { poss =>
        !poss.jsonList.addresses.isEmpty
      }

      val hasAuthority = hasAddresses || addressService.validAuthority(postcode)

      val addressSelect = SelectField(
        key = keys.lastUkAddress.uprn,
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
          number = questionNumber,
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = overseas.LastUkAddressStep.routing.get.url,
        manualUrl = overseas.LastUkAddressManualStep.routing.get.url,
        postcode = TextField(keys.lastUkAddress.postcode),
        address = addressSelectWithError,
        possibleJsonList = TextField(keys.possibleAddresses.jsonList).copy(
          value = maybeAddresses.map { poss =>
            serialiser.toJson(poss.jsonList)
          }.getOrElse("")
        ),
        possiblePostcode = TextField(keys.possibleAddresses.postcode).copy(
          value = form(keys.lastUkAddress.postcode).value.getOrElse("")
        ),
        hasAddresses = hasAddresses,
        hasAuthority = hasAuthority
      )
    }

    private[lastUkAddress] def lookupAddresses(
        maybePostcode:Option[String]): Option[PossibleAddress] = {

    maybePostcode.map { postcode =>
      val addresses = addressService.lookupPartialAddress(postcode)
      PossibleAddress(
        jsonList = Addresses(addresses),
        postcode = postcode
      )
    }
  }
}
