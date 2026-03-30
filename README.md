# scrdroid

[ScrcpyForAndroid](https://github.com/zwc456baby/ScrcpyForAndroid) をベースに、個人利用向けの改善を加えたフォークです。

## オリジナルとの主な変更点

- **強制縦画面表示** — リモートデバイスが横向きでも、ローカル側を縦表示のまま維持できるオプションを追加
- **フローティングメニュー** — 接続中画面に⚙ボタンを配置し、縦横切り替え・戻る・ホーム・タスクリストを操作可能に
- **キーイベント修正** — 戻る・ホーム・タスクリストのキー送信が正しく動作するよう修正（元コードのバグ）
- **タッチ座標修正** — 強制縦表示時のタッチ座標マッピングを修正
- **targetSdkVersion 34** — Google Play Protect の警告を回避
- **アプリ名・アイコン変更** — scrdroid として独自のアイコンを設定
- **バージョン表示** — メイン画面ヘッダーにバージョン番号を表示

## 使い方

1. 両方のデバイスを同じローカルネットワークに接続する
2. ミラーリング対象デバイスで **ワイヤレスデバッグ（ADB over Wi-Fi）** を有効にする
3. scrdroid を起動し、対象デバイスの IP アドレスを入力する
4. 解像度・ビットレートを選択して **開始** をタップ
5. 対象デバイスで ADB 接続の許可プロンプトが出たら承認する

### 接続アドレスの例

- `192.168.1.222`
- `host.example.com:5555`
- `[2000:2000:2000:2000::2000]:5555`

## ビルド

```bash
export JAVA_HOME=~/tools/jdk-17.0.12
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=~/Android/Sdk
./gradlew assembleScrcpyDebug
```

APK は `app/build/outputs/apk/scrcpy/debug/` に出力されます。

## 元リポジトリ・参考

- [zwc456baby/ScrcpyForAndroid](https://github.com/zwc456baby/ScrcpyForAndroid) — 本リポジトリのベース
- [Genymobile/scrcpy](https://github.com/Genymobile/scrcpy) — オリジナルの scrcpy（PC 向け）
- [las2mile/scrcpy-android](https://gitlab.com/las2mile/scrcpy-android) — Android 移植の原型
- [lzhiyong/android-sdk-tools](https://github.com/lzhiyong/android-sdk-tools) — Android 向け ADB バイナリ

## ライセンス

元リポジトリのライセンスに準じます。