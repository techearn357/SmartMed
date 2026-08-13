from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import ai_engine

app = FastAPI(
    title="SmartMed AI Personalized Reminder Service",
    description="AI service analyzing medication patterns and suggesting optimal pre-reminder times",
    version="1.0.0"
)

class SuggestionRequest(BaseModel):
    userId: str
    medicineId: Optional[str] = None

class PatternAnalysisRequest(BaseModel):
    userId: str
    medicineId: str
    history: Optional[List[dict]] = None

@app.get("/")
def read_root():
    return {"status": "OK", "service": "SmartMed AI Personalized Reminder Service"}

@app.post("/api/suggestions")
def get_suggestions(request: SuggestionRequest):
    try:
        suggestions = ai_engine.generate_personalized_suggestions(request.userId, request.medicineId)
        return {"success": True, "data": suggestions}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/analyze-pattern")
def analyze_pattern(request: PatternAnalysisRequest):
    try:
        result = ai_engine.analyze_user_routine(request.userId, request.medicineId, request.history or [])
        return {"success": True, "data": result}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
