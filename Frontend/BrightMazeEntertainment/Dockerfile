FROM node:18

# Werkdirectory
WORKDIR /app

# Alleen package info kopiëren voor caching van npm install
COPY package*.json ./

# Dependencies installeren
RUN npm install

# De rest van de code kopiëren
COPY . .

# Poort voor Vite dev server
EXPOSE 5174

# Start commando
CMD ["npm", "run", "dev"]
