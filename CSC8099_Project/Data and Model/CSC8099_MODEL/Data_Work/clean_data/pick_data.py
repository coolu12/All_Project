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
data = pd.read_csv('good_data.csv')


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


def train_model(model, train_loader, epochs, lr):
    criterion = nn.BCELoss()
    optimizer = optim.Adam(model.parameters(), lr=lr)
    for epoch in range(epochs):
        model.train()
        for inputs, targets in train_loader:
            inputs = inputs.cuda()
            targets = targets.cuda()
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, targets)
            loss.backward()
            optimizer.step()


good_idx = []
for train_idx, test_idx in KFold(5, random_state=42, shuffle=True).split(range(len(X))):
    X_train, X_test = X[train_idx], X[test_idx]
    y_train, y_test = y[train_idx], y[test_idx]

    X_train_tensor = torch.tensor(X_train, dtype=torch.float32).unsqueeze(2)
    X_test_tensor = torch.tensor(X_test, dtype=torch.float32).unsqueeze(2)
    y_train_tensor = torch.tensor(y_train, dtype=torch.float32).unsqueeze(1)
    y_test_tensor = torch.tensor(y_test, dtype=torch.float32).unsqueeze(1)

    train_dataset = TensorDataset(X_train_tensor, y_train_tensor)
    test_dataset = TensorDataset(X_test_tensor, y_test_tensor)

    train_loader = DataLoader(train_dataset, batch_size=32, shuffle=True)
    test_loader = DataLoader(test_dataset, batch_size=32, shuffle=False)

    # Train CNN model
    cnn_model = CNNModel(X_train_tensor.shape).cuda()
    train_model(cnn_model, train_loader, epochs=10, lr=0.001)

    cnn_model.eval()
    y_pred_prob = []
    y_true = []
    with torch.no_grad():
        for inputs, targets in test_loader:
            inputs = inputs.cuda()
            targets = targets.cuda()
            outputs = cnn_model(inputs)
            y_pred_prob.extend(outputs.cpu().numpy())
            y_true.extend(targets.cpu().numpy())
    y_pred_prob = np.array(y_pred_prob).flatten()
    y_pred = (y_pred_prob > 0.5).astype(int)
    y_true = np.array(y_true).flatten()
    is_correct = (y_pred == y_true)
    good = list(test_idx[is_correct])
    good_idx.extend(good)

print(len(data), len(good_idx))
data = data.iloc[good_idx]
data.to_csv('good_data2.csv', index=False)
