## Get Knora, Salsah, Sipi and GraphDB up and running with Docker

### Attention: Ontologies in GraphDB are not stored in a persistent way yet.  This will be the next step and will come soon.

### Prerequisites:
 - Install Docker. If you use a Linux distribution, you might have to install docker-compose as well.
 - You need to have 20GB of free storage
 - You might need to install "expect" for step 5. Check if you have it installed: <pre>expect -v</pre>
 - If you use Mac OS X: Increase Memory and CPUs that Docker is allowed to use. It works with 10 GB, maybe less as well.
	 - 	Mac: Go to Docker > Preferences > Advanced
	 - If images stop running without an explanation, you might need to increase the memory a little bit more.

### Deployment alternatives
You can find two alternatives here to perform the deployment.

1. Fast and recommended alternative: Go to the KnoSaSi-PrebuiltImages - Folder and follow the instructions below. This alternative will pull all images from Dockerhub and get them up and running.
2. Slow alternative: To be sure to get the latest version of all parts of the software, you can built all images "from scratch" on your machine as well. Stay in the current directory and follow the instructions below.

After you have chosen your alternative, only perform the instructions that are necessary as indicated below for the alternative that you have chosen.

|   | Instructions   | Alternative 1| Alternative 2  | 
|--:| ------------- |:-------------:| :-----:        |
| 0 | If you use Mac OS X: Make sure that you have increased the memory allocated to Docker as described above under "Prerequisites" | x | x |
| 1 | <pre>git clone --recursive <br/>https://github.com/<br/>nie-ine/Quick-Installation-Guide<br/>---Knora-Salsah-Sipi-GraphDB</pre> | x | x |
| 2 | Change hostname for graphdb to "graphdb" and sipi to "sipi", both are localhost before this change.| not necessary for alternative 1 | x in Knora/Knora/webapi /src /main /resources /application.conf|
| 3 | Map the name sipi to the ip address of localhost. e.g. next to localhost the name sipi should be mapped to the same ip adress as localhost. <pre>sudo vi /etc/hosts</pre> The file should contain the following lines:<pre>127.0.0.1       localhost</pre><pre>127.0.0.1       sipi</pre>| x in /etc/hosts file | <--- x|
| 4 | <pre>docker-compose up</pre> Wait until Salsah, Sipi and GraphDB are running. If no new lines show up from docker-compose in the terminal, check the services in the Browser. Knora should not give any response at this point. The services are available at: Sipi: localhost:1024, Knora: localhost:3333, GraphDB: localhost:7200, Salsah: localhost:4200 | x in ./PrebuiltImages | x in ./ --> meaning in docker-compose-deployment-Knora-Salsah-Sipi-GraphDB |
| 5 | After GraphDB is running: <pre>./updateOntology.sh</pre> This will import the ontologies and restart Knora afterwards. It will take some time. While waiting, you can do the steps 7 and you can install pyhton3 and the requested package as described in step 8. | x in ./ --> meaning in docker-compose-deployment-Knora-Salsah-Sipi-GraphDB | <--- x |
| 7 | <pre>./createSipiFileStructure.sh </pre> Sipi will store the images in this folder structure | x | x  |
| 8 | Change the label in the python script on line 10 to an individual name. To execute the import Script in ImportPictureTest you need python3 and the pypthon package "requests" which you can install in the following way. <pre>pip3 install requests</pre> Execute the script.<pre>python3 import.py</pre> If you get a json with the resource description back from knora after executing the code and if you can find the resource and the picture in Salsah using the full text search searching for the given label, your setup is working.  | x in ./ImportPictureTest  | <--- x |



 
If you would like to stop the containers:

 - If you press ctrl+c twice, docker will force containers to stop, the containers wont be deleted though. Since the imported data for the triple store is not saved persistently yet, this option saves the data in the container as well.
 - docker-compose down stops and deletes all containers.
