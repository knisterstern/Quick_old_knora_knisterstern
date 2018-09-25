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

package org.knora.webapi.messages.v2.responder.ontologymessages

import org.knora.webapi._
import org.knora.webapi.messages.v2.responder.ontologymessages.Cardinality.KnoraCardinalityInfo
import org.knora.webapi.util.IriConversions._
import org.knora.webapi.util.{SmartIri, StringFormatter}

/**
  * Represents the `knora-api` ontology, version 2, in the [[ApiV2Simple]] schema.
  */
object KnoraApiV2Simple {

    private implicit val stringFormatter: StringFormatter = StringFormatter.getInstanceForConstantOntologies

    val OntologyMetadata = OntologyMetadataV2(
        ontologyIri = OntologyConstants.KnoraApiV2Simple.KnoraApiOntologyIri.toSmartIri,
        label = Some("The simplified knora-api ontology")
    )

    val Resource: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.Resource,
        subClassOf = Set(OntologyConstants.SchemaOrg.Thing),
        directCardinalities = Map(
            OntologyConstants.SchemaOrg.Name -> Cardinality.MustHaveOne,
            OntologyConstants.KnoraApiV2Simple.HasStandoffLinkTo -> Cardinality.MayHaveMany,
            OntologyConstants.KnoraApiV2Simple.CreationDate -> Cardinality.MustHaveOne,
            OntologyConstants.KnoraApiV2Simple.LastModificationDate -> Cardinality.MayHaveOne
        )
    )

    val ForbiddenResource: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.ForbiddenResource,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Resource),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A ForbiddenResource is a proxy for a resource that the client has insufficient permissions to see."
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A ForbiddenResource is a proxy for a resource that the client has insufficient permissions to see."
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasComment -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val Result: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.Result,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Ergebnis",
                    LanguageCodes.EN -> "result",
                    LanguageCodes.FR -> "résultat",
                    LanguageCodes.IT -> "risultato"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Provides a message indicating that an operation was successful"
                )
            )
        ),
        objectType = Some(OntologyConstants.Xsd.String)
    )

    val Error: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.Error,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Fehler",
                    LanguageCodes.EN -> "error",
                    LanguageCodes.FR -> "erreur",
                    LanguageCodes.IT -> "errore"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Provides a message indicating that an operation was unsuccessful"
                )
            )
        ),
        objectType = Some(OntologyConstants.Xsd.String)
    )

    val HasStandoffLinkTo: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasStandoffLinkTo,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasLinkTo),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat Standofflink zu",
                    LanguageCodes.EN -> "has standoff link to",
                    LanguageCodes.FR -> "a lien standoff vers",
                    LanguageCodes.IT -> "ha link standoff verso"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a direct connection between two resources, generated by standoff markup"
                )
            )
        ),
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Resource),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.Resource)
    )

    val CreationDate: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.CreationDate,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Creation date"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Indicates when a resource was created"
                )
            )
        ),
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Resource),
        objectType = Some(OntologyConstants.Xsd.DateTimeStamp)
    )

    val LastModificationDate: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.LastModificationDate,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Last modification date"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Indicates when a resource was last modified"
                )
            )
        ),
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Resource),
        objectType = Some(OntologyConstants.Xsd.DateTimeStamp)
    )

    val Region: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.Region,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Resource),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Region",
                    LanguageCodes.EN -> "Region",
                    LanguageCodes.FR -> "Région",
                    LanguageCodes.IT -> "Regione"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a geometric region of a resource. The geometry is represented currently as JSON string."
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasColor -> Cardinality.MustHaveOne,
            OntologyConstants.KnoraApiV2Simple.IsRegionOf -> Cardinality.MustHaveOne,
            OntologyConstants.KnoraApiV2Simple.HasGeometry -> Cardinality.MustHaveSome,
            OntologyConstants.KnoraApiV2Simple.HasComment -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val IsPartOf: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.IsPartOf,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Resource),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.Resource),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasLinkTo),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "ist Teil von",
                    LanguageCodes.EN -> "is part of",
                    LanguageCodes.FR -> "fait partie de",
                    LanguageCodes.IT -> "fa parte di"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Indicates that this resource is part of another resource"
                )
            )
        )
    )

    val IsRegionOf: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.IsRegionOf,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Region),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.Representation),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasLinkTo),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "ist Region von",
                    LanguageCodes.EN -> "is region of",
                    LanguageCodes.FR -> "est région de",
                    LanguageCodes.IT -> "è regione di"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Indicates which representation a region refers to"
                )
            )
        )
    )

    val HasGeometry: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasGeometry,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Region),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.Geom),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasValue),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Geometrie",
                    LanguageCodes.EN -> "Geometry",
                    LanguageCodes.FR -> "Géometrie",
                    LanguageCodes.IT -> "Geometria"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a geometrical shape."
                )
            )
        )
    )

    val LinkObject: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.LinkObj,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Resource),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Link Object"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a generic link object."
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasComment -> Cardinality.MayHaveMany,
            OntologyConstants.KnoraApiV2Simple.HasLinkTo -> Cardinality.MustHaveOne
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val Representation: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.Representation,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Resource),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Repräsentation",
                    LanguageCodes.EN -> "Representation",
                    LanguageCodes.FR -> "Répresentation",
                    LanguageCodes.IT -> "Rappresentazione"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A Resource that can store one or more Files"
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasFile -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val StillImageRepresentation: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.StillImageRepresentation,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Representation),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Repräsentation (Bild)",
                    LanguageCodes.EN -> "Representation (Image)",
                    LanguageCodes.FR -> "Répresentation (Image)",
                    LanguageCodes.IT -> "Rappresentazione (Immagine)"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A Resource that can contain two-dimensional still image files"
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasStillImageFile -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val MovingImageRepresentation: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.MovingImageRepresentation,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Representation),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Repräsentation (Video)",
                    LanguageCodes.EN -> "Representation (Movie)",
                    LanguageCodes.FR -> "Répresentation (Film)",
                    LanguageCodes.IT -> "Rappresentazione (Film)"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A Resource containing moving image data"
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasMovingImageFile -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val AudioRepresentation: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.AudioRepresentation,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Representation),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Repräsentation (Audio)",
                    LanguageCodes.EN -> "Representation (Audio)",
                    LanguageCodes.FR -> "Répresentation (Audio)",
                    LanguageCodes.IT -> "Rappresentazione (Audio)"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A Resource containing audio data"
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasAudioFile -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val DDDRepresentation: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.DDDRepresentation,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Representation),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Repräsentation (3D)",
                    LanguageCodes.EN -> "Representation (3D)",
                    LanguageCodes.FR -> "Répresentation (3D)",
                    LanguageCodes.IT -> "Rappresentazione (3D)"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A Resource containing 3D data"
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasDDDFile -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val TextRepresentation: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.TextRepresentation,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Representation),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Repräsentation (Text)",
                    LanguageCodes.EN -> "Representation (Text)",
                    LanguageCodes.FR -> "Répresentation (Texte)",
                    LanguageCodes.IT -> "Rappresentazione (Testo)"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A Resource containing text files"
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasTextFile -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val DocumentRepresentation: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.DocumentRepresentation,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.Representation),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Repräsentation (Dokument)",
                    LanguageCodes.EN -> "Representation (Document)",
                    LanguageCodes.FR -> "Répresentation (Document)",
                    LanguageCodes.IT -> "Rappresentazione (Documento)"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "A Resource containing documents"
                )
            )
        ),
        directCardinalities = Map(
            OntologyConstants.KnoraApiV2Simple.HasDocumentFile -> Cardinality.MustHaveSome
        ),
        inheritedCardinalities = Resource.allCardinalities
    )

    val HasValue: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasValue,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Resource)
    )

    val HasLinkTo: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasLinkTo,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat Link zu",
                    LanguageCodes.EN -> "has Link to",
                    LanguageCodes.FR -> "a lien vers",
                    LanguageCodes.IT -> "ha Link verso"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a direct connection between two resources"
                )
            )
        ),
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Resource),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.Resource)
    )

    val SubjectType: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.SubjectType,
        propertyType = OntologyConstants.Rdf.Property,
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Subject type"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Specifies the required type of the subjects of a property"
                )
            )
        )
    )

    val ObjectType: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.ObjectType,
        propertyType = OntologyConstants.Rdf.Property,
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Object type"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Specifies the required type of the objects of a property"
                )
            )
        )

    )

    val ResourceIcon: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.ResourceIcon,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        subjectType = Some(OntologyConstants.Owl.Class),
        objectType = Some(OntologyConstants.Xsd.String),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Resource icon"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Specifies an icon to be used to represent instances of a resource class"
                )
            )
        )
    )

    val HasColor: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasColor,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Region),
        objectType = Some(OntologyConstants.Xsd.String),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasValue),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Farbe",
                    LanguageCodes.EN -> "Color",
                    LanguageCodes.FR -> "Couleur",
                    LanguageCodes.IT -> "Colore"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Specifies the color of a Region"
                )
            )
        )
    )

    val HasComment: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasComment,
        propertyType = OntologyConstants.Owl.DatatypeProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Resource),
        objectType = Some(OntologyConstants.Xsd.String),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasValue),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "Kommentar",
                    LanguageCodes.EN -> "Comment",
                    LanguageCodes.FR -> "Commentaire",
                    LanguageCodes.IT -> "Commento"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a comment on a Resource"
                )
            )
        )
    )

    val HasFile: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasFile,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.Representation),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.File),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasValue),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat Datei",
                    LanguageCodes.EN -> "has file",
                    LanguageCodes.FR -> "a fichier",
                    LanguageCodes.IT -> "ha file"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Connects a Representation to a file"
                )
            )
        )
    )

    val HasStillImageFile: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasStillImageFile,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.StillImageRepresentation),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.StillImageFile),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasFile),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat Bilddatei",
                    LanguageCodes.EN -> "has image file",
                    LanguageCodes.FR -> "a fichier d'image",
                    LanguageCodes.IT -> "ha file immagine"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Connects a Representation to an image file"
                )
            )
        )
    )

    val HasMovingImageFile: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasMovingImageFile,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.MovingImageRepresentation),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.MovingImageFile),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasFile),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat Filmdatei",
                    LanguageCodes.EN -> "has movie file",
                    LanguageCodes.FR -> "a fichier de film",
                    LanguageCodes.IT -> "ha file film"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Connects a Representation to a moving image file"
                )
            )
        )
    )

    val HasAudioImageFile: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasAudioFile,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.AudioRepresentation),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.AudioFile),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasFile),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat Audiodatei",
                    LanguageCodes.EN -> "has audio file",
                    LanguageCodes.FR -> "a fichier d'audio",
                    LanguageCodes.IT -> "ha file audio"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Connects a Representation to an audio file"
                )
            )
        )
    )

    val HasDDDFile: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasDDDFile,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.DDDRepresentation),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.DDDFile),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasFile),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat 3D-Datei",
                    LanguageCodes.EN -> "has 3D file",
                    LanguageCodes.FR -> "a fichier 3D",
                    LanguageCodes.IT -> "ha file 3D"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Connects a Representation to a 3D file"
                )
            )
        )
    )

    val HasTextFile: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasTextFile,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.TextRepresentation),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.TextFile),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasFile),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat Textdatei",
                    LanguageCodes.EN -> "has text file",
                    LanguageCodes.FR -> "a fichier de texte",
                    LanguageCodes.IT -> "ha file testo"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Connects a Representation to a text file"
                )
            )
        )
    )

    val HasDocumentFile: ReadPropertyInfoV2 = makeProperty(
        propertyIri = OntologyConstants.KnoraApiV2Simple.HasDocumentFile,
        propertyType = OntologyConstants.Owl.ObjectProperty,
        subjectType = Some(OntologyConstants.KnoraApiV2Simple.DocumentRepresentation),
        objectType = Some(OntologyConstants.KnoraApiV2Simple.DocumentFile),
        subPropertyOf = Set(OntologyConstants.KnoraApiV2Simple.HasFile),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.DE -> "hat Dokument",
                    LanguageCodes.EN -> "has document",
                    LanguageCodes.FR -> "a document",
                    LanguageCodes.IT -> "ha documento"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Connects a Representation to a document"
                )
            )
        )
    )

    val Date: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.Date,
        xsdStringRestrictionPattern = Some("(GREGORIAN|JULIAN):\\d{1,4}(-\\d{1,2}(-\\d{1,2})?)?( BC| AD| BCE| CE)?(:\\d{1,4}(-\\d{1,2}(-\\d{1,2})?)?( BC| AD| BCE| CE)?)?"),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Date literal"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a date as a period with different possible precisions."
                )
            )
        )
    )

    val Color: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.Color,
        xsdStringRestrictionPattern = Some("#([0-9a-fA-F]{3}){1,2}"),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Color literal"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a color."
                )
            )
        )
    )

    val Interval: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.Interval,
        xsdStringRestrictionPattern = Some("\\d+(\\.\\d+)?,\\d+(\\.\\d+)?"),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Interval literal"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents an interval."
                )
            )
        )
    )

    val Geoname: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.Geoname,
        xsdStringRestrictionPattern = Some("\\d{1,8}"),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Geoname code"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a Geoname code."
                )
            )
        )
    )

    val Geom: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.Geom,
        subClassOf = Some(OntologyConstants.Xsd.String),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Geometry specification"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a geometry specification in JSON."
                )
            )
        )
    )

    val File: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.File,
        subClassOf = Some(OntologyConstants.Xsd.Uri),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "File URI"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a file URI."
                )
            )
        )
    )


    val TextFile: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.TextFile,
        subClassOf = Some(OntologyConstants.KnoraApiV2Simple.File),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Text file URI"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a text file URI."
                )
            )
        )
    )

    val StillImageFile: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.StillImageFile,
        subClassOf = Some(OntologyConstants.KnoraApiV2Simple.File),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Still image file URI"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a still image file URI."
                )
            )
        )
    )

    val MovingImageFile: ReadClassInfoV2 = makeDatatype(
        datatypeIri = OntologyConstants.KnoraApiV2Simple.MovingImageFile,
        subClassOf = Some(OntologyConstants.KnoraApiV2Simple.File),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Moving image file URI"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a moving image file URI."
                )
            )
        )
    )

    val AudioFile: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.AudioFile,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.File),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Audio file URI"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents an audio file URI."
                )
            )
        )
    )

    val DDDFile: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.DDDFile,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.File),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "3D file URI"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a 3D file URI."
                )
            )
        )
    )

    val DocumentFile: ReadClassInfoV2 = makeClass(
        classIri = OntologyConstants.KnoraApiV2Simple.DocumentFile,
        subClassOf = Set(OntologyConstants.KnoraApiV2Simple.File),
        predicates = Seq(
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Label,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Document file URI"
                )
            ),
            makePredicate(
                predicateIri = OntologyConstants.Rdfs.Comment,
                objectsWithLang = Map(
                    LanguageCodes.EN -> "Represents a document file URI."
                )
            )
        )
    )

    /**
      * All the classes in the `knora-api` v2 ontology in the [[ApiV2Simple]] schema.
      */
    val Classes: Map[SmartIri, ReadClassInfoV2] = Set(
        Resource,
        ForbiddenResource,
        Region,
        LinkObject,
        Representation,
        StillImageRepresentation,
        MovingImageRepresentation,
        AudioRepresentation,
        DDDRepresentation,
        TextRepresentation,
        DocumentRepresentation,
        File,
        TextFile,
        StillImageFile,
        MovingImageFile,
        AudioFile,
        DDDFile,
        DocumentFile,
        Date,
        Color,
        Interval,
        Geoname,
        Geom
    ).map {
        classInfo => classInfo.entityInfoContent.classIri -> classInfo
    }.toMap

    /**
      * All the properties in the `knora-api` v2 ontology in the [[ApiV2Simple]] schema.
      */
    val Properties: Map[SmartIri, ReadPropertyInfoV2] = Set(
        Result,
        Error,
        CreationDate,
        LastModificationDate,
        HasValue,
        HasLinkTo,
        HasStandoffLinkTo,
        SubjectType,
        ObjectType,
        ResourceIcon,
        IsPartOf,
        IsRegionOf,
        HasGeometry,
        HasColor,
        HasComment,
        HasFile,
        HasStillImageFile,
        HasMovingImageFile,
        HasAudioImageFile,
        HasDDDFile,
        HasTextFile,
        HasDocumentFile
    ).map {
        propertyInfo => propertyInfo.entityInfoContent.propertyIri -> propertyInfo
    }.toMap

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Convenience functions for building ontology entities, to make the code above more concise.

    /**
      * Makes a [[PredicateInfoV2]].
      *
      * @param predicateIri    the IRI of the predicate.
      * @param objects         the non-language-specific objects of the predicate.
      * @param objectsWithLang the language-specific objects of the predicate.
      * @return a [[PredicateInfoV2]].
      */
    private def makePredicate(predicateIri: IRI,
                              objects: Set[String] = Set.empty[String],
                              objectsWithLang: Map[String, String] = Map.empty[String, String]): PredicateInfoV2 = {
        PredicateInfoV2(
            predicateIri = predicateIri.toSmartIri,
            objects = objects,
            objectsWithLang = objectsWithLang
        )
    }

    /**
      * Makes a [[ReadPropertyInfoV2]].
      *
      * @param propertyIri   the IRI of the property.
      * @param propertyType  the type of the property (owl:ObjectProperty, owl:DatatypeProperty, or rdf:Property).
      * @param subPropertyOf the set of direct superproperties of this property.
      * @param predicates    the property's predicates.
      * @param subjectType   the required type of the property's subject.
      * @param objectType    the required type of the property's object.
      * @return a [[ReadPropertyInfoV2]].
      */
    private def makeProperty(propertyIri: IRI,
                             propertyType: IRI,
                             subPropertyOf: Set[IRI] = Set.empty[IRI],
                             predicates: Seq[PredicateInfoV2] = Seq.empty[PredicateInfoV2],
                             subjectType: Option[IRI] = None,
                             objectType: Option[IRI] = None): ReadPropertyInfoV2 = {
        val propTypePred = makePredicate(
            predicateIri = OntologyConstants.Rdf.Type,
            objects = Set(propertyType)
        )

        val maybeSubjectTypePred = subjectType.map {
            subjType =>
                makePredicate(
                    predicateIri = OntologyConstants.KnoraApiV2Simple.SubjectType,
                    objects = Set(subjType)
                )
        }

        val maybeObjectTypePred = objectType.map {
            objType =>
                makePredicate(
                    predicateIri = OntologyConstants.KnoraApiV2Simple.ObjectType,
                    objects = Set(objType)
                )
        }

        val predsWithTypes = predicates ++ maybeSubjectTypePred ++ maybeObjectTypePred :+ propTypePred

        ReadPropertyInfoV2(
            entityInfoContent = PropertyInfoContentV2(
                propertyIri = propertyIri.toSmartIri,
                ontologySchema = ApiV2Simple,
                predicates = predsWithTypes.map {
                    pred => pred.predicateIri -> pred
                }.toMap,
                subPropertyOf = subPropertyOf.map(_.toSmartIri)
            )
        )
    }

    /**
      * Makes a [[ReadClassInfoV2]] representing an owl:Class.
      *
      * @param classIri               the IRI of the class.
      * @param subClassOf             the set of direct superclasses of this class.
      * @param predicates             the predicates of the class.
      * @param directCardinalities    the direct cardinalities of the class.
      * @param inheritedCardinalities the inherited cardinalities of the class.
      * @return a [[ReadClassInfoV2]].
      */
    private def makeClass(classIri: IRI,
                          subClassOf: Set[IRI] = Set.empty[IRI],
                          predicates: Seq[PredicateInfoV2] = Seq.empty[PredicateInfoV2],
                          directCardinalities: Map[IRI, Cardinality.Value] = Map.empty[IRI, Cardinality.Value],
                          inheritedCardinalities: Map[SmartIri, KnoraCardinalityInfo] = Map.empty[SmartIri, KnoraCardinalityInfo]): ReadClassInfoV2 = {

        val rdfType = OntologyConstants.Rdf.Type.toSmartIri -> PredicateInfoV2(
            predicateIri = OntologyConstants.Rdf.Type.toSmartIri,
            objects = Set(OntologyConstants.Owl.Class)
        )

        ReadClassInfoV2(
            entityInfoContent = ClassInfoContentV2(
                classIri = classIri.toSmartIri,
                predicates = predicates.map {
                    pred => pred.predicateIri -> pred
                }.toMap + rdfType,
                directCardinalities = directCardinalities.map {
                    case (propertyIri, cardinality) => propertyIri.toSmartIri -> KnoraCardinalityInfo(cardinality)
                },
                subClassOf = subClassOf.map(_.toSmartIri),
                ontologySchema = ApiV2Simple
            ),
            inheritedCardinalities = inheritedCardinalities
        )
    }

    /**
      * Makes a [[ReadClassInfoV2]] representing an rdfs:Datatype.
      *
      * @param datatypeIri                 the IRI of the datatype.
      * @param subClassOf                  the superclass of the datatype.
      * @param xsdStringRestrictionPattern an optional xsd:pattern specifying
      *                                    the regular expression that restricts its values. This has the effect of making the
      *                                    class a subclass of a blank node with owl:onDatatype xsd:string.
      * @param predicates                  the predicates of the datatype.
      * @return a [[ReadClassInfoV2]].
      */
    private def makeDatatype(datatypeIri: IRI,
                             subClassOf: Option[IRI] = None,
                             xsdStringRestrictionPattern: Option[String] = None,
                             predicates: Seq[PredicateInfoV2] = Seq.empty[PredicateInfoV2]): ReadClassInfoV2 = {

        val rdfType = OntologyConstants.Rdf.Type.toSmartIri -> PredicateInfoV2(
            predicateIri = OntologyConstants.Rdf.Type.toSmartIri,
            objects = Set(OntologyConstants.Rdfs.Datatype)
        )

        ReadClassInfoV2(
            entityInfoContent = ClassInfoContentV2(
                classIri = datatypeIri.toSmartIri,
                xsdStringRestrictionPattern = xsdStringRestrictionPattern,
                predicates = predicates.map {
                    pred => pred.predicateIri -> pred
                }.toMap + rdfType,
                subClassOf = subClassOf.toSet.map {
                    iri: IRI => iri.toSmartIri
                },
                ontologySchema = ApiV2Simple
            )
        )
    }
}
