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

package org.knora.webapi.responders

import akka.pattern._
import org.knora.webapi.IRI
import org.knora.webapi.messages.v1.responder.standoffmessages.{GetMappingRequestV1, GetMappingResponseV1, GetXSLTransformationRequestV1, GetXSLTransformationResponseV1}
import org.knora.webapi.messages.v1.responder.usermessages.UserProfileV1
import org.knora.webapi.util.ConstructResponseUtilV2
import org.knora.webapi.util.ConstructResponseUtilV2.{MappingAndXSLTransformation, ResourceWithValueRdfData}

import scala.concurrent.Future

/**
  * An abstract class with standoff utility methods for v2 responders.
  */
abstract class ResponderWithStandoffV2 extends Responder {

    /**
      * Gets mappings referred to in query results [[Map[IRI, ResourceWithValueRdfData]]].
      *
      * @param queryResultsSeparated query results referring to mappings.
      * @param userProfile           the user making the request.
      * @return the referred mappings.
      */
    protected def getMappingsFromQueryResultsSeparated(queryResultsSeparated: Map[IRI, ResourceWithValueRdfData], userProfile: UserProfileV1): Future[Map[IRI, MappingAndXSLTransformation]] = {

        // collect the Iris of the mappings referred to in the resources' text values
        val mappingIris: Set[IRI] = queryResultsSeparated.flatMap {
            case (_, assertions: ResourceWithValueRdfData) =>
                ConstructResponseUtilV2.getMappingIrisFromValuePropertyAssertions(assertions.valuePropertyAssertions)
        }.toSet

        // get all the mappings
        val mappingResponsesFuture: Vector[Future[GetMappingResponseV1]] = mappingIris.map {
            (mappingIri: IRI) =>
                for {
                    mappingResponse: GetMappingResponseV1 <- (responderManager ? GetMappingRequestV1(mappingIri = mappingIri, userProfile = userProfile)).mapTo[GetMappingResponseV1]
                } yield mappingResponse
        }.toVector


        for {

            mappingResponses: Vector[GetMappingResponseV1] <- Future.sequence(mappingResponsesFuture)

            // get the default XSL transformations
            mappingsWithFuture: Vector[Future[(IRI, MappingAndXSLTransformation)]] = mappingResponses.map {
                (mapping: GetMappingResponseV1) =>

                    for {
                    // if given, get the default XSL transformation
                        xsltOption: Option[String] <- if (mapping.mapping.defaultXSLTransformation.nonEmpty) {
                            for {
                                xslTransformation: GetXSLTransformationResponseV1 <- (responderManager ? GetXSLTransformationRequestV1(mapping.mapping.defaultXSLTransformation.get, userProfile = userProfile)).mapTo[GetXSLTransformationResponseV1]
                            } yield Some(xslTransformation.xslt)
                        } else {
                            Future(None)
                        }
                    } yield mapping.mappingIri -> MappingAndXSLTransformation(mapping = mapping.mapping, standoffEntities = mapping.standoffEntities, XSLTransformation = xsltOption)

            }

            mappings: Vector[(IRI, MappingAndXSLTransformation)] <- Future.sequence(mappingsWithFuture)
            mappingsAsMap: Map[IRI, MappingAndXSLTransformation] = mappings.toMap


        } yield mappingsAsMap

    }

}