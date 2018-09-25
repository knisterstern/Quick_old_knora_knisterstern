/*
 * Copyright © 2015 Lukas Rosenthaler, Benjamin Geer, Ivan Subotic,
 * Tobias Schweizer, André Kilchenmann, and Sepideh Alassi.
 *
 * This file is part of Knora.
 *
 * Knora is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Knora is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Knora.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.knora.webapi.routing.v1

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.knora.webapi.messages.v1.responder.sipimessages.SipiFileInfoGetRequestV1
import org.knora.webapi.routing.{Authenticator, RouteUtilV1}
import org.knora.webapi.util.StringFormatter
import org.knora.webapi.{BadRequestException, SettingsImpl}

import scala.concurrent.Future

/**
  * Provides a spray-routing function for the API routes that Sipi connects to.
  */
object SipiRouteV1 extends Authenticator {

    /**
      * A spray-routing function for the API routes that Sipi connects to.
      */
    def knoraApiPath(_system: ActorSystem, settings: SettingsImpl, log: LoggingAdapter): Route = {
        implicit val system: ActorSystem = _system
        implicit val executionContext = system.dispatcher
        implicit val timeout = settings.defaultTimeout
        val responderManager = system.actorSelection("/user/responderManager")
        val stringFormatter = StringFormatter.getGeneralInstance

        path("v1" / "files" / Segment) { file =>
            get {
                requestContext =>
                    val userProfile = getUserProfileV1(requestContext)
                    //val fileValueIRI = StringFormatter.validateAndEscapeIri(iri, throw BadRequestException(s"Invalid file value IRI: $iri"))
                    val filename = stringFormatter.toSparqlEncodedString(file, throw BadRequestException(s"Invalid filename: '$file'"))
                    val requestMessage = SipiFileInfoGetRequestV1(filename, userProfile)

                    RouteUtilV1.runJsonRoute(
                        requestMessage,
                        requestContext,
                        settings,
                        responderManager,
                        log
                    )
            }
        }

    }
}
