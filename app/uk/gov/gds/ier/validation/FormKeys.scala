package uk.gov.gds.ier.validation

import play.api.templates.Html
import scala.Some
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

case class Key(key:String) {
  def asId(value:String = "") = List(key.replace(".", "_"), value.replace(" ", "_")).filter(_.nonEmpty).mkString("_")
  def item(i:Int) = this.copy(s"$key[$i]")
}

trait FormKeys {

  lazy val keys = new Keys{}

  trait Keys {
    lazy val namespace = ""

    def prependNamespace(k:Key):Key = {
      if (namespace.nonEmpty) {
        k.copy(namespace + "." + k.key)
      } else {
        k
      }
    }

    lazy val country = prependNamespace(Key("country"))
    lazy val residence = prependNamespace(Key("residence"))
    lazy val livingAbroad = prependNamespace(Key("livingAbroad"))
    lazy val origin = prependNamespace(Key("origin"))

    lazy val nationality = prependNamespace(Key("nationality"))

    lazy val british = prependNamespace(Key("british"))
    lazy val irish = prependNamespace(Key("irish"))
    lazy val nationalities = prependNamespace(Key("nationalities"))
    lazy val hasOtherCountry = prependNamespace(Key("hasOtherCountry"))
    lazy val otherCountries = prependNamespace(Key("otherCountries"))
    lazy val noNationalityReason = prependNamespace(Key("noNationalityReason"))

    lazy val name = prependNamespace(Key("name"))
    lazy val previousName = prependNamespace(Key("previousName"))
    lazy val hasPreviousName = prependNamespace(Key("hasPreviousName"))
    lazy val hasPreviousNameOption = prependNamespace(Key("hasPreviousNameOption"))
    lazy val firstName = prependNamespace(Key("firstName"))
    lazy val middleNames = prependNamespace(Key("middleNames"))
    lazy val lastName = prependNamespace(Key("lastName"))

    lazy val parentNameFormEntries = prependNamespace(Key("parentNameFormEntries"))
    lazy val parentName = prependNamespace(Key("parentName"))
    lazy val parentPreviousName = prependNamespace(Key("parentPreviousName"))

    lazy val overseasName = prependNamespace( Key("overseasName"))
    lazy val overseasParentName = prependNamespace(Key("overseasParentName"))

    lazy val dob = prependNamespace(Key("dob"))
    lazy val noDob = prependNamespace(Key("noDob"))
    lazy val range = prependNamespace(Key("range"))
    lazy val reason = prependNamespace(Key("reason"))

    lazy val day = prependNamespace(Key("day"))
    lazy val month = prependNamespace(Key("month"))
    lazy val year = prependNamespace(Key("year"))

    lazy val nino = prependNamespace(Key("NINO"))
    lazy val noNinoReason = prependNamespace(Key("NoNinoReason"))

    lazy val address = prependNamespace(Key("address"))

    lazy val lineOne = prependNamespace(Key("lineOne"))
    lazy val lineTwo = prependNamespace(Key("lineTwo"))
    lazy val lineThree = prependNamespace(Key("lineThree"))
    lazy val city = prependNamespace(Key("city"))
    lazy val county = prependNamespace(Key("county"))
    lazy val postcode = prependNamespace(Key("postcode"))
    lazy val uprn = prependNamespace(Key("uprn"))
    lazy val manualAddress = prependNamespace(Key("manualAddress"))
    lazy val addressLine = prependNamespace(Key("addressLine"))
    lazy val gssCode = prependNamespace(Key("gssCode"))

    lazy val previousAddress = prependNamespace(Key("previousAddress"))
    lazy val movedRecently = prependNamespace(Key("movedRecently"))
    lazy val wasRegisteredWhenAbroad = prependNamespace(Key("wasRegisteredWhenAbroad"))
    lazy val findAddress = prependNamespace(Key("findAddress"))
    lazy val otherAddress = prependNamespace(Key("otherAddress"))
    lazy val hasOtherAddress = prependNamespace(Key("hasOtherAddress"))
    lazy val openRegister = prependNamespace(Key("openRegister"))
    lazy val postalVote = prependNamespace(Key("postalVote"))
    lazy val postalOrProxyVote = prependNamespace(Key("postalOrProxyVote"))
    lazy val optIn = prependNamespace(Key("optIn"))
    lazy val deliveryMethod = prependNamespace(Key("deliveryMethod"))
    lazy val methodName = prependNamespace(Key("methodName"))
    lazy val emailAddress = prependNamespace(Key("emailAddress"))
    lazy val voteType = prependNamespace(Key("voteType"))

    lazy val contact = prependNamespace(Key("contact"))
    lazy val contactType = prependNamespace(Key("contactType"))
    lazy val contactMe = prependNamespace(Key("contactMe"))
    lazy val detail = prependNamespace(Key("detail"))
    lazy val email = prependNamespace(Key("email"))
    lazy val textNum = prependNamespace(Key("textNum"))
    lazy val phone = prependNamespace(Key("phone"))
    lazy val post = prependNamespace(Key("post"))

    lazy val possibleAddresses = prependNamespace(Key("possibleAddresses"))
    lazy val jsonList = prependNamespace(Key("jsonList"))

    lazy val overseasAddress = prependNamespace(Key("overseasAddress"))
    lazy val addressLine1 = prependNamespace(Key("addressLine1"))
    lazy val addressLine2 = prependNamespace(Key("addressLine2"))
    lazy val addressLine3 = prependNamespace(Key("addressLine3"))
    lazy val addressLine4 = prependNamespace(Key("addressLine4"))
    lazy val addressLine5 = prependNamespace(Key("addressLine5"))

    lazy val lastUkAddress = prependNamespace(Key("lastUkAddress"))
    lazy val dateLeftUk = prependNamespace(Key("dateLeftUk"))
    lazy val lastRegisteredToVote = prependNamespace(Key("lastRegisteredToVote"))
    lazy val registeredType = prependNamespace(Key("registeredType"))

    lazy val passportNumber = prependNamespace(Key("passportNumber"))
    lazy val authority = prependNamespace(Key("authority"))
    lazy val issueDate = prependNamespace(Key("issueDate"))

    lazy val dateBecameCitizen = prependNamespace(Key("dateBecameCitizen"))
    lazy val howBecameCitizen = prependNamespace(Key("howBecameCitizen"))
    lazy val birthplace = prependNamespace(Key("birthplace"))

    lazy val hasPassport = prependNamespace(Key("hasPassport"))
    lazy val bornInsideUk = prependNamespace(Key("bornInsideUk"))

    lazy val passportDetails = prependNamespace(Key("passportDetails"))
    lazy val citizenDetails = prependNamespace(Key("citizenDetails"))
    lazy val passport = prependNamespace(Key("passport"))

    lazy val waysToVote = prependNamespace(Key("waysToVote"))
    lazy val wayType = prependNamespace(Key("wayType"))
    lazy val waysToVoteAndPostalProxy = prependNamespace(Key("waysToVoteAndPostalProxy"))

    lazy val dateLeftSpecial = prependNamespace(Key("dateLeftSpecial"))

    lazy val parentsAddress = prependNamespace(Key("parentsAddress"))

    lazy val statement = prependNamespace(Key("statement"))
    lazy val forcesMember = prependNamespace(Key("forcesMember"))
    lazy val partnerForcesMember = prependNamespace(Key("partnerForcesMember"))
    lazy val service = prependNamespace(Key("service"))
    lazy val serviceNumber = prependNamespace(Key("serviceNumber"))
    lazy val rank = prependNamespace(Key("rank"))
    lazy val contactAddress = prependNamespace(Key("contactAddress"))
    lazy val job = prependNamespace(Key("job"))
    lazy val jobTitle = prependNamespace(Key("jobTitle"))
    lazy val payrollNumber = prependNamespace(Key("payrollNumber"))
    lazy val govDepartment = prependNamespace(Key("govDepartment"))
    lazy val serviceName = prependNamespace(Key("serviceName"))
    lazy val regiment = prependNamespace(Key("regiment"))

    lazy val crownServant = prependNamespace(Key("crownServant"))
    lazy val crownPartner = prependNamespace(Key("crownPartner"))
    lazy val councilEmployee = prependNamespace(Key("councilEmployee"))
    lazy val councilPartner = prependNamespace(Key("councilPartner"))

    lazy val addressType = prependNamespace(Key("addressType"))
    lazy val ukAddressLine = prependNamespace(Key("ukAddressLine"))
    lazy val ukAddress = prependNamespace(Key("ukAddress"))
    lazy val bfpoAddress = prependNamespace(Key("bfpoAddress"))
    lazy val contactAddressType = prependNamespace(Key("contactAddressType"))
    lazy val ukAddressTextLine = prependNamespace(Key("ukAddressTextLine"))
    lazy val ukContactAddress = prependNamespace(Key("ukContactAddress"))
    lazy val bfpoContactAddress = prependNamespace(Key("bfpoContactAddress"))
    lazy val otherContactAddress = prependNamespace(Key("otherContactAddress"))

    lazy val forceToRedirect = prependNamespace(Key("forceToRedirect"))

    lazy val hasAddress =  prependNamespace(Key("hasAddress"))

    lazy val feedback =  prependNamespace(Key("feedback"))
    lazy val sourcePath = prependNamespace(Key("sourcePath"))
    lazy val feedbackText =  prependNamespace(Key("feedbackText"))
    lazy val contactName =  prependNamespace(Key("contactName"))
    lazy val contactEmail =  prependNamespace(Key("contactEmail"))

    lazy val localAuthority = prependNamespace(Key("localAuthority"))
    lazy val backLink = prependNamespace(Key("backLink"))

    lazy val sessionId = prependNamespace(Key("sessionId"))
  }

  implicit class key2namespace(key:Key) extends Keys {
    override lazy val namespace = key.key
  }
  implicit class keys2Traversal(key:Key)(implicit formData:ErrorTransformForm[InprogressOrdinary]) {
    def each(from:Int = 0)(block: (String, Int) => Html):Html = {
      val field = formData(key.item(from))
      field.value match {
        case Some(value) => block(field.name, from) += each(from+1)(block)
        case None => Html.empty
      }
    }
  }
}

object FormKeys extends FormKeys
