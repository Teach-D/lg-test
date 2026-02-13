# assets

이 디렉토리에 실제 이미지 파일을 추가한 후 아래 커맨드를 실행하세요.

## 필요한 파일

| 파일 | 용도 | 권장 크기 |
|---|---|---|
| `icon.png` | 앱 아이콘 (flutter_launcher_icons) | 1024x1024px |
| `icon_foreground.png` | Android Adaptive Icon 포그라운드 레이어 | 1024x1024px (안전 영역 내 640x640px) |
| `splash.png` | 스플래시 로고 이미지 (flutter_native_splash) | 200x200px 이상 |

## 이미지 교체 후 실행 커맨드

```bash
# 프로젝트 루트(apps/app/)에서 실행

# 1. 패키지 설치
flutter pub get

# 2. 앱 아이콘 생성
flutter pub run flutter_launcher_icons

# 3. 스플래시 화면 생성
flutter pub run flutter_native_splash:create
```

## 색상 기준 (플레이스홀더)

- 스플래시 배경색: `#FFFFFF` (흰색)
- Adaptive Icon 배경색: `#6750A4` (Material Purple)

이미지 없이 색상만으로 동작하므로 현재 상태에서도 커맨드 실행 가능합니다.
단, `icon.png`와 `icon_foreground.png`는 아이콘 생성 시 필수입니다.
스플래시는 `splash_image`가 없으면 단색 배경만 생성됩니다.
