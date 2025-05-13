from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder, FewShotChatMessagePromptTemplate
from langchain.chains import create_history_aware_retriever, create_retrieval_chain
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain_upstage import ChatUpstage
from langchain_upstage import UpstageEmbeddings
from langchain_pinecone import PineconeVectorStore
from langchain_community.chat_message_histories import ChatMessageHistory
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.runnables.history import RunnableWithMessageHistory
from config import answer_examples
from mongoDBClient import MongoDBChatMessageHistory
import textwrap
import os
from dotenv import load_dotenv

load_dotenv()

def get_session_history(session_id: str) -> BaseChatMessageHistory:
    return MongoDBChatMessageHistory(session_id)

def get_retriever():
    embedding = UpstageEmbeddings(model="embedding-query")
    index_name = 'vett-upstage-index'
    database = PineconeVectorStore.from_existing_index(index_name=index_name, embedding=embedding)
    retriever = database.as_retriever(search_kwargs={'k': 4})
    return retriever

def get_llm(model='solar-pro'):
    llm = ChatUpstage(model=model,api_key=os.getenv("UPSTAGE_API_KEY"))
    return llm

def get_history_retriever():
    llm = get_llm()
    retriever = get_retriever()
    
    contextualize_q_system_prompt = (
        "Given a chat history and the latest user question, "
        "which might reference context in the chat history, "
        "formulate a standalone question which can be understood without the chat history. "
        "Do NOT answer the question, just reformulate it if needed and otherwise return it as is. "
        "Additionally, insert natural line breaks at appropriate places such as after periods (.), dashes (-), "
        "or other punctuation marks where it improves readability."
        "Use '\\n' to indicate line breaks in the output text."
    )

    contextualize_q_prompt = ChatPromptTemplate.from_messages(
        [
            ("system", contextualize_q_system_prompt),
            MessagesPlaceholder("chat_history"),
            ("human", "{input}"),
        ]
    )
    
    history_aware_retriever = create_history_aware_retriever(
        llm, retriever, contextualize_q_prompt
    )
    return history_aware_retriever

def get_dictionary_chain():
    dictionary = []
    llm = get_llm()
    prompt = ChatPromptTemplate.from_template(
        textwrap.dedent(f"""
            사용자의 질문을 보고, 우리의 사전을 참고해서 사용자의 질문을 변경해주세요.
            만약 변경할 필요가 없다고 판단된다면, 사용자의 질문을 변경하지 않아도 됩니다.
            그런 경우에는 질문만 리턴해주세요
            사전: {dictionary}
            질문: {{question}}
        """)
    )
    dictionary_chain = prompt | llm | StrOutputParser()
    return dictionary_chain

def get_rag_chain():
    llm = get_llm()
    example_prompt = ChatPromptTemplate.from_messages(
        [
            ("human", "{input}"),
            ("ai", "{answer}"),
        ]
    )
    few_shot_prompt = FewShotChatMessagePromptTemplate(
        example_prompt=example_prompt,
        examples=answer_examples,
    )

    system_prompt = (
        "당신은 반려동물 건강 전문가입니다. 사용자의 반려동물 건강에 관한 질문에 답변해주세요."
        "아래에 제공된 문서를 활용해서 답변해주시고,"
        "답변을 알 수 없다면 모른다고 답변해주세요."
        "답변을 제공할 때는 '출처에 따르면' 이라고 시작하면서 답변해주시고,"
        "4 ~ 5 문장 정도의 내용의 답변을 원합니다."
        "\n\n"
        "{context}"
    )
    
    qa_prompt = ChatPromptTemplate.from_messages(
        [
            ("system", system_prompt),
            few_shot_prompt,
            MessagesPlaceholder("chat_history"),
            ("human", "{input}"),
        ]
    )
    history_aware_retriever = get_history_retriever()
    question_answer_chain = create_stuff_documents_chain(llm, qa_prompt)

    rag_chain = create_retrieval_chain(history_aware_retriever, question_answer_chain)
    
    conversational_rag_chain = RunnableWithMessageHistory(
        rag_chain,
        get_session_history,
        input_messages_key="input",
        history_messages_key="chat_history",
        output_messages_key="answer",
    ).pick('answer')
    
    return conversational_rag_chain