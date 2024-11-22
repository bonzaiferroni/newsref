from typing import Union
from fastapi import FastAPI
from dotenv import load_dotenv
import os
import requests


# Load environment variables from .env
load_dotenv(dotenv_path="../.env")  # Adjust path if needed

hf_key = os.getenv("HF_KEY")

# print(hf_key)

API_URL = "https://api-inference.huggingface.co/models/cardiffnlp/twitter-roberta-base-sentiment-latest"
headers = {"Authorization": f"Bearer {hf_key}"}
payload = {
    "inputs": "Today is a great day",
}

# response = requests.post(API_URL, headers=headers, json=payload)
# print(response.json())

app = FastAPI()

@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.get("/items/{item_id}")
def read_item(item_id: int, q: Union[str, None] = None):
    return {"item_id": item_id, "q": q}

@app.post("/models/{model_name}")
def get(model_name: str):
    return model_name