# Use the official image as a parent image.
FROM lscr.io/linuxserver/code-server:latest

ENV SUDO_PASSWORD=test

RUN apt update && apt install -y npm
RUN /app/code-server/bin/code-server \
    --extensions-dir /config/extensions \
    --install-extension dbaeumer.vscode-eslint \
    --install-extension mhutchie.git-graph


# Expose VS Code
EXPOSE 8443

# Set the working directory.
WORKDIR /config
