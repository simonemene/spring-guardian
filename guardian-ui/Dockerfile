FROM node:20-alpine AS build
WORKDIR /app
COPY guardian-ui/package.json guardian-ui/package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY guardian-ui/ ./
RUN npm run build

FROM nginx:1.27-alpine
COPY guardian-ui/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/guardian-ui /usr/share/nginx/html
EXPOSE 80
