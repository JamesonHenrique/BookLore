FROM node:21 as build-stage

WORKDIR /app

COPY booklore-ui/package.json booklore-ui/package-lock.json ./
RUN npm install -g @angular/cli
RUN npm install
COPY . .
EXPOSE 4200
CMD ["ng", "serve", "--host","0.0.0.0"]