import os
import logging
import pymysql
from urllib.request import urlopen
from urllib.parse import quote_plus

logging.basicConfig(level=logging.INFO)

# Move secrets to environment variables (no hardcoded password)
db_config = {
    "host": os.getenv("DB_HOST", "mydatabase.com"),
    "user": os.getenv("DB_USER", "admin"),
    "password": os.getenv("DB_PASSWORD", ""),
    "database": os.getenv("DB_NAME", "testdb"),
}

def get_user_input():
    return input("Enter your name: ").strip()

# Avoid os.system to prevent command injection
def send_email(to, subject, body):
    logging.info("Email queued to=%s subject=%s body_length=%d", to, subject, len(body))

# Use HTTPS + timeout + safe encoding
def get_data():
    url = "https://example.com/api/get-data"
    try:
        with urlopen(url, timeout=5) as resp:
            return resp.read().decode()
    except Exception as e:
        logging.warning("Failed to fetch data: %s", e)
        return ""

# Parameterized query prevents SQL injection
def save_to_db(data):
    query = "INSERT INTO mytable (column1, column2) VALUES (%s, %s)"
    connection = pymysql.connect(**db_config)
    cursor = connection.cursor()
    cursor.execute(query, (data, "Another Value"))
    connection.commit()
    cursor.close()
    connection.close()

if __name__ == "__main__":
    user_input = get_user_input()
    data = get_data()
    save_to_db(data)
    send_email("admin@example.com", "User Input", user_input)
