import os
import re
from fastapi import FastAPI, Request, HTTPException, Depends
from fastapi.responses import PlainTextResponse, JSONResponse
from dotenv import load_dotenv
from fastapi.responses import StreamingResponse
from llm import get_rag_chain
from llm import get_dictionary_chain
from llm import get_llm
from fastapi.middleware.cors import CORSMiddleware
import textwrap
import json

load_dotenv()

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True,
    allow_methods=["*"], 
    allow_headers=["*"],
)

async def verify_frontend_token(request: Request):
    token = request.headers.get("X_VETT_TOKEN")
    expected_token = os.getenv("X_VETT_TOKEN")
    if token != expected_token:
        raise HTTPException(
            status_code=403,
            detail={
                "success": False,
                "detail": "Invalid FrontEnd token"
            }
        )


@app.post("/chat", response_class=PlainTextResponse)
async def chat_with_ai(request: Request, _: None = Depends(verify_frontend_token)):
    payload = await request.json()
    user_message = payload.get("message", "")
    session_id = payload.get("session_id", "default-session")

    def response_stream():
        chain = get_rag_chain()
        dictionary_chain = get_dictionary_chain()
        tax_chain = {"input": dictionary_chain} | chain
        stream = tax_chain.stream(
            {"question": user_message},
            config={"configurable": {"session_id": session_id}},
        )
        for chunk in stream:
            yield chunk

    return StreamingResponse(response_stream(), media_type="text/plain")
