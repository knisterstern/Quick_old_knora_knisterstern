#!/usr/bin/env python3

import requests, json

# a Python dictionary that will be turned into a JSON object
resourceParams = {
   'restype_id': 'http://www.knora.org/ontology/anything#ThingPicture',
   'properties': {
   },
   'label': "knisterstern",
   'project_id': 'http://rdfh.ch/projects/0001'
}

# the name of the file to be submitted
filename = "logo.jpg"

# a tuple containing the file's name, its binaries and its mimetype
file = {'file': (filename, open(filename, 'rb'), "image/jpeg")} # use name "file"

# do a POST request providing both the JSON and the binaries
r = requests.post("http://localhost:3333/v1/resources",
                  data={'json': json.dumps(resourceParams)}, # use name "json"
                  files=file,
                  auth=('root@example.com', 'test'))

print(r.text)
