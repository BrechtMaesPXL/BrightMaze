
FROM node:18-alpine

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install

RUN npm install markdown-it dompurify

COPY . .

EXPOSE 5174


CMD ["npm", "run", "dev", "--", "--host"]
