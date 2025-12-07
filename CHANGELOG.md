# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- CI/CD pipeline for automated JitPack releases
- GitHub Actions workflow for release validation and testing
- JitPack configuration file (`jitpack.yml`)
- Comprehensive release documentation (`.github/RELEASE_GUIDE.md`)
- CI/CD setup summary (`CI_CD_SETUP.md`)
- JitPack and CI/CD badges in README

## [0.1.0] - 2024-XX-XX

### Added
- Initial release of Synheart Emotion Android SDK
- Core emotion inference engine (`EmotionEngine`)
- Feature extraction utilities (`FeatureExtractor`)
- Linear SVM model implementation (`LinearSvmModel`)
- Configuration system (`EmotionConfig`)
- Error handling with sealed error classes (`EmotionError`)
- Result data classes (`EmotionResult`)
- Thread-safe concurrent data processing using `ConcurrentLinkedQueue`
- Sliding window support (60s window, 5s step, configurable)
- Support for three emotion states: Amused, Calm, Stressed
- HRV feature extraction (HR mean, SDNN, RMSSD)
- Input validation for HR and RR intervals
- Buffer statistics and management
- Maven publishing configuration
- ProGuard rules for library consumers
- Comprehensive documentation

### Security
- All processing happens on-device (privacy-first)
- No data transmission to external servers
- **Warning**: Demo placeholder model weights included (not for production use)

### Technical Details
- Minimum Android API: 21 (Android 5.0 Lollipop)
- Kotlin 1.8+
- Coroutine support
- <5ms inference latency

[0.1.0]: https://github.com/synheart-ai/synheart-emotion-android/releases/tag/v0.1.0

