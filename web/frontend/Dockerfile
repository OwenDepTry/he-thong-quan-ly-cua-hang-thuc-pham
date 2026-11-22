# ---------- Build ----------
FROM node:22-alpine AS build
WORKDIR /src
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
# Để trống: trình duyệt gọi /api cùng domain, nginx chuyển tiếp tới backend
ARG VITE_API_URL=
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build

# ---------- Serve ----------
FROM nginx:1.27-alpine
# Ảnh nginx chính thức tự thay ${BACKEND_URL} trong /etc/nginx/templates/*.template khi khởi động
COPY nginx.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /src/dist /usr/share/nginx/html
ENV BACKEND_URL=http://backend:8080
EXPOSE 80
