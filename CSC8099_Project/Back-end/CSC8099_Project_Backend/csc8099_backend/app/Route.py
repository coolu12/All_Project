import os
import socket
from urllib.parse import urlparse

from flask import Flask, request, jsonify
import pymysql
from flask_cors import CORS
import torch
import torch.nn as nn
import numpy as np
import pandas as pd
import spacy

app = Flask(__name__)
CORS(app)  # 允许所有来源的跨域请求

@app.route('/addurl', methods=['POST'])
def addurl():
    url = request.json.get("url")
    type = request.json.get("type")
    if not url:
        return jsonify({"error": "url is empty"}), 400
    if not type:
        return jsonify({"error": "type is empty"}), 400

    try:
        conn = pymysql.connect(
            host='localhost',
            port=3306,
            user='root',
            passwd='1234',
            db='malicious_url_db',
            charset='utf8',
            client_flag=pymysql.constants.CLIENT.MULTI_STATEMENTS,
        )
        cursor = conn.cursor()
        cursor.execute("INSERT INTO reported_urls (url, type) VALUES (%s, %s)", (url, type))
        conn.commit()
    except pymysql.MySQLError as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if 'cursor' in locals() and cursor:
            cursor.close()
        if 'conn' in locals() and conn:
            conn.close()

    return jsonify({"status": "Success"})



#
# Load spaCy model
nlp = spacy.load('en_core_web_md')

# Define function to preprocess URL text
def preprocess_url(url):
    if pd.isna(url):
        return ''
    return ''.join([char if char.isalnum() else ' ' for char in url])

def preprocess_text(text):
    return text.split()

# Convert text to vectors using spaCy
def text_to_vector(text):
    doc = nlp(' '.join(text))
    return doc.vector

def process_data(url, hostname, ip_address, path):
    url = preprocess_text(preprocess_url(url))
    hostname = preprocess_text(preprocess_url(hostname))
    ip_address = preprocess_text(preprocess_url(ip_address))
    path = preprocess_text(preprocess_url(path))
    x_url = text_to_vector(url)
    x_host = text_to_vector(hostname)
    x_ip = text_to_vector(ip_address)
    x_path = text_to_vector(path)
    x = np.concatenate([x_url, x_host, x_ip, x_path])[None]
    return torch.tensor(x, dtype=torch.float32).unsqueeze(2)

# Move tensors to GPU if available
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# Define CNN model
class CNNModel(nn.Module):
    def __init__(self, input_shape):
        super(CNNModel, self).__init__()
        self.conv1 = nn.Conv1d(1, 64, kernel_size=3, padding=1)
        self.pool1 = nn.MaxPool1d(kernel_size=2)
        self.dropout1 = nn.Dropout(0.5)
        self.conv2 = nn.Conv1d(64, 128, kernel_size=3, padding=1)
        self.pool2 = nn.MaxPool1d(kernel_size=2)
        self.dropout2 = nn.Dropout(0.5)
        self.conv3 = nn.Conv1d(128, 256, kernel_size=3, padding=1)
        self.pool3 = nn.MaxPool1d(kernel_size=2)
        self.dropout3 = nn.Dropout(0.5)
        self.flatten = nn.Flatten()
        self.fc1 = nn.Linear(256 * (input_shape[1] // 8), 512)
        self.dropout4 = nn.Dropout(0.5)
        self.fc2 = nn.Linear(512, 256)
        self.dropout5 = nn.Dropout(0.5)
        self.output = nn.Linear(256, 1)
        self.sigmoid = nn.Sigmoid()

    def forward(self, x):
        x = x.transpose(1, 2)
        x = self.conv1(x)
        x = nn.ReLU()(x)
        x = self.pool1(x)
        x = self.dropout1(x)
        x = self.conv2(x)
        x = nn.ReLU()(x)
        x = self.pool2(x)
        x = self.dropout2(x)
        x = self.conv3(x)
        x = nn.ReLU()(x)
        x = self.pool3(x)
        x = self.dropout3(x)
        x = self.flatten(x)
        x = self.fc1(x)
        x = nn.ReLU()(x)
        x = self.dropout4(x)
        x = self.fc2(x)
        x = nn.ReLU()(x)
        x = self.dropout5(x)
        x = self.output(x)
        x = self.sigmoid(x)
        return x

# Define Transformer model
class TransformerModel(nn.Module):
    def __init__(self, input_shape):
        super(TransformerModel, self).__init__()
        self.multihead_attn1 = nn.MultiheadAttention(embed_dim=input_shape[1], num_heads=4)
        self.layer_norm1 = nn.LayerNorm(input_shape[1])
        self.dropout1 = nn.Dropout(0.5)
        self.multihead_attn2 = nn.MultiheadAttention(embed_dim=input_shape[1], num_heads=4)
        self.layer_norm2 = nn.LayerNorm(input_shape[1])
        self.dropout2 = nn.Dropout(0.5)
        self.flatten = nn.Flatten()
        self.fc1 = nn.Linear(input_shape[1] * input_shape[2], 512)
        self.dropout3 = nn.Dropout(0.5)
        self.fc2 = nn.Linear(512, 256)
        self.dropout4 = nn.Dropout(0.5)
        self.output = nn.Linear(256, 1)
        self.sigmoid = nn.Sigmoid()

    def forward(self, x):
        x = x.permute(2, 0, 1)  # (seq_len, batch_size, input_dim)
        attn_output, _ = self.multihead_attn1(x, x, x)
        x = self.layer_norm1(x + attn_output)
        x = self.dropout1(x)
        attn_output, _ = self.multihead_attn2(x, x, x)
        x = self.layer_norm2(x + attn_output)
        x = self.dropout2(x)
        x = x.transpose(0, 1)  # (batch_size, seq_len, input_dim)
        x = self.flatten(x)
        x = self.fc1(x)
        x = nn.ReLU()(x)
        x = self.dropout3(x)
        x = self.fc2(x)
        x = nn.ReLU()(x)
        x = self.dropout4(x)
        x = self.output(x)
        x = self.sigmoid(x)
        return x

# 获取当前文件所在目录的绝对路径
current_dir = os.path.dirname(os.path.abspath(__file__))

# 拼接模型文件的绝对路径
model_path1 = os.path.join(current_dir, 'cnn_model42.bin')
model_path2 = os.path.join(current_dir, 'cnn_model43.bin')
model_path3 = os.path.join(current_dir, 'cnn_model44.bin')
transformer_model_path = os.path.join(current_dir, 'transformer_model.bin')

# 确认模型路径
print(f"Loading models from: {model_path1}, {model_path2}, {model_path3},{transformer_model_path}")

# 获取输入形状
input_shape = (1, 1200, 1)
print(f"Input shape: {input_shape}")

# Load models
model1 = CNNModel(input_shape).to(device)
model1.load_state_dict(torch.load(model_path1, map_location=device))
model1.eval()

model2 = CNNModel(input_shape).to(device)
model2.load_state_dict(torch.load(model_path2, map_location=device))
model2.eval()

model3 = CNNModel(input_shape).to(device)
model3.load_state_dict(torch.load(model_path3, map_location=device))
model3.eval()


transformer_model = TransformerModel(input_shape).to(device)
transformer_model.load_state_dict(torch.load(transformer_model_path, map_location=device))
transformer_model.eval()

def bagging_predict(url, hostname, ip_address, path):
    x = process_data(url, hostname, ip_address, path).to(device)
    pred = 0
    with torch.no_grad():
        pred += model1(x)
        pred += model2(x)
        pred += model3(x)

    pred /= 4  # 4 个 CNN 模型的平均值
    return pred[0].item()

def transformer_predict(url, hostname, ip_address, path):
    x = process_data(url, hostname, ip_address, path).to(device)
    with torch.no_grad():
        pred = transformer_model(x)
    return pred[0].item()

def parse_url(full_url):
    parsed_url = urlparse(full_url)
    hostname = parsed_url.hostname
    path = parsed_url.path
    try:
        ip_address = socket.gethostbyname(hostname)
    except socket.gaierror:
        ip_address = '0.0.0.0'
    return full_url, hostname, ip_address, path

@app.route('/checkurl', methods=['POST'])
def checkurl():
    url = request.json.get("url")
    if url:
        full_url, hostname, ip_address, path = parse_url(url)
        bagging_prediction = bagging_predict(full_url, hostname, ip_address, path)
        transformer_prediction = 1-transformer_predict(full_url, hostname, ip_address, path)
        combined_prediction = 0.5 * bagging_prediction + 0.5 * transformer_prediction
        malicious=1-combined_prediction
        return jsonify({
            'bengin': combined_prediction,
            'malicious': malicious
        })
    else:
        return jsonify({'error': 'URL not provided'}), 400
if __name__ == '__main__':
    app.run(debug=True)
