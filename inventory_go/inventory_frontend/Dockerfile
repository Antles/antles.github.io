# Stage 1: Build the React application
FROM node:20-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json (or yarn.lock, etc.)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application source code
COPY . .

# Build the application for production
RUN npm run build

# Stage 2: Serve the application using Nginx
# Use a lightweight Nginx image
FROM nginx:1.25-alpine

# Copy the built static files from the builder stage to the Nginx public directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy the custom Nginx configuration file
# This is crucial for routing API requests and serving the React app correctly.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# The default Nginx command will start the server
CMD ["nginx", "-g", "daemon off;"]
