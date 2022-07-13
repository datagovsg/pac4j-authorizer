package p4jauth

import java.util.Optional
import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.context.WebContext
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.{CommonProfile, UserProfile}
import org.pac4j.core.profile.UserProfile
import org.slf4j.LoggerFactory

class EnvDomainAuthorizer extends AuthorizationGenerator {
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
                        sessionStore: SessionStore,
                        _profile: UserProfile): Optional[UserProfile] = {
    val profile = _profile.asInstanceOf[CommonProfile]
    val userEmail = profile.getEmail

    if (isAuthorized(userEmail)) profile.addRole("user")
    if (isAdmin(userEmail)) profile.addRole("admin")

    profile.setId(userEmail)
    profile.removeLoginData()
    profile.setRemembered(true)

    Optional.of(profile)
  }
}
