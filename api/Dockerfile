FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cahche-dir -r requirements.txt

COPY . .

CMD ["python", "manage.py", "runserver", "0.0.0.0:8000"]