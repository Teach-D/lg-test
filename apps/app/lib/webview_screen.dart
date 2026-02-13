import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'config.dart';

// WebView 로딩 상태 프로바이더
final _isLoadingProvider = StateProvider<bool>((ref) => true);

// WebView 에러 상태 프로바이더 (null이면 에러 없음)
final _errorProvider = StateProvider<String?>((ref) => null);

/// WebView 메인 화면
class WebViewScreen extends ConsumerStatefulWidget {
  const WebViewScreen({super.key});

  @override
  ConsumerState<WebViewScreen> createState() => _WebViewScreenState();
}

class _WebViewScreenState extends ConsumerState<WebViewScreen> {
  InAppWebViewController? _controller;
  String? _currentUrl;

  static const _settings = InAppWebViewSettings(
    javaScriptEnabled: true,
    domStorageEnabled: true,
    useShouldOverrideUrlLoading: true,
    mediaPlaybackRequiresUserGesture: false,
    allowsBackForwardNavigationGestures: true,
    // http 로컬 개발 서버 허용 (운영 배포 시 제거)
    clearCache: false,
  );

  Future<void> _onPopInvoked(bool didPop) async {
    if (didPop) return;

    final canGoBack = await _controller?.canGoBack() ?? false;
    if (canGoBack) {
      await _controller?.goBack();
      return;
    }

    // 히스토리 없으면 앱 종료
    await SystemNavigator.pop();
  }

  Future<void> _retry() async {
    ref.read(_errorProvider.notifier).state = null;
    ref.read(_isLoadingProvider.notifier).state = true;

    final url = _currentUrl ?? AppConfig.baseUrl;
    await _controller?.loadUrl(
      urlRequest: URLRequest(url: WebUri(url)),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isLoading = ref.watch(_isLoadingProvider);
    final error = ref.watch(_errorProvider);

    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, _) => _onPopInvoked(didPop),
      child: Scaffold(
        body: SafeArea(
          child: Stack(
            children: [
              _buildWebView(),
              if (error != null) _buildErrorView(error),
              if (isLoading && error == null) _buildLoadingIndicator(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildWebView() {
    return InAppWebView(
      initialUrlRequest: URLRequest(
        url: WebUri(AppConfig.baseUrl),
      ),
      initialSettings: _settings,
      onWebViewCreated: (controller) {
        _controller = controller;
      },
      onLoadStart: (controller, url) {
        _currentUrl = url?.toString();
        ref.read(_isLoadingProvider.notifier).state = true;
      },
      onLoadStop: (controller, url) {
        ref.read(_isLoadingProvider.notifier).state = false;
      },
      onReceivedError: (controller, request, error) {
        // 메인 프레임 에러만 처리 (서브리소스 에러 무시)
        if (request.isForMainFrame != true) return;

        ref.read(_isLoadingProvider.notifier).state = false;
        ref.read(_errorProvider.notifier).state = error.description;
      },
      shouldOverrideUrlLoading: (controller, action) async {
        final url = action.request.url?.toString() ?? '';

        // 기본 URL 도메인이면 WebView 내에서 처리
        if (url.startsWith(AppConfig.baseUrl)) {
          return NavigationActionPolicy.ALLOW;
        }

        // 외부 URL은 차단 (필요 시 launchUrl 추가)
        return NavigationActionPolicy.CANCEL;
      },
    );
  }

  Widget _buildErrorView(String errorDescription) {
    return Container(
      color: Theme.of(context).scaffoldBackgroundColor,
      child: Center(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 32),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Icon(
                Icons.wifi_off_rounded,
                size: 72,
                color: Colors.grey,
              ),
              const SizedBox(height: 24),
              const Text(
                '페이지를 불러올 수 없습니다',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                '네트워크 연결을 확인하거나\n잠시 후 다시 시도해 주세요.',
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 14,
                  color: Colors.grey,
                  height: 1.5,
                ),
              ),
              const SizedBox(height: 32),
              ElevatedButton.icon(
                onPressed: _retry,
                icon: const Icon(Icons.refresh_rounded),
                label: const Text('다시 시도'),
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 32,
                    vertical: 12,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildLoadingIndicator() {
    return const Center(
      child: CircularProgressIndicator(),
    );
  }
}
