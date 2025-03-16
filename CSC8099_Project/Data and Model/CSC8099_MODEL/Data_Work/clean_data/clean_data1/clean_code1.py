import pandas as pd
import socket
from urllib.parse import urlparse
import requests
import validators
from concurrent.futures import ThreadPoolExecutor
import os
#data source: Benign and Malicious URLs (link: https://www.kaggle.com/datasets/samahsadiq/benign-and-malicious-urls)
def parse_url(url):
    try:
        parsed_url = urlparse(url)
        return parsed_url.netloc, parsed_url.path
    except Exception as e:
        print(f"Error parsing URL {url}: {e}")
        return None, None

def is_valid_url(url):
    return validators.url(url)

def get_server_ip(hostname):
    try:
        ip_address = socket.gethostbyname(hostname)
        if ip_address == "0.0.0.0":
            return None
        return ip_address
    except socket.gaierror:
        return None
    except Exception as e:
        print(f"Error getting IP for hostname {hostname}: {e}")
        return None

def get_last_modified(url):
    if not is_valid_url(url):
        print(f"Skipping invalid URL: {url}")
        return None
    try:
        response = requests.head(url, allow_redirects=True, timeout=10)
        return response.headers.get('Last-Modified', None)
    except requests.RequestException as e:
        print(f"Error getting last modified for URL {url}: {e}")
        return None

def list_run(new_data):
    parsed_data = {}
    url = new_data['url']
    url_type = new_data['url_type']
    hostname, path = parse_url(url)
    if not hostname:
        print(f"Skipping URL {url} due to invalid hostname")
        return
    ip_address = get_server_ip(hostname)
    last_modified = get_last_modified(url)

    if ip_address and last_modified:
        parsed_data["URL"] = url
        parsed_data["Type"] = url_type
        parsed_data["Hostname"] = hostname
        parsed_data["IP Address"] = ip_address
        parsed_data["Last Modified"] = last_modified
        parsed_data["Path"] = path
        df = pd.DataFrame(parsed_data, index=[0])


        df.to_csv('data1_clean.csv', mode='a', header=False, index=False)

def main():
    file_path = r'F:\data\Graduate_Design\project_8099\clean_data\clean_data1\data1.csv'

    # check if urls exists
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        return

    # read csv

    try:
        data = pd.read_csv(file_path, encoding='utf-8')
    except UnicodeDecodeError:
        try:
            data = pd.read_csv(file_path, encoding='ISO-8859-1')
        except UnicodeDecodeError:
            data = pd.read_csv(file_path, encoding='latin1')


    output_file = 'data1_clean.csv'
    if not data.empty:
        df_header = pd.DataFrame(columns=["URL", "Type", "Hostname", "IP Address", "Last Modified", "Path"])
        df_header.to_csv(output_file, mode='w', header=True, index=False)

    new_data = []
    for index, row in data.iterrows():
        row_ = {}
        url = row['url']
        url_type = row['type']


        if pd.isna(url_type) or not isinstance(url_type, str):
            print(f"Skipping URL {url} due to invalid or overly long label")
            continue

        if len(url_type) > 100:
            print(f"Skipping URL {url} due to overly long label")
            continue


        if not is_valid_url(url) or len(url) > 300:
            print(f"Skipping invalid URL format or overly long URL: {url}")
            continue
        row_['url'] = url
        row_['url_type'] = url_type
        new_data.append(row_)
    print(len(new_data))
    with ThreadPoolExecutor(max_workers=8) as t:
        t.map(list_run, new_data)

if __name__ == '__main__':
    main()
