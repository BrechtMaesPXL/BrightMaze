# --- Stage 1: Test ---
FROM python:3.9-slim AS test

WORKDIR /app
COPY requirements.txt /app/
RUN pip install --no-cache-dir -r requirements.txt
COPY . /app/
ENV PYTHONUNBUFFERED=1

# Run tests
RUN pytest

# --- Stage 2: Production ---
FROM python:3.9-slim AS prod

WORKDIR /app
COPY requirements.txt /app/
RUN pip install --no-cache-dir -r requirements.txt
COPY . /app/
ENV PYTHONUNBUFFERED=1

EXPOSE 8001
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001"]