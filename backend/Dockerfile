FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --omit=dev

COPY src ./src
COPY migrations ./migrations
COPY sql ./sql
COPY scripts ./scripts

ENV NODE_ENV=production
EXPOSE 8080

CMD ["npm", "start"]
