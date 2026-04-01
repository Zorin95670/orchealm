FROM alpine:3.22

RUN apk add --no-cache \
    bash \
    curl \
    jq \
    ca-certificates \
    tzdata

COPY crontab /etc/crontabs/root
COPY entrypoint.sh /entrypoint.sh
COPY check-versions.sh /usr/local/bin/check-versions.sh
COPY projects.conf /etc/version-checker/projects.conf

RUN chmod +x \
    /entrypoint.sh \
    /usr/local/bin/check-versions.sh

ENTRYPOINT ["/entrypoint.sh"]