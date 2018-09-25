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

package org.knora.webapi.messages.v2.responder

import java.util.UUID

import org.knora.webapi.messages.v1.responder.usermessages.UserProfileV1
import org.knora.webapi.util.jsonld.JsonLDDocument

/**
  * A tagging trait for messages that can be sent to Knora API v2 responders.
  */
trait KnoraRequestV2

/**
  * A trait for objects that can generate case class instances based on JSON-LD input.
  *
  * @tparam C the type of the case class that can be generated.
  */
trait KnoraJsonLDRequestReaderV2[C] {
    /**
      * Converts JSON-LD input into a case class instance.
      *
      * @param jsonLDDocument the JSON-LD input.
      * @param apiRequestID   the UUID of the API request.
      * @param userProfile    the profile of the user making the request.
      * @return a case class instance representing the input.
      */
    def fromJsonLD(jsonLDDocument: JsonLDDocument,
                   apiRequestID: UUID,
                   userProfile: UserProfileV1): C
}
