FROM flyway/flyway:10

# Security: run as non-root
USER root

# Create working directory
WORKDIR /flyway

# Copy migrations
COPY db/migrations /flyway/sql

# Drop privileges
USER flyway

# Default command (can be overridden)
ENTRYPOINT ["flyway"]