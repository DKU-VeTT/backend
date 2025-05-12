from pymongo import MongoClient
from typing import List
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.messages import AIMessage, HumanMessage, BaseMessage
from pytz import timezone
from datetime import datetime
import os

client = MongoClient(os.getenv("MONGODB_URI"))
db = client["VeTT"]
collection = db["VeTT_LLM_Chat"]

class MongoDBChatMessageHistory(BaseChatMessageHistory):
    def __init__(self, session_id: str):
        self.session_id = session_id

    @property
    def messages(self) -> List[BaseMessage]:
        return self.get_messages()

    def get_messages(self) -> List[BaseMessage]:
        records = collection.find({"session_id": self.session_id})
        messages = []
        for record in records:
            role = record["role"]
            content = record["content"]
            if role == "human":
                messages.append(HumanMessage(content=content))
            elif role == "ai":
                messages.append(AIMessage(content=content))
        return messages

    def add_message(self, message: BaseMessage) -> None:
        if isinstance(message, HumanMessage):
            role = "human"
        elif isinstance(message, AIMessage):
            role = "ai"
        else:
            raise ValueError("Unknown message type")

        collection.insert_one({
            "session_id": self.session_id,
            "role": role,
            "content": message.content.strip(),
            "timestamp": datetime.now(timezone('Asia/Seoul'))
        })

    def clear(self) -> None:
        collection.delete_many({"session_id": self.session_id})