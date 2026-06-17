FROM node:22-alpine AS build
WORKDIR /app
COPY guardian-ui/package*.json ./
RUN npm ci
COPY guardian-ui/ ./
RUN npm run build

FROM nginx:1.27-alpine
COPY guardian-ui/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
