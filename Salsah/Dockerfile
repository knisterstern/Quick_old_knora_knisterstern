FROM ubuntu:17.04

MAINTAINER Jan Stoffregen "jan.stoffregen@uzh.ch"

#
# Install Angular Dependencies
#
RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install -y curl
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash -
RUN apt-get install -y nodejs
RUN npm install -g @angular/cli
RUN curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add -
RUN echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list
RUN apt-get update && apt-get install yarn
RUN apt-get install -y git
RUN git clone https://github.com/dhlab-basel/Salsah.git
RUN cd /Salsah && yarn install --production=false
WORKDIR /Salsah
EXPOSE 4200

CMD ng serve --host 0.0.0.0