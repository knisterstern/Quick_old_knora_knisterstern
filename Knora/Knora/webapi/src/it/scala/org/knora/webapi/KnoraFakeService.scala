/*
 * Copyright © 2015 Lukas Rosenthaler, Benjamin Geer, Ivan Subotic,
 * Tobias Schweizer, André Kilchenmann, and Sepideh Alassi.
 * This file is part of Knora.
 * Knora is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Knora is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public
 * License along with Knora.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.knora.webapi

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.knora.webapi.util.StringFormatter

/**
  * Created by subotic on 26.06.17.
  */
trait KnoraFakeService {

    this: Core =>

    // Initialise StringFormatter with the system settings.
    StringFormatter.initForTest()

    /**
      * Timeout definition (need to be high enough to allow reloading of data so that checkActorSystem doesn't timeout)
      */
    implicit private val timeout = settings.defaultRestoreTimeout

    /**
      * Faked `webapi` routes
      */
    private val apiRoutes = {
        path("v1" / "files" / Segment) { file =>
            get {
                complete(
                    """
                    {
                        "permissionCode": 2,
                        "status": 0
                    }
                    """
                )
            }
        }
    }

    /**
      * Starts the Faked Knora API server.
      */
    def startService(): Unit = {

        implicit val materializer = ActorMaterializer()

        // needed for startup flags and the future map/flatmap in the end
        implicit val executionContext = system.dispatcher

        // Either HTTP or HTTPs, or both, must be enabled.
        if (!(settings.knoraApiUseHttp || settings.knoraApiUseHttps)) {
            throw HttpConfigurationException("Neither HTTP nor HTTPS is enabled")
        }

        // Activate HTTP if enabled.
        if (settings.knoraApiUseHttp) {
            Http().bindAndHandle(Route.handlerFlow(apiRoutes), settings.knoraApiHost, settings.knoraApiHttpPort)
            println(s"Knora API Server using HTTP at http://${settings.knoraApiHost}:${settings.knoraApiHttpPort}.")
        }

        // HTTPS not supported for integration testing
    }

    /**
      * Stops Knora.
      */
    def stopService(): Unit = {
        system.terminate()
    }

}
