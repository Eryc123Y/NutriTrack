# NutriTrack - Comprehensive Nutrition Tracking App

[![Android](https://img.shields.io/badge/Android-API%2031%2B-green.svg)](https://android-arsenal.com/api?level=31)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02.00-orange.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> 📱 **NutriTrack** is a sophisticated Android nutrition tracking application built with modern Android development tools and libraries. The app provides personalized nutrition coaching through AI, comprehensive user insights, and specialized tools for healthcare professionals.

## 📋 Table of Contents

- [🌟 Features](#-features)
- [🏗️ Architecture](#-architecture)
- [📚 Documentation](#-documentation)
- [🛠️ Technology Stack](#-technology-stack)
- [🚀 Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [📱 Usage](#-usage)
  - [For End Users](#for-end-users)
  - [For Healthcare Professionals](#for-healthcare-professionals)
- [🧪 Testing](#-testing)
- [📈 Performance](#-performance)
- [🔒 Security](#-security)
- [🌐 Accessibility](#-accessibility)
- [🚨 Troubleshooting](#-troubleshooting)
- [⚠️ Known Limitations](#-known-limitations)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [👥 Team](#-team)
- [📞 Support](#-support)
- [🔄 Version History](#-version-history)
- [🚀 Future Enhancements](#-future-enhancements)

---

## 🌟 Features

### 🔐 User Management
- **User Authentication & Registration**: Secure user management with BCrypt password hashing
- **Personalized Questionnaires**: Multi-step onboarding to capture user preferences and personas
- **Profile Management**: Customizable user profiles with dietary preferences

### 🤖 AI-Powered Features
- **AI Nutrition Coach**: Real-time chat with Gemini AI for personalized advice
- **Smart Recommendations**: AI-powered meal suggestions based on user goals
- **Progress Tracking**: Intelligent insights into nutrition patterns

### 📊 Analytics & Tracking
- **Score Tracking & Insights**: Comprehensive nutrition scoring with historical data
- **Visual Analytics**: Charts and graphs for progress visualization
- **Goal Setting**: Customizable nutrition and health goals

### 👨‍⚕️ Professional Tools
- **Clinician Dashboard**: Professional tools for healthcare providers
- **Patient Management**: Monitor multiple patients with detailed analytics
- **Export Functionality**: Generate reports for further analysis

### 🎨 User Experience
- **Dark/Light Theme**: Modern Material Design 3 with automatic theme switching
- **Offline-First**: Full functionality without internet connection
- **Responsive Design**: Optimized for various screen sizes and orientations

## 🏗️ Architecture

The application follows Clean Architecture principles with:

```
├── Presentation Layer (View)
│   ├── Composable UI Components
│   ├── Screen Composables
│   └── Navigation Setup
├── Business Logic Layer (ViewModel)
│   ├── State Management
│   ├── Business Rules
│   └── User Interactions
└── Data Layer (Model)
    ├── Room Database
    ├── Repository Pattern
    ├── Network APIs
    └── Data Sources
```

## 📚 Documentation

This project includes comprehensive documentation covering all aspects of the application:

### 🚀 [Complete API Documentation](./API_DOCUMENTATION.md)
The main documentation file covering:
- Application overview and architecture
- Getting started guide
- Navigation system
- Data layer (entities, DAOs, repositories)
- ViewModels and state management
- UI components and screens
- Utilities and helper classes
- API integration
- Theme and styling
- Usage examples with code samples

### 🗄️ [Database Documentation](./DATABASE_DOCUMENTATION.md)
Detailed database documentation including:
- Database schema and entity relationships
- Room configuration and migrations
- DAO interfaces with all available methods
- Repository pattern implementation
- Type converters and database initialization
- Performance optimization strategies
- Usage examples for database operations

### 🎨 [UI Components Documentation](./UI_COMPONENTS_DOCUMENTATION.md)
Comprehensive UI components guide covering:
- Core reusable components (ScoreProgressBar, TimePickerComponent, etc.)
- Navigation components (BottomNavigationBar, TopAppBar)
- Authentication components (AuthenticationButton, UserIdDropdown)
- Form components with validation
- Chart components for data visualization
- Animation components and page transitions
- Best practices for component composition
- Accessibility and performance guidelines

### 🏛️ [ViewModels Documentation](./VIEWMODEL_DOCUMENTATION.md)
Complete ViewModels and state management guide including:
- MVVM architecture implementation
- State management patterns with UiState
- Dependency injection with ViewModelProviderFactory
- Detailed documentation for all ViewModels:
  - AuthViewModel (authentication and registration)
  - GenAIViewModel (AI chat functionality)
  - QuestionnaireViewModel (multi-step questionnaire)
  - ClinicianDashboardViewModel (healthcare professional tools)
  - UserStatsViewModel (statistics and scoring)
- Testing strategies for ViewModels
- Performance optimization techniques

## 🛠️ Technology Stack

### Core Technologies
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room with SQLite
- **Navigation**: Navigation Compose
- **Async Programming**: Coroutines and Flow
- **Minimum SDK**: 31 (Android 12)
- **Target SDK**: 35 (Android 14)

### Key Libraries
- **AI Integration**: Google Generative AI (Gemini)
- **Image Loading**: Coil for Compose
- **Networking**: Retrofit with Gson converter
- **Security**: BCrypt for password hashing
- **Splash Screen**: AndroidX Core SplashScreen
- **Dependency Injection**: Manual DI with Factory pattern

### Development Tools
- **Build System**: Gradle with Kotlin DSL
- **Code Generation**: KSP (Kotlin Symbol Processing)
- **Database Schema**: Room with auto-generated DAOs

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK API 31 or higher
- Kotlin 2.0.21 or newer
- Google Gemini API key

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/nutritrack.git
   cd nutritrack
   ```

2. **Set up API keys**
   Create a `secrets.properties` file in the root directory:
   ```properties
   API_KEY=your_gemini_api_key_here
   MAPS_API_KEY=your_google_maps_api_key_here
   ```
   
   > **Note**: The MAPS_API_KEY is required for potential future location-based features.

3. **Build and run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

### Configuration

The app requires minimal configuration:
- **API Keys**: Add your Gemini AI API key to `secrets.properties`
- **Database**: Automatically initialized on first run with sample data
- **Permissions**: Internet permission for AI functionality

## 📱 Usage

### For End Users
1. **Registration**: Create an account or use existing user data
2. **Questionnaire**: Complete the onboarding questionnaire to set preferences
3. **Home Dashboard**: View nutrition scores and personalized insights
4. **AI Coach**: Chat with the nutrition coach for personalized advice
5. **Insights**: Track progress over time with detailed analytics

### For Healthcare Professionals
1. **Clinician Dashboard**: Access specialized dashboard for patient overview
2. **Patient Management**: View and monitor multiple patients
3. **AI Recommendations**: Generate professional nutrition recommendations
4. **Data Export**: Export patient data for further analysis

## 🧪 Testing

The project includes comprehensive testing strategies:

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### UI Tests
```bash
./gradlew connectedCheck
```

## 📈 Performance

The app is optimized for performance with:
- **Lazy Loading**: Efficient list rendering with LazyColumn/LazyRow
- **Database Indexing**: Optimized queries with proper indexes
- **Memory Management**: Proper lifecycle-aware components
- **Image Optimization**: Efficient image loading with Coil
- **Animation Performance**: Hardware-accelerated animations

## 🔒 Security

Security measures implemented:
- **Password Hashing**: BCrypt for secure password storage
- **API Key Management**: Secure API key handling with build variants
- **Data Validation**: Input validation at all layers
- **SQL Injection Prevention**: Parameterized queries with Room

## 🌐 Accessibility

The app follows Android accessibility guidelines:
- **Content Descriptions**: Proper content descriptions for screen readers
- **Color Contrast**: WCAG-compliant color contrast ratios
- **Touch Targets**: Minimum 48dp touch target sizes
- **Keyboard Navigation**: Full keyboard navigation support

## 🚨 Troubleshooting

### Common Issues and Solutions

#### Build Issues
**Problem**: Build fails with missing API key error
```bash
> Task :app:compileDebugKotlin FAILED
```
**Solution**: 
- Ensure `secrets.properties` file exists in project root
- Verify API_KEY is properly set
- Check for typos in property names

**Problem**: KSP processing fails
```bash
Could not resolve com.google.devtools.ksp
```
**Solution**:
- Update KSP version in `build.gradle.kts`
- Clean and rebuild project: `./gradlew clean build`

#### Runtime Issues
**Problem**: App crashes on startup
**Solution**:
- Check if all required permissions are granted
- Verify database initialization
- Check logcat for specific error messages

**Problem**: AI chat not working
**Solution**:
- Verify internet connection
- Check Gemini API key validity
- Ensure API key has proper quotas

#### Database Issues
**Problem**: Data not persisting
**Solution**:
- Check Room database version migrations
- Verify entity annotations
- Check database operations on main thread

### Getting Help
1. Check [GitHub Issues](https://github.com/Eryc123Y/NutriTrack/issues)
2. Review the [documentation files](#-documentation)
3. Enable debug logging in `local.properties`:
   ```properties
   DEBUG_MODE=true
   ```

## ⚠️ Known Limitations

### Current Limitations
- **Offline AI**: AI features require internet connection
- **Single Language**: Currently supports English only
- **Device Compatibility**: Requires Android 12+ (API 31)
- **Storage**: Local database only (no cloud sync yet)
- **User Accounts**: No account recovery mechanism

### Performance Considerations
- Large datasets may impact scrolling performance
- Image loading depends on network speed
- Database queries should be optimized for large user bases

### Security Notes
- API keys stored in local.properties (not committed to repo)
- Passwords hashed with BCrypt (secure storage)
- No end-to-end encryption for chat messages

## 🤝 Contributing

We welcome contributions! Please follow these steps:

### 📝 Contribution Guidelines

1. **Fork the Repository**
   ```bash
   git clone https://github.com/Eryc123Y/NutriTrack.git
   cd NutriTrack
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **Make Your Changes**
   - Follow Kotlin coding conventions
   - Add KDoc comments for public APIs
   - Include unit tests for new features
   - Update documentation as needed

4. **Commit Your Changes**
   ```bash
   git commit -m 'feat: add amazing feature'
   ```

5. **Push and Open a Pull Request**
   ```bash
   git push origin feature/amazing-feature
   ```

### 🎯 Code Style Guidelines
- **Kotlin Style**: Follow official Kotlin conventions
- **Naming**: Use clear, descriptive names
- **Documentation**: KDoc for all public APIs
- **Testing**: Maintain 80%+ test coverage
- **Commits**: Use [Conventional Commits](https://www.conventionalcommits.org/)

### 🐛 Reporting Issues
When reporting bugs, please include:
- Android device and OS version
- App version (v1.0.0)
- Steps to reproduce
- Expected vs actual behavior
- Logcat output if applicable

---

## 🏆 Acknowledgments

- **FIT2081** - Monash University for the opportunity
- **Google** - For Gemini AI and Android development tools
- **JetBrains** - For the amazing Kotlin language
- **Open Source Community** - For the libraries and tools used

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Developer**: Xingyu Yang
- **Student ID**: 33533563
- **Course**: FIT2081 Assignment 1

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the documentation files for detailed information
- Review the code examples in the documentation

## 🔄 Version History

### Version 1.0.0 (Current)
- Initial release with core functionality
- User authentication and registration
- AI-powered nutrition coaching
- Comprehensive questionnaire system
- Clinician dashboard
- Score tracking and insights

## 🚀 Future Enhancements

### 🎯 High Priority
- **🔔 Push Notifications**: Meal reminders and achievement notifications
- **📊 Advanced Analytics**: Detailed reports and nutrition insights
- **☁️ Cloud Sync**: Secure data synchronization across devices

### 🎯 Medium Priority
- **🏃 Fitness Integration**: Connect with fitness trackers and health apps
- **🌍 Multi-language Support**: Expand to Spanish, French, German
- **📸 Food Recognition**: AI-powered food logging from photos
- **💬 Social Features**: Community challenges and sharing

### 🎯 Low Priority
- **⚡ Offline AI**: Downloadable AI models for offline advice
- **🎨 Custom Themes**: More personalization options
- **📺 Wear OS Support**: Companion app for smartwatches
- **🔧 Export/Import**: Data portability features

### 🗓️ Roadmap Timeline
- **Q1 2025**: Push notifications and cloud sync
- **Q2 2025**: Advanced analytics and fitness integration
- **Q3 2025**: Multi-language support and food recognition
- **Q4 2025**: Social features and offline AI capabilities

---

## 📈 Project Stats

<!-- Project stats will be updated when repository is public -->
- 📱 **App Version**: 1.0.0
- 🆔 **Package ID**: com.example.fit2081a1_yang_xingyu_33533563
- 📦 **APK Size**: ~15MB
- 🎯 **Minimum API**: 31 (Android 12)
- 🚀 **Target API**: 35 (Android 14)

---

---

## Quick Navigation

- **[📖 Complete API Documentation](./API_DOCUMENTATION.md)** - Start here for a comprehensive overview
- **[🗄️ Database Documentation](./DATABASE_DOCUMENTATION.md)** - Database schema and operations
- **[🎨 UI Components Documentation](./UI_COMPONENTS_DOCUMENTATION.md)** - UI components and design system
- **[🏛️ ViewModels Documentation](./VIEWMODEL_DOCUMENTATION.md)** - State management and business logic

---

*Built with ❤️ using modern Android development practices*