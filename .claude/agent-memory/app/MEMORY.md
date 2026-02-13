# Flutter App 메모리

## 프로젝트 기본 정보
- 패키지명: com.example.pointroulette
- 앱 이름: 포인트 룰렛
- Flutter 3.x / Dart 3.5+
- flutter_inappwebview: ^6.1.5 (webview_flutter 아님)
- flutter_riverpod: ^2.6.1
- Android minSdk: 21, compileSdk: 34, targetSdk: 34
- Kotlin: 1.9.24, Gradle: 8.6, AGP: 8.3.0

## 주요 파일 경로
- apps/app/lib/config.dart — BASE_URL 설정 (AppConfig.baseUrl)
- apps/app/lib/webview_screen.dart — WebView 핵심 로직
- apps/app/lib/main.dart — 앱 진입점 (ProviderScope 래핑)
- apps/app/android/app/src/main/AndroidManifest.xml — INTERNET 권한, usesCleartextTraffic

## WebView 패턴 (flutter_inappwebview 6.x)
- InAppWebView 위젯 + InAppWebViewController 조합 사용
- ConsumerStatefulWidget (Riverpod) 사용
- PopScope(canPop: false) + onPopInvokedWithResult 로 뒤로가기 처리
  - _controller?.canGoBack() → true 면 goBack(), false 면 앱 종료
- 쿠키: InAppWebView CookieManager가 httpOnly 쿠키 자동 관리 (별도 설정 불필요)
- 로딩 인디케이터: onLoadStart/onLoadStop 으로 StateProvider<bool> 토글
- URL 필터링: shouldOverrideUrlLoading 콜백으로 외부 도메인 차단
- initialSettings: InAppWebViewSettings(javaScriptEnabled, domStorageEnabled, useShouldOverrideUrlLoading 등)
- URL 생성: WebUri(url) 사용 (Uri.parse 아님)

## 상태 관리 패턴
- 파일 내부 프로바이더: final _isLoadingProvider = StateProvider<bool>((ref) => true)
- 에러 상태: final _errorProvider = StateProvider<String?>((ref) => null)
- main.dart: ProviderScope로 앱 전체 래핑 필수

## 에러 페이지 패턴
- onReceivedError 콜백에서 request.isForMainFrame == true 일 때만 에러 처리
- 에러 상태(_errorProvider)에 error.description 저장
- 에러 화면은 WebView 위에 Container로 덮어서 표시 (Stack 활용)
- 다시 시도: _errorProvider를 null로 초기화 후 controller.loadUrl() 호출
- _currentUrl 필드로 마지막 요청 URL 추적 (재시도 대상)

## 아이콘 / 스플래시 설정
- flutter_launcher_icons: ^0.14.3 (dev_dependencies)
- flutter_native_splash: ^2.4.4 (dev_dependencies)
- flutter_launcher_icons 설정은 pubspec.yaml 내 주석으로 보관 (이미지 준비 후 주석 해제)
- flutter_native_splash 설정은 apps/app/flutter_native_splash.yaml 별도 파일
- assets/ 디렉토리 등록: pubspec.yaml flutter.assets 에 `- assets/` 추가
- Adaptive Icon 배경색: #6750A4 (Material Purple), 포그라운드: assets/icon_foreground.png
- 스플래시 배경색: #FFFFFF (라이트), #121212 (다크)
- 이미지 없이 색상만으로 스플래시 생성 가능 (아이콘은 image_path 필수)
- 생성 커맨드: flutter pub run flutter_launcher_icons / flutter pub run flutter_native_splash:create

## 주의사항
- WillPopScope는 Flutter 3.12+에서 deprecated → PopScope 사용
- onPopInvokedWithResult 는 Flutter 3.22+에서 사용 (이전은 onPopInvoked)
- usesCleartextTraffic="true" 는 개발용 http URL 허용 목적 (운영에서는 제거 권장)
- settings.gradle.kts 에서 local.properties 로 flutter.sdk 경로를 읽어야 빌드 가능
- flutter_inappwebview는 useShouldOverrideUrlLoading=true 설정 없이는 shouldOverrideUrlLoading 콜백이 호출되지 않음
