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

package org.knora.webapi.e2e.v1

import java.net.URLEncoder

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.RouteTestTimeout
import akka.pattern._
import akka.util.Timeout
import org.knora.webapi.messages.v1.responder.ontologymessages.LoadOntologiesRequest
import org.knora.webapi.messages.store.triplestoremessages.{RdfDataObject, ResetTriplestoreContent}
import org.knora.webapi.responders.{ResponderManager, _}
import org.knora.webapi.routing.v1.ValuesRouteV1
import org.knora.webapi.store._
import org.knora.webapi.util.{AkkaHttpUtils, MutableTestIri}
import org.knora.webapi.{IRI, LiveActorMaker, R2RSpec, SharedTestDataV1}
import org.knora.webapi.messages.v1.responder.valuemessages.ApiValueV1JsonProtocol._
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Tests the values route.
  */
class ValuesV1R2RSpec extends R2RSpec {

    override def testConfigSource =
        """
         # akka.loglevel = "DEBUG"
         # akka.stdout-loglevel = "DEBUG"
        """.stripMargin

    private val responderManager = system.actorOf(Props(new ResponderManager with LiveActorMaker), name = RESPONDER_MANAGER_ACTOR_NAME)
    private val storeManager = system.actorOf(Props(new StoreManager with LiveActorMaker), name = STORE_MANAGER_ACTOR_NAME)

    private val valuesPath = ValuesRouteV1.knoraApiPath(system, settings, log)

    private val incunabulaUser = SharedTestDataV1.incunabulaProjectAdminUser

    implicit val timeout: Timeout = settings.defaultRestoreTimeout

    implicit def default(implicit system: ActorSystem) = RouteTestTimeout(new DurationInt(30).second)

    private val integerValueIri = new MutableTestIri
    private val textValueIri = new MutableTestIri
    private val linkValueIri = new MutableTestIri

    private val boringComment = "This is a boring comment."

    private val rdfDataObjects = List(
        RdfDataObject(path = "_test_data/all_data/anything-data.ttl", name = "http://www.knora.org/data/anything")
    )

    private val anythingUser = SharedTestDataV1.anythingUser1
    private val anythingUserEmail = anythingUser.userData.email.get
    private val testPass = "test"

    private val mappingIri = "http://rdfh.ch/standoff/mappings/StandardMapping"

    "Load test data" in {
        Await.result(storeManager ? ResetTriplestoreContent(rdfDataObjects), 300.seconds)
        Await.result(responderManager ? LoadOntologiesRequest(incunabulaUser), 30.seconds)
    }

    "The Values Endpoint" should {
        "add an integer value to a resource" in {
            val params =
                """
                  |{
                  |    "res_id": "http://data.knora.org/a-thing",
                  |    "prop": "http://www.knora.org/ontology/anything#hasInteger",
                  |    "int_value": 1234
                  |}
                """.stripMargin

            Post("/v1/values", HttpEntity(ContentTypes.`application/json`, params)) ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
                val responseJson: Map[String, JsValue] = responseAs[String].parseJson.asJsObject.fields
                val valueIri: IRI = responseJson("id").asInstanceOf[JsString].value
                integerValueIri.set(valueIri)
            }
        }

        "change an integer value" in {
            val params =
                """
                  |{
                  |    "res_id": "http://data.knora.org/a-thing",
                  |    "prop": "http://www.knora.org/ontology/anything#hasInteger",
                  |    "int_value": 4321
                  |}
                """.stripMargin

            Put(s"/v1/values/${URLEncoder.encode(integerValueIri.get, "UTF-8")}", HttpEntity(ContentTypes.`application/json`, params)) ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
                val responseJson: Map[String, JsValue] = responseAs[String].parseJson.asJsObject.fields
                val valueIri: IRI = responseJson("id").asInstanceOf[JsString].value
                integerValueIri.set(valueIri)
            }
        }

        "mark an integer value as deleted" in {
            Delete(s"/v1/values/${URLEncoder.encode(integerValueIri.get, "UTF-8")}?deleteComment=deleted%20for%20testing") ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
            }
        }


        "get a link value" in {
            Get(s"/v1/links/${URLEncoder.encode("http://data.knora.org/contained-thing-1", "UTF-8")}/${URLEncoder.encode("http://www.knora.org/ontology/anything#isPartOfOtherThing", "UTF-8")}/${URLEncoder.encode("http://data.knora.org/containing-thing", "UTF-8")}") ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)

                val linkValue = AkkaHttpUtils.httpResponseToJson(response).fields("value").asJsObject.fields

                assert(
                    linkValue("subjectIri").asInstanceOf[JsString].value == "http://data.knora.org/contained-thing-1" &&
                        linkValue("predicateIri").asInstanceOf[JsString].value == "http://www.knora.org/ontology/anything#isPartOfOtherThing" &&
                        linkValue("objectIri").asInstanceOf[JsString].value == "http://data.knora.org/containing-thing" &&
                        linkValue("referenceCount").asInstanceOf[JsNumber].value.toInt == 1
                )
            }
        }

        "not add an empty text value to a resource" in {
            val params =
                """
                  |{
                  |    "res_id": "http://data.knora.org/a-thing",
                  |    "prop": "http://www.knora.org/ontology/anything#hasText",
                  |    "richtext_value": {"utf8str":""}
                  |}
                """.stripMargin

            Post("/v1/values", HttpEntity(ContentTypes.`application/json`, params)) ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.BadRequest, response.toString)
            }
        }

        "add a text value containing a standoff reference to another resource" in {
            val xmlStr =
                """<?xml version="1.0" encoding="UTF-8"?>
                    |<text>
                    |   This text links to another <a class="salsah-link" href="http://data.knora.org/another-thing">resource</a>.
                    |</text>
                """.stripMargin


            val params =
                s"""
                  |{
                  |    "res_id": "http://data.knora.org/a-thing",
                  |    "prop": "http://www.knora.org/ontology/anything#hasText",
                  |    "richtext_value": {"xml": ${xmlStr.toJson.compactPrint}, "mapping_id": "$mappingIri"}
                  |}
                """.stripMargin

            Post("/v1/values", HttpEntity(ContentTypes.`application/json`, params)) ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
                val responseJson: Map[String, JsValue] = responseAs[String].parseJson.asJsObject.fields

                // check for standoff link in value creation response
                assert(responseJson("value").asInstanceOf[JsObject].fields("xml").toString.contains("http://data.knora.org/another-thing"), "standoff link target is not contained in value creation response")

                val valueIri: IRI = responseJson("id").asInstanceOf[JsString].value
                textValueIri.set(valueIri)
            }
        }

        "change a text value containing a standoff reference to another resource" in {
            val xmlStr =
                """<?xml version="1.0" encoding="UTF-8"?>
                  |<text>
                  |   This new version of the text links to another <a class="salsah-link" href="http://data.knora.org/a-thing-with-text-values">resource</a>.
                  |</text>
                """.stripMargin

            val params =
                s"""
                  |{
                  |    "res_id": "http://data.knora.org/a-thing",
                  |    "prop": "http://www.knora.org/ontology/anything#hasText",
                  |    "richtext_value": {"xml": ${xmlStr.toJson.compactPrint}, "mapping_id": "$mappingIri"}
                  |}
                """.stripMargin

            Put(s"/v1/values/${URLEncoder.encode(textValueIri.get, "UTF-8")}", HttpEntity(ContentTypes.`application/json`, params)) ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
                val responseJson: Map[String, JsValue] = responseAs[String].parseJson.asJsObject.fields

                // check for standoff link in value creation response
                assert(responseJson("value").asInstanceOf[JsObject].fields("xml").toString.contains("http://data.knora.org/a-thing-with-text-values"), "standoff link target is not contained in value creation response")

                val valueIri: IRI = responseJson("id").asInstanceOf[JsString].value
                textValueIri.set(valueIri)
            }
        }

        "get the version history of a value" in {
            Get(s"/v1/values/history/${URLEncoder.encode("http://data.knora.org/a-thing", "UTF-8")}/${URLEncoder.encode("http://www.knora.org/ontology/anything#hasText", "UTF-8")}/${URLEncoder.encode(textValueIri.get, "UTF-8")}") ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)

                val versionHistory: JsValue = AkkaHttpUtils.httpResponseToJson(response).fields("valueVersions")

                val (mostRecentVersion, originalVersion) = versionHistory match {
                    case JsArray(Vector(mostRecent, original)) => (mostRecent.asJsObject.fields, original.asJsObject.fields)
                }

                assert(mostRecentVersion("previousValue").asInstanceOf[JsString].value == originalVersion("valueObjectIri").asInstanceOf[JsString].value)
                assert(originalVersion("previousValue") == JsNull)
            }
        }

        "mark as deleted a text value containing a standoff reference to another resource" in {
            Delete(s"/v1/values/${URLEncoder.encode(textValueIri.get, "UTF-8")}?deleteComment=deleted%20for%20testing") ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
            }
        }

        "add a link value to a resource" in {
            val params =
                """
                  |{
                  |    "res_id": "http://data.knora.org/a-thing",
                  |    "prop": "http://www.knora.org/ontology/anything#hasOtherThing",
                  |    "link_value": "http://data.knora.org/another-thing"
                  |}
                """.stripMargin

            Post("/v1/values", HttpEntity(ContentTypes.`application/json`, params)) ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
                val responseJson: Map[String, JsValue] = responseAs[String].parseJson.asJsObject.fields
                val valueIri: IRI = responseJson("id").asInstanceOf[JsString].value
                linkValueIri.set(valueIri)
            }
        }

        "mark a link value as deleted" in {
            Delete(s"/v1/values/${URLEncoder.encode(linkValueIri.get, "UTF-8")}?deleteComment=deleted%20for%20testing") ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
            }
        }

        "add a link value with a comment to a resource" in {
            val params =
                s"""
                   |{
                   |    "res_id": "http://data.knora.org/a-thing",
                   |    "prop": "http://www.knora.org/ontology/anything#hasOtherThing",
                   |    "link_value": "http://data.knora.org/another-thing",
                   |    "comment":"$boringComment"
                   |}
                """.stripMargin

            Post("/v1/values", HttpEntity(ContentTypes.`application/json`, params)) ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)
                val responseJson: Map[String, JsValue] = responseAs[String].parseJson.asJsObject.fields
                val valueIri: IRI = responseJson("id").asInstanceOf[JsString].value
                linkValueIri.set(valueIri)
            }
        }

        "get a link value with a comment" in {
            Get(s"/v1/links/${URLEncoder.encode("http://data.knora.org/a-thing", "UTF-8")}/${URLEncoder.encode("http://www.knora.org/ontology/anything#hasOtherThing", "UTF-8")}/${URLEncoder.encode("http://data.knora.org/another-thing", "UTF-8")}") ~> addCredentials(BasicHttpCredentials(anythingUserEmail, testPass)) ~> valuesPath ~> check {
                assert(status == StatusCodes.OK, response.toString)

                val responseObj = AkkaHttpUtils.httpResponseToJson(response).fields
                val comment = responseObj("comment").asInstanceOf[JsString].value
                val linkValue = responseObj("value").asJsObject.fields

                assert(
                    linkValue("subjectIri").asInstanceOf[JsString].value == "http://data.knora.org/a-thing" &&
                        linkValue("predicateIri").asInstanceOf[JsString].value == "http://www.knora.org/ontology/anything#hasOtherThing" &&
                        linkValue("objectIri").asInstanceOf[JsString].value == "http://data.knora.org/another-thing" &&
                        linkValue("referenceCount").asInstanceOf[JsNumber].value.toInt == 1 &&
                        comment == boringComment
                )
            }
        }
    }
}
