"""
FastAPI AI 微服务 — 包装现有 RAG + CoT 引擎
"""
import os
import sys

# 将 НИРС 目录加入 Python 路径，复用现有代码
SYSTEM_ROOT = os.path.join(os.path.dirname(__file__), "..", "..", "НИРС")
sys.path.insert(0, SYSTEM_ROOT)

# 强制加载 НИРС/.env 中的环境变量（含 Embedding API Key）
from dotenv import load_dotenv
load_dotenv(os.path.join(SYSTEM_ROOT, ".env"))

import uvicorn
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from rag_engine import RAGEngine
from cot_grader import CoTGrader

app = FastAPI(title="RAG+CoT AI Service")

# 延迟加载，启动快
_rag = None
_grader = None

def get_rag():
    global _rag
    if _rag is None:
        _rag = RAGEngine()
    return _rag

def get_grader():
    global _grader
    if _grader is None:
        _grader = CoTGrader()
    return _grader


class SearchRequest(BaseModel):
    query: str
    k: int = 5


class GenerateRequest(BaseModel):
    rag_context: str


class GradeRequest(BaseModel):
    rag_context: str
    standard_answer_points: str
    student_answer: str


@app.post("/ai/search")
def search(req: SearchRequest):
    results = get_rag().search(req.query, k=req.k)
    return {"results": results}


@app.post("/ai/generate-question")
def generate_question(req: GenerateRequest):
    result = get_grader().generate_question(req.rag_context)
    if result is None:
        raise HTTPException(500, "出题失败")
    return result


@app.post("/ai/grade-answer")
def grade_answer(req: GradeRequest):
    result = get_grader().grade_answer(
        req.rag_context, req.standard_answer_points, req.student_answer
    )
    if result is None:
        raise HTTPException(500, "批改失败")
    return result


import base64
import tempfile


class LoadPdfRequest(BaseModel):
    filename: str
    data_b64: str


@app.post("/ai/load-pdf")
def load_pdf(req: LoadPdfRequest):
    # Decode base64 + save to temp file, then load into ChromaDB
    pdf_bytes = base64.b64decode(req.data_b64)
    with tempfile.NamedTemporaryFile(delete=False, suffix=".pdf") as tmp:
        tmp.write(pdf_bytes)
        tmp_path = tmp.name
    try:
        count = get_rag().load_pdf(tmp_path)
        return {"chunks": count}
    finally:
        os.unlink(tmp_path)


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
