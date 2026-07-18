"""
FastAPI AI 微服务 — 包装现有 RAG + CoT 引擎
"""
import os
import sys

# 将 НИРС 目录加入 Python 路径，复用现有代码
SYSTEM_ROOT = os.path.join(os.path.dirname(__file__), "..", "..", "НИРС")
sys.path.insert(0, SYSTEM_ROOT)

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from rag_engine import RAGEngine
from cot_grader import CoTGrader

app = FastAPI(title="RAG+CoT AI Service")
rag = RAGEngine()
grader = CoTGrader()


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
    results = rag.search(req.query, k=req.k)
    return {"results": results}


@app.post("/ai/generate-question")
def generate_question(req: GenerateRequest):
    result = grader.generate_question(req.rag_context)
    if result is None:
        raise HTTPException(500, "出题失败")
    return result


@app.post("/ai/grade-answer")
def grade_answer(req: GradeRequest):
    result = grader.grade_answer(
        req.rag_context, req.standard_answer_points, req.student_answer
    )
    if result is None:
        raise HTTPException(500, "批改失败")
    return result
