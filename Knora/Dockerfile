FROM dhlabbasel/webapi-base:latest

MAINTAINER Ivan Subotic "ivan.subotic@unibas.ch"

# Get and build webapi release package, cleanup
RUN mkdir /Knora
RUN ls
WORKDIR /
COPY /Knora ./Knora

RUN cd /Knora/webapi && sbt stage
RUN mkdir /webapi
RUN cp -r /Knora/webapi/target/universal/stage/* /webapi
RUN rm -rf /Knora

VOLUME ["/localdata"]

# Expose the webapi default port
EXPOSE 3333

CMD ["/webapi/bin/webapi"]