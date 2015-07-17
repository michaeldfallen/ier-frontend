package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.mustache.InheritedGovukMustache
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.mustache.MustacheModel
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.LocalAuthorityContactDetails
import uk.gov.gds.ier.langs.Messages

trait LocalAuthorityMustache
  extends InheritedGovukMustache with MustacheModel {
  self: WithRemoteAssets
  with WithConfig =>

    case class LocalAuthorityShowPage (
      localAuthorityContact: Option[LocalAuthorityContactDetails],
      visitAuthorityPage: String,
      override val sourcePath: String
    ) (
        implicit override val lang: Lang
    ) extends InheritedMustachio("localAuthority/show")

    object LocalAuthorityShowPage {
      def apply(
        localAuthorityContact: Option[LocalAuthorityContactDetails],
        sourcePath: Option[String]
      )(implicit lang: Lang): LocalAuthorityShowPage = {

        val visitAuthorityPage = localAuthorityContact.map(details => (details.url.getOrElse(""), details.name.getOrElse(""))) match {
          case Some((authUrl, authName)) if authUrl.nonEmpty =>
            Messages("lookup_show_visitWebsite", authUrl, authName)
          case _ => ""
        }

        LocalAuthorityShowPage(localAuthorityContact, visitAuthorityPage, sourcePath.getOrElse(""))
      }
    }

    case class LocalAuthorityPostcodePage (
        postcode: Field,
        override val sourcePath: String,
        postUrl: String,
        errorMessages: Seq[String] = Seq.empty
    ) (
        implicit override val lang: Lang
    ) extends InheritedMustachio("localAuthority/lookup")

    object LocalAuthorityPostcodePage {
      def apply(
          form: ErrorTransformForm[LocalAuthorityRequest],
          sourcePath: Option[String],
          postUrl: String
      ) (implicit lang: Lang): LocalAuthorityPostcodePage = {
        implicit val _form = form
        LocalAuthorityPostcodePage(
          postcode = TextField(key = keys.postcode),
          sourcePath = sourcePath getOrElse "",
          postUrl = postUrl,
          errorMessages = Messages.translatedGlobalErrors(form)
        )
      }
    }
}
