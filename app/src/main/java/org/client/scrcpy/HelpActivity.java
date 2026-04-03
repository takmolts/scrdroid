package org.client.scrcpy;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(Html.fromHtml(getHelpHtml(), Html.FROM_HTML_MODE_COMPACT));

        Button backButton = findViewById(R.id.help_back);
        backButton.setOnClickListener(v -> finish());
    }

    private String getHelpHtml() {
        String lang = getResources().getConfiguration().getLocales().get(0).getLanguage();
        if ("ja".equals(lang)) {
            return getHelpJa();
        }
        return getHelpEn();
    }

    private String getHelpJa() {
        return "<h3>接続先の設定</h3>"
                + "<p>リモートデバイスの <b>IP アドレス</b>を入力します。</p>"
                + "<p>・ローカル IP（例: <tt>192.168.1.100</tt>）だけでなく、<b>グローバル IP</b> でも接続できます。</p>"
                + "<p>・ポートを指定する場合は <tt>192.168.1.100:5555</tt> のように入力してください。省略時は 5555 が使われます。</p>"
                + "<p>・IPv6 も利用可能です（例: <tt>[2001:db8::1]:5555</tt>）</p>"

                + "<h3>解像度・ビットレート</h3>"
                + "<p>通信速度が遅い環境では、<b>解像度を下げる</b>・<b>ビットレートを下げる</b>ことで映像が安定します。</p>"
                + "<p>Wi-Fi 環境では 1280x720 / 4Mbps 程度がバランスが良く、モバイル回線では 640x360 / 1Mbps を推奨します。</p>"

                + "<h3>遅延コントロール</h3>"
                + "<p>フレームの遅延許容値を設定します。低くすると古いフレームが破棄され、高くすると安定しますが遅延が増えます。</p>"

                + "<h3>自動切断</h3>"
                + "<p>設定した時間が経過すると<b>自動的に切断</b>してメイン画面に戻ります。</p>"
                + "<p>バッテリーの消耗を防ぎたいときや、つけっぱなし防止に便利です。</p>"

                + "<h3>強制縦画面表示</h3>"
                + "<p>リモートデバイスが横画面でも、<b>縦表示のまま</b>レターボックス（上下黒帯）で表示します。</p>"
                + "<p>縦持ちのまま横画面のアプリを確認したいときに便利です。</p>"

                + "<h3>ビューイングモード</h3>"
                + "<p>ON にすると、タッチ操作がリモートデバイスに<b>送信されません</b>（閲覧専用）。</p>"
                + "<p>ビューイングモード中は以下の操作ができます：</p>"
                + "<p>・<b>ピンチイン/アウト</b>で画面を拡大（1.0x〜5.0x）</p>"
                + "<p>・拡大中は<b>ドラッグ</b>でパン（スクロール）</p>"
                + "<p>・<b>ダブルタップ</b>で等倍にリセット</p>"

                + "<h3>接続中の操作（⚙ フローティングメニュー）</h3>"
                + "<p>接続中は画面右下の <b>⚙ ボタン</b>をタップするとメニューが開きます。<br>"
                + "⚙ ボタンはドラッグで好きな位置に移動できます。</p>"
                + "<p>メニューの内容：</p>"
                + "<p>・<b>📴 画面OFF</b> — リモートの画面を ON/OFF トグル</p>"
                + "<p>・<b>🔊 音声ON / 🔇 ミュート</b> — 音声の ON/OFF 切替</p>"
                + "<p>・<b>◁ Back</b> — 戻るキー送信</p>"
                + "<p>・<b>○ Home</b> — ホームキー送信</p>"
                + "<p>・<b>□ Tasks</b> — タスクリスト表示</p>"
                + "<p>・<b>✖ 切断</b> — 接続を終了してメイン画面に戻る</p>";
    }

    private String getHelpEn() {
        return "<h3>Connection Settings</h3>"
                + "<p>Enter the <b>IP address</b> of the remote device.</p>"
                + "<p>• Both local IPs (e.g. <tt>192.168.1.100</tt>) and <b>global IPs</b> are supported.</p>"
                + "<p>• To specify a port: <tt>192.168.1.100:5555</tt> (default is 5555).</p>"
                + "<p>• IPv6 is also supported (e.g. <tt>[2001:db8::1]:5555</tt>)</p>"

                + "<h3>Resolution / Bitrate</h3>"
                + "<p>On slow connections, <b>lowering the resolution and bitrate</b> will stabilize the video stream.</p>"
                + "<p>Recommended: 1280x720 / 4Mbps on Wi-Fi, 640x360 / 1Mbps on mobile.</p>"

                + "<h3>Delay Control</h3>"
                + "<p>Sets the frame delay tolerance. Lower values discard old frames for less lag; higher values are more stable but increase delay.</p>"

                + "<h3>Auto Disconnect</h3>"
                + "<p>Automatically <b>disconnects</b> after the specified time and returns to the main screen.</p>"
                + "<p>Useful for preventing battery drain or unattended sessions.</p>"

                + "<h3>Force Portrait</h3>"
                + "<p>Displays the remote screen in <b>portrait mode</b> with letterboxing, even if the remote device is in landscape.</p>"
                + "<p>Useful for checking landscape apps while holding your phone vertically.</p>"

                + "<h3>Viewing Mode</h3>"
                + "<p>When enabled, touch input is <b>not sent</b> to the remote device (view-only).</p>"
                + "<p>In viewing mode you can:</p>"
                + "<p>• <b>Pinch in/out</b> to zoom (1.0x–5.0x)</p>"
                + "<p>• <b>Drag</b> to pan while zoomed in</p>"
                + "<p>• <b>Double-tap</b> to reset to 1x</p>"

                + "<h3>Controls During Connection (⚙ Floating Menu)</h3>"
                + "<p>Tap the <b>⚙ button</b> at the bottom-right to open the menu.<br>"
                + "The ⚙ button can be dragged to any position.</p>"
                + "<p>Menu items:</p>"
                + "<p>• <b>📴 Screen OFF</b> — Toggle remote screen ON/OFF</p>"
                + "<p>• <b>🔊 Sound / 🔇 Mute</b> — Toggle audio</p>"
                + "<p>• <b>◁ Back</b> — Send back key</p>"
                + "<p>• <b>○ Home</b> — Send home key</p>"
                + "<p>• <b>□ Tasks</b> — Show task switcher</p>"
                + "<p>• <b>✖ Disconnect</b> — End connection and return to main screen</p>";
    }
}
