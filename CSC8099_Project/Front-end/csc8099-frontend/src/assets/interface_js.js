// 计算并获取渐变色
export function getGradientColor(value, type) {
    let startColor, midColor, endColor;

    if (type === 'benign') {
        startColor = { r: 255, g: 0, b: 0 };   // Red
        midColor = { r: 255, g: 255, b: 0 };   // Yellow
        endColor = { r: 0, g: 128, b: 0 };     // Green
    } else {
        startColor = { r: 0, g: 128, b: 0 };   // Green
        midColor = { r: 255, g: 255, b: 0 };   // Yellow
        endColor = { r: 255, g: 0, b: 0 };     // Red
    }

    let r, g, b;

    if (value <= 50) {
        r = startColor.r + (midColor.r - startColor.r) * (value / 50);
        g = startColor.g + (midColor.g - startColor.g) * (value / 50);
        b = startColor.b + (midColor.b - startColor.b) * (value / 50);
    } else {
        r = midColor.r + (endColor.r - midColor.r) * ((value - 50) / 50);
        g = midColor.g + (endColor.g - midColor.g) * ((value - 50) / 50);
        b = midColor.b + (endColor.b - midColor.b) * ((value - 50) / 50);
    }

    return `rgb(${Math.round(r)}, ${Math.round(g)}, ${Math.round(b)})`;
}

// 更新进度条的宽度和颜色
export function updateProgressBar(bar, value, type) {
    bar.style.width = value + "%";
    bar.innerText = value + "%"; // 在进度条末端显示具体概率值
    bar.style.backgroundColor = getGradientColor(value, type);
}

// 显示弹窗
export function showModal(modal, modalContent) {
    console.log("Show modal: ", modal, modalContent);
    if (modal && modalContent) {
        modal.style.display = "block";
        modalContent.style.backgroundColor = "#fff"; // 白色背景
    }
}

// 关闭弹窗
export function closeModal(modal) {
    console.log("Close modal: ", modal);
    if (modal) {
        modal.style.display = "none";
    }
}

// 提交URL并调用后端检查URL
// 提交URL并调用后端检查URL
export function submitURL(urlInput, fetchDataAndUpdateProgressBars, errorCallback) {
    console.log("Submitting URL:", urlInput);  // 添加日志输出
    fetch('http://localhost:5000/checkurl', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            url: urlInput  // 确保这里的键与后端期望的键匹配
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        fetchDataAndUpdateProgressBars(data);
    })
    .catch((error) => {
        console.error('Error:', error);
        errorCallback(error.message);
    });
}


// 提交错误报告
export function submitError(errorUrl, errorType, successCallback, errorCallback) {
    fetch('http://localhost:5000/addurl', {  // 确保这里的URL是正确的
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            url: errorUrl,
            type: errorType
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.error);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        successCallback(data);
    })
    .catch((error) => {
        console.error('Error:', error);
        errorCallback(error.message);
    });
}
