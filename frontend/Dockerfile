FROM node:20-alpine AS build
WORKDIR /app
ARG VITE_API_URL=http://localhost:8082/api
ENV VITE_API_URL=${VITE_API_URL}
COPY package.json ./
RUN npm install
COPY . .
RUN npm test && npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
HEALTHCHECK --interval=20s --timeout=3s --start-period=10s --retries=3 CMD wget -qO- http://127.0.0.1/ || exit 1
