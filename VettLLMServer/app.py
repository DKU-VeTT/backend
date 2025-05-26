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


@app.post("/py/llm/diagnosis", response_class=JSONResponse)
async def diagnosis_with_ai(request: Request, _: None = Depends(verify_frontend_token)):
    
    payload = await request.json()
    disease = payload.get("disease", "")
    confidence = payload.get("confidence", "")
    description = payload.get("description", "")
    llm = get_llm()
    request_message = f"""
    당신은 반려동물 건강 진단 전문가입니다. 사용자는 '{disease}'라는 질병이 감지되었고, 신뢰도는 {confidence}%입니다.
    다음은 반려동물의 환경 및 증상 설명입니다: "{description}"

    위 정보를 기반으로 아래와 같이 응답해주세요:
    1. 이 반려동물에게 '{disease}' 질병이 {confidence}% 신뢰도로 발생했을 가능성이 높습니다.
    2. 해당 질병에 대한 짧은 요약 설명 (1~2문장)
    3. 현재 증상과 환경을 고려했을 때의 예상 원인
    4. 집에서 시도해볼 수 있는 초기 조치 (가능한 경우)
    5. 반드시 병원에 방문해야 하는지 여부에 대한 전문가의 조언

    ※ 추정이 아닌 가능성에 기반한 설명을 해주세요.
    ※ 문장은 사용자에게 직접 설명하듯이 친절하게 작성해주세요. 그리고 번호는 붙이지 마세요.
    """

    diagnosis_result = f"""
    당신은 반려동물 질병 진단 평가 전문가입니다. 질병 이름은 '{disease}'이며, 신뢰도는 {confidence}%입니다.
    반려동물의 환경 및 상태는 다음과 같습니다: "{description}"

    위 정보를 바탕으로 다음 5단계 중 하나로 위험 수준을 진단해주세요:
    [매우 높음, 높음, 보통, 낮음, 매우 낮음]

    ※ 반드시 아래 형식 중 **하나만** content로 반환해주세요.
    예: "높음"
    ※ 부가설명 없이 오직 하나의 단어만 포함되게 해주세요.
    """

    response = llm.invoke(request_message)
    diagnosis_response = llm.invoke(diagnosis_result)

    valid_levels = ["매우 높음", "높음", "보통", "낮음", "매우 낮음"]
    response_text = diagnosis_response.content.strip()
    diagnosis_level = next((level for level in valid_levels if level in response_text), None)

    return {
        "content": response.content.strip(),
        "diagnosis": diagnosis_level
    }



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
