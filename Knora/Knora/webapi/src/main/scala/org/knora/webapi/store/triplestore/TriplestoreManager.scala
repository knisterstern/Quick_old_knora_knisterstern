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

package org.knora.webapi.store.triplestore

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import akka.pattern._
import akka.routing.FromConfig
import org.knora.webapi.SettingsConstants._
import org.knora.webapi.messages.store.triplestoremessages.{CheckConnection, InitializedResponse, ResetTriplestoreContent, ResetTriplestoreContentACK, _}
import org.knora.webapi.store._
import org.knora.webapi.store.triplestore.embedded.JenaTDBActor
import org.knora.webapi.store.triplestore.http.HttpTriplestoreConnector
import org.knora.webapi.util.FakeTriplestore
import org.knora.webapi.{ActorMaker, Settings, UnsuportedTriplestoreException}

import scala.collection.JavaConverters._
import scala.concurrent.Await

/**
  * This actor receives messages representing SPARQL requests, and forwards them to instances of one of the configured triple stores (embedded or remote).
  */
class TriplestoreManager extends Actor with ActorLogging {
    this: ActorMaker =>

    private val settings = Settings(context.system)

    implicit val timeout = settings.defaultRestoreTimeout

    var httpBased: Boolean = _
    var storeActorRef: ActorRef = _
    var initialized: Boolean = false

    // TODO: run the fake triple store as an actor (the fake triple store will not be needed anymore, once the embedded triple store is implemented)
    FakeTriplestore.init(settings.fakeTriplestoreDataDir)

    if (settings.useFakeTriplestore) {
        FakeTriplestore.load()
        log.info("Loaded fake triplestore")
    } else {
        log.debug(s"Using triplestore: ${settings.triplestoreType}")
    }

    if (settings.prepareFakeTriplestore) {
        FakeTriplestore.clear()
        log.info("About to prepare fake triplestore")
    }

    log.debug(settings.triplestoreType)

    override def preStart() {
        log.debug("TriplestoreManagerActor: start with preStart")

        storeActorRef = settings.triplestoreType match {
            case HTTP_GRAPH_DB_TS_TYPE => httpBased = true; makeActor(FromConfig.props(Props[HttpTriplestoreConnector]), name = HTTP_TRIPLESTORE_ACTOR_NAME)
            case HTTP_FUSEKI_TS_TYPE => httpBased = true; makeActor(FromConfig.props(Props[HttpTriplestoreConnector]), name = HTTP_TRIPLESTORE_ACTOR_NAME)
            case EMBEDDED_JENA_TDB_TS_TYPE => httpBased = false; makeActor(Props[JenaTDBActor], name = EMBEDDED_JENA_ACTOR_NAME)
            case unknownType => throw UnsuportedTriplestoreException(s"Embedded triplestore type $unknownType not supported")
        }

        // Checking the connection to the triple store on startup
        storeActorRef ! CheckConnection

        //println(rdfDataObjectList.length)
        val reloadDataOnStart = settings.tripleStoreConfig.getBoolean("reload-on-start")

        // data reload. this is only needed for HTTP-based triplestores because of the actor pool, so that only one actor
        // initiates the reloading of data.
        // println(s"reloadDataOnStart: $reloadDataOnStart, httpBased: $httpBased")
        if (reloadDataOnStart && httpBased) {
            log.debug("initiating data reload")
            val configList = settings.tripleStoreConfig.getConfigList("rdf-data")
            val rdfDataObjectList = configList.asScala.map {
                config => RdfDataObjectFactory(config)
            }
            val resultFuture = storeActorRef ? ResetTriplestoreContent(rdfDataObjectList)
            val result = Await.result(resultFuture, timeout.duration).asInstanceOf[ResetTriplestoreContentACK]
        }
        initialized = true
        log.debug("TriplestoreManagerActor: finished with preStart")
    }

    def receive = LoggingReceive {
        case Initialized() => sender ! InitializedResponse(this.initialized)
        case msg ⇒ storeActorRef forward msg
    }
}
