<template>
  <div class="container">
    <!-- 输入URL的部分 -->
    <div class="input-group">
      <input type="text" v-model="urlInput" placeholder="Enter URL">
      <button @click="submitURLWrapper">Check</button>
    </div>

    <!-- 显示结果的部分 -->
    <div class="result" id="resultDisplay">
      <h3>URL Categorization Result:</h3>
      <div>
        Benign:
        <div class="progress-bar-container">
          <div ref="benignBar" class="progress-bar">{{ benign }}%</div>
        </div>
      </div>
      <div>
        Malicious:
        <div class="progress-bar-container">
          <div ref="maliciousBar" class="progress-bar">{{ malicious }}%</div>
        </div>
      </div>
    </div>

    <!-- error button -->
    <div class="error-report">
      <button @click="showModalWrapper">Report Error</button>
    </div>

    <!-- 弹窗的部分 -->
    <div v-if="showModalFlag" ref="errorModal" class="modal" @click.self="closeModalWrapper">
      <div class="modal-content" ref="modalContent">
        <h3>Report an Error</h3>
        <input type="text" v-model="errorUrl" placeholder="Enter URL">
        <select v-model="errorType">
          <option value="Benign">Benign</option>
          <option value="Malicious">Malicious</option>
        </select>
        <button class="submit" @click="submitErrorWrapper">Submit</button>
        <button class="cancel" @click="closeModalWrapper">Cancel</button>
      </div>
    </div>

    <!-- URL不能为空提示弹窗 -->
    <div v-if="showAlertFlag" class="alert-modal" @click.self="closeAlert">
      <div class="alert-modal-content">
        <h3>Warning</h3>
        <p>URL cannot be empty!</p>
        <button @click="closeAlert">OK</button>
      </div>
    </div>

    <!-- 错误信息提示弹窗 -->
    <div v-if="showErrorFlag" class="alert-modal" @click.self="closeErrorAlert">
      <div class="alert-modal-content">
        <h3>Error</h3>
        <p>{{ errorMessage }}</p>
        <button @click="closeErrorAlert">OK</button>
      </div>
    </div>
  </div>
</template>

<script>
import { nextTick } from 'vue';
import { getGradientColor, updateProgressBar, showModal, closeModal, submitURL, submitError } from '@/assets/interface_js.js';

export default {
  data() {
    return {
      urlInput: '',
      benign: 0,
      malicious: 0,
      showModalFlag: false,
      errorUrl: '',
      errorType: 'Benign',
      showAlertFlag: false,  // 控制提示弹窗的显示
      showErrorFlag: false,  // 控制错误提示弹窗的显示
      errorMessage: ''       // 错误信息
    };
  },
  methods: {
    fetchDataAndUpdateProgressBars(data) {
    // 注意这里改为 data.bengin
    const benignPercentage = Math.round(data.bengin * 100);
    const maliciousPercentage = Math.round(data.malicious * 100);

    this.benign = isNaN(benignPercentage) ? 0 : benignPercentage;
    this.malicious = isNaN(maliciousPercentage) ? 0 : maliciousPercentage;

    const benignBar = this.$refs.benignBar;
    const maliciousBar = this.$refs.maliciousBar;
    
    updateProgressBar(benignBar, this.benign, 'benign');
    updateProgressBar(maliciousBar, this.malicious, 'malicious');
  },
    showModalWrapper() {
      this.showModalFlag = true;
      nextTick(() => {
        const modal = this.$refs.errorModal;
        const modalContent = this.$refs.modalContent;
        console.log("showModalWrapper: ", modal, modalContent);
        showModal(modal, modalContent);
      });
    },
    closeModalWrapper() {
      this.showModalFlag = false;
      nextTick(() => {
        const modal = this.$refs.errorModal;
        console.log("closeModalWrapper: ", modal);
        closeModal(modal);
      });
    },
    submitURLWrapper() {
      if (!this.urlInput) {
        this.showAlertFlag = true;  // 显示提示弹窗
        return;
      }
      submitURL(this.urlInput, this.fetchDataAndUpdateProgressBars, (errorMessage) => {
        this.errorMessage = errorMessage;  // 设置错误信息
        this.showErrorFlag = true;         // 显示错误提示弹窗
      });
    },
    submitErrorWrapper() {
      if (!this.errorUrl) {
        this.showAlertFlag = true;  // 显示提示弹窗
        return;
      }
      submitError(this.errorUrl, this.errorType, () => this.closeModalWrapper(), (errorMessage) => {
        this.errorMessage = errorMessage;  // 设置错误信息
        this.showErrorFlag = true;         // 显示错误提示弹窗
      });
    },
    closeAlert() {
      this.showAlertFlag = false;  // 关闭提示弹窗
    },
    closeErrorAlert() {
      this.showErrorFlag = false;  // 关闭错误提示弹窗
    }
  },
  // mounted() {
  //   window.onclick = (event) => {
  //     const modal = this.$refs.errorModal;
  //     if (event.target == modal) {
  //       this.closeModalWrapper();
  //     }
  //   };
  //   this.fetchDataAndUpdateProgressBars({
  //     benign: 90,
  //     malicious: 10
  //   });
  // }
};
</script>

<style scoped>
@import "@/assets/interface_css.css";

.alert-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000; /* 确保提示弹窗在最上层 */
}

.alert-modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999; /* 确保模态弹窗在提示弹窗的下面 */
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
}
</style>
