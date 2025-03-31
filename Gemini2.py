
from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain.chains import RetrievalQA
from langchain.docstore.document import Document
from pydantic import BaseModel
import os
import fitz  # PyMuPDF
from dotenv import load_dotenv
import logging
import firebase_admin
from firebase_admin import credentials, firestore

# Load environment variables
load_dotenv()

app = FastAPI()

# Initialize Firebase
cred_path = r"C:\Users\HP\OneDrive\Desktop\RAGChat\service-account.json (2).json"  # Updated path
if not os.path.exists(cred_path):
    raise FileNotFoundError(f"Firebase credentials file not found at {cred_path}")

cred = credentials.Certificate(cred_path)
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://<your-project-id>.firebaseio.com/'  # Replace with your Firestore database URL
})

# Get Firestore client
db = firestore.client()

# Embedding function
embedding_fn = HuggingFaceEmbeddings(model_name="all-MiniLM-L6-v2")

# Text splitter
text_splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=100)

# Pydantic model for query request
class QueryRequest(BaseModel):
    query: str

def extract_text_from_pdf(file_path):
    """Extracts text from a PDF file using PyMuPDF."""
    try:
        with fitz.open(file_path) as doc:
            text = ""
            for page in doc:
                text += page.get_text("text") + "\n"
        return text.strip()
    except Exception as e:
        raise ValueError(f"Error extracting text from PDF: {str(e)}")

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

@app.post("/upload-pdf")
async def upload_pdf(file: UploadFile = File(...)):
    try:
        logger.debug(f"Received file: {file.filename}")
        file_path = f"./{file.filename}"
        with open(file_path, "wb") as buffer:
            buffer.write(await file.read())

        logger.debug("Extracting text from PDF...")
        text = extract_text_from_pdf(file_path)
        if not text:
            os.remove(file_path)
            raise HTTPException(status_code=400, detail="No extractable text found in PDF. Try OCR.")

        logger.debug("Splitting text into chunks...")
        chunks = text_splitter.split_text(text)

        logger.debug("Storing data in Firestore...")
        # Store metadata in Firestore
        doc_ref = db.collection('pdf_documents').document(file.filename)
        doc_ref.set({
            'filename': file.filename,
            'text': text,
            'chunks': chunks,
            'timestamp': firestore.SERVER_TIMESTAMP
        })

        os.remove(file_path)
        return JSONResponse(content={"message": "PDF uploaded and processed successfully"})

    except Exception as e:
        logger.error(f"Error processing PDF: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.delete("/delete-all-data")
async def delete_all_data():
    try:
        # Delete all documents in the 'pdf_documents' collection
        docs = db.collection('pdf_documents').stream()
        for doc in docs:
            doc.reference.delete()
        
        return JSONResponse(content={"message": "All data deleted successfully"})
    
    except Exception as e:
        logger.error(f"Error deleting data: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

def get_firestore_data():
    """Fetch all documents from Firestore and handle both formats."""
    try:
        docs = db.collection('pdf_documents').stream()
        documents = []
        
        for doc in docs:
            doc_data = doc.to_dict()
            
            # Handle API format
            if 'chunks' in doc_data:
                for chunk in doc_data.get('chunks', []):
                    documents.append(Document(page_content=chunk))
            
            # Handle Android format
            elif 'extracted_text' in doc_data:
                full_text = doc_data['extracted_text']
                # Split the text using the same logic as your text splitter
                chunks = text_splitter.split_text(full_text)
                for chunk in chunks:
                    documents.append(Document(page_content=chunk))
                    
        return documents
        
    except Exception as e:
        raise ValueError(f"Error retrieving documents from Firestore: {str(e)}")
    
from langchain.vectorstores import FAISS
from langchain.chains import RetrievalQA
from langchain.retrievers import ContextualCompressionRetriever
from langchain.retrievers.document_compressors import LLMChainExtractor

@app.post("/chat")
async def chat(request: QueryRequest):
    """Handles chatbot queries using Gemini AI and Firestore."""
    try:
        # Retrieve data from Firestore
        documents = get_firestore_data()
        if not documents:
            return {"error": "No documents found in Firestore"}

        # Load Gemini API key
        gemini_api_key = os.getenv("GEMINI_API_KEY")
        if not gemini_api_key:
            return {"error": "Missing Gemini API Key"}

        # Initialize Gemini AI chat model
        gemini_chat = ChatGoogleGenerativeAI(
            google_api_key=gemini_api_key,
            model="gemini-1.5-pro-latest"
        )

        # Create a vector store from the documents
        vector_store = FAISS.from_documents(documents, embedding_fn)

        # Create a retriever from the vector store
        retriever = vector_store.as_retriever()

        # Create QA Chain
        chain = RetrievalQA.from_chain_type(
            llm=gemini_chat,
            chain_type="stuff",
            retriever=retriever
        )

        # Get response from chatbot
        result = chain({"query": request.query})
        return {"response": result["result"]}

    except Exception as e:
        return {"error": str(e)}
    
    #  uvicorn Gemini:app --reload
