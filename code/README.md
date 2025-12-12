# 万能语音翻译官 (Android版)

**万能语音翻译官** 是一款基于 Android 平台的实时语音翻译应用。它利用 Microsoft Azure Cognitive Services Speech SDK，实现了智能的双向语音互译功能。应用能够自动识别说话人的语言（中文或外语），并根据上下文进行智能翻译和语音播报，旨在实现“傻瓜式”面对面跨语言交流。

## ✨ 主要功能

*   **多语言支持**: 支持中文 (zh-CN) 与 9 种主流外语的互译：
    *   🇬🇧 英语 (English)
    *   🇯🇵 日语 (Japanese)
    *   🇰🇷 韩语 (Korean)
    *   🇫🇷 法语 (French)
    *   🇪🇸 西班牙语 (Spanish)
    *   🇩🇪 德语 (German)
    *   🇷🇺 俄语 (Russian)
    *   🇮🇹 意大利语 (Italian)
    *   🇵🇹 葡萄牙语 (Portuguese)
*   **智能自动检测**: 用户无需手动切换语言，应用会自动监听并检测说话人的语言。
*   **上下文记忆翻译**:
    *   当检测到**外语**时：自动记录该语种，将其翻译为中文并播报。
    *   当检测到**中文**时：自动翻译为最近一次检测到的外语并播报。
*   **自然语音合成 (TTS)**: 使用 Azure Neural Voices 提供自然流畅的语音播报。
*   **对话流界面**: 像聊天软件一样直观地展示识别到的原文和翻译结果。

## 🛠️ 技术栈

*   **开发语言**: Kotlin
*   **架构模式**: MVVM (Model-View-ViewModel)
*   **核心 SDK**: Microsoft Azure Cognitive Services Speech SDK for Android
*   **最低系统要求**: Android 8.0 (API Level 26)+

## 🚀 快速开始

### 前置条件

1.  你需要一个 Microsoft Azure 账号。
2.  在 Azure 门户中创建一个 **Speech** (语音服务) 资源。
3.  获取资源的 **Key** (密钥) 和 **Region** (区域)。

### 安装与运行

1.  克隆本仓库到本地：
    ```bash
    git clone https://github.com/your-username/azure-translator-android.git
    ```
2.  使用 Android Studio 打开项目根目录。
3.  等待 Gradle 同步完成。
4.  连接 Android 真机或启动模拟器（需支持麦克风输入）。
5.  运行应用 (`Run 'app'`)。
6.  在应用设置界面或首次启动时，输入你的 Azure Speech **Key** 和 **Region**。

## 📖 使用指南

1.  **设置**: 首次使用请点击设置图标，输入有效的 Azure Key 和 Region。
2.  **开始翻译**:
    *   点击或按住底部的“麦克风”按钮开始监听。
    *   **说外语**（如英语 "Hello"）：App 会显示英文原文，翻译成中文 "你好"，并播放中文语音。此时 App 记住了“英语”为目标语言。
    *   **说中文**（如 "你好"）：App 会根据记忆，将中文翻译成英语 "Hello"，并播放英文语音。
    *   **切换语种**: 直接说另一种外语（如日语 "こんにちは"），App 会自动识别并更新目标语言为日语，随后翻译成中文。

## 📄 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。
