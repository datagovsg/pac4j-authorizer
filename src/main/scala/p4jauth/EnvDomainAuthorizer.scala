package p4jauth

import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.context.WebContext
import org.pac4j.core.profile.CommonProfile
import org.slf4j.LoggerFactory

class EnvDomainAuthorizer extends AuthorizationGenerator[CommonProfile] {
  // Throw exception when missing AUTHORIZED_DOMAIN for now
  val authorizedDomain: String = sys.env("AUTHORIZED_DOMAIN")

  val adminUsers =
    sys.env.getOrElse("ADMIN_USERS", "").split(",").map(_.toLowerCase).toSet

  val logger = LoggerFactory.getLogger(getClass.getName)

  if (adminUsers.isEmpty) {
    logger.warn(
      "There are no admin users specified in the environment. You won't be able to administer this service.")
  }

  private def isAdmin(email: String) = adminUsers(email.toLowerCase)
  private def isAuthorized(email: String) =
    email.toLowerCase.endsWith("@" + authorizedDomain)

  override def generate(context: WebContext,
                        profile: CommonProfile): CommonProfile = {
    val userEmail = profile.getEmail

    if (isAuthorized(userEmail)) profile.addRole("user")
    if (isAdmin(userEmail)) profile.addRole("admin")

    profile.setId(userEmail)
    profile.clearSensitiveData()
    profile.setRemembered(true)

    profile
  }
}
