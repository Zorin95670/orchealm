FROM node:22 AS builder
RUN corepack enable && corepack prepare pnpm@10.25.0 --activate
WORKDIR /app
COPY . .
ENV CI=true
# pnpm store kept in a BuildKit cache: packages are not downloaded again on each build.
RUN --mount=type=cache,target=/root/.local/share/pnpm/store pnpm install --frozen-lockfile
RUN pnpm build

# Unprivileged nginx: runs as uid 101 and can only listen on ports above 1024.
FROM nginxinc/nginx-unprivileged:stable-alpine
COPY --from=builder /app/dist/spa /usr/share/nginx/html
EXPOSE 8443
CMD ["nginx", "-g", "daemon off;"]
