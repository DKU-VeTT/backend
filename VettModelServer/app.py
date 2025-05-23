from fastapi import FastAPI, UploadFile, File, Query
from fastapi.responses import JSONResponse
from torchvision import transforms
from PIL import Image
import torch
import torch.nn as nn
import io
import os
from torchvision.models import efficientnet_b0, EfficientNet_B0_Weights
import pandas as pd
import requests, json
import cv2
import numpy as np
import base64

app = FastAPI()

yolo_model = torch.hub.load('ultralytics/yolov5', 'custom', path='./model_trains/skin_weights.pt', force_reload=True)

@app.post("/py/predict/skin")
async def detect_image(file: UploadFile = File(...)):
    try:
        image_bytes = await file.read()
        pil_img = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        img = np.array(pil_img)

        results = yolo_model(img, size=640)
        detections = []

        for *box, conf, cls in results.xyxy[0].cpu().numpy():
            if conf >= 0.25:
                detections.append({
                    "xmin": float(box[0]),
                    "ymin": float(box[1]),
                    "xmax": float(box[2]),
                    "ymax": float(box[3]),
                    "confidence": float(conf),
                    "class": int(cls),
                    "name": results.names[int(cls)]
                })

        results.render() 
        detections = []

        yolo_class_name_map = {
            0: '미란/궤양',
            1: '결절/종양'
        }

        for *box, conf, cls in results.xyxy[0].cpu().numpy():
            if conf >= 0.25:
                class_id = int(cls)
                detections.append({
                    "xmin": float(box[0]),
                    "ymin": float(box[1]),
                    "xmax": float(box[2]),
                    "ymax": float(box[3]),
                    "confidence": round(float(conf), 4),
                    "class": class_id,
                    "name": yolo_class_name_map.get(class_id, "알 수 없음")
                })

        symptom_label = '유' if detections else '무'

        results.render()
        rendered_img = results.ims[0]
        pil_rendered = Image.fromarray(rendered_img)
        buffer = io.BytesIO()
        pil_rendered.save(buffer, format="JPEG")
        encoded_img_data = base64.b64encode(buffer.getvalue()).decode('utf-8')

        return JSONResponse(content={
            "detections": detections,
            "count": len(detections),
            "symptom": symptom_label,
            "img_base64": encoded_img_data
        })

    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})


eye_class_labels = ['무', '유']     

class ImageClassifier:
    def __init__(self, model_path):
        self.device = 'cpu'
        self.model = efficientnet_b0(weights=EfficientNet_B0_Weights.IMAGENET1K_V1)
        self.model.classifier[1] = nn.Linear(1280, 2)
        self.model.load_state_dict(torch.load(model_path, map_location=self.device))
        self.model.to(self.device)
        self.model.eval()

        self.transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406],
                                 std=[0.229, 0.224, 0.225]),
        ])

    def predict(self, image_bytes):
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        input_tensor = self.transform(image).unsqueeze(0).to(self.device)
        with torch.no_grad():
            outputs = self.model(input_tensor)
            _, predicted = torch.max(outputs, 1)
            label = eye_class_labels[predicted.item()]
            confidence = torch.softmax(outputs, dim=1)[0][predicted.item()].item()
        return label, confidence


eye_model_names = {
    'dog': ['안검내반증', '안검염', '안검종양', '유루증', '핵경화'],
    'cat': ['각막궤양', '각막부골편', '결막염', '비궤양성각막염', '안검염']
}


@app.post("/py/predict/eye")
async def predict_image(
    file: UploadFile = File(...),
    species: str = Query(..., description="동물 종류: 'dog' 또는 'cat'")
):
    try:
        if species not in eye_model_names:
            return JSONResponse(content={"error": f"Invalid species: {species}"}, status_code=400)

        image_bytes = await file.read()
        results = []
        high_data = {"disease": None, "label": None, "confidence": 0.0}

        for disease in model_names[species]:
            model_path = f"./model_result/{species}_{disease}.pth"
            if not os.path.exists(model_path):
                continue

            classifier = ImageClassifier(model_path)
            label, confidence = classifier.predict(image_bytes)

            result = {
                "disease": disease,
                "label": label,
                "confidence": round(confidence, 4)
            }
            results.append(result)

            if label == '유' and confidence > high_data["confidence"]:
                high_data = result

        return JSONResponse(content={
            "all_list": results,
            "high_data": high_data if high_data["disease"] else None
        })

    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)
