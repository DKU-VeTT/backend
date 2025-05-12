import os
import re
import textwrap
import json

from pinecone import Pinecone
from langchain_pinecone import PineconeVectorStore
from langchain_upstage import UpstageEmbeddings
from langchain_community.document_loaders import Docx2txtLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter

from fastapi import FastAPI, Request, HTTPException, Depends
from fastapi.responses import PlainTextResponse, JSONResponse
from dotenv import load_dotenv
from fastapi.responses import StreamingResponse
from llm import get_rag_chain
from llm import get_dictionary_chain
from llm import get_llm
from fastapi.middleware.cors import CORSMiddleware

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


@app.post("/py/llm/fetch", response_class=PlainTextResponse)
async def initial_vector_database(request: Request, _: None = Depends(verify_frontend_token)):

    embedding = UpstageEmbeddings(model="embedding-query")
    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=1500,
        chunk_overlap=200
    )
    loader = Docx2txtLoader('./data.docx')
    document_list = loader.load_and_split(text_splitter=text_splitter)

    index_name = 'vett-upstage-index'
    pinecone_api_key = os.environ.get("PINECONE_API_KEY")
    pc = Pinecone(api_key=pinecone_api_key)
    database = PineconeVectorStore.from_documents(document_list, embedding, index_name=index_name)
    return "Success Initial Vector Database."


@app.post("/py/llm/chat", response_class=PlainTextResponse)
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
