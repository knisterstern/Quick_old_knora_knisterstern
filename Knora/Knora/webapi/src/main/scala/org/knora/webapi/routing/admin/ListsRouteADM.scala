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

package org.knora.webapi.routing.admin

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import org.knora.webapi.messages.admin.responder.listsmessages._
import org.knora.webapi.routing.{Authenticator, RouteUtilADM}
import org.knora.webapi.util.StringFormatter
import org.knora.webapi.{BadRequestException, IRI, NotImplementedException, SettingsImpl}

import scala.concurrent.ExecutionContextExecutor

/**
  * Provides a spray-routing function for API routes that deal with lists.
  */
object ListsRouteADM extends Authenticator with ListADMJsonProtocol {

    def knoraApiPath(_system: ActorSystem, settings: SettingsImpl, log: LoggingAdapter): Route = {
        implicit val system: ActorSystem = _system
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher
        implicit val timeout: Timeout = settings.defaultTimeout
        val responderManager = system.actorSelection("/user/responderManager")
        val stringFormatter = StringFormatter.getGeneralInstance

        path("admin" / "lists") {
            get {
                /* return all lists */
                parameters("projectIri".?) { maybeProjectIri: Option[IRI] =>
                    requestContext =>
                        val requestingUser = getUserADM(requestContext)

                        val projectIri = stringFormatter.toOptionalIri(maybeProjectIri, throw BadRequestException(s"Invalid param project IRI: $maybeProjectIri"))

                        val requestMessage = ListsGetRequestADM(projectIri, requestingUser)

                        RouteUtilADM.runJsonRoute(
                            requestMessage,
                            requestContext,
                            settings,
                            responderManager,
                            log
                        )
                }
            } ~
            post {
                /* create a list */
                entity(as[CreateListApiRequestADM]) { apiRequest =>
                    requestContext =>
                        val requestingUser = getUserADM(requestContext)

                        val requestMessage = ListCreateRequestADM(
                            createListRequest = apiRequest,
                            requestingUser = requestingUser,
                            apiRequestID = UUID.randomUUID()
                        )

                        RouteUtilADM.runJsonRoute(
                            requestMessage,
                            requestContext,
                            settings,
                            responderManager,
                            log
                        )
                }
            }
        } ~
        path("admin" / "lists" / Segment) {iri =>
            get {
                /* return a list (a graph with all list nodes) */
                requestContext =>
                    val requestingUser = getUserADM(requestContext)
                    val listIri = stringFormatter.validateAndEscapeIri(iri, throw BadRequestException(s"Invalid param list IRI: $iri"))

                    val requestMessage = ListGetRequestADM(listIri, requestingUser)

                    RouteUtilADM.runJsonRoute(
                        requestMessage,
                        requestContext,
                        settings,
                        responderManager,
                        log
                    )
            } ~
            put {
                /* update list */
                throw NotImplementedException("Method not implemented.")
                ???
            } ~
            delete {
                /* delete (deactivate) list */
                throw NotImplementedException("Method not implemented.")
                ???
            }
        } ~
        path("admin" / "lists" / "infos" / Segment) {iri =>
            get {
                /* return information about a list (without children) */
                requestContext =>
                    val requestingUser = getUserADM(requestContext)
                    val listIri = stringFormatter.validateAndEscapeIri(iri, throw BadRequestException(s"Invalid param list IRI: $iri"))

                    val requestMessage = ListInfoGetRequestADM(listIri, requestingUser)

                    RouteUtilADM.runJsonRoute(
                        requestMessage,
                        requestContext,
                        settings,
                        responderManager,
                        log
                    )
            } ~
            put {
                /* update list info */
                entity(as[ChangeListInfoApiRequestADM]) { apiRequest =>
                    requestContext =>
                        val requestingUser = getUserADM(requestContext)
                        val listIri = stringFormatter.validateAndEscapeIri(iri, throw BadRequestException(s"Invalid param list IRI: $iri"))

                        val requestMessage = ListInfoChangeRequestADM(
                            listIri = listIri,
                            changeListRequest = apiRequest,
                            requestingUser = requestingUser,
                            apiRequestID = UUID.randomUUID()
                        )

                        RouteUtilADM.runJsonRoute(
                            requestMessage,
                            requestContext,
                            settings,
                            responderManager,
                            log
                        )
                }
            }
        } ~
        path("admin" / "lists" / "nodes" / Segment) {iri =>
            get {
                /* return information about a single node (without children) */
                requestContext =>
                    val requestingUser = getUserADM(requestContext)
                    val listIri = stringFormatter.validateAndEscapeIri(iri, throw BadRequestException(s"Invalid param list IRI: $iri"))

                    val requestMessage = ListNodeInfoGetRequestADM(listIri, requestingUser)

                    RouteUtilADM.runJsonRoute(
                        requestMessage,
                        requestContext,
                        settings,
                        responderManager,
                        log
                    )
            } ~
            put {
                /* update list node */
                throw NotImplementedException("Method not implemented.")
                ???
            } ~
            delete {
                /* delete list node */
                throw NotImplementedException("Method not implemented.")
                ???
            }
        }
    }
}
