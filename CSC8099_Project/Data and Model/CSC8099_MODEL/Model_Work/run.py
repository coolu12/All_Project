import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, TensorDataset, random_split
import numpy as np
import pandas as pd
import spacy
from sklearn.model_selection import train_test_split, KFold
from sklearn.metrics import accuracy_score, precision_score, recall_score, confusion_matrix, classification_report
from tqdm import tqdm
import time

# Load the data
# data = pd.read_csv('Total_data.csv')
data = pd.read_csv('good_data2.csv')

# Define function to preprocess URL text
def preprocess_url(url):
    if pd.isna(url):
        return ''
    return ''.join([char if char.isalnum() else ' ' for char in url])

def preprocess_text(text):
    return text.split()

# Preprocess URLs and metadata
data['processed_url'] = data['URL'].apply(preprocess_url).apply(preprocess_text)
metadata = data[['Hostname', 'IP Address', 'Last Modified', 'Path']].copy()
metadata['Hostname'] = metadata['Hostname'].apply(preprocess_url).apply(preprocess_text)
metadata['IP Address'] = metadata['IP Address'].apply(preprocess_url).apply(preprocess_text)
metadata['Path'] = metadata['Path'].apply(preprocess_url).apply(preprocess_text)
# Load spaCy model
nlp = spacy.load('en_core_web_md')

# Convert text to vectors using spaCy
def text_to_vector(text):
    doc = nlp(' '.join(text))
    return doc.vector

# Process data in batches to avoid memory issues
def process_in_batches(data_series, batch_size=1000):
    vectors = []
    for i in range(0, len(data_series), batch_size):
        batch = data_series[i:i + batch_size]
        batch_vectors = [text_to_vector(text) for text in batch]
        vectors.extend(batch_vectors)
    return np.array(vectors)

print('Preprocessing data...')
X_url = process_in_batches(data['processed_url'])
X_hostname = process_in_batches(metadata['Hostname'])
X_ip_address = process_in_batches(metadata['IP Address'])
X_path = process_in_batches(metadata['Path'])

X = np.hstack((X_url, X_hostname, X_ip_address, X_path))
y = (data['Type'] == 'malicious').astype(int).values

# Split the data
X_train, X_temp, y_train, y_temp = train_test_split(X, y, test_size=0.4, random_state=42, stratify=y)
X_val, X_test, y_val, y_test = train_test_split(X_temp, y_temp, test_size=0.75, random_state=42, stratify=y_temp)

# Convert data to PyTorch tensors
X_train_tensor = torch.tensor(X_train, dtype=torch.float32).unsqueeze(2)
X_val_tensor = torch.tensor(X_val, dtype=torch.float32).unsqueeze(2)
X_test_tensor = torch.tensor(X_test, dtype=torch.float32).unsqueeze(2)
y_train_tensor = torch.tensor(y_train, dtype=torch.float32).unsqueeze(1)
y_val_tensor = torch.tensor(y_val, dtype=torch.float32).unsqueeze(1)
y_test_tensor = torch.tensor(y_test, dtype=torch.float32).unsqueeze(1)

# Move tensors to GPU if available
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
X_train_tensor = X_train_tensor.to(device)
X_val_tensor = X_val_tensor.to(device)
X_test_tensor = X_test_tensor.to(device)
y_train_tensor = y_train_tensor.to(device)
y_val_tensor = y_val_tensor.to(device)
y_test_tensor = y_test_tensor.to(device)

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
        x  = x.transpose(1, 2)
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
        x = x.transpose(0,1)  # (batch_size, seq_len, input_dim)
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

# Training function
def train_model(model, train_loader, val_loader, epochs, lr):
    criterion = nn.BCELoss()
    optimizer = optim.Adam(model.parameters(), lr=lr)
    for epoch in range(epochs):
        model.train()
        for inputs, targets in train_loader:
            inputs, targets = inputs.to(device), targets.to(device)
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, targets)
            loss.backward()
            optimizer.step()

        model.eval()
        val_loss = 0.0
        with torch.no_grad():
            for inputs, targets in val_loader:
                inputs, targets = inputs.to(device), targets.to(device)
                outputs = model(inputs)
                loss = criterion(outputs, targets)
                val_loss += loss.item()
        print(f'Epoch {epoch + 1}/{epochs}, Validation Loss: {val_loss / len(val_loader)}')

# Create data loaders
train_dataset = TensorDataset(X_train_tensor, y_train_tensor)
val_dataset = TensorDataset(X_val_tensor, y_val_tensor)
test_dataset = TensorDataset(X_test_tensor, y_test_tensor)

train_loader = DataLoader(train_dataset, batch_size=32, shuffle=True)
val_loader = DataLoader(val_dataset, batch_size=32, shuffle=False)
test_loader = DataLoader(test_dataset, batch_size=32, shuffle=False)

# Train Transformer model
transformer_model = TransformerModel(X_train_tensor.shape).to(device)
train_model(transformer_model, train_loader, val_loader, epochs=10, lr=0.001)

# Train CNN model
cnn_model = CNNModel(X_train_tensor.shape).to(device)
train_model(cnn_model, train_loader, val_loader, epochs=10, lr=0.001)

# Evaluate models
def evaluate_model(model, test_loader):
    model.eval()
    y_pred_prob = []
    y_true = []
    with torch.no_grad():
        for inputs, targets in test_loader:
            inputs, targets = inputs.to(device), targets.to(device)
            outputs = model(inputs)
            y_pred_prob.extend(outputs.cpu().numpy())
            y_true.extend(targets.cpu().numpy())
    y_pred_prob = np.array(y_pred_prob).flatten()
    y_pred = (y_pred_prob > 0.5).astype(int)
    y_true = np.array(y_true).flatten()
    accuracy = accuracy_score(y_true, y_pred)
    precision = precision_score(y_true, y_pred)
    recall = recall_score(y_true, y_pred)
    return accuracy, precision, recall

transformer_accuracy, transformer_precision, transformer_recall = evaluate_model(transformer_model, test_loader)
cnn_accuracy, cnn_precision, cnn_recall = evaluate_model(cnn_model, test_loader)

print(f'CNN Model Accuracy: {cnn_accuracy}')
print(f'CNN Model Precision: {cnn_precision}')
print(f'CNN Model Recall: {cnn_recall}')
torch.save(cnn_model.state_dict(), 'cnn_model.bin')
torch.save(transformer_model.state_dict(), 'transformer_model.bin')
print(f'Transformer Model Accuracy: {transformer_accuracy}')
print(f'Transformer Model Precision: {transformer_precision}')
print(f'Transformer Model Recall: {transformer_recall}')