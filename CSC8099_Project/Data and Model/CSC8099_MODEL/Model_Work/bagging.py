import torch
import torch.nn as nn
import numpy as np
import pandas as pd
import spacy


# Define function to preprocess URL text
def preprocess_url(url):
    if pd.isna(url):
        return ''
    return ''.join([char if char.isalnum() else ' ' for char in url])


def preprocess_text(text):
    return text.split()


nlp = spacy.load('en_core_web_md')


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


model1 = CNNModel((1, 1200))
model1.load_state_dict(torch.load('cnn_model42.bin'))
model1.eval()

model2 = CNNModel((1, 1200))
model2.load_state_dict(torch.load('cnn_model43.bin'))
model2.eval()

model3 = CNNModel((1, 1200))
model3.load_state_dict(torch.load('cnn_model44.bin'))
model3.eval()


def bagging_predict(url, hostname, ip_address, path):
    x = process_data(url, hostname, ip_address, path)
    pred = 0
    with torch.no_grad():
        pred += model1(x)
        pred += model2(x)
        pred += model3(x)
    pred /= 3
    return pred[0].item()


if __name__ == '__main__':
    r = bagging_predict('https://www.google.com', 'www.google.com', '172.217.168.206', '/search')
    print(r)
